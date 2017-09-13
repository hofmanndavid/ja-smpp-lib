package smpp4j;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smpp4j.pdu.Command;
import smpp4j.pdu.DeliverSm;
import smpp4j.pdu.DeliverSmResp;
import smpp4j.pdu.EnquireLink;
import smpp4j.pdu.EnquireLinkResp;
import smpp4j.pdu.SubmitSm;
import smpp4j.pdu.SubmitSmResp;
import smpp4j.util.PduIO;
import smpp4j.util.SocketUtil;

public class Smpp4j {
	
	private static final Logger log = LoggerFactory.getLogger(Smpp4j.class);
	
	private static enum ConnectionState { CONNECTED, DISCONNECTED };

	private final String host;
    private final Integer port;
    
    private final Command bind;
    private volatile AtomicInteger sequenceNumber = new AtomicInteger(1);
    
    private final int socketTimeoutMillis = 5_000;
    private final int enquireLinkEveryMillis = 1000;
    private final int reconnectAfterInactivityMillis = 7_000;
    private final int waitMillisToReconnectAfterFailure = 7_000;
    private final int spinReadRetryMillis = 500;
    
    private volatile SmppEventListener smppEventListener;
    private volatile Socket socket;
    // will the use use of atomic log/boolean insteand of volatile improve performance ?
    private volatile long lastActivityTimestamp = 0; 
    private volatile boolean bound = false; 
    private volatile boolean running = false;
    
    private volatile ConnectionState lastconnectionState = ConnectionState.DISCONNECTED;
    
    private final Thread enquireLinkThread;
	private final Thread readerThread;
	private final Thread watcherThread;
	
	private final String logPreFix;
	
	public Smpp4j(String host, int port, Command bind) {
		this.host = host;
		this.port = port;
		this.bind = bind;
		
		this.logPreFix = "SMPP SMSC["+host+":"+port + "] ";
		
		running = true;
		
		enquireLinkThread = getEnquireLinkThread();
		readerThread = getReaderThread();
		watcherThread = getWatcherThread();
	}
	
	public void start() {
		watcherThread.start();
		readerThread.start();
		enquireLinkThread.start();
	}

	private final Object lock = new Object();
	public void send(Command command) {
		
		if (!command.getClass().getSimpleName().endsWith("Resp"))
			command.setSequenceNumber(sequenceNumber.getAndIncrement());
		
		byte[] pdubytes = command.encode();
		
		if ( 
				command instanceof EnquireLinkResp || 
			 command instanceof EnquireLink
			 || command instanceof DeliverSmResp ||
			 command instanceof SubmitSm
			 ) {	
		} else {
			log.info("--> {} {}", logPreFix, command);
		}
		
		synchronized (lock) {
			try {
				if (bound || command == bind) {
					socket.getOutputStream().write(pdubytes);
				} else { 
					log.error(logPreFix+"Packet lost: :"+ command);
				}
			} catch (Exception e) {
				log.error(logPreFix+"While sending command: "+command.toString()+", "+e.getMessage(), e);
				bound = false;
				watcherThread.interrupt(); // in case it is sleeping
			}
		}
	}
	
	private Thread getReaderThread() {
		return new Thread()  {
			public void run() {
				
				while (running) {
					try {
						if (!bound) {
							try {
								Thread.sleep(spinReadRetryMillis);
							} catch (InterruptedException ignore) { }
							continue;
						}
						
						Command pdu = PduIO.readPduCommand(socket.getInputStream());
						
						if (pdu != null) {
							lastActivityTimestamp = System.currentTimeMillis();
							
							if ( pdu instanceof EnquireLinkResp || 
								 pdu instanceof EnquireLink 
								 ||
								 pdu instanceof DeliverSm ||
								 (pdu instanceof SubmitSmResp && pdu.getCommandStatus() == 0 ) 
								 ) {
								
							} else {
								log.info("<-- {} {}", logPreFix, pdu);
							}
							
							if (pdu instanceof EnquireLinkResp || (pdu instanceof SubmitSmResp && pdu.getCommandStatus() == 0) )
								continue;
							
							if (pdu instanceof EnquireLink) {
								EnquireLinkResp elr = new EnquireLinkResp();
								elr.setSequenceNumber(pdu.getSequenceNumber());
								send(elr);
								continue;
							}
							
							if (pdu instanceof DeliverSm) {
								DeliverSmResp dsmr = new DeliverSmResp();
								dsmr.setSequenceNumber(pdu.getSequenceNumber());
								send(dsmr);
							}
							
							smppEventListener.onReceive(pdu); // TODO add try catch...
						}
						
					} catch (Exception e) {
						log.error(logPreFix+"While reading packet: "+e.getMessage(), e);
						
						// // if e == java.net.SocketException
							// unbound the connection...
							bound = false;
							watcherThread.interrupt(); // in case it is sleeping
						
					}
				}
			}
		};
	}
	
	private Thread getEnquireLinkThread() {
		return new Thread() {
			private static final int swapSeqNrAt = Integer.MAX_VALUE-1_000_000;// safety ?
			public void run() {
				while (running) {
					try {
						sequenceNumber.compareAndSet(swapSeqNrAt, 1); 
						
						if (bound && inactivityMillis() > enquireLinkEveryMillis)
							send(new EnquireLink());
						
						try {
							Thread.sleep(enquireLinkEveryMillis);
						} catch (InterruptedException ignore) { }
						
					} catch (Exception e) {
						log.error(logPreFix+"Enquirelink Thread: "+e.getMessage(), e);
					}
				}
			}
		};
	}
	
	private Thread getWatcherThread() {
		return new Thread() { // watch for broken connection
			public void run() {
				while (running) {
					try {
						if (trafficLooksBroken()) { // if traffic looks unbounded
							while (!reconnect()) {
								Thread.sleep(waitMillisToReconnectAfterFailure);
							}
						} else {
							try {
								Thread.sleep(reconnectAfterInactivityMillis);
							} catch (InterruptedException ie) {}
						}
					} catch (Exception e) {
						log.error(logPreFix+"Connection Activity Watcher thread: "+e.getMessage(), e);
						try { Thread.sleep(reconnectAfterInactivityMillis); } catch (Exception ignore) { }
					}
				}
			}
		};
	}
	
	
	// CONNECTION lifecycle utilities...
	// ================================================================
	private boolean trafficLooksBroken() {
		return !bound || inactivityMillis() > reconnectAfterInactivityMillis;
	}
	
	private long inactivityMillis() {
		return System.currentTimeMillis() - lastActivityTimestamp;
	}
	
	public void setPacketListener(SmppEventListener packetListener) {
		this.smppEventListener = packetListener;
	}
	
	private boolean reconnect() {
		tearDownSocket();
		socket = SocketUtil.openSocket(host, port, socketTimeoutMillis);
		if (socket != null)
			return bind();
		
		return false;
	}
	
	private void tearDownSocket() {
		SocketUtil.tearDownSocket(socket);

		if (lastconnectionState == ConnectionState.CONNECTED)
			smppEventListener.connectionClosed(); // TODO try/catch here
		
		lastconnectionState = ConnectionState.DISCONNECTED;
	}
	
	private boolean bind() {
		try {
			send(bind);
			Command command = PduIO.readPduCommand(socket.getInputStream());
			bound = command.getCommandStatus() == 0;
			log.info(logPreFix+ "Socket bind: "+ bound + ": "+ command.toString());
			lastActivityTimestamp = System.currentTimeMillis();
			if (bound) {
				smppEventListener.connectionActive();
				readerThread.interrupt();
				enquireLinkThread.interrupt();
				lastconnectionState = ConnectionState.CONNECTED;	
			}
			return bound;
		} catch (Exception e) {
			log.error(logPreFix+"While binding: "+e.getMessage(), e);
		}
		
		return false;
	}
	
}

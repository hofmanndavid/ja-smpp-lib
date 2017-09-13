package smpp4j.test;

import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import smpp4j.Constants.Npi;
import smpp4j.Constants.Ton;
import smpp4j.SmppEventListener;
import smpp4j.pdu.BindReceiver;
import smpp4j.pdu.BindReceiverResp;
import smpp4j.pdu.BindTransceiver;
import smpp4j.pdu.BindTransceiverResp;
import smpp4j.pdu.BindTransmitter;
import smpp4j.pdu.BindTransmitterResp;
import smpp4j.pdu.Command;
import smpp4j.pdu.DeliverSm;
import smpp4j.pdu.EnquireLink;
import smpp4j.pdu.EnquireLinkResp;
import smpp4j.pdu.SubmitSm;
import smpp4j.pdu.SubmitSmResp;
import smpp4j.util.LineConverter;
import smpp4j.util.PduIO;

public class Smpp4jServer {
	
	private final Integer port;
    
    private final AtomicInteger sequenceNumber = new AtomicInteger(1); // checkpoint to restart the connection at 2.100.000.000
    private final SmppEventListener smppEventListener;
    private volatile Socket socket;
    
	public Smpp4jServer(int port, SmppEventListener smppEventListener) {
		this.port = port;
		this.smppEventListener= smppEventListener;
	}
	
	public void start() {
		new Thread() {
			public void run() {
				try {
					socket = new ServerSocket(port).accept();
					startThreads();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	
	public void sendMessage(String line, String shortCode, String message) {

		DeliverSm dsm = new DeliverSm();
		dsm.setDestAddrTon(Ton.NETWORK_SPECIFIC);
		dsm.setDestAddrNpi(Npi.ISDN);
		dsm.setDestAddr(shortCode);
		
		dsm.setSourceAddr(LineConverter.toMsisdn(line));
		dsm.setSourceAddrTon(Ton.INTERNATIONAL);
		dsm.setSourceAddrNpi(Npi.ISDN);
		
		try {
			dsm.setShortMessageBytes("".getBytes());
			dsm.setShortMessageBytes(message.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		sendPacket(dsm);
		
	}
	
	public synchronized void sendPacket(Command command) {
		try {
			if (command.getSequenceNumber() == 0)
				command.setSequenceNumber(sequenceNumber.getAndIncrement());
				
			socket.getOutputStream().write(command.encode());
			
			System.out.println("--> "+ command.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startThreads() {
		
		new Thread() { // READER THREAD
			public void run() {
				while (true) {
					try {
						Command pdu = PduIO.readPduCommand(socket.getInputStream());
						
						if (pdu instanceof EnquireLink) {
							
							EnquireLinkResp elr = new EnquireLinkResp();
							elr.setSequenceNumber(pdu.getSequenceNumber());
							sendPacket(elr);
							continue;
						}
						
						if (pdu instanceof SubmitSm) {
							SubmitSmResp dsmr = new SubmitSmResp();
							dsmr.setSequenceNumber(pdu.getSequenceNumber());
							sendPacket(dsmr);
						}
						
						System.out.println("<-- "+ pdu.toString());
						
						if (pdu instanceof BindTransceiver) {
							BindTransceiverResp btr = new BindTransceiverResp();
							btr.setSequenceNumber(pdu.getSequenceNumber());
							sendPacket(btr);
							continue;
						}
						
						if (pdu instanceof BindTransmitter) {
							BindTransmitterResp btr = new BindTransmitterResp();
							btr.setSequenceNumber(pdu.getSequenceNumber());
							sendPacket(btr);
							continue;
						}
						
						if (pdu instanceof BindReceiver) {
							BindReceiverResp btr = new BindReceiverResp();
							btr.setSequenceNumber(pdu.getSequenceNumber());
							sendPacket(btr);
							continue;
						}
						
						smppEventListener.onReceive(pdu);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}
	
}

package smpp4j.ussd;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import smpp4j.Smpp4j;
import smpp4j.SmppEventListener;
import smpp4j.Constants.*;
import smpp4j.pdu.Command;
import smpp4j.pdu.DeliverSm;
import smpp4j.pdu.SubmitSm;
import smpp4j.pdu.SubmitSmResp;
import smpp4j.pdu.Tlv;

public class UssdManager {
	
	public static interface SessionInitializer {
		public UssdSession getNewSession(String number);
	}
	
	private final Smpp4j connection;
	private final Executor executor;
	private final String shortUssdCode;
	private final ConcurrentMap<String, UssdSession> sessions;
	private final SessionInitializer sessionInitializer;
	private final String sourceAddr;
	public UssdManager(Smpp4j connection, String shortUssdCode, SessionInitializer sessionInitializer, Executor executor) {
		this.connection = connection;
		this.shortUssdCode="*"+shortUssdCode;
		this.executor = executor;
		this.sessionInitializer =sessionInitializer;
		this.sessions =  new ConcurrentHashMap<String, UssdSession>(64, 0.75f, getConcurrentMapPartitions());
		this.sourceAddr = shortUssdCode;
	}
	
	private int getConcurrentMapPartitions() {
		int mapPartitions = Runtime.getRuntime().availableProcessors();
		if (mapPartitions > 3)
			mapPartitions = mapPartitions - 1;
		else if (mapPartitions < 2)
			mapPartitions = 3;
		
		return mapPartitions;
	}
	
	public void startPush(UssdSession eiSession) {
		eiSession.isPush = true;
		try {
			UssdMessage ussdmsg = eiSession.next("push");
			SubmitSm ssm = new SubmitSm();
			ssm.setDestAddr(eiSession.getMsisdin());
			ssm.setDestAddrTon(Ton.NATIONAL);
			ssm.setDestAddrNpi(Npi.ISDN);
			ssm.setDataCoding(DataCoding.DEFAULT);
//			ssm.setShortMessageBytes(compress(convertUnicode2GSM(ussdmsg.message)));//.getBytes("UTF-8"/*"US-ASCII"*/));
//			ssm.setShortMessageBytes(ussdmsg.message.getBytes("ISO-8859-1"));
			ssm.setShortMessageBytes(ussdmsg.message.getBytes("US-ASCII"));
			ssm.setSourceAddr(sourceAddr);
			ssm.setSourceAddrTon(Ton.NATIONAL);
			ssm.setSourceAddrNpi(Npi.ISDN);
			ssm.setServiceType("USSD");
//			ssm.setReplaceIfPresentFlag(BooleanValue.TRUE);
			
			if (ussdmsg.isFinal()) {
				ssm.setTlvArray(new Tlv[] {new Tlv(TlvCode.ussd_service_op, new byte[]{3})
//				, new Tlv(TlvCode.its_session_info, new byte[] {0, 1})
				});
			} else {
				ssm.setTlvArray(new Tlv[] {new Tlv(TlvCode.ussd_service_op, new byte[]{2})						   
						, new Tlv(TlvCode.its_session_info, new byte[] {0, 0})
				});
				
				sessions.put(eiSession.getMsisdin(), eiSession);
			}
			
			connection.send(ssm);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void removeSession(String sessionKey) {
		UssdSession ussdSession = sessions.remove(sessionKey);
		ussdSession.cleanUpSession();
	}
	private String getSessionKey(String addr, byte itssessioninfo) {
		return addr+String.valueOf((int) itssessioninfo);
	}
	
	public void start() {
		
		this.connection.setPacketListener(new SmppEventListener() {
			public void onReceive(Command packet) {
				if (packet instanceof SubmitSmResp) {
					// hay error, solo nos llegan los submitsmresp con status code != 0
					// loguear ?
					return;
				}
				
				if (! (packet instanceof DeliverSm)) {
					// loguear ?
					return;
				}
				
				final DeliverSm dsm = (DeliverSm) packet;
				
				Runnable handle_ussd_req = new Runnable() {
					public void run() {
						
						Tlv ussdServiceOp = dsm.getTlv(TlvCode.ussd_service_op);
						if (ussdServiceOp.value[0] == 19) {
							System.out.println("ussd service op == null");
							return;
						}
						
						Tlv itsSessionInfo = dsm.getTlv(TlvCode.its_session_info);
						String sessionKey = getSessionKey(dsm.getSourceAddr(), itsSessionInfo.value[0]);
						
						boolean isNewMoSession = false;
						boolean isNewEiSession = false;
						
						UssdSession ussdSession = sessions.get(sessionKey);
						if (ussdSession == null) {
							ussdSession = sessions.remove(dsm.getSourceAddr());
							if (ussdSession != null) { // esto vino de push
								sessions.put(sessionKey, ussdSession);
								isNewEiSession = true;
							}
						}
						
						if (ussdSession == null) {
							ussdSession = sessionInitializer.getNewSession(dsm.getSourceAddr());
							sessions.put(sessionKey, ussdSession);
							if (!isNewEiSession)
								isNewMoSession = true;
						}
						
						String str = null;
						try {
							str = new String(dsm.getShortMessageBytes(), "US-ASCII").trim();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						UssdMessage ussdmsg = null;
						String[] shortCodes = isNewMoSession ? getShortCodeArrayIfFound(str) : null;
						
						if (shortCodes == null) // the shortcut way (menu)
							ussdmsg = ussdSession.next(str);
						else // the shortcut way
							for (String s : shortCodes) 
								if ((ussdmsg = ussdSession.next(s)).isFinal()) 
									break;
						
						if (ussdmsg.isFinal()) // TODO add code to catch when the network closes the session for some esotheric reason
							removeSession(sessionKey);
						
						SubmitSm ssm = new SubmitSm();
						ssm.setDestAddr(dsm.getSourceAddr());
						ssm.setDestAddrTon(Ton.NATIONAL);
						ssm.setDestAddrNpi(Npi.ISDN);
						ssm.setDataCoding(DataCoding.DEFAULT);
						ssm.setSourceAddr(sourceAddr); 	
						ssm.setSourceAddrTon(Ton.NATIONAL);
						ssm.setSourceAddrNpi(Npi.ISDN);
						ssm.setServiceType("USSD");
//						ssm.setReplaceIfPresentFlag(BooleanValue.TRUE);
						
						try {
							ssm.setShortMessageBytes(ussdmsg.message.getBytes("US-ASCII"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						byte ussd_op_code_byte = 0;
						byte[] its_session_info_bytes = new byte[] {
								dsm.getTlv(TlvCode.its_session_info).value[0],
								dsm.getTlv(TlvCode.its_session_info).value[1] 
										};
						if (ussdSession.isPush) { // Mobile initiated
							ussd_op_code_byte = (byte) (ussdmsg.isFinal() ? 3: 2);
						} else { // Esme Initiated	
							ussd_op_code_byte = (byte) (ussdmsg.isFinal() ? 17: 2);
						}
						
						if (ussdmsg.isFinal())
							its_session_info_bytes[1] = (byte) (its_session_info_bytes[1] | 0b00000001);
						
						ssm.setTlvArray(new Tlv[] { 
								new Tlv(TlvCode.ussd_service_op, new byte[]{ussd_op_code_byte}),
								new Tlv(TlvCode.its_session_info, its_session_info_bytes)}
						);
						
						connection.send(ssm);
					}
				};
				
				executor.execute(handle_ussd_req);
			}
			
			public void connectionClosed() {
				System.out.println("USSD manager connection CLOSED notification");
			}
			
			public void connectionActive() {
				System.out.println("USSD manager connection ACTIVE notification");
			}
		});
		
		connection.start();
	}
	
	private String[] getShortCodeArrayIfFound(String message) {
		try {
			if (message.charAt(shortUssdCode.length()) == '#')
				return null;
			
			String[] split = message.substring(shortUssdCode.length()+1, message.indexOf("#")).split("\\*");
			
			String[] resp = new String[split.length+1];
			resp[0] = message;
			for (int i=0;i<split.length;i++)
				resp[i+1] = split[i];
			
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	  
}

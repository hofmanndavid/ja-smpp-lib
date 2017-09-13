package smpp4j.test.ussd;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.dom4j.Element;

import smpp4j.Smpp4j;
import smpp4j.Constants.Npi;
import smpp4j.Constants.Ton;
import smpp4j.pdu.BindTransceiver;
import smpp4j.ussd.UssdManager;
import smpp4j.ussd.UssdMessage;
import smpp4j.ussd.UssdSession;
import smpp4j.ussd.UssdManager.SessionInitializer;
import smpp4j.ussd.UssdMessage.R;
import smpp4j.ussd.xml.XmlUssdSession;

public class SimpleSessionR extends UssdSession {

	public static void main(String[] args) throws Exception {
		BindTransceiver bind = new BindTransceiver();
		bind.setAddrNpi(Npi.ISDN);
		bind.setAddrTon(Ton.NATIONAL);
		bind.setSystemType("");
		bind.setSystemId("1");
		bind.setPassword("");
		bind.setAddressRange("111");
		
//		bind.setSystemType("USSD");
//		bind.setSystemId("739");
//		bind.setPassword("123");
//		bind.setAddressRange("");

//		ussd01: 127.0.0.1
//		port: 4545
//		system-id: 000
//		pass: asdf

//		"UssdNpi";"1"
//		"USSDShortNumber";"111"
//		"UssdSystemId";"asdf"
//		"UssdSystemPass";"asdf"
//		"UssdSystemType";"USSD"
//		"UssdTon";"2"
		
		SessionInitializer si = new SessionInitializer() {
			public UssdSession getNewSession(String number) {
				return new SimpleSessionR(number);
			}
		};
		
		Executor executorService = Executors.newCachedThreadPool();
		
		// "10.255.98.10[12]", 10500
		// "10.16.28.[23]", 4545
		
		UssdManager mgr = new UssdManager(new Smpp4j("127.0.0.1", 5050, bind), "739", si, executorService);
		mgr.start();

//		UssdManager mgr2 = new UssdManager(new Smpp4j("10.255.98.102", 10500, bind), "739", si, executorService);
//		mgr2.start();
		
		Thread.sleep(800);
		mgr.startPush(new SimpleSessionR("59597111111"));
//		Thread.sleep(2000);
		mgr.startPush(new SimpleSessionR("59597111111"));
		System.exit(0);
	}
	
	public SimpleSessionR(String msisdn) {
		super(msisdn);
	
		setNextReceiver(iniMenu);
	}
	
	private final R iniMenu = new R() { 
		public UssdMessage onReceive(String message) {
			if (message.equals("0"))
				return new UssdMessage("prueba ussd nuevo hijo");
			else
				return new UssdMessage("prueba ussd nuevo f", iniMenu);
			
		} };

	public void cleanUpSession() {
		
	}
}


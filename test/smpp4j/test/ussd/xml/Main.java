package smpp4j.test.ussd.xml;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import smpp4j.Constants.Npi;
import smpp4j.Constants.Ton;
import smpp4j.Smpp4j;
import smpp4j.pdu.BindTransceiver;
import smpp4j.ussd.UssdManager;
import smpp4j.ussd.UssdSession;
import smpp4j.ussd.UssdManager.SessionInitializer;
import smpp4j.ussd.xml.XmlUssdSession;

public class Main {
	static final String menuFile = "C:/hdavid/wrk/eclipse/smpp4j/test/smpp4j/test/ussd/xml/simplemenu.xml";
	public static void main(String[] args) throws Exception {
		
		BindTransceiver bind = new BindTransceiver();
		bind.setAddrNpi(Npi.ISDN);
		bind.setAddrTon(Ton.NATIONAL);
		bind.setSystemType("USSD");
		bind.setSystemId("739");
		bind.setPassword("123");
		
		SessionInitializer si = new SessionInitializer() {
			public UssdSession getNewSession(String number) {
				Element e = getXmlRootElement(menuFile);
				return new XmlUssdSession(number, e, "root", null);
			}
		};
		
		Executor executorService = Executors.newCachedThreadPool();
		
		UssdManager mgr = new UssdManager(new Smpp4j("127.0.0.1", 4545, bind), "739", si, executorService);
		mgr.start();

		UssdManager mgr2 = new UssdManager(new Smpp4j("127.0.0.1", 4545, bind), "739", si, executorService);
		mgr2.start();

		startPush(mgr);
	}
	
	public static void startPush(UssdManager mgr) {
		mgr.startPush(new XmlUssdSession("595111111", getXmlRootElement(menuFile), "op1", null));
	}
	
	public static Element getXmlRootElement(String file) {
		try {
			SAXReader saxReader = new SAXReader();
			saxReader.setEncoding("UTF-8");
			Element e = saxReader.read(new File(file)).getRootElement();
			return e;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

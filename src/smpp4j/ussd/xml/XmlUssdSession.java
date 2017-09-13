package smpp4j.ussd.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import smpp4j.ussd.UssdSession;


public class XmlUssdSession extends UssdSession {
	
	public XmlUssdSession(String number, Element rootDef, String initEntry, Map<String, String> collectedCodes) {
		super(number);
		
		if (collectedCodes == null)
			collectedCodes = new HashMap<>();
		
		collectedCodes.put("msisdn", number);
		
		Map<String, String> urlFallbackMsgs = Util.getUrlFallbackMsgs(rootDef);
		
		Set<String> opCodes = Util.getOpCodes(rootDef, collectedCodes, urlFallbackMsgs);
		
		Entry entry = Util.getEntry(initEntry == null ? "root" : initEntry, rootDef);
		
		XmlR rr = new XmlR(opCodes, entry, true, collectedCodes, urlFallbackMsgs);
		
		setNextReceiver(rr);
	}

	public void cleanUpSession() { }
}

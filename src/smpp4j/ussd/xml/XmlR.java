package smpp4j.ussd.xml;

import java.util.Map;
import java.util.Set;

import smpp4j.ussd.UssdMessage;
import smpp4j.ussd.UssdMessage.R;
import smpp4j.ussd.xml.Entry.Action;
import smpp4j.ussd.xml.Entry.ActionType;
import smpp4j.ussd.xml.Entry.ExtActionType;
import smpp4j.ussd.xml.Entry.MessageType;
import smpp4j.ussd.xml.Entry.OpEntryMsg;

public class XmlR implements R {
	
	final Entry e;
	final boolean firstHalf;
	final Set<String> opCodes;
	final Map<String, String> collectedCodes;
	final Map<String, String> urlFallbackMsgs;
	
	public XmlR(Set<String> opCodes, Entry e, boolean firstHalf, Map<String, String> collectedCodes, Map<String, String> urlFallbackMsgs) {
		this.e = e;
		this.firstHalf = firstHalf;
		this.opCodes = opCodes;
		this.collectedCodes = collectedCodes;
		this.urlFallbackMsgs = urlFallbackMsgs;
	}
	
	public UssdMessage onReceive(String message) {
		return firstHalf ? processFirstHalf(message) : processSecondHalf(message);
	}
	
	private UssdMessage processFirstHalf(String message) {
		
		if (e.extActBefore != null) {
			String resp = null;
			if (e.extActBefore.type == ExtActionType.HTTP) {
				resp = Util.get(e.extActBefore.value, urlFallbackMsgs.get(e.extActBefore.fallbackCode), collectedCodes);
				
			} else if (e.extActBefore.type == ExtActionType.CLASS) {
				resp = Util.getReflexValue(e.extActBefore.value, urlFallbackMsgs.get(e.extActBefore.fallbackCode), collectedCodes);
			} // else ?
			
			if (!resp.startsWith("OK"))
				return new UssdMessage(Util.getMsgContent(resp));
		}
		
		String msg = null;
		if (e.mt == MessageType.STATIC) {
			msg = e.staticMsg;
		} else if (e.mt == MessageType.LIST) {
			StringBuilder sb = new StringBuilder();
			Integer i= 1;
			e.displayedOpId_realOpId.clear();
			for (OpEntryMsg opentry : e.msgOpList) {
				if (opentry.id.equals("0")) {
					sb.append(opentry.msg);
					continue;
				}
				if (opentry.requiresCode != null && !opentry.requiresCode.trim().isEmpty())
					if (!opCodes.contains(opentry.requiresCode)) {
						System.out.println("Ignoring option list: "+ opentry.id);
						continue;
					}
				
				if (sb.length()>0)
					sb.append("\n");
				
				sb.append(i.toString()).append("- ").append(opentry.msg);
				e.displayedOpId_realOpId.put(i.toString(), opentry.id);
				i++;
			}
			msg = sb.toString();
		} else if (e.mt == MessageType.DINAMIC){
			String extActMsg = "";
			if (e.extActMsg.type == ExtActionType.HTTP)
				extActMsg = Util.get(e.extActMsg.value, urlFallbackMsgs.get(e.extActMsg.fallbackCode), collectedCodes);
			else //if (e.extActMsg.type == ExtActionType.CLASS)
				extActMsg = Util.getReflexValue(e.extActMsg.value, urlFallbackMsgs.get(e.extActMsg.fallbackCode), collectedCodes);
			msg = extActMsg;
		} else {
			throw new RuntimeException("not implemented exception e.mt == MessageType.DINAMIC");
		}
		
		R nextR = e.hasNext() ? new XmlR(opCodes, e, false, collectedCodes, urlFallbackMsgs) : null;
		
		return new UssdMessage(msg, nextR);		
	}
	
	private UssdMessage processSecondHalf(String message) {
		collectedCodes.put(e.msgCode, message);
		if (e.extActAfter != null) {
			String resp = null;
			if (e.extActAfter.type == ExtActionType.HTTP) {
				resp = Util.get(e.extActAfter.value, urlFallbackMsgs.get(e.extActAfter.fallbackCode), collectedCodes);
				
			} else if (e.extActBefore.type == ExtActionType.CLASS) {
				resp = Util.getReflexValue(e.extActAfter.value, urlFallbackMsgs.get(e.extActAfter.fallbackCode), collectedCodes);
			} // else ?
			
			if (!resp.startsWith("OK"))
				return new UssdMessage(Util.getMsgContent(resp));
		}
		
		for (Action a : e.actionList) {
			if (a.at == ActionType.NAVIGATE) {
				Entry nextEntry = Util.getEntry(a.value, e.rootElem);
				R nextR = new XmlR(opCodes, nextEntry, true, collectedCodes, urlFallbackMsgs);
				return nextR.onReceive(message);
			} if (a.at == ActionType.SEL_IF) {
				String id = e.displayedOpId_realOpId.get(message); // TODO clean and sanitize the message before comparision and use a metdod for fuzzy matching
				if (a.value.equals(id) || a.value.equals("*")){ 
					if (a.nestedAction.at == ActionType.NAVIGATE) {
						Entry nextEntry = Util.getEntry(a.nestedAction.value, e.rootElem);
						R nextR = new XmlR(opCodes, nextEntry, true, collectedCodes, urlFallbackMsgs);
						return nextR.onReceive(message);
					} else if (a.nestedAction.at == ActionType.SET) {
						collectedCodes.put(a.nestedAction.value, a.nestedAction.value2);
					} else {
						throw new RuntimeException("(sel-if) not implemented exception nested action != navigate");
					}
				}
				
			} else if (a.at == ActionType.VAL_IF) {
				// TODO clean and sanitize the message before comparision and use a metdod for fuzzy matching
				if (a.value.equals(message)) { 
					if (a.nestedAction.at == ActionType.NAVIGATE) {
						Entry nextEntry = Util.getEntry(a.nestedAction.value, e.rootElem);
						R nextR = new XmlR(opCodes, nextEntry, true, collectedCodes, urlFallbackMsgs);
						return nextR.onReceive(message);
					} else if (a.nestedAction.at == ActionType.SET) {
						collectedCodes.put(a.nestedAction.value, a.nestedAction.value2);
					} else {
						throw new RuntimeException("(val-if) not implemented exception nested action != navigate");
					}
				}
			} else if (a.at == ActionType.SET) {
				collectedCodes.put(a.value, a.value2);
			} else {
				throw new RuntimeException("Not implemented");
			}
		}
		return new UssdMessage(message);
	}
}

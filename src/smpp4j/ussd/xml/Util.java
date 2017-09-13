package smpp4j.ussd.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import smpp4j.ussd.xml.Entry.Action;
import smpp4j.ussd.xml.Entry.ActionType;
import smpp4j.ussd.xml.Entry.ExtAction;
import smpp4j.ussd.xml.Entry.ExtActionType;
import smpp4j.ussd.xml.Entry.MessageType;
import smpp4j.ussd.xml.Entry.OpEntryMsg;

class Util {
	
	static String getReflexValue(String class_method, String fallbackMsg, Map<String, String> collectedCodes) {
		String result = fallbackMsg;
		try {
			String clazzName = class_method.substring(0, class_method.lastIndexOf("."));
			String methodName = class_method.substring(class_method.lastIndexOf(".")+1);
			
			Class<?> clazz = Class.forName(clazzName);
			Method method = clazz.getMethod(methodName, Map.class);
			
			Object instance = clazz.newInstance();
			result = (String) method.invoke(instance, collectedCodes);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = fallbackMsg;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	static String getGetUrlWithParams(String baseUrl, Map<String, String> collectedCodes) {
		String url = null;
		try {
			StringBuilder sb = new StringBuilder(baseUrl).append("?");
			boolean first = true;
			for (String key : collectedCodes.keySet()){
				if (!first)
					sb.append("&");
				first = false;
				
				sb.append(URLEncoder.encode(key, "UTF-8"))
				  .append("=")
				  .append(URLEncoder.encode(collectedCodes.get(key), "UTF-8"));
			}
			url = sb.toString();
			System.out.println(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	
	static String get(String baseUrl, String fallbackMsg, Map<String, String> collectedCodes) {
		String url = getGetUrlWithParams(baseUrl, collectedCodes);
		String result = fallbackMsg;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(3000); // millis
			conn.setReadTimeout(7000); // millis
			is = conn.getInputStream();
			int statusCode = conn.getResponseCode();
			
			if (statusCode == 200) {
				bos = new ByteArrayOutputStream();
				int readed = 0;
				while ((readed = is.read()) != -1)
					bos.write(readed);
				result = new String(bos.toByteArray(), "UTF-8");
			}
			
		} catch (Exception e1) {
			System.out.println(e1.getMessage()); 
			result = fallbackMsg;
		} finally {
			try { bos.close(); } catch (Exception ex) {System.out.println(ex.getMessage()); }
			try { is.close(); } catch (Exception ex) {System.out.println(ex.getMessage()); }
			try { conn.disconnect(); } catch (Exception ex) {System.out.println(ex.getMessage()); }
		}
		
		return result;
	}
	
	static Set<String> getOpCodes(Element rootDef, Map<String, String> collectedCodes, Map<String, String> fallbackUrlMsgs) {
		Set<String> opCodes = new HashSet<>();
		
		Element opCodesElem = rootDef.element("load-codes");
		if (opCodesElem == null)
			return opCodes;
		
		if (opCodesElem.elements().isEmpty()) { // load xml string codes
			opCodes.addAll(Arrays.asList(opCodesElem.getTextTrim().split(",")));
			return opCodes;
		}
		
		if (opCodesElem == null) {
			opCodes.addAll(Arrays.asList(opCodesElem.getTextTrim().split(",")));
			return opCodes;
		}
		
		if (opCodesElem.element("url") != null) {
			Element urlElem = opCodesElem.element("url");
			if (urlElem == null || urlElem.getText() == null || urlElem.getTextTrim().isEmpty())
				return opCodes;
			
			String url = urlElem.getTextTrim();
			String fid = urlElem.attributeValue("fallback-code");
			
			String content = get(url, fallbackUrlMsgs.get(fid), collectedCodes);
			
			opCodes.addAll(Arrays.asList(content.split(",")));
		} else { // load from reflex
			Element reflexElem = opCodesElem.element("reflex");
			String name = reflexElem.attributeValue("name");
			String fallbackCode = reflexElem.attributeValue("fallback-code");
			
			String resp = Util.getReflexValue(name, fallbackUrlMsgs.get(fallbackCode), collectedCodes);
			
			opCodes.addAll(Arrays.asList(resp.split(",")));
		} // else ?
		
		return opCodes;
	}
	
	static Map<String, String> getUrlFallbackMsgs(Element rootDef) {
		Map<String, String> msgs = new HashMap<>();
		
		Element msgsElem = rootDef.element("ext-action-fallback-msg");
		if (msgsElem == null)
			return msgs;
		
		for (Element m : (List<Element>) msgsElem.elements()) { // m == <msg code="1"> msg here </msg>
			msgs.put(m.attributeValue("code"), m.getTextTrim());
		}
		
		return msgs;
	}
	
	static Entry getEntry(String entryId, Element rootDef) {
		Element re = null;
		for (Element e : (List<Element>) rootDef.elements("entry")) {
			if (entryId.equals(e.attributeValue("id"))) {
				re = e;
				break;
			}
		}

		Entry entry = new Entry();
		entry.rootElem = rootDef;
		entry.id = re.attributeValue("id");
		for (Element entryElement : (List<Element>) re.elements()) {
			if (entryElement.getName().equals("before")) {
				ExtAction extAction = new ExtAction();
				if (entryElement.element("url") != null) {
					extAction.type = ExtActionType.HTTP;
					extAction.value = entryElement.element("url").getTextTrim();
					extAction.fallbackCode = entryElement.element("url").attributeValue("fallback-code");
				} else { // class
					extAction.type = ExtActionType.CLASS;
					extAction.value = entryElement.element("reflex").attributeValue("name");
					extAction.fallbackCode = entryElement.element("reflex").attributeValue("fallback-code");
				}
				entry.extActBefore = extAction;
			}
			if (entryElement.getName().equals("after")) {
				ExtAction extAction = new ExtAction();
				if (entryElement.element("url") != null) {
					extAction.type = ExtActionType.HTTP;
					extAction.value = entryElement.element("url").getTextTrim();
					extAction.fallbackCode = entryElement.element("url").attributeValue("fallback-code");
				} else { // class
					extAction.type = ExtActionType.CLASS;
					extAction.value = entryElement.element("reflex").attributeValue("name");
					extAction.fallbackCode = entryElement.element("reflex").attributeValue("fallback-code");
				}
				entry.extActBefore = extAction;
			}
			if (entryElement.getName().equals("msg")) {
				entry.msgCode= entryElement.attributeValue("code"); 
				if (entryElement.elements().isEmpty()) {
					entry.staticMsg = entryElement.getText();
					entry.mt = MessageType.STATIC;
				} else {
					for (Element msgElement : (List<Element>) entryElement.elements()) {
						if (msgElement.getName().equals("url") ) {
							ExtAction extAction = new ExtAction();
							extAction.value = msgElement.getTextTrim();
							extAction.fallbackCode = msgElement.attributeValue("fallback-code");
							entry.extActMsg = extAction;		
							entry.mt = MessageType.DINAMIC;
						} else if (msgElement.getName().equals("reflex")) {
							ExtAction extAction = new ExtAction();
							extAction.value = msgElement.attributeValue("name");
							extAction.fallbackCode = msgElement.attributeValue("fallback-code");
							entry.extActMsg = extAction;		
							entry.mt = MessageType.DINAMIC;
						} else if (msgElement.getName().equals("op")) {
							OpEntryMsg opEntryMsg = new OpEntryMsg();
							opEntryMsg.id = msgElement.attributeValue("id");
							opEntryMsg.requiresCode = msgElement.attributeValue("requires-code");
							opEntryMsg.msg = msgElement.getText();
							entry.msgOpList.add(opEntryMsg);
							entry.mt = MessageType.LIST;
						} else { 
							// ??
						}
					}
				}
			}
			
			if (entryElement.getName().equals("act")) {
				for (Element actElement : (List<Element>) entryElement.elements()) {
					if (actElement.getName().equals("navigate")) {
						Action action = new Action();
						action.at = ActionType.NAVIGATE;
						action.value = actElement.attributeValue("entry");
						entry.actionList.add(action);
					} else if (actElement.getName().equals("sel-if")) {
						for (Element actionInnerElem : (List<Element>) actElement.elements()) {
							Action action = new Action();
							action.at = ActionType.SEL_IF;
							action.value = actElement.attributeValue("id");
							action.nestedAction = new Action();
							if (actionInnerElem.getName().equals("navigate")) {	
								action.nestedAction.at = ActionType.NAVIGATE;
								action.nestedAction.value = actionInnerElem.attributeValue("entry");
							} else if (actionInnerElem.getName().equals("set")) {
								action.nestedAction.at = ActionType.SET;
								action.nestedAction.value = actionInnerElem.attributeValue("key");
								action.nestedAction.value2 = actionInnerElem.getText();
							} // else ?
							entry.actionList.add(action);
						}
					} else  if (actElement.getName().equals("val-if")) {
						for (Element actionInnerElem : (List<Element>) actElement.elements()) {
							Action action = new Action();
							action.at = ActionType.VAL_IF;
							action.value = actElement.attributeValue("value");
							action.nestedAction = new Action();
							if (actionInnerElem.getName().equals("navigate")) {	
								action.nestedAction.at = ActionType.NAVIGATE;
								action.nestedAction.value = actionInnerElem.attributeValue("entry");
							} else if (actionInnerElem.getName().equals("set")) {
								action.nestedAction.at = ActionType.SET;
								action.nestedAction.value = actionInnerElem.attributeValue("key");
								action.nestedAction.value2 = actionInnerElem.getText();
							} // else ?
							entry.actionList.add(action);
						}
					} else if (actElement.getName().equals("set")) {
						Action action = new Action();
						action.at = ActionType.SET;
						action.value = actElement.attributeValue("key");
						action.value2 = actElement.getText();
						entry.actionList.add(action);
					} // else ???
				}
			}
		}
		return entry;
	}

	static String getMsgContent(String extActionResp) {
		int idx = extActionResp.indexOf("\n")+1;
		return idx <= 0 ? "" : extActionResp.substring(idx);
	}
}

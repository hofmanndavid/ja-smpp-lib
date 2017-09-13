package smpp4j.ussd.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

public  class Entry {
	public static enum ExtActionType { HTTP, CLASS }
	public static class ExtAction {
		public ExtActionType type;
		public String value;
		public String fallbackCode;
	}
	
	public Set<String> opCodes = new HashSet<>();
	public Element rootElem;
	public String id;
	public MessageType mt;
	public String msgCode;
	public List<OpEntryMsg> msgOpList = new ArrayList<>();
	public Map<String, String> displayedOpId_realOpId = new HashMap<>();
	public String staticMsg;
	public ExtAction extActMsg;
	public ExtAction extActBefore;
	public ExtAction extActAfter;
	public List<Action> actionList = new ArrayList<>();
	
	public static enum ActionType { NAVIGATE, SEL_IF, VAL_IF, SET }
	public static class Action {
		public ActionType at;
		public String value;
		public String value2;
		public Action nestedAction;
	}
	
	public static class OpEntryMsg {
		public String id;
		public String requiresCode;
		public String msg;
	}
	
	public boolean hasNext() {
		return !actionList.isEmpty() || extActAfter != null;
	}
	
	public static enum MessageType { LIST, STATIC, DINAMIC }
}
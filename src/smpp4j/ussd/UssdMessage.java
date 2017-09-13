package smpp4j.ussd;

import java.util.HashMap;
import java.util.Map;

public class UssdMessage {
	
	public interface R {
		public UssdMessage onReceive(String message);
	}
	
	public final String message;
	public final R nextRR;
	public boolean isFinal() {
		return nextRR == null;
	}

	public UssdMessage (String message) {
		this.message = message;
		this.nextRR = null;
	}
	
	public UssdMessage (String message, R nextRR) {
		this.message = message;
		this.nextRR = nextRR;
	}
	
	public UssdMessage (String message, Object ... nextRR) {
		this.message = message;
		
		this.nextRR = getRRFromObject(nextRR);
	}
	
	private R getRRFromObject(Object[] obj) {
		if (obj == null || obj.length == 0)
			return null;
		if (obj[0] != null && obj[0] instanceof R)
			return (R) obj[0];
		
		final Map<Integer, R> map = new HashMap<Integer, R>();
		Object[] objArr = (Object[]) obj;
		for (int i=0;i<objArr.length;i = i+2)
			map.put((int) objArr[i], (R) objArr[i+1]);
		return new R() {
			public UssdMessage onReceive(String message) {
				return map.get(
						Integer.parseInt(
								message.replaceAll("[^0-9]", ""))).onReceive(message);
			}
		};
	}
}

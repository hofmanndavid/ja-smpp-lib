package smpp4j.ussd;
import smpp4j.ussd.UssdMessage.R;

public abstract class UssdSession {
	
	public final String msisdn;
	public UssdSession(String number) {
		this.msisdn = number;
	}
	
	public abstract void cleanUpSession();
	
	public String getMsisdin(){
		return msisdn;
	}
	private volatile R rr;
	
	public volatile boolean isPush = false;
	
	protected void setNextReceiver(R rr) {
		this.rr = rr;
	}
	
	public UssdMessage next(String message) {
		UssdMessage ussdmsg = rr.onReceive(message);
		this.rr = ussdmsg.nextRR;
		return ussdmsg;
	}
}

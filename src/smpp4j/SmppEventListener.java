package smpp4j;

import smpp4j.pdu.Command;

public interface SmppEventListener {

	public void onReceive(Command packet);
	public void connectionClosed();
	public void connectionActive();

}

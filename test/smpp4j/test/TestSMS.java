package smpp4j.test;

import smpp4j.Smpp4j;
import smpp4j.SmppEventListener;
import smpp4j.Constants.Npi;
import smpp4j.Constants.Ton;
import smpp4j.pdu.BindTransceiver;
import smpp4j.pdu.Command;
import smpp4j.pdu.DeliverSm;
import smpp4j.pdu.SubmitSm;

public class TestSMS  {

	public static void main(String[] args) {
//		smpp.1 = systemId:2,addrRange=11111
		
		BindTransceiver bt = new BindTransceiver();
		bt.setAddrTon(Ton.valueOf("NATIONAL"));
		bt.setAddrNpi(Npi.valueOf("ISDN"));
		bt.setAddressRange("11111");
		bt.setSystemType("SMS2");
		bt.setSystemId("2");
		bt.setPassword("");
		// a todas las instancias
		
		final Smpp4j smpp4j = new Smpp4j("127.0.0.1", 5050, bt);
		smpp4j.setPacketListener(new SmppEventListener(){
			public void onReceive(Command packet) { 
				System.out.println(packet);
				if (packet instanceof DeliverSm) {
					SubmitSm ssm = new SubmitSm();
					ssm.setShortMessageBytes(("[pong] "+new String(((DeliverSm)packet).getShortMessageBytes())).getBytes());
					ssm.setSourceAddr("11111");
					ssm.setDestAddr("595111111");
					smpp4j.send(ssm);
				}
			}
			
			public void connectionClosed() { System.out.println("Con Closed"); }
			public void connectionActive() { System.out.println("Con Active");  }});
		
		smpp4j.start();
	}


}

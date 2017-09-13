package smpp4j.pdu;

import smpp4j.Constants.PduCommand;

public class EnquireLink extends Command {

   public EnquireLink() {
      commandId = PduCommand.enquire_link.getCode();
   }

   protected int decodeBody(byte[] pdu, int offset) {
      return offset;
   }

   protected byte[][] encodeBody() {
      return null;
   }


}

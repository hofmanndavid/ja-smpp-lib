package smpp4j.pdu;

import smpp4j.Constants.PduCommand;

public class Unbind extends Command {

   public Unbind() {
      commandId = PduCommand.unbind.getCode();
   }

   protected int decodeBody(byte[] pdu, int offset) {
      return offset;
   }

   protected byte[][] encodeBody() {
      return null;
   }


}

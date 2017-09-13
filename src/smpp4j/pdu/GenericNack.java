package smpp4j.pdu;

import smpp4j.Constants.PduCommand;

public class GenericNack extends Command {

   public GenericNack() {
      commandId = PduCommand.generic_nack.getCode();
   }

   @Override
   protected int decodeBody(byte[] pdu, int offset) {
      return offset;
   }

   @Override
   protected byte[][] encodeBody() {
      return null;
   }
}

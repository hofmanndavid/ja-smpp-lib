package smpp4j.pdu;

import smpp4j.Constants.PduCommand;

public class CancelSmResp extends Command {

   public CancelSmResp() {
      commandId = PduCommand.cancel_sm_resp.getCode();
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

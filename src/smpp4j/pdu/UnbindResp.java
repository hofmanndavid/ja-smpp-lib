package smpp4j.pdu;

import smpp4j.Constants.PduCommand;

public class UnbindResp extends Command {

   public UnbindResp() {
      commandId = PduCommand.unbind_resp.getCode();
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

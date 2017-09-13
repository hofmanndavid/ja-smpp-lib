package smpp4j.pdu;

import smpp4j.Constants.PduCommand;

public class EnquireLinkResp extends Command {

   public EnquireLinkResp() {
      commandId = PduCommand.enquire_link_resp.getCode();
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

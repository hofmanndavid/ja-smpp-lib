package smpp4j.pdu;

import smpp4j.Constants.PduCommand;
import smpp4j.util.PduIO;


public class SubmitSmResp extends Command {

   public SubmitSmResp() {
      commandId = PduCommand.submit_sm_resp.getCode();
   }

   private String messageId;

   public void setMessageId(String message_id) {
      this.messageId = message_id;
   }

   public String getMessageId() {
      return messageId;
   }

   @Override
   protected int decodeBody(byte[] pdu, int offset) {
      messageId = PduIO.bytesToAsciiCString(pdu, offset);
      return offset + messageId.length() + 1;
   }

   @Override
   protected byte[][] encodeBody() {
      return new byte[][]{PduIO.stringToCString(messageId, true)};
   }


}

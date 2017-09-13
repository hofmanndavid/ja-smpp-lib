package smpp4j.pdu;

import smpp4j.Constants.PduCommand;
import smpp4j.util.PduIO;


public class QuerySmResp extends Command {

   public QuerySmResp() {
      commandId = PduCommand.query_sm_resp.getCode();
   }

   private String messageId = null; 
   private String finalDate = null;
   private int messageState;
   private int errorCode;

   public String getMessageId() {
      return messageId;
   }
   
   public void setMessageId(String message_id) {
      this.messageId = message_id;
   }
   
   public String getFinalDate() {
      return finalDate;
   }

   public void setFinalDate(String final_date) {
      this.finalDate = final_date;
   }
   
   public int getMessageState() {
      return messageState;
   }
   
   public void setMessageState(int message_state) {
      this.messageState = message_state;
   }
   
   public int getErrorCode() {
      return errorCode;
   }
   
   public void setErrorCode(int error_code) {
      this.errorCode = error_code;
   }

   @Override
   protected int decodeBody(byte[] pdu, int offset) {
      messageId = PduIO.bytesToAsciiCString(pdu, offset);
      offset = offset + messageId.length() + 1;
      
      finalDate = PduIO.bytesToAsciiCString(pdu, offset);
      offset = offset + finalDate.length() + 1;
      
      messageState = PduIO.bytesToInt(pdu, offset, 1);
      offset++;
      
      errorCode = PduIO.bytesToInt(pdu, offset, 1);
      
      return offset + 1;
   }

   @Override
   protected byte[][] encodeBody() {
      return new byte[][]{PduIO.stringToCString(messageId, true),
                  PduIO.stringToCString(finalDate, true),
                  PduIO.intToBytes(messageState, 1),
                  PduIO.intToBytes(errorCode, 1)};
   }


}

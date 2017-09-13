package smpp4j.pdu;

import smpp4j.Constants.Npi;
import smpp4j.Constants.PduCommand;
import smpp4j.Constants.Ton;
import smpp4j.util.PduIO;


public class QuerySm extends Command {

   public QuerySm() {
      commandId = PduCommand.query_sm.getCode();
   }

   private String messageId      = "";
   private int    sourceAddrTon = 0;
   private int    sourceAddrNpi = 0;
   private String sourceAddr     = "";

   public String getMessageId() {
      return messageId;
   }
   
   public void setMessageId(String message_id) {
      this.messageId = message_id;
   }

   public Ton getSourceAddrTon() {
      return Ton.forCode(sourceAddrTon);
   }

   public void setSourceAddrTon(Ton ton) {
      this.sourceAddrTon = ton.getCode();
   }
   
   public Npi getSourceAddrNpi() {
      return Npi.forCode(sourceAddrNpi);
   }
   
   public void setSourceAddrNpi(Npi npi) {
      this.sourceAddrNpi = npi.getCode();
   }

   public String getSourceAddr() {
      return sourceAddr;
   }
   
   public void setSourceAddr(String source_addr) {
      this.sourceAddr = source_addr;
   }

   @Override
   protected int decodeBody(byte[] pduBytes, int offset) {
      messageId = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + messageId.length() + 1;

      sourceAddrTon = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      sourceAddrNpi = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      sourceAddr = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + sourceAddr.length() + 1;

      return offset + sourceAddr.length();
   }

   @Override
   protected byte[][] encodeBody() {

      return new byte[][]{PduIO.stringToCString(messageId, true),
                  PduIO.intToBytes(sourceAddrTon, 1), 
                  PduIO.intToBytes(sourceAddrNpi, 1),
                  PduIO.stringToCString(sourceAddr, true)};
   }

}

package smpp4j.pdu;

import smpp4j.Constants.Npi;
import smpp4j.Constants.PduCommand;
import smpp4j.Constants.Ton;
import smpp4j.util.PduIO;


public class CancelSm extends Command {

   public CancelSm() {
      commandId = PduCommand.cancel_sm.getCode();
   }

   private String serviceType    = "";
   private String messageId      = "";
   private int    sourceAddrTon = 0;
   private int    sourceAddrNpi = 0;
   private String sourceAddr     = "";
   private int    destAddrTon = 0;
   private int    destAddrNpi = 0;
   private String destinationAddr     = "";

   public String getServiceType() {
      return serviceType;
   }
   
   public void setServiceType(String service_type) {
      this.serviceType = service_type;
   }
   
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
   
   public Ton getDestAddrTon() {
      return Ton.forCode(destAddrTon);
   }
   
   public void setDestAddrTon(Ton ton) {
      this.destAddrTon = ton.getCode();
   }
   
   public Npi getDestAddrNpi() {
      return Npi.forCode(destAddrNpi);
   }

   public void setDestAddrNpi(Npi npi) {
      this.destAddrNpi = npi.getCode();
   }

   public String getDestinationAddr() {
      return destinationAddr;
   }
   
   public void setDestinationAddr(String destination_addr) {
      this.destinationAddr = destination_addr;
   }

   @Override
   protected int decodeBody(byte[] pduBytes, int offset) {

      serviceType = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + serviceType.length() + 1;
      
      messageId = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + messageId.length() + 1;

      sourceAddrTon = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      sourceAddrNpi = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      sourceAddr = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + sourceAddr.length() + 1;
      
      destAddrTon = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      destAddrNpi = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      destinationAddr = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + destinationAddr.length() + 1;

      return offset + destinationAddr.length();
   }

   @Override
   protected byte[][] encodeBody() {

      return new byte[][]{PduIO.stringToCString(messageId, true),
                  PduIO.intToBytes(sourceAddrTon, 1), 
                  PduIO.intToBytes(sourceAddrNpi, 1),
                  PduIO.stringToCString(sourceAddr, true)};
   }

}

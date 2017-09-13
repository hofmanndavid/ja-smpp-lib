package smpp4j.pdu;

import smpp4j.Constants.Npi;
import smpp4j.Constants.PduCommand;
import smpp4j.Constants.Ton;
import smpp4j.util.PduIO;


public class AlertNotification extends Command {

   public AlertNotification() {
      commandId = PduCommand.alert_notification.getCode();
      hasTlvs = true;
   }

   private int    sourceAddrTon = 0;
   private int    sourceAddrNpi = 0;
   private String sourceAddr     = "";
   
   private int    esmeAddrTon = 0;
   private int    esmeAddrNpi = 0;
   private String esmeAddr     = "";

   public Ton getSourceAddrTon() {
      return Ton.forCode(sourceAddrTon) ;
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
   
   public Ton getEsmeAddrTon() {
      return Ton.forCode(esmeAddrTon);
   }
   
   public void setEsmeAddrTon(Ton ton) {
      this.esmeAddrTon = ton.getCode();
   }
   
   public Npi getEsmeAddrNpi() {
      return Npi.forCode(esmeAddrNpi);
   }
   
   public void setEsmeAddrNpi(Npi npi) {
      this.esmeAddrNpi = npi.getCode();
   }

   public String getEsmeAddr() {
      return esmeAddr;
   }
   
   public void setEsmeAddr(String esme_addr) {
      this.esmeAddr = esme_addr;
   }

   @Override
   protected int decodeBody(byte[] pduBytes, int offset) {
      
      sourceAddrTon = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      sourceAddrNpi = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      sourceAddr = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + sourceAddr.length() + 1;
      
      esmeAddrTon = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      esmeAddrNpi = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      esmeAddr = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + esmeAddr.length() + 1;

      return offset + esmeAddr.length();
   }

   @Override
   protected byte[][] encodeBody() {

      return new byte[][]{PduIO.intToBytes(sourceAddrTon, 1), 
                  PduIO.intToBytes(sourceAddrNpi, 1),
                  PduIO.stringToCString(sourceAddr, true),
                  PduIO.intToBytes(esmeAddrTon, 1), 
                  PduIO.intToBytes(esmeAddrNpi, 1),
                  PduIO.stringToCString(esmeAddr, true)};
   }

}

package smpp4j.pdu;

import java.util.Arrays;

import smpp4j.Constants.BooleanValue;
import smpp4j.Constants.DataCoding;
import smpp4j.Constants.Npi;
import smpp4j.Constants.PduCommand;
import smpp4j.Constants.PriorityFlag;
import smpp4j.Constants.Ton;
import smpp4j.util.PduIO;


public class SubmitSm extends Command {

   public SubmitSm() {
      commandId = PduCommand.submit_sm.getCode();
      hasTlvs = true;
   }

   private String serviceType            = "";
   private int    sourceAddrTon         = 0;
   private int    sourceAddrNpi         = 0;
   private String sourceAddr             = "";
   private int    destAddrTon           = 0;
   private int    destAddrNpi           = 0;
   private String destinationAddr;
   private int    esmClass               = 0;
   private int    protocolId             = 0; // research this.
   private int    priorityFlag           = 0;
   private String scheduleDeliveryTime  = "";
   private String validityPeriod         = "";
   private int    registeredDelivery     = 0;
   private int    replaceIfPresentFlag = 0;
   private int    dataCoding             = 0;
   private int    smDefaultMsgId       = 0;
//   private int    smLength;                   // should be set according to short_message
   private byte[] udh = null;
   private byte[] shortMessageBytes;

   public String getServiceType() {
      return serviceType;
   }

   public void setServiceType(String service_type) {
      this.serviceType = service_type;
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

   public String getDestAddr() {
      return destinationAddr;
   }

   public void setDestAddr(String destination_addr) {
      this.destinationAddr = destination_addr;
   }

   public int getEsmClass() {
      return esmClass;
   }

   public void setEsmClass(int esm_class) {
      this.esmClass = esm_class;
   }

   public int getProtocolId() {
      return protocolId;
   }

   public void setProtocolId(int protocol_id) {
      this.protocolId = protocol_id;
   }

   public PriorityFlag getPriorityFlag() {
      return PriorityFlag.forCode(priorityFlag);
   }

   public void setPriorityFlag(PriorityFlag pf) {
      this.priorityFlag = pf.getCode();
   }

   public String getScheduleDeliveryTime() {
      return scheduleDeliveryTime;
   }

   public void setScheduleDeliveryTime(String schedule_delivery_time) {
      this.scheduleDeliveryTime = schedule_delivery_time;
   }

   public String getValidityPeriod() {
      return validityPeriod;
   }

   public void setValidityPperiod(String validity_period) {
      this.validityPeriod = validity_period;
   }

   public int getRegisteredDelivery() {
      return registeredDelivery;
   }

   public void setRegisteredDelivery(int registered_delivery) {
      this.registeredDelivery = registered_delivery;
   }

   public BooleanValue getReplaceIfPresentFlag() {
      return BooleanValue.forCode(replaceIfPresentFlag);
   }

   public void setReplaceIfPresentFlag(BooleanValue bv) {
      this.replaceIfPresentFlag = bv.getCode();
   }

   public DataCoding getDataCoding() {
      return DataCoding.forCode(dataCoding);
   }

   public void setDataCoding(DataCoding dc) {
      this.dataCoding = dc.getCode();
   }

   public int getSmDefaultMsgId() {
      return smDefaultMsgId;
   }

   public void setSmDefaultMsgId(int sm_default_msg_id) {
      this.smDefaultMsgId = sm_default_msg_id;
   }

//   public int getSmLength() {
//      return smLength;
//   }
//
//   public void setSmLength(int sm_length) {
//      this.smLength = sm_length;
//   }
//
//   public String getShortMessage() {
//      return shortMessage;
//   }
//
//   public void setShortMessage(String short_message) {
//      this.shortMessage = short_message;
//   }
   
   public void setShortMessageBytes(byte[] shortMessageBytes) {
      this.shortMessageBytes = shortMessageBytes;
   }
   
   public byte[] getShortMessageBytes() {
      return shortMessageBytes;
   }
   
   public void setUdh(byte[] udh) {
      this.udh = udh;
   }
   
   @Override
   protected int decodeBody(byte[] pduBytes, int offset) {
      serviceType = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + serviceType.length() + 1;

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

      esmClass = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      protocolId = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      priorityFlag = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      scheduleDeliveryTime = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + scheduleDeliveryTime.length() + 1;

      validityPeriod = PduIO.bytesToAsciiCString(pduBytes, offset);
      offset = offset + validityPeriod.length() + 1;

      registeredDelivery = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      replaceIfPresentFlag = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      dataCoding = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      smDefaultMsgId = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      int smLength = PduIO.bytesToInt(pduBytes, offset, 1);
      offset++;

      shortMessageBytes = Arrays.copyOfRange(pduBytes, offset, offset + smLength);
      offset = offset + smLength;

      return offset;
   }

   @Override
   protected byte[][] encodeBody() {

//      byte[] short_message_bytes = PduIO.stringToCString(shortMessage, false);
      
      if (this.udh != null) {
         byte[] udh_sm = Arrays.copyOf(udh, shortMessageBytes.length + udh.length);
         for (int i = udh.length; i < shortMessageBytes.length; i++)
            udh_sm[i] = shortMessageBytes[i-udh.length];
         shortMessageBytes = udh_sm;
      }
      
//      int smLength = shortMessageBytes.length;
//      byte[] sm_length_bytes = ;

      return new byte[][]{PduIO.stringToCString(serviceType, true),
                  PduIO.intToBytes(sourceAddrTon, 1), 
                  PduIO.intToBytes(sourceAddrNpi, 1),
                  PduIO.stringToCString(sourceAddr, true), 
                  PduIO.intToBytes(destAddrTon, 1),
                  PduIO.intToBytes(destAddrNpi, 1),
                  PduIO.stringToCString(destinationAddr, true), 
                  PduIO.intToBytes(esmClass, 1),
                  PduIO.intToBytes(protocolId, 1), 
                  PduIO.intToBytes(priorityFlag, 1),
                  PduIO.stringToCString(scheduleDeliveryTime, true),
                  PduIO.stringToCString(validityPeriod, true),
                  PduIO.intToBytes(registeredDelivery, 1),
                  PduIO.intToBytes(replaceIfPresentFlag, 1), 
                  PduIO.intToBytes(dataCoding, 1),
                  PduIO.intToBytes(smDefaultMsgId, 1), 
                  PduIO.intToBytes(shortMessageBytes.length, 1), 
                  shortMessageBytes};
   }
   
   @Override
  	public String toString() {
  	
  		return super.toString() + " (" + 
  		
		"datn"+ getAddrTonAndNpiForDebug(destinationAddr, destAddrTon, destAddrNpi) +
		" satn"+ getAddrTonAndNpiForDebug(sourceAddr, sourceAddrTon, sourceAddrNpi) +
  		" dc"+ Integer.toBinaryString(dataCoding) + ")"+ getMsgForToString(shortMessageBytes);
      
  	   // NOT NEEDED FOR DEBUG
//     serviceType
//	   esmClass
//	   protocolId
//	   priorityFlag
//	   scheduleDeliveryTime
//	   validityPeriod
//	   registeredDelivery
//	   replaceIfPresentFlag
//	   smDefaultMsgId
//  	   udh
  		
   }	
   

}

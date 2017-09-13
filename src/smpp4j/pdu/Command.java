package smpp4j.pdu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smpp4j.Constants.Npi;
import smpp4j.Constants.TlvCode;
import smpp4j.Constants.Ton;
import smpp4j.util.PduIO;


public abstract class Command {

   protected byte[]  pduBytes;

   // HEADER
   protected int     commandLength  = 0;
   protected int     commandId      = 0;
   protected int     commandStatus  = 0;
   protected int     sequenceNumber = 0;

   protected boolean hasTlvs        = false;
   protected Tlv[]   tlvArray;

   public int getCommandId() {
      return commandId;
   }
   
   public Tlv getTlv(TlvCode code) {
      return getTlv(code.getCode());
   }
   
   private Tlv getTlv(int code) {
      Tlv tlv = null;

      if (hasTlvs && tlvArray != null) {
         for (Tlv current : tlvArray) {
            if (current.type.code == code) {
               tlv = current;
               break;
            }
         }
      }

      return tlv;
   }

   public void setTlvArray(Tlv[] tlvTable) {
      if (hasTlvs)
    	  this.tlvArray = tlvTable;
   }

   public Tlv[] getTlvArray() {
      return tlvArray;
   }

   public Integer getSequenceNumber() {
      return sequenceNumber;
   }

   public void setSequenceNumber(Integer sequence_number) {
      this.sequenceNumber = sequence_number;
   }

   public int getCommandStatus() {
      return commandStatus;
   }

   public void setCommandStatus(int command_status) {
      this.commandStatus = command_status;
   }


   public byte[] encode() {
      byte[][] encodeBody = encodeBody();
      createPdu(encodeBody);
      return pduBytes;
   }

   protected abstract byte[][] encodeBody();

   public void decode(byte[] pdu) {

      commandLength = PduIO.bytesToInt(pdu, 0, 4);
      commandId = PduIO.bytesToInt(pdu, 4, 4);
      commandStatus = PduIO.bytesToInt(pdu, 8, 4);
      sequenceNumber = PduIO.bytesToInt(pdu, 12, 4);

      int offset = decodeBody(pdu, 16);

      if (hasTlvs && pdu.length > offset)
         decodeTlvArray(pdu, offset);
   }

   // it should return the offset ... of the tlv
   protected abstract int decodeBody(byte[] pdu, int offset);

   private void decodeTlvArray(byte[] pdu, int offset) {
//TODO review this method
      Tlv[] tlvs = new Tlv[16]; // maximmun amount of tlvs
      int i =0;
      // look for tlvs
      while (pdu.length > (offset + 4)) {// +4 tag value + value length
         Tlv tlv = Tlv.decodeNextTag(pdu, offset);
         tlvs[i++] = tlv;
         
         offset = offset + tlv.getSize();
      }
      
      tlvArray = Arrays.copyOf(tlvs, i);
   }


   protected void createPdu(byte[][] arrays) {
      commandLength = 16; // command_length + command_id + command_status + command_number

      byte[][] tlvBytes = null;
      if (tlvArray != null) {
         tlvBytes = new byte[tlvArray.length][];
         for (int i = 0; i < tlvArray.length; i++) {
            tlvBytes[i] = tlvArray[i].encode();
            commandLength = commandLength + tlvBytes[i].length;
         }
      }

      if (arrays != null)
         for (byte[] array : arrays)
            commandLength = commandLength + array.length;

      byte[] command_length_bytes = PduIO.intToBytes(commandLength, 4);
      byte[] command_id_bytes = PduIO.intToBytes(commandId, 4);
      byte[] command_status_bytes = PduIO.intToBytes(commandStatus, 4);
      byte[] sequence_number_bytes = PduIO.intToBytes(sequenceNumber, 4);

      pduBytes = new byte[commandLength];

      int i = 0;

      for (byte b : command_length_bytes)
         pduBytes[i++] = b;
      for (byte b : command_id_bytes)
         pduBytes[i++] = b;
      for (byte b : command_status_bytes)
         pduBytes[i++] = b;
      for (byte b : sequence_number_bytes)
         pduBytes[i++] = b;
      
      if (arrays != null)
         for (byte[] array : arrays)
            for (byte b : array)
               pduBytes[i++] = b;

      if (tlvBytes != null)
         for (byte[] tlv : tlvBytes)
            for (byte b : tlv)
               pduBytes[i++] = b;
   }

   @Override
   public String toString() {
	   String s = this.getClass().getSimpleName();
	   if (commandStatus != 0)
		   s = s + " [status: " + Integer.toHexString(commandStatus) +"]";
	   
	   if (hasTlvs && tlvArray != null)
		   for (Tlv t : tlvArray)
			   s = s + t.toString();
	   
	   return s;
   }

   protected static String getMsgForToString(byte[] smb) {
	   try {
		   return new String(smb, "UTF-8");//.replaceAll("[^\\p{Graph} ]", "");//.replace('\n', '|');
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   return "";
   }
   
   protected static String getAddrTonAndNpiForDebug(String address, int ton, int npi) {
	   return address+"|"+ ton + "|"+ npi;
   }
   
}

package smpp4j.pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smpp4j.Constants.TlvCode;
import smpp4j.util.PduIO;

public class Tlv {

   private static final Logger log = LoggerFactory.getLogger(Tlv.class);
   public final TlvType type;

   public final byte[]  value;
   
   public Tlv(TlvType tlvType, byte[] value) {
      this.type = tlvType;
      this.value = value;
   }

   public Tlv(TlvCode code, Object value) {
      this(new TlvType(code), value);
   }
   
   public Tlv(TlvType tlvType, Object value) {
      this.type = tlvType;
      byte[] this_value = null;
      
      if (value == null) {
      
      } else {
      
	      if (type.valueType == -1) {
	         // do nothing, value should be null  
	      }
	      
	      else if (type.valueType == 0) { // value es entero
	         if (value instanceof Integer) 
	        	 this_value = PduIO.intToBytes(new Integer((Integer) value), tlvType.size);
	         else if (value instanceof Long) 
	        	 this_value = PduIO.longToBytes(new Long((Long) value), tlvType.size);
	         else {
	            // todo, what to do here !!!!
	         }
	      }
	      else if (type.valueType == 1) {// value es String
	         if (value instanceof String) {
	        	 this_value = PduIO.stringToCString((String) value, false);
	         }
	         if (value instanceof byte[]) { // if (type.valueType == 2) // value es byte ( para bitmask )
	        	 this_value = (byte[]) value ;            
	         }
	         else {
	            // todo, what to do here !!!!
	         }
	      }
	      else if (type.valueType == 2) {// value es byte ( para bitmask ?)
	         if (value instanceof Byte) { // if (type.valueType == 2) // value es byte ( para bitmask )
	        	 this_value = new byte[]{(Byte) value};            
	         }
	      }
      }
      
      this.value = this_value;
   }

   public <T> T getValue(Class<T> clazz) {
      if (type.size == 0)
         return null;

      if (value == null)
         return null;

      if (clazz == Integer.class)
         return clazz.cast(PduIO.bytesToInt(value, 0, value.length));

      if (clazz == String.class) 
            return clazz.cast(PduIO.bytesToAsciiCString(value, 0, value.length));
      
      if (clazz == Byte.class) 
         return clazz.cast(value[0]);
      
      if (clazz == Long.class)
         return clazz.cast(PduIO.bytesToLong(value, 0, value.length));
      
      return null;
   }

   public byte[] encode() {
      byte[] tlvCode_bytes = PduIO.intToBytes(type.code, 2);
      byte[] tlvValueSize_bytes = PduIO.intToBytes(value == null ? 0 : value.length, 2);

      return createTLV(tlvCode_bytes, tlvValueSize_bytes, value);
   }

   public static Tlv decodeNextTag(byte[] pdu, int offset) {

      int code = PduIO.bytesToInt(pdu, offset, 2);
      offset = offset + 2;
      
      int size = PduIO.bytesToInt(pdu, offset, 2);
      offset = offset + 2;
      
      byte[] value = null;
      if (size > 0) {
    	  value = new byte[size];

         for (int i = 0; i < size; i++)
            value[i] = pdu[offset++];
      }


      TlvCode pt = null;
      for (TlvCode currentPredefinedType : TlvCode.values()) {
         if (currentPredefinedType.getCode() == code) {
            pt = currentPredefinedType;
            break;
         }
      }

      TlvType tlvType = null;

      if (pt != null)
         tlvType = new TlvType(pt);
      else 
         if (size == 0)
            tlvType = new TlvType(code, 0, -1);
         else
            tlvType = new TlvType(code, size, 3);

      return new Tlv(tlvType, value);
   }

   private byte[] createTLV(byte[] code, byte[] valueSize, byte[] value) {
      byte[] encodedTlv = new byte[code.length + 
                                   valueSize.length + 
                                   (value != null ? value.length : 0)];
      int i = 0;

      encodedTlv[i++] = code[0];
      encodedTlv[i++] = code[1];
      encodedTlv[i++] = valueSize[0];
      encodedTlv[i++] = valueSize[1];

      if (value != null)
         for (byte b : value)
            encodedTlv[i++] = b;

      return encodedTlv;
   }

   public int getSize() {
      return 4 + (value != null ? value.length : 0);
   }

   @Override
	public String toString() {
		TlvCode tlvcode = TlvCode.getTlvCodeFromCode(type.code);
		if (shouldIgnoreTlvForDebug(tlvcode))
			return "";
		
		String s = "["+ (tlvcode != null ? tlvcode.name() : Integer.toHexString(type.code)) +" ";
		
		if (tlvcode == TlvCode.its_session_info)
			s = s + getItsSessionInfoDebugValue();
		else if (tlvcode == TlvCode.ussd_service_op)
			s = s + value[0];
		else
			s = s + "rawBits: "+ getRawBitValues() + " rawBytes: " + getRawByteValues();
		
		s = s + "]";
		return s;
	}
   
   private boolean shouldIgnoreTlvForDebug(TlvCode tlvcode) {
	   boolean ignore = true;
	   if (tlvcode == TlvCode.network_error_code) {
		   for (byte b : value)
			   if (b != 0)
				   ignore = false;
		   return ignore;
	   } else {
		   ignore = false;
	   }
	   
	   return ignore;
}

private String getRawBitValues() {
	   String s = null;
	   for (byte b : value)
		   s = (s == null ? "" : s +" ") + Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
	   return s;
   }
   
   private String getRawByteValues() {
	   String s = null;
	   for (byte b : value)
		   s = (s == null ? "" : s +" ") + b;
	   return s;
   }
   
   private String getItsSessionInfoDebugValue() {
	   String oc2 = Integer.toBinaryString((value[1] & 0xFF) + 0x100).substring(1);
	   return "s"+ value[0] + 
			   " n"+ Integer.valueOf(oc2.substring(0,7), 2) + 
			   " e"+ oc2.substring(7);
   }
   
//   public static void main(String[] args) {
//	   for (byte b = Byte.MIN_VALUE;b<Byte.MAX_VALUE;b++) {
//		   String binVal = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
//		   if (Arrays.asList(
//				   "00000000", 
//				   "00000001",
//				   "00000010",
//				   "00000011",
//				   "00000100",
//				   "00000101",
//				   "00000110",
//				   "00000111",
//				   "00001000",
//				   "00001001",
//				   "00001010",
//				   "00001011",
//				   "00001100",
//				   "00001101",
//				   "00001110",
//				   "00001111"
//				   ).contains(binVal))
//			   log.print(b+": "+ binVal);
//   		}
//}
}

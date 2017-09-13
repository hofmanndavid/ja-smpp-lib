package smpp4j.util;

import java.io.IOException; 
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import smpp4j.Constants.PduCommand;
import smpp4j.pdu.Command;

public class PduIO {

	public static Command readPduCommand(InputStream is) throws IOException, InstantiationException, IllegalAccessException {
		
		byte[] pdu = readPdu(is);
		if (pdu == null)
			return null;
		
		Class<? extends Command> clazz = PduCommand.classForCode(PduIO.bytesToInt(pdu, 4, 4));
        
        Command newCommand = clazz.newInstance();

		newCommand.decode(pdu);
		
		return newCommand;
	}
	
	   public static byte[] readPdu(InputStream is) throws IOException {
		      try {
		    	  byte[] pdu = new byte[4];
		      
				  	if (is.read(pdu) != 4)
				  		return null;
				  
				  int commandLenghtBytes = PduIO.bytesToInt(pdu, 0, 4);
				  pdu = Arrays.copyOf(pdu, commandLenghtBytes);
				  int leftoverbytesToRead = commandLenghtBytes-4;
				  
				  int offset = 4;
				  while (leftoverbytesToRead > 0) {
					  int readedBytes = is.read(pdu, offset, leftoverbytesToRead);
					  if (readedBytes == -1)
						  return null;
					  leftoverbytesToRead = leftoverbytesToRead - readedBytes;
					  offset = offset +readedBytes;
				  }
				  
				  return pdu;
		      } catch (SocketTimeoutException ste) {
		    	  System.out.println(ste.getMessage());
		      }
		      
		      return null;
		   }
	   


   public static int bytesToInt(byte[] b, int offset, int size) {
      int num = 0;
      int sw = 8 * (size - 1);

      for (int loop = 0; loop < size; loop++) {
         num |= ((int) b[offset + loop] & 0x00ff) << sw;
         sw -= 8;
      }

      return num;
   }

   public static byte[] intToBytes(int num, int len) {

      byte[] b = new byte[len];

      int sw = (len - 1) * 8;
      int mask = 0xff << sw;

      for (int l = 0; l < len; l++) {
         b[0 + l] = (byte) ((num & mask) >>> sw);

         sw -= 8;
         mask >>>= 8;
      }

      return b;
   }

   public static String bytesToAsciiCString(byte[] b, int offset) {
      String s;
      try {
         int p = offset;
         while (p < b.length && b[p] != (byte) 0) {
            p++;
         }

         if (p > offset) {
            s = new String(b, offset, p - offset, "US-ASCII");
         }
         else {
            s = "";
         }
      }
      catch (java.io.UnsupportedEncodingException x) {
         s = "";
      }
      return s;
   }

   public static String bytesToAsciiCString(byte[] b, int offset, int len) {
      String s = "";
      try {
         if (len > 0) {
            s = new String(b, offset, len, "US-ASCII");
         }
      }
      catch (java.io.UnsupportedEncodingException x) {

      }
      return s;
   }

   public static byte[] stringToCString(String s, boolean addNullByte) {

      if (addNullByte) {
         byte[] bytes = s != null ? s.getBytes() : "".getBytes();
         bytes = Arrays.copyOf(bytes, bytes.length + 1);
         bytes[bytes.length - 1] = (byte) 0;
         return bytes;
      }

      return s.getBytes();
   }

   public static byte[] longToBytes(long num, int len) {
      byte[] b = new byte[len];

      long sw = (len - 1) * 8;
      long mask = 0xffL << sw;

      for (int l = 0; l < len; l++) {
         b[l] = (byte) ((num & mask) >>> sw);

         sw -= 8;
         mask >>>= 8;
      }

      return b;
   }

   public static long bytesToLong(byte[] b, int offset, int size) {
      long num = 0;
      long sw = 8L * ((long) size - 1L);

      for (int loop = 0; loop < size; loop++) {
         num |= ((long) b[offset + loop] & 0x00ff) << sw;
         sw -= 8;
      }

      return num;
   }
}

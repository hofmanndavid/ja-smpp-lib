/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package smpp4j.util;

import java.util.Arrays;

/**
 * copied from jsmpp
 */
public class HexUtil {

//   private final static Logger logger = Logger.getLogger(HexUtil.class);
   
   private static final char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
               'b', 'c', 'd', 'e', 'f' };

   public static String stringToHex(String data) {
      return bytesToHex(data.getBytes());
   }

   public static String bytesToHex(byte[] data) {
      return bytesToHex(data, 0, data.length);
   }

   public static String bytesToHex(byte[] data, int offset, int length) {
      StringBuffer sBuf = new StringBuffer();
      for (int i = offset; i < length; i++) {
         sBuf.append(hexChar[(data[i] >> 4) & 0xf]);
         sBuf.append(hexChar[data[i] & 0xf]);
      }
      return sBuf.toString();
   }

   public static String hexToString(String hexString) {
      String uHexString = hexString.toLowerCase();
      StringBuffer sBuf = new StringBuffer();
      for (int i = 0; i < uHexString.length(); i = i + 2) {
         char c = (char) Integer.parseInt(uHexString.substring(i, i + 2), 16);
         sBuf.append(c);
      }
      return sBuf.toString();
   }

   public static byte[] hexToBytes(String hexString) {
      return hexToBytes(hexString, 0, hexString.length());
   }

   public static byte[] hexToBytes(String hexString, int offset, int endIndex) {
      byte[] data;
      String realHexString = hexString.substring(offset, endIndex).toLowerCase();
      if ((realHexString.length() % 2) == 0)
         data = new byte[realHexString.length() / 2];
      else
         data = new byte[(int) Math.ceil(realHexString.length() / 2d)];

      int j = 0;
      char[] tmp;
      for (int i = 0; i < realHexString.length(); i += 2) {
         try {
            tmp = realHexString.substring(i, i + 2).toCharArray();
         }
         catch (StringIndexOutOfBoundsException siob) {
            // it only contains one character, so add "0" string
            tmp = (realHexString.substring(i) + "0").toCharArray();
         }
         data[j] = (byte) ((Arrays.binarySearch(hexChar, tmp[0]) & 0xf) << 4);
         data[j++] |= (byte) (Arrays.binarySearch(hexChar, tmp[1]) & 0xf);
      }

      for (int i = realHexString.length(); i > 0; i -= 2) {

      }
      return data;
   }
}

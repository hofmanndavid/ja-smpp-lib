package smpp4j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.awt.windows.ThemeReader;

/**
 * viene de algun projecto de smpp de apache ?
 */
public final class SmsPduUtil
{
  public static final char EXT_TABLE_PREFIX = 27;
  public static final char[] GSM_DEFAULT_ALPHABET_TABLE = { '@', 163, '$', 165, 232, 233, 249, 236, 242, 199, '\n', 216, 248, '\r', 197, 229, 916, '_', 934, 915, 923, 937, 928, 936, 931, 920, 926, 160, 198, 230, 223, 201, ' ', '!', '"', '#', 164, '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', 161, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 196, 214, 209, 220, 167, 191, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 228, 246, 241, 252, 224 };
  public static final char[] GSM_DEFAULT_ALPHABET_ALTERNATIVES = { 199, '\t', 913, 'A', 914, 'B', 919, 'H', 921, 'I', 922, 'K', 924, 'M', 925, 'N', 927, 'O', 929, 'P', 932, 'T', 933, 'U', 935, 'X', 918, 'Z' };

  public static byte[] getSeptets(String theMsg)
  {
     
     if ((theMsg.length() + 1) % 8 == 0) {
        theMsg = theMsg + "\r";
     }

     // NOMELEAS, en la doc dice que esto hay que hacer, pero no tiene efecto de cualquier forma ...
//     if ( theMsg.length() < 160 && 
//          theMsg.endsWith("\r") && 
//         (theMsg.length() * 7) % 8 == 0) {
//        theMsg = theMsg + "\r";
//     }
     
     
    ByteArrayOutputStream baos = new ByteArrayOutputStream(140);
    try
    {
      writeSeptets(baos, theMsg);
      baos.close();
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }

    return baos.toByteArray();
  }
  
  public static String readSeptets(byte[] theArray)
  {
     
     int theLength = (int) Math.floor(theArray.length / 7) * 8 + (theArray.length % 7);
     
    if (theArray == null)
    {
      return null;
    }

    try
    {
      return readSeptets(new ByteArrayInputStream(theArray), theLength);
    }
    catch (IOException ex)
    {
    }
    return null;
  }


  public static void writeSeptets(OutputStream theOs, String theMsg)
    throws IOException
  {
    int data = 0;
    int nBits = 0;

    for (int i = 0; i < theMsg.length(); ++i)
    {
      byte gsmChar = toGsmCharset(theMsg.charAt(i));

      data |= gsmChar << nBits;
      nBits += 7;

      while (nBits >= 8)
      {
        theOs.write((char)(data & 0xFF));

        data >>>= 8;
        nBits -= 8;
      }

    }

    if (nBits > 0)
    {
      theOs.write(data);
    }
  }


  public static String readSeptets(InputStream theIs, int theLength)
    throws IOException
  {
    StringBuffer msg = new StringBuffer(160);

    int rest = 0;
    int restBits = 0;

    while (msg.length() < theLength)
    {
      int data = theIs.read();

      if (data == -1)
      {
        throw new IOException("Unexpected end of stream");
      }

      rest |= data << restBits;
      restBits += 8;

      while ((msg.length() < theLength) && (restBits >= 7))
      {
        msg.append(fromGsmCharset((byte)(rest & 0x7F)));

        rest >>>= 7;
        restBits -= 7;
      }
    }

    return msg.toString();
  }

  public static void writeBcdNumber(OutputStream theOs, String theNumber)
    throws IOException
  {
    int bcd = 0;
    int n = 0;

    for (int i = 0; i < theNumber.length(); ++i)
    {
      switch (theNumber.charAt(i))
      {
      case '0':
        bcd |= 0;
        break;
      case '1':
        bcd |= 16;
        break;
      case '2':
        bcd |= 32;
        break;
      case '3':
        bcd |= 48;
        break;
      case '4':
        bcd |= 64;
        break;
      case '5':
        bcd |= 80;
        break;
      case '6':
        bcd |= 96;
        break;
      case '7':
        bcd |= 112;
        break;
      case '8':
        bcd |= 128;
        break;
      case '9':
        bcd |= 144;
        break;
      case '*':
        bcd |= 160;
        break;
      case '#':
        bcd |= 176;
        break;
      case 'a':
        bcd |= 192;
        break;
      case 'b':
        bcd |= 224;
      }

      ++n;

      if (n == 2)
      {
        theOs.write(bcd);
        n = 0;
        bcd = 0;
      }
      else
      {
        bcd >>= 4;
      }
    }

    if (n == 1)
    {
      bcd |= 240;
      theOs.write(bcd);
    }
  }

  public static String readBcdNumber(InputStream theIs, int theLength)
    throws IOException
  {
    byte[] arr = new byte[theLength];
    theIs.read(arr, 0, theLength);
    return readBcdNumber(arr, 0, theLength);
  }

  public static String readBcdNumber(byte[] arr, int offset, int theLength)
  {
    StringBuffer out = new StringBuffer();
    for (int i = offset; i < offset + theLength; ++i)
    {
      int arrb = arr[i];
      if ((arr[i] & 0xF) <= 9)
      {
        out.append("" + (arr[i] & 0xF));
      }
      if ((arr[i] & 0xF) == 10)
      {
        out.append("*");
      }
      if ((arr[i] & 0xF) == 11)
      {
        out.append("#");
      }
      arrb >>>= 4;
      if ((arrb & 0xF) <= 9)
      {
        out.append("" + (arrb & 0xF));
      }
      if ((arrb & 0xF) == 10)
      {
        out.append("*");
      }
      if ((arrb & 0xF) == 11)
      {
        out.append("#");
      }
    }
    return out.toString();
  }

  public static char fromGsmCharset(byte gsmChar)
  {
    return GSM_DEFAULT_ALPHABET_TABLE[gsmChar];
  }

  public static byte[] toGsmCharset(String str)
  {
    byte[] gsmBytes = new byte[str.length()];

    for (int i = 0; i < gsmBytes.length; ++i)
    {
      gsmBytes[i] = toGsmCharset(str.charAt(i));
    }

    return gsmBytes;
  }

  public static byte toGsmCharset(char theUnicodeCh)
  {
    for (int i = 0; i < GSM_DEFAULT_ALPHABET_TABLE.length; ++i)
    {
      if (GSM_DEFAULT_ALPHABET_TABLE[i] == theUnicodeCh)
      {
        return (byte)i;
      }

    }

    for (int i = 0; i < GSM_DEFAULT_ALPHABET_ALTERNATIVES.length / 2; i += 2)
    {
      if (GSM_DEFAULT_ALPHABET_ALTERNATIVES[(i * 2)] == theUnicodeCh)
      {
        return (byte)(GSM_DEFAULT_ALPHABET_ALTERNATIVES[(i * 2 + 1)] & 0x7F);
      }

    }

    return 63;
  }

  public static void arrayCopy(byte[] theSrc, int theSrcStart, byte[] theDest, int theDestStart, int theLength)
  {
    for (int i = 0; i < theLength; ++i)
    {
      theDest[(i + theDestStart)] = theSrc[(i + theSrcStart)];
    }
  }

  public static void arrayCopy(byte[] theSrc, int theSrcStart, byte[] theDest, int theDestStart, int theDestBitOffset, int theBitLength)
  {
    int c = 0;
    int nBytes = theBitLength / 8;
    int nRestBits = theBitLength % 8;

    for (int i = 0; i < nBytes; ++i)
    {
      c |= (theSrc[(theSrcStart + i)] & 0xFF) << theDestBitOffset;
      int tmp50_49 = (theDestStart + i);
      byte[] tmp50_45 = theDest; tmp50_45[tmp50_49] = (byte)(tmp50_45[tmp50_49] | (byte)(c & 0xFF));
      c >>>= 8;
    }

    if (nRestBits > 0)
    {
      c |= (theSrc[(theSrcStart + nBytes)] & 255 >> 8 - nRestBits) << theDestBitOffset;
    }
    if (nRestBits + theDestBitOffset > 0)
    {
      int tmp117_116 = (theDestStart + nBytes);
      byte[] tmp117_112 = theDest; tmp117_112[tmp117_116] = (byte)(tmp117_112[tmp117_116] | c & 0xFF);
    }
  }
}

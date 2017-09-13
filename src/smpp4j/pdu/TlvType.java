package smpp4j.pdu;

import smpp4j.Constants.TlvCode;

// todo, comment on how to include tlvs not defined in the spec
public class TlvType {
   
   public final int code;
   public final int size;
   public final int valueType;

   public TlvType(TlvCode definedType) {
      this.code = definedType.getCode();
      this.size = definedType.getSize();
      this.valueType = definedType.getValueType();
   }

   /** 
    * valueType:
    *    -1 means null, 
    *    0 means int, 
    *    1 means string, 
    *    2 byte,
    *    3 unknown 
    * 
    * size: 
    *    if size == -1, size is variable, 
    *    size 0 is null value, 
    *    and size > 0 is the size in bytes of the value
    */
   public TlvType(int code, int size, int valueType) {
      
      System.err.println("Creating nonDefined Tag. Decimal code: "+code+", size: "+size);
      
      this.code = code;
      this.size = size;
      this.valueType = valueType;
   }
}

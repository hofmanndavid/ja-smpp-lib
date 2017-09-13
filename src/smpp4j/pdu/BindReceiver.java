package smpp4j.pdu;

import smpp4j.Constants.InterfaceVersion;
import smpp4j.Constants.Npi;
import smpp4j.Constants.PduCommand;
import smpp4j.Constants.Ton;
import smpp4j.util.PduIO;


public class BindReceiver extends Command {

   public BindReceiver() {
      commandId = PduCommand.bind_receiver.getCode();
   }

   private String systemId;
   private String password;
   private String systemType;
   private int    interfaceVersion = InterfaceVersion.v34.getCode();
   private int    addrTon = 0;
   private int    addrNpi = 0;
   private String addressRange = null;

   public String getSystemId() {
      return systemId;
   }

   public void setSystemId(String system_id) {
      this.systemId = system_id;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getSystemType() {
      return systemType;
   }

   public void setSystemType(String system_type) {
      this.systemType = system_type;
   }

   public InterfaceVersion getInterfaceVersion() {
      return InterfaceVersion.forCode(interfaceVersion);
   }

   public void setInterfaceVersion(InterfaceVersion iv) {
      this.interfaceVersion = iv.getCode();
   }

   public Ton getAddrTon() {
      return Ton.forCode(addrTon);
   }

   public void setAddrTon(Ton ton) {
      this.addrTon = ton.getCode();
   }

   public Npi getAddrNpi() {
      return Npi.forCode(addrNpi);
   }

   public void setAddrNpi(Npi npi) {
      this.addrNpi = npi.getCode();
   }

   public String getAddressRange() {
      return addressRange;
   }

   public void setAddressRange(String address_range) {
      this.addressRange = address_range;
   }

   @Override
   protected int decodeBody(byte[] pdu, int offset) {
      systemId = PduIO.bytesToAsciiCString(pdu, offset);
      offset = offset + systemId.length() + 1;

      password = PduIO.bytesToAsciiCString(pdu, offset);
      offset = offset + password.length() + 1;

      systemType = PduIO.bytesToAsciiCString(pdu, offset);
      offset = offset + systemType.length() + 1;

      interfaceVersion = PduIO.bytesToInt(pdu, offset, 1);
      offset++;

      addrTon = PduIO.bytesToInt(pdu, offset, 1);
      offset++;

      addrNpi = PduIO.bytesToInt(pdu, offset, 1);
      offset++;

      addressRange = PduIO.bytesToAsciiCString(pdu, offset);

      return offset + addressRange.length() + 1;
   }

   @Override
   public byte[][] encodeBody() {

      return new byte[][]{PduIO.stringToCString(systemId, true),
                  PduIO.stringToCString(password, true), 
                  PduIO.stringToCString(systemType, true),
                  PduIO.intToBytes(interfaceVersion, 1), 
                  PduIO.intToBytes(addrTon, 1),
                  PduIO.intToBytes(addrNpi, 1), 
                  PduIO.stringToCString(addressRange, true)};
   }
   
   @Override
	public String toString() {
	   return super.toString() + 
				" ( sid: "+ systemId + ", pass: "+ password + ", stype: " + systemType + 
				", ton: " + Integer.toHexString(addrTon) +
				", npi: " + Integer.toHexString(addrNpi) +
				", addrRange: " + addressRange + ")";
	}

}

package smpp4j.pdu;

import smpp4j.Constants.PduCommand;
import smpp4j.util.PduIO;


public class BindReceiverResp extends Command {

   public BindReceiverResp() {
      commandId = PduCommand.bind_receiver_resp.getCode();
      hasTlvs = true;
   }

   private String systemId;

   public void setSystemId(String system_id) {
      this.systemId = system_id;
   }

   public String getSystemId() {
      return systemId;
   }

   @Override
   protected int decodeBody(byte[] pdu, int offset) {
      systemId = PduIO.bytesToAsciiCString(pdu, offset);
      return offset + systemId.length() + 1;
   }

   @Override
   protected byte[][] encodeBody() {
      return new byte[][]{PduIO.stringToCString(systemId, true)};
   }
}

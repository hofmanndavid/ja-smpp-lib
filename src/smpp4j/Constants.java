package smpp4j;

import smpp4j.pdu.AlertNotification;
import smpp4j.pdu.BindReceiver;
import smpp4j.pdu.BindReceiverResp;
import smpp4j.pdu.BindTransceiver;
import smpp4j.pdu.BindTransceiverResp;
import smpp4j.pdu.BindTransmitter;
import smpp4j.pdu.BindTransmitterResp;
import smpp4j.pdu.CancelSm;
import smpp4j.pdu.CancelSmResp;
import smpp4j.pdu.Command;
import smpp4j.pdu.DeliverSm;
import smpp4j.pdu.DeliverSmResp;
import smpp4j.pdu.EnquireLink;
import smpp4j.pdu.EnquireLinkResp;
import smpp4j.pdu.GenericNack;
import smpp4j.pdu.QuerySm;
import smpp4j.pdu.QuerySmResp;
import smpp4j.pdu.SubmitSm;
import smpp4j.pdu.SubmitSmResp;
import smpp4j.pdu.Unbind;
import smpp4j.pdu.UnbindResp;


public class Constants {
   
   public enum ConnectionType { TRANSMITTER, TRANSCEIVER, RECEIVER }
   
   public enum DataCoding {
	      // review !!
	   
		   DEFAULT(0), //0 0 0 0 0 0 0 0 SMSC Default Alphabet
		   IA5_ASCII(1), //0 0 0 0 0 0 0 1 IA5 (CCITT T.50)/ASCII (ANSI X3.4) b
//		   BINARY_8_BIT(), 0 0 0 0 0 0 1 0 Octet unspecified (8-bit binary) b
		   ISO_8859_1(3), //0 0 0 0 0 0 1 1 Latin 1 (ISO-8859-1) b
//		   BINARY_8_BIT2(), 0 0 0 0 0 1 0 0 Octet unspecified (8-bit binary) a
//		   JIS(), 0 0 0 0 0 1 0 1 JIS (X 0208-1990) b
//		   0 0 0 0 0 1 1 0 Cyrllic (ISO-8859-5) b
//		   0 0 0 0 0 1 1 1 Latin/Hebrew (ISO-8859-8) b
		   UCS_2(8),  //0 0 0 0 1 0 0 0 UCS2 (ISO/IEC-10646) a
//		   0 0 0 0 1 0 0 1 Pictogram Encoding b
//		   0 0 0 0 1 0 1 0 ISO-2022-JP (Music Codes) b
//		   0 0 0 0 1 0 1 1 reserved
//		   0 0 0 0 1 1 0 0 reserved
//		   JIS_EXTddd(), 0 0 0 0 1 1 0 1 Extended Kanji JIS(X 0212-1990) b
//		   0 0 0 0 1 1 1 0 KS C 5601 b
//		   0 0 0 0 1 1 1 1 reserved
//		   :
//		   1 0 1 1 1 1 1 1 reserved
//		   GSM_MWI(), 1 1 0 0 x x x x GSM MWI control - see [GSM 03.38] d
//		   GSM_MWI(), 1 1 0 1 x x x x GSM MWI control - see [GSM 03.38] d
//		   1 1 1 0 x x x x reserved
//		   MCC(), 1 1 1 1 x x x x GSM message class control - see [GSM 03.38] e
		   
		   ;
	      
	      private final int code;
	      
	      public int getCode() {
	         return this.code;
	      }
	      
	      private DataCoding(int code) {
	         this.code = code;   
	      }
	      
	      public static DataCoding forCode(int code) {
	         for (DataCoding currentDataCoding : DataCoding.values())
	            if (currentDataCoding.getCode() == code)
	               return currentDataCoding;
	         
	         return null;
	      }
	   }
   
   public enum BooleanValue {
      TRUE(0),
      FALSE(1);
      
      
      private final int code;
      
      public int getCode() {
         return this.code;
      }
      
      private BooleanValue(int code) {
         this.code = code;   
      }

      public static BooleanValue forCode(int code) {
         for (BooleanValue currentBooleanValue : BooleanValue.values())
            if (currentBooleanValue.getCode() == code)
               return currentBooleanValue;
         
         return null;
      }
   }
   
   public enum PriorityFlag {
      LEVEL_0(0),
      LEVEL_1(1),
      LEVEL_2(2),
      LEVEL_3(3);
      
      private final int code;
      
      public int getCode() {
         return this.code;
      }
      
      private PriorityFlag(int code) {
         this.code = code;   
      }
      
      public static PriorityFlag forCode(int code) {
         for (PriorityFlag currentPriorityFlag : PriorityFlag.values())
            if (currentPriorityFlag.getCode() == code)
               return currentPriorityFlag;
         
         return null;
      }
   }
   
   public enum InterfaceVersion {
      v34(0x34);
      
      private final int code;
      
      public int getCode() {
         return this.code;
      }
      
      private InterfaceVersion(int code) {
         this.code = code;   
      }
      
      public static InterfaceVersion forCode(int code) {
         for (InterfaceVersion currentInterfaceVersion : InterfaceVersion.values())
            if (currentInterfaceVersion.getCode() == code)
               return currentInterfaceVersion;
         
         return null;
      }
   }
   
   public enum Ton {
      UNKNOWN(0),
      INTERNATIONAL(1),
      NATIONAL(2),
      NETWORK_SPECIFIC(3),
      SUBSCRIBER_NUMBER(4),
      ALPHANUMERIC(5),
      ABBREVIATED(6);
      
      private final int code;
      
      public int getCode() {
         return this.code;
      }
      
      private Ton(int code) {
         this.code = code;   
      }
      public static Ton forCode(int code) {
         for (Ton currentTon : Ton.values())
            if (currentTon.getCode() == code)
               return currentTon;
         
         return null;
      }
   }
   
   public enum Npi {
      UNKNOWN(0),
      ISDN(1),
      DATA(3),
      TELEX(4),
      LAND_MOBILE(6),
      NATIONAL(8),
      PRIVATE(9),
      ERMES(10),
      INTERNET(14),
      WAP_CLIENT_ID(18);
      
      private final int code;
      
      public int getCode() {
         return this.code;
      }
      
      private Npi(int code) {
         this.code = code;   
      }
      public static Npi forCode(int code) {
         for (Npi currentNpi : Npi.values())
            if (currentNpi.getCode() == code)
               return currentNpi;
         
         return null;
      }
   }

   public enum PduCommand {
      
	  submit_sm(0x00000004, SubmitSm.class),
      submit_sm_resp(0x80000004, SubmitSmResp.class),
      deliver_sm(0x00000005, DeliverSm.class),
      deliver_sm_resp(0x80000005, DeliverSmResp.class),
      enquire_link(0x00000015, EnquireLink.class),
      enquire_link_resp(0x80000015, EnquireLinkResp.class),
	      
      generic_nack(0x80000000, GenericNack.class),
      bind_receiver(0x00000001, BindReceiver.class),
      bind_receiver_resp(0x80000001, BindReceiverResp.class),
      bind_transmitter(0x00000002, BindTransmitter.class),
      bind_transmitter_resp(0x80000002, BindTransmitterResp.class),
      query_sm(0x00000003, QuerySm.class),
      query_sm_resp(0x80000003, QuerySmResp.class),
      
      unbind(0x00000006, Unbind.class),
      unbind_resp(0x80000006, UnbindResp.class),
      replace_sm(0x00000007, null),
      replace_sm_resp(0x80000007, null),
      cancel_sm(0x00000008, CancelSm.class),
      cancel_sm_resp(0x80000008, CancelSmResp.class),
      bind_transceiver(0x00000009, BindTransceiver.class),
      bind_transceiver_resp(0x80000009, BindTransceiverResp.class),
      outbind(0x0000000B, null),
      
      submit_multi(0x00000021, null),
      submit_multi_resp(0x80000021, null),
      alert_notification(0x00000102, AlertNotification.class),
      data_sm(0x00000103, null),
      data_sm_resp(0x80000103, null);

      public int getCode() {
         return code;
      }

      private final int code;
      private final Class<? extends Command> representingClass;

      private PduCommand(int code, Class<? extends Command> representingClass) {
         this.code = code;
         this.representingClass = representingClass;
      }
      
      public static Class<? extends Command> classForCode(int code) {
         for (PduCommand currentCommand : PduCommand.values())
            if (currentCommand.getCode() == code)
               return currentCommand.getPduClass();
         return null;
      }
      
      public static PduCommand commandForCode(int code) {
         for (PduCommand currentCommand : PduCommand.values()) 
            if (currentCommand.getCode() == code)
               return currentCommand;
         
         return null;
      }
      
      public static boolean isResponse(int code) {
         return !isRequest(code);
      }
      
      public static boolean isRequest(int code) {
         
         PduCommand pduCommand = commandForCode(code);
         
         switch (pduCommand) {
            case generic_nack: return false;
            case bind_receiver_resp: return false;
            case bind_transmitter_resp: return false;
            case query_sm_resp: return false;
            case submit_sm_resp: return false;
            case deliver_sm_resp: return false;
            case unbind_resp: return false;
            case replace_sm_resp: return false;
            case cancel_sm_resp: return false;
            case bind_transceiver_resp: return false;
            case enquire_link_resp: return false;
            case submit_multi_resp: return false;
            case data_sm_resp: return false;
            
            case bind_receiver: return true;
            case bind_transmitter: return true;
            case query_sm: return true;
            case submit_sm: return true;
            case deliver_sm: return true;
            case unbind: return true;
            case replace_sm: return true;
            case cancel_sm: return true;
            case bind_transceiver: return true;
            case outbind: return true;
            case enquire_link: return true;
            case submit_multi: return true;
            case alert_notification: return true;
            case data_sm: return true;
         }
         
         return false;
      }
      
      private Class<? extends Command> getPduClass() {
         return representingClass;
      }
    }
   
   public enum TlvCode {
      
      dest_addr_subunit(0x0005, 0, 1),
      source_addr_subunit(0x000D, 0, 1),
      dest_network_type(0x0006, 0, 1),
      source_network_type(0x000E, 0, 1),
      dest_bearer_type(0x0007, 0, 1),
      source_bearer_type(0x000F, 0, 1),
      dest_telematics_id(0x0008, 0, 2),
      source_telematics_id(0x0010, 0, 1),
      qos_time_to_live(0x0017, 0, 4),
      payload_type(0x0019, 0, 1),
      additional_status_info_text(0x001D, 1, -1),
      receipted_message_id(0x001E, 1, -1),
      ms_msg_wait_facilities(0x0030, 3, 1),
      privacy_indicator(0x0201, 0, 1),
      source_subaddress(0x0202, 1, -1),
      dest_subaddress(0x0203, 1, -1),
      user_message_reference(0x0204, 0, 2),
      user_response_code(0x0205, 0, 1),
      language_indicator(0x020D, 0, 1),
      source_port(0x020A, 0, 2),
      destination_port(0x020B, 0, 2),
      sar_msg_ref_num(0x020C, 0, 2),
      sar_total_segments(0x020E, 0, 1),
      sar_segment_seqnum(0x020F, 0, 1),
      sc_interface_version(0x0210, 0, 1),
      display_time(0x1201, 0, 1),
      ms_validity(0x1204, 0, 1),
      dpf_result(0x0420, 0, 1),
      set_dpf(0x0421, 0, 1),
      ms_availability_status(0x0422, 0, 1),
      network_error_code(0x0423, 1, 3),
      message_payload(0x0424, 1, -1),
      delivery_failure_reason(0x0425, 0, 1),
      more_messages_to_send(0x0426, 0, 1),
      message_state(0x0427, 0, 1),
      callback_num(0x0381, 1, -1),
      callback_num_pres_ind(0x0302, 2, 1),
      callback_num_atag(0x0303, 1, -1),
      number_of_messages(0x0304, 0, 1),
      sms_signal(0x1203, 0, 2),
      alert_on_message_delivery(0x130C, -1, 0),
      its_reply_type(0x1380, 0, 1),
      its_session_info(0x1383, 1, 2),
      ussd_service_op(0x0501, 1, 1);

     private int code;
     private int valueType;
     private int size;


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
     private TlvCode(int code, int valueType, int size) {
        this.code = code;
        this.valueType = valueType;
        this.size = size;
     }

     public int getCode() {
        return this.code;
     }
     
     public static TlvCode getTlvCodeFromCode(int code) {
    	 for (TlvCode c : TlvCode.values())
    		 if (c.code == code)
    			 return c;
    	 return null;
     }

     public int getValueType() {
        return this.valueType;
     }

     public int getSize() {
        return this.size;
     }
  }
}

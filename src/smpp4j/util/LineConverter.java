package smpp4j.util;
public class LineConverter {
	
	public static String toMsisdn(Object line) {
		return "595"+toNationalWithoutZero(line);
	}
	public static String toNational(Object line) {
		return "0"+toNationalWithoutZero(line);
	}
	public static String toNationalWithoutZero(Object line) {
		// no NULL check here, this should be handled at another level.
		// we asume we have the least necesary data to convert any national subscriber number 
		// to msisdn, national, or national without zero regardless of the original format.
		return line.toString().replaceAll("\\..*", "").substring(line.toString().length()-9);
	}
	
}
package logic;

/**
 * Utility class.
 * 
 * @author sam
 *
 */
public final class Utils {

	/**
	 * Method to safely convert Strings to byte arrays.
	 * 
	 * As mentioned above this is recommended so the system can operate unaffected
	 * by the char set used by your JVM.
	 * 
	 * @param string the String to be converted.
	 * @return the safely converted byte array.
	 */
	public static byte[] toByteArray(String string) {
		byte[] bytes = new byte[string.length()];
		char[] chars = string.toCharArray();
		
		for(int i = 0; i != chars.length; i++) {
			
			bytes[i] = (byte) chars[i];
		}
		
		return bytes;
	}
	
	/**
	 * Convert a byte array of 8 bit characters into a String.
	 * 
	 * @param bytes the array containing the characters
	 * @param length the number of bytes to process
	 * @return a String representation of bytes
	 */
	public static String toString(byte[] bytes) {
		
		char[] chars = new char[bytes.length];
		
		for(int i = 0; i < chars.length; i++)
			chars[i] = (char) (bytes[i] & 0xff);
		
		return new String(chars);
	}
}

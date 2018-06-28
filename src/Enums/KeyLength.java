package Enums;

/**
 * Enum to provide the STE with key lengths for symmetric and asymmetric encryption.
 * 
 * @author sam
 */
public enum KeyLength {
	x0, x64, x128, x192, x256, x1024, x4096;
	
	/**
	 * Method to return key length as an int to be used in CryptoManager class.
	 * 
	 * @return key length as int.
	 */
	public int asInt() {
		return Integer.parseInt(this.toString().substring(1));
	}
	
	/**
	 * Method to get key lengths compatible with encryption methods.
	 * 
	 * @param encryption the encryption method used
	 * 
	 * @return an array of all key lengths compatible with the encryption method used.
	 */
	public static KeyLength[] getKeyLength(EncryptionType encryption) {
		
		switch(encryption) {
		
		case none:
			return new KeyLength[] {x0};
			
		case DES:
			return new KeyLength[] {x64};
			
		case AES:
			return new KeyLength[] {x128, x192, x256};
			
		case RSA:
			return new KeyLength[] {x1024, x4096};
		
		default:
			return new KeyLength[] {};
		}
	}
}

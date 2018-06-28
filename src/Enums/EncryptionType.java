package Enums;

/**
 * Enum to provide all the possible encryption methods, including multiple symmetric,
 * RSA for asymmetric and a couple passwordbased methods.
 * 
 * @author sam
 */
public enum EncryptionType {
	none, DES, AES, RSA, PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4;
	
	/**
	 * Method to get encryption methods compatible with certain operations. Used to fill
	 * the encryption GUI with valid options, as well as junit tests for encryption combos.
	 * 
	 * @param operation the operation used
	 * 
	 * @return an array of encryption methods compatible with the operation used.
	 */
	public static EncryptionType[] getValuesByOperation(Operation operation) {
		
		switch(operation) {
		
		case Symmetric:
			return new EncryptionType[] {none, DES, AES};
			
		case Asymmetric:
			return new EncryptionType[] {RSA};
			
		case Password:
			return new EncryptionType[] {PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4};
		default:
			return new EncryptionType[] {};
		}
	}
	
	/**
	 * Method to get block size dictated by encryption methods.
	 * 
	 * Though cipher.getBlockSize() exists I had to include this because I need
	 * the information prior to instantiating the cipher object.
	 * 
	 * @return an int value of the block size dictated by this encryption method.
	 */
	public int getBlockSize() {
		
		if(this == DES || this == PBEWithMD5AndDES)
			return 8;
		if(this == AES)
			return 16;
		if(this == PBEWithSHAAnd128BitAES_CBC_BC)
			return 128;
		if(this == PBEWithSHAAnd40BitRC4)
			return 40;
		
		return 0;
	}
	
	// >:I
	public String toString() {
		if(this == PBEWithSHAAnd128BitAES_CBC_BC)
			return "PBEWithSHAAnd128BitAES-CBC-BC";
		else return this.name();
	}
}

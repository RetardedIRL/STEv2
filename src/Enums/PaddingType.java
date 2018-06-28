package Enums;

/**
 * Enum to provide the STE with padding modes.
 * 
 * @author sam
 */
public enum PaddingType {
	NoPadding, PKCS7Padding, ZeroBytePadding;
	
	/**
	 * Method to get all padding modes compatible with an encryption mode.
	 * Used to fill the encryption GUI with valid options, as well as junit tests
	 * for encryption combos.
	 * 
	 * @param mode the mode used
	 * @return an array of all padding modes compatible with the encryption mode used.
	 */
	public static PaddingType[] getPaddingByMode(EncryptionMode mode) {
		
		if(	mode == EncryptionMode.CTS ||
			mode == EncryptionMode.OFB ||
			mode == EncryptionMode.CFB8||
			mode == EncryptionMode.GCM)
			
			return new PaddingType[] {NoPadding};
		
		if( mode == EncryptionMode.ECB ||
			mode == EncryptionMode.CBC)
			
			return new PaddingType[] {NoPadding, PKCS7Padding, ZeroBytePadding};
		
		if( mode == EncryptionMode.None)
			return new PaddingType[] {NoPadding};
		
		return new PaddingType[] {};
	}
}

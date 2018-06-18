package Enums;

public enum PaddingType {
	NoPadding, PKCS7Padding, ZeroBytePadding;
	
	public static PaddingType[] getPaddingByMode(EncryptionMode mode) {
		
		if(	mode == EncryptionMode.CTR ||
			mode == EncryptionMode.OFB ||
			mode == EncryptionMode.CFB8)
			
			return new PaddingType[] {NoPadding};
		
		if( mode == EncryptionMode.ECB ||
			mode == EncryptionMode.CBC)
			
			return new PaddingType[] {NoPadding, PKCS7Padding, ZeroBytePadding};
		
		
		return new PaddingType[] {};
	}
}

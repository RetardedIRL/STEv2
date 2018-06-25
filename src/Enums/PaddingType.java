package Enums;

public enum PaddingType {
	NoPadding, PKCS7Padding, ZeroBytePadding;
	
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

package Enums;
public enum EncryptionType {
	none, DES, AES, RSA, PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4;
	
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
	
	public int getIVSize() {
		
		if(this == DES)
			return 8;
		if(this == AES)
			return 16;
		
		return 0;
	}
	
	public int getBlockSize() {
		if(this == DES)
			return 8;
		if(this == AES)
			return 16;
		
		return 0;
	}
}

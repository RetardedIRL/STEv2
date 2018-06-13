package Enums;

public enum KeyLength {
	x0, x64, x128, x192, x256, x1024, x4096;
	
	public int asInt() {
		return Integer.parseInt(this.toString().substring(1));
	}
	
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

package Enums;

public enum EncryptionMode {
ECB, CBC, CTR, OFB, CFB8;

	public int requiresIV() {
	
		if(this == CBC) return 0;
		
		else if(this == CTR ||
				this == OFB ||
				this == CFB8) return 1;
		
		//ECB
		return -1;
	}
}

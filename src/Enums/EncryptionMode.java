package Enums;

public enum EncryptionMode {
None, ECB, CBC, CTR, OFB, CFB8;

	public int requiresIV() {
	
		if(this == CBC) return 0;
		
		else if(this == CTR ||
				this == OFB ||
				this == CFB8) return 1;
		
		//ECB
		return -1;
	}
	
	public static EncryptionMode[] getModeByOperation(Operation operation) {
		
		switch(operation) {
		
			case Symmetric:
			case Password:
				return new EncryptionMode[] {ECB, CBC, CTR, OFB, CFB8};
			
			case Asymmetric:
				return new EncryptionMode[] {None};
			
			default:
				return new EncryptionMode[] {};
		}
	}
}

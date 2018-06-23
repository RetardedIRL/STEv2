package Enums;

public enum EncryptionMode {
None, ECB, CBC, CTS, OFB, CFB8, GCM;

	public String getType() {
		
		if(this == CBC || this == CTS)
			return "ivBlock";
		
		else if(this == OFB ||
				this == GCM ||
				this == CFB8)
			return "stream";
		
		return "block";
	}
	
	public static EncryptionMode[] getModeByOperation(Operation operation) {
		
		switch(operation) {
		
			case Symmetric:
			case Password:
				return new EncryptionMode[] {ECB, CBC, CTS, OFB, CFB8, GCM};
			
			case Asymmetric:
				return new EncryptionMode[] {None};
			
			default:
				return new EncryptionMode[] {};
		}
	}
}

package Enums;

/**
 * Enum to provide the STE with encryption modes, ranging from standard block cipher modes
 * to more advanced stream cipher modes.
 * 
 * @author sam
 */
public enum EncryptionMode {
None, ECB, CBC, CTS, OFB, CFB8, GCM;

	/**
	 * Method to return the 'type' of the encryption mode, which has to be one of the following:
	 * - "block": 	which indicates a normal block cipher mode with no IV,
	 * 				meaning it's very vulnerable to NoPadding uses.
	 * 
	 * - "ivBlock":	block cipher modes that use IVs.
	 * 
	 * - "stream":	stream cipher modes, which require NoPadding and an IV.
	 * 
	 * @return the 'type' of the encryption mode as string.
	 */
	public String getType() {
		
		if(this == CBC || this == CTS)
			return "ivBlock";
		
		else if(this == OFB ||
				this == GCM ||
				this == CFB8)
			return "stream";
		
		return "block";
	}
	
	/**
	 * Method to get all compatible encryption modes of a certain operation. Used to fill
	 * the GUI with valid options, as well as junit tests about encryption combos.
	 * 
	 * @param operation the operation used
	 * 
	 * @return an array of what encryption modes are compatible with the operation.
	 */
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

package src;

import Enums.EncryptionType;
import Enums.ModeType;
import Enums.PaddingType;

public class Logic {

	
	static boolean isStreamCipher(ModeType mode) {
		if(mode == ModeType.CTR || mode == ModeType.OFB || mode == ModeType.CFB8)
			return true;
		
		return false;
	}
	/**
	 * Method to check whether or not a mode requires an IV
	 * @param mode the mode you want to apply
	 * @return -1: no IV
	 * 			0: normal inline IV
	 * 			1: stream cipher IV
	 */
	static int requiresIV(ModeType mode) {
		
		//returns 0 for cipher mode that needs an IV
		if(mode == ModeType.CBC) {
			return 0;
		}
		
		//returns 1 for stream cipher modes
		if(mode == ModeType.CTR || mode == ModeType.OFB || mode == ModeType.CFB8)
			return 1;
		
		return -1;
	}
	
	/**
	 * When using NoPadding you can only encrypt when
	 * the input bytes are block size or multiples of that.
	 * 
	 * @param input the input array
	 * @param blockSize the block size associated with an Encryption mode
	 * @return true if block size or multiples of that
	 */
	static boolean isMultiple(byte[] input, int blockSize) {
		
		// if input.length mod block size then it can be encrypted without padding
		if((input.length % blockSize) == 0)
			return true;
		
		return false;
	}
	
	/**
	 * Method to check if the encryption, mode and padding are compatible with each other.
	 * 
	 * @param encryption Encryption Mode
	 * @param mode Mode
	 * @param padding Padding Type
	 * @return true if everything is compatible
	 */
	static boolean isCompatible(EncryptionType encryption, ModeType mode, PaddingType padding) {
		
		if(mode == ModeType.CBC && padding == PaddingType.NoPadding) {}
		
		return true;
	}
	
	/**
	 * Method to do a complete validity check based on compatibility methods.
	 * 
	 * TODO: throw exceptions
	 * 
	 * @param encryption Encryption Method
	 * @param mode Mode
	 * @param padding Padding Type
	 * @param input input byte array
	 * @param blockSize block size
	 * @return true if everything is valid
	 */
	public static boolean isValid(EncryptionType encryption, ModeType mode, PaddingType padding, byte[] input, int blockSize) {
		
		// if there's no padding and the input isn't compatible with block size return false
		if(padding == PaddingType.NoPadding && !(isMultiple(input, blockSize) || isStreamCipher(mode)))
			return false;
		
		// if the chosen encryption, mode and padding have incompatibilities return false
		if(!isCompatible(encryption, mode, padding))
			return false;
		
		return true;
	}
}

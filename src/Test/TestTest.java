package Test;

import org.junit.Test;

import Enums.EncryptionMode;
import Enums.EncryptionType;
import Enums.Operation;

public class TestTest {

	@Test
	public void testTest() {
		
		for(Operation operation : new Operation[] {Operation.Symmetric, Operation.Asymmetric}) {
			
			System.out.println("--------");
			
			for(EncryptionMode mode : EncryptionMode.getModeByOperation(operation))
				System.out.println(mode);
			
			System.out.println("---");
			
			for(EncryptionType type : EncryptionType.getValuesByOperation(operation))
				System.out.println(type);
		}
	}
}

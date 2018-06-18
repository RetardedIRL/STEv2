package persistence;

import Enums.Operation;
import Enums.EncryptionType;
import Enums.EncryptionMode;
import Enums.PaddingType;
import Enums.KeyLength;
import Enums.HashFunction;


public class MetaData {

	Operation		operation;
	EncryptionType 	encryption;
	EncryptionMode 	mode;
	PaddingType		padding;
	KeyLength		keyLength;
	HashFunction	hashFunction;
	String			hashValue;
	byte[]			IV;
	
	String			password;
	
	
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public MetaData() {}
	public MetaData(EncryptionType encryption, EncryptionMode mode, PaddingType padding, HashFunction hashFunction, String hashValue) {
		
		this.encryption = encryption;
		this.mode = mode;
		this.padding = padding;
		this.hashFunction = hashFunction;
		this.hashValue = hashValue;
	}
	
	public void setHashValue(String value) {
		this.hashValue = value;
	}
	
	public String getHashValue() {
		return this.hashValue;
	}
	
	public void setHashFunction(HashFunction function) {
		this.hashFunction = function;
	}
	
	public HashFunction getHashFunction() {
		return this.hashFunction;
	}
	
	public void setIV(byte[] value) {
		this.IV = value;
	}
	
	public byte[] getIV() {
		return this.IV;
	}
	
	public void setEncryptionType(EncryptionType encryption) {
		this.encryption = encryption;
	}
	
	public EncryptionType getEncryptionType() {
		return this.encryption;
	}
	
	public void setEncryptionMode(EncryptionMode mode) {
		this.mode = mode;
	}
	
	public EncryptionMode getEncryptionMode() {
		return this.mode;
	}
	
	public void setPaddingType(PaddingType padding) {
		this.padding = padding;
	}
	
	public PaddingType getPaddingType() {
		return this.padding;
	}
	
	public void setKeyLength(KeyLength keyLength) {
		this.keyLength = keyLength;
	}
	
	public KeyLength getKeyLength() {
		return this.keyLength;
	}
	
	public String toString() {
		return 	"Encryption Method:\t" + this.encryption + "\n" +
				"Encryption Mode:\t" + this.mode + "\n" +
				"Padding:\t" + this.padding + "\n" +
				"Key Length:\t" + this.keyLength + "\n" +
				"Hash Function:\t" + this.hashFunction + "\n" +
				"Hash Value:\t" + this.hashValue + "\n" +
				"Instanzvektor:\t" + this.IV;
	}
}

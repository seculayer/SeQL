package com.seculayer.seql.util.cipher;

/**
 * This class is.
 * This block is a block cipher algorithm, type the password should be an exact multiple of the size.
 * If you, or an exact multiple of plaintext to be encrypted,
 * encrypting the data before adding padding to support the operation.
 * 
 * @author devhome.tistory.com
 */
public abstract class BlockPadding {

	public static final String ANSIX = "ANSIX923";
	public static final String ISO   = "ISO10126";
	public static final String PKCS7 = "PKCS7";
	
	private static BlockPadding ins = null;
	
	/**
	 * To provide byte padding to create an instance.
	 * 'ANSI X.923' byte padding provides a way.
	 * @return Byte padding instance (ANSI X.923)
	 */
	public static BlockPadding getInstance() {
		
		if( ins == null )
			return new AnsiX923Padding();
		
		return ins;
	}
	
	/**
	 * To provide byte padding to create an instance.
	 * Byte padding to provide that kind of 'ANSI X.923', 'ISO 10126', 'PKCS7' is.
	 * @param type The type of padding bytes (ANSI X.923 : ANSIX923, ISO 10126 : ISO10126, PKCS7 : PKCS7)
	 * @return Byte padding instance, if that does not support the null of the padding bytes is returned.
	 */
	public static BlockPadding getInstance(String type) {
		
		boolean isCreate = false;
		
		if( ins == null ) {
			isCreate = true;
		} else {
			if( ins.getPaddingType().equals(type) != true ) {
				isCreate = true;
			}
		}
		
		if( isCreate == true ) {
			
			if( type.equals(ANSIX) == true ) {
				ins = new AnsiX923Padding();
			} else if( type.equals(ISO) == true ) {
				ins = null;
			} else if( type.equals(PKCS7) == true ) {
				ins = null;
			} else {
				ins = null;
			}
		}
		
		return ins;
	}
	
	/**
	 * Type of the current instance, brings paeting bytes.
	 * @return The type of padding bytes
	 */
	public abstract String getPaddingType();
	
	/**
	 * Block size is short, add enough padding.
	 * @param source Target Data Encryption
	 * @param blockSize block size
	 * @return Added padding target data encryption
	 */
	public abstract byte[] addPadding(byte[] source, int blockSize);
	
	
	public abstract byte[] removePadding(byte[] source, int blockSize);
}

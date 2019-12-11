package com.seculayer.seql.util.cipher;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * SEED algorithm to encrypt or decrypt the data is the class that provides the ability to.
 * @author devhome.tistory.com
 *
 */
public class Seed128Cipher {
	
	/**
	 * SEED encryption algorithm block size
	 */
	private static final int SEED_BLOCK_SIZE = 16;
	
	/**
	 * SEED algorithm to encrypt the data.
	 * @param data Target Data
	 * @param key Masterkey
	 * @param charset Data character set
	 * @return Encrypted data
	 * @throws UnsupportedEncodingException If character is not supported
	 */
	public static String encrypt(String data, byte[] key, String charset)
	throws UnsupportedEncodingException {
		
		byte[] encrypt = null;
		if( charset == null ) {
			encrypt = BlockPadding.getInstance().addPadding(data.getBytes(), SEED_BLOCK_SIZE);
		} else {
			encrypt = BlockPadding.getInstance().addPadding(data.getBytes(charset), SEED_BLOCK_SIZE);
		}
		
		int pdwRoundKey[] = new int[32];
		SEED128.SeedRoundKey(pdwRoundKey, key);
		
		int blockCount = encrypt.length / SEED_BLOCK_SIZE;
		for( int i = 0; i < blockCount; i++ ) {
			
			byte sBuffer[] = new byte[SEED_BLOCK_SIZE];
			byte tBuffer[] = new byte[SEED_BLOCK_SIZE];
			
			System.arraycopy(encrypt, (i * SEED_BLOCK_SIZE), sBuffer, 0, SEED_BLOCK_SIZE);
			
			SEED128.SeedEncrypt(sBuffer, pdwRoundKey, tBuffer);
			
			System.arraycopy(tBuffer, 0, encrypt, (i * SEED_BLOCK_SIZE), tBuffer.length);
		}
		
		return Base64.getEncoder().encodeToString(encrypt);
	}
	
	/**
	 * ARIA algorithm to decrypt the data.
	 * @param data Target Data
	 * @param key Masterkey
	 * @param keySize Masterkey Size
	 * @param charset Data character set
	 * @return Decrypted data
	 * @throws UnsupportedEncodingException If character is not supported
	 */
	public static String decrypt(String data, byte[] key, String charset)
	throws UnsupportedEncodingException {
		
		int pdwRoundKey[] = new int[32];
		SEED128.SeedRoundKey(pdwRoundKey, key);
		
		byte[] decrypt = Base64.getDecoder().decode(data);
		int blockCount = decrypt.length / SEED_BLOCK_SIZE;
		for( int i = 0; i < blockCount; i++ ) {
			
			byte sBuffer[] = new byte[SEED_BLOCK_SIZE];
			byte tBuffer[] = new byte[SEED_BLOCK_SIZE];
			
			System.arraycopy(decrypt, (i * SEED_BLOCK_SIZE), sBuffer, 0, SEED_BLOCK_SIZE);
			
			SEED128.SeedDecrypt(sBuffer, pdwRoundKey, tBuffer);
			
			System.arraycopy(tBuffer, 0, decrypt, (i * SEED_BLOCK_SIZE), tBuffer.length);
		}
		
		if( charset == null ) {
			return new String(BlockPadding.getInstance().removePadding(decrypt, SEED_BLOCK_SIZE));
		} else {
			return new String(BlockPadding.getInstance().removePadding(decrypt, SEED_BLOCK_SIZE), charset);
		}
	}
	
	public static byte[] makeKey(String keyStr) {
		byte[] key = new byte[32];
		byte[] kw = keyStr.getBytes();
		for(int i=0; i<kw.length; i++) {
			key[i] = kw[i];
		}
		
		for( int i = kw.length; i < key.length; i++ ) {
			key[i] = (byte)i;
		}
		return key;
	}
	
	/**
	 * The sample code in the Cipher class
	 * @param args none
	 */
	public static void main(String args[]) {
		
		try {
			
//			byte[] key = new byte[32];
//			for( int i = 0; i < key.length; i++ ) {
//				key[i] = (byte)i;
//			}
			
			String keyStr = "test123";
			//keyStr = Base64Util.base64Decode(keyStr);
			byte[] key    = makeKey(keyStr);
			System.out.println("> key=["+(new String(key))+"]");
			System.out.println("> keyLength=["+key.length+"]");
			
			String data = "This is test...";
			
			data = Seed128Cipher.encrypt(data, key, null);
			System.out.println("> encrypt=[" + data + "]");
			
			data = Seed128Cipher.decrypt(data, key, null);
			System.out.println("> decrypt=[" + data + "]");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

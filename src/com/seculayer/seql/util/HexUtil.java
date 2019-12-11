package com.seculayer.seql.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.seculayer.seql.Constants;

public class HexUtil {
	public static String hexToStringOriginal(String in) throws Exception {
		byte buf[] = (new BigInteger(in.replaceAll("[^0-9a-fA-F]", ""), 16))
				.toByteArray();
		byte bitBuf[] = new byte[16];
		char dumpColBuf[] = new char[buf.length];
		Arrays.fill(dumpColBuf, ' ');
		int count = 0;
		int dumpIdx = 0;
		for (int j = 0; j <= buf.length; j += 16) {
			count = Math.min(bitBuf.length, buf.length - j);
			System.arraycopy(buf, j, bitBuf, 0, count);
			for (int i = 0; i < count; i++) {
				char ch = (char) bitBuf[i];
				//System.out.printf("%d====>%c\n", i, ch);
				if (ch < ' ' || ch > '~') {
					ch = '.';
				}
				dumpColBuf[dumpIdx++] = ch;
			}
		}
		return new String(dumpColBuf, 0, dumpColBuf.length);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte data[] = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));

		return data;
	}
	
	public static String stringToMD5(String str) {
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();			
		} catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			MD5 = null; 
		}
		return MD5;
	}
	
	public static String hexRemoveHeader(String in) throws Exception {
		String hexStr = in.replaceAll("[^0-9a-fA-F]", "");
		int len = hexStr.length();
		
		if (len >= 93 && hexStr.charAt(92) == '5' && hexStr.charAt(93) == '0') {
			hexStr = (hexStr.substring(104, hexStr.length())).toLowerCase();
		} else if (len >= 93 && hexStr.charAt(92) == '6' && hexStr.charAt(93) == '0') {
			hexStr = (hexStr.substring(117, hexStr.length())).toLowerCase();
		} else if (len >= 93 && hexStr.charAt(92) == '8' && hexStr.charAt(93) == '0') {
			hexStr = (hexStr.substring(132, hexStr.length())).toLowerCase();
		} else if (len >= 29 && hexStr.charAt(28) == '8' && hexStr.charAt(29) == '5') {
			hexStr = (hexStr.substring(84, hexStr.length())).toLowerCase();
		} else if (len >= 124){
			hexStr = (hexStr.substring(124, hexStr.length())).toLowerCase();
		}
		return hexStr;
	}
	
	public static String hexToString(String hexStr) throws Exception {
		return hexToString(hexStr, Constants.DEFAULT_CHARSET).replaceAll("\u0000", "");
	}
	
	public static String hexToString(String hexStr, String charset) throws Exception {
		return new String(new BigInteger(hexStr.replaceAll("[^0-9a-fA-F]", ""), 16).toByteArray(), charset).replaceAll("\u0000", "");
	}
	
	public static String hexToStringReplaceUnrecognizedChar(String hexStr, char replaceChar) throws Exception {
		return replaceUnrecognizedChar(hexToString(hexStr), replaceChar);
	}
	
	public static String replaceUnrecognizedChar(String str, char replaceChar) {
		StringBuilder builder = new StringBuilder();
		char c;
		for(int i=0; i<str.length(); i++) {
			c = str.charAt(i);
			try {
				if(c>=48 && c<=57) { //Numeric : 9
					builder.append(c);
					//System.out.printf("[%c]=[%d] is numeric.\n", c, cType);
				} else if((c>=65 && c<=90) || (c>=97 && c<=122)) { //Alphabet : 1,2
					builder.append(c);
					//System.out.printf("[%c]=[%d] is Alphabet.\n", c, cType);
				} else if((c>=33 && c<=47) || 
						(c>=58 && c<=64) ||
						(c>=91 && c<=96) ||
						(c>=123 && c<=126)) { //Special Character : 24
					builder.append(c);
					//System.out.printf("[%c]=[%d] is Special Character.\n", c, cType);
				} else if(Character.getType(c)==Character.OTHER_LETTER) { //Other Letter(Hangul) : 5
					builder.append(c);
					//System.out.printf("[%c]=[%d] is Hangul.\n", c, cType);
				} else if(c=='\n'||c=='\r'||c==' ') { //Line Separator : 12, 15
					builder.append(c);
					//System.out.printf("[%c]=[%d] is Line Separator.\n", c, cType);
				} else {
					builder.append(replaceChar);
					//System.out.printf("[%c]=[%d] is etc.\n", c, cType);
				}
			} catch(Exception ex) {
				builder.append(replaceChar);
				//System.out.printf("[%c]=[%d] is error.\n", c, cType);
			}
		}
		return builder.toString();
	}
}

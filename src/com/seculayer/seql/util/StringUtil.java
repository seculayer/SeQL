package com.seculayer.seql.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import com.seculayer.seql.Constants;

/** 
 * <p>문자열을 다룰때 필요한 기능을 모아 놓은 유틸성 클래스. </p>
 * @version  1.0
 */
public class StringUtil {
    /**
     * <p>지정 문자열에서 특정 문자를 변경함.</p>
     *
     * @param    찾으려고하는 단어.
	 * @param    바꾸고자하는 단어.
	 * @param    대상 문자열.
     * @return   변경된 문자열.
     */
	public static String nvl(Object obj) 
	{
		String strRtn = "";
		
		if ( obj != null ) {
			if( obj instanceof String ) 
				strRtn = ( ( String )obj).trim();
			else 
				strRtn = obj.toString();
		}

		return strRtn;
	}
	
	public static String nvl(String source) {
		if(source == null) {
			return "";
		}else{
			return source;
		}
	}

    /**
     * <p>지정 문자열에서 특정 문자를 변경함.</p>
     *
     * @param    찾으려고하는 단어.
	 * @param    바꾸고자하는 단어.
	 * @param    대상 문자열.
     * @return   변경된 문자열.
     */	
    public static String nvl(String source, String value) {
		if(source == null) {
			if(value != null) {
				return value;
			}else{
				return "";
			}
		}else{
			return source;			
		}
	}
    
    public static String strCut(String szText, int nLength, String charset) { // 문자열 자르기
		String r_val = szText;
		int oF = 0, oL = 0, rF = 0, rL = 0;
		int nLengthPrev = 0;
		try {
			byte[] bytes = r_val.getBytes(charset); // 바이트로 보관
			// x부터 y길이만큼 잘라낸다. 한글안깨지게.
			int j = 0;
			if (nLengthPrev > 0)
				while (j < bytes.length) {
					if ((bytes[j] & 0x80) != 0) {
						oF += 2;
						rF += 3;
						if (oF + 2 > nLengthPrev) {
							break;
						}
						j += 3;
					} else {
						if (oF + 1 > nLengthPrev) {
							break;
						}
						++oF;
						++rF;
						++j;
					}
				}
			j = rF;
			while (j < bytes.length) {
				if ((bytes[j] & 0x80) != 0) {
					if (oL + 2 > nLength) {
						break;
					}
					oL += 2;
					rL += 3;
					j += 3;
				} else {
					if (oL + 1 > nLength) {
						break;
					}
					++oL;
					++rL;
					++j;
				}
			}
			r_val = new String(bytes, rF, rL, charset); // charset 옵션
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return r_val;
	}

	public static boolean isEmpty(Object obj) {
		boolean bRtn = false;
		
		if ( obj == null ) bRtn = true;
		else if(obj instanceof String ) bRtn = ( ( String )obj).trim().equals("");
		
		return bRtn;
	}
	
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	/**
	 * String을Long으로 변환
	 * 객체를 위에있는 get(Object obj)를 이용해 String화한 후 빈칸이 아니면 스트링을 Long 변환
	 * 소수점이하는 버린다.
	 * @param obj
	 * @return String을 Long로 변환
	 */
	public static long getLong(Object obj) 
	{
		long dRtn = 0;
		
		if ( !nvl(obj).equals("") )
			dRtn = Long.parseLong(Trunc(obj.toString()));

		return dRtn;
	}
	
	/**
	 * String을 Int으로 변환
	 * 객체를 위에있는 get(Object obj)를 이용해 String화한 후 빈칸이 아니면 스트링을 Int로 변환
	 * 소수점이하는 버린다.
	 * @param obj
	 * @return String을 Int로 변환
	 */
	public static int getInt(Object obj) 
	{
		int dRtn = 0;
		
		if ( !nvl(obj).equals("") )
			dRtn = Integer.parseInt(Trunc(obj.toString()));

		return dRtn;
	}
	
	/**
	 * String이 소수일때 소수점 이하 버리기
	 * @param val
	 * @return 소수점이하 버리고 정수부분만 리턴
	 */
	public static String Trunc( String val )
    {
        if( val == null || val.equals("") )
			return "0";

		int prdIdx = val.indexOf('.');
		
		if ( prdIdx != -1 )
			return val.substring(0, prdIdx);
			
        return val;
    }
	
	/**
	 * String배열 str(입력값)에 ','문자와 '/'문자를 구별하여 배열값들을 ','로 나누어 표현
	 * @param str
	 * @return 배열을 String화하여 리턴
	 */
	public static String join(String[] str) {
		return join(str, ',', '/');
	}
	
	public static String join(String[] str, char sep, char esc) {
		if(str == null) return null;
		
		StringBuffer sb = new StringBuffer();
		
		String dbEsc = String.valueOf(esc) + esc;
		String escSep = String.valueOf(esc) + sep;
		
		boolean bFlag = false;
		for(String s : str) {
			if(bFlag) sb.append(",");
			else bFlag = !bFlag;
			
			sb.append(nvl(s).replaceAll(String.valueOf(esc), dbEsc).replaceAll(String.valueOf(sep), escSep));
		}
		
		return sb.toString();
	}
	
	/**
	 * join()와 반대. ','문자와 '/'문자를 구별하여 배열값들을 ','로 나누어 표현되어있는 것을 다시 원래 값으로 표현
	 * @param str
	 * @return String형을 String배열화하여 리턴
	 */
	public static String[] split(String str) {
		return split(str, ',', '/');
	}
	
	public static String[] split(String str, char sep, char esc) {
		if(str == null) return null;
		
		boolean bfEsc = false;
		char cChar;
		
		List<String> ar = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < str.length(); i++) {
			cChar = str.charAt(i);
			
			if(cChar == esc && !bfEsc) {
				bfEsc = true;
				continue;
			}
			else if (cChar == sep) {
				if(bfEsc) cChar = sep;
				else {
					ar.add(sb.toString());
					sb = new StringBuffer();
					continue;
				}
			}
			
			sb.append(cChar);
			bfEsc = false;
		}
		ar.add(sb.toString());
		
		return (String[])ar.toArray(new String[ar.size()]);
	}
	
	/**
	 * String 객체의 공백제거 및 null값의 ""화(null값인 String를 ""로 변환해서 공백이라는 의미를 부여)
	 * String get(Object obj) 의 최종 경로
	 * @param str 빈칸을 제거 하고 싶은 String ..즉  String 객체에서 쓸데없는부분없애고 의미있게 사용할수 있게! 
	 * @return 의미있는(앞뒤공백제거,null값의 ""화) String을 리턴
	 */
	public static String get(String str) {
		if ( str == null ) return "";			
		return str.trim();
	}
	
	/**
	 * 객체의 String화 객체의 String 정보 얻고 싶을때
	 * @param obj String값을 얻어내고 싶은 obj
	 * @return obj가 String이었다면 앞뒤공백제거하고 String리턴 그렇지 않으면 객제 정보를 String으로 리턴
	 */
	public static String get(Object obj) {
		String strRtn = "";		
		if ( obj != null ) {
			if( obj instanceof String ) {
				strRtn = ( ( String )obj).trim();
			} else {
				strRtn = obj.toString();
			}
		}
		return strRtn;
	}
	
	public static String rpad(String str, Integer length, char car) {
		return (str + String.format("%" + length + "s", "").replace(" ", String.valueOf(car))).substring(0, length);
	}

	public static String lpad(String str, Integer length, char car) {
		return (String.format("%" + length + "s", "").replace(" ", String.valueOf(car)) + str).substring(str.length(), length + str.length());
	}
	
	public static String getMD5(String str) throws NoSuchAlgorithmException {
		String md5Hash = null;
	    try {
	    	MessageDigest md = MessageDigest.getInstance("MD5");
	    	byte[] digest = md.digest();
			md.update(str.getBytes(Constants.DEFAULT_CHARSET));
			md5Hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return md5Hash;
	}
	
	public static String getSha256(String value, String algorithm) throws NoSuchAlgorithmException {
		String encoded = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] hash = digest.digest(value.getBytes(Constants.DEFAULT_CHARSET));
			encoded = Base64.getEncoder().encodeToString(hash);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encoded;
	}
	
	public static boolean like(final String str, String expr) {
//		final String[] parts = expr.split("%");
		final String[] parts = split(expr, '%', '\\');
		final boolean traillingOp = expr.endsWith("%");
		expr = "";
		for (int i = 0, l = parts.length; i < l; ++i) {
			final String[] p = parts[i].split("\\\\\\?");
			if (p.length > 1) {
				for (int y = 0, l2 = p.length; y < l2; ++y) {
					expr += p[y];
					if (i + 1 < l2)
						expr += ".";
				}
			} else {
				expr += parts[i];
			}
			if (i + 1 < l)
				expr += "%";
		}
		if (traillingOp)
			expr += "%";
		expr = expr.replace("?", ".");
		expr = expr.replace("%", ".*");
		return str.matches(expr);
	}
	
	public static Object invokeClassMethod(String classBinName, String methodName) {
		Object result = null;
		try {
			// Create a new JavaClassLoader
			ClassLoader classLoader = StringUtil.class.getClassLoader();
			
			// Load the target class using its binary name
			Class loadedMyClass = classLoader.loadClass(classBinName);
			
			// Create a new instance from the loaded class
			Constructor constructor = loadedMyClass.getConstructor();
			Object myClassObject = constructor.newInstance();
			
			// Getting the target method from the loaded class and invoke it using its name
			Method method = loadedMyClass.getMethod(methodName);
			result = method.invoke(myClassObject);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Object invokeClassMethod(String classBinName, String methodName, Object[] params) {
		Object result = null;
		try {
			// Create a new JavaClassLoader
			ClassLoader classLoader = StringUtil.class.getClassLoader();

			// Load the target class using its binary name
			Class loadedMyClass = classLoader.loadClass(classBinName);

			// Create a new instance from the loaded class
			Constructor constructor = loadedMyClass.getConstructor();
			Object myClassObject = constructor.newInstance();

			// Getting the target method from the loaded class and invoke it using its name
			Method method;
			if(params == null || params.length==0) {
				method = loadedMyClass.getMethod(methodName);
				result = method.invoke(myClassObject);
			} else {
				Class[] arguments = new Class[params.length];
				int idx = 0;
				for(Object o:params) {
					if(o instanceof String) {
						arguments[idx] = String.class;	
					} else if(o instanceof Integer) {
						arguments[idx] = Integer.class;					
					} else if(o instanceof Long) {
						arguments[idx] = Long.class;					
					} else if(o instanceof Float) {
						arguments[idx] = Float.class;					
					} else if(o instanceof Double) {
						arguments[idx] = Double.class;					
					} else if(o instanceof Boolean) {
						arguments[idx] = Boolean.class;					
					} else if(o instanceof List) {
						arguments[idx] = List.class;					
					} else if(o instanceof Map) {
						arguments[idx] = Map.class;					
					} else if(o instanceof Set) {
						arguments[idx] = Set.class;					
					} else arguments[idx] = Object.class;
					idx++;
				}
				method = loadedMyClass.getMethod(methodName, arguments);
				result = method.invoke(myClassObject, params);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Object invokeClassMethod(String classBinName, Object[] constructorParams, String methodName) {
		Object result = null;
		try {
			// Create a new JavaClassLoader
			ClassLoader classLoader = StringUtil.class.getClassLoader();

			// Load the target class using its binary name
			Class loadedMyClass = classLoader.loadClass(classBinName);

			// Create a new instance from the loaded class
			Constructor constructor;
			Object myClassObject;
			if(constructorParams==null || constructorParams.length==0) {
				constructor = loadedMyClass.getConstructor();
				myClassObject = constructor.newInstance();
			} else {
				Class[] constructorArguments = new Class[constructorParams.length];
				int idx = 0;
				for(Object o:constructorParams) {
					if(o instanceof String) {
						constructorArguments[idx] = String.class;	
					} else if(o instanceof Integer) {
						constructorArguments[idx] = Integer.class;					
					} else if(o instanceof Long) {
						constructorArguments[idx] = Long.class;					
					} else if(o instanceof Float) {
						constructorArguments[idx] = Float.class;					
					} else if(o instanceof Double) {
						constructorArguments[idx] = Double.class;					
					} else if(o instanceof Boolean) {
						constructorArguments[idx] = Boolean.class;					
					} else if(o instanceof List) {
						constructorArguments[idx] = List.class;					
					} else if(o instanceof Map) {
						constructorArguments[idx] = Map.class;					
					} else if(o instanceof Set) {
						constructorArguments[idx] = Set.class;					
					} else constructorArguments[idx] = Object.class;
					idx++;
				}
				constructor = loadedMyClass.getConstructor(constructorArguments);
				myClassObject = constructor.newInstance(constructorParams);
			}

			// Getting the target method from the loaded class and invoke it using its name
			Method method = loadedMyClass.getMethod(methodName);
			result = method.invoke(myClassObject);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Object invokeClassMethod(String classBinName, Object[] constructorParams, String methodName, Object[] methodParams) {
		Object result = null;
		try {
			// Create a new JavaClassLoader
			ClassLoader classLoader = StringUtil.class.getClassLoader();

			// Load the target class using its binary name
			Class loadedMyClass = classLoader.loadClass(classBinName);

			// Create a new instance from the loaded class
			Constructor constructor;
			Object myClassObject;
			if(constructorParams==null || constructorParams.length==0) {
				constructor = loadedMyClass.getConstructor();
				myClassObject = constructor.newInstance();
			} else {
				Class[] constructorArguments = new Class[constructorParams.length];
				int idx = 0;
				for(Object o:constructorParams) {
					if(o instanceof String) {
						constructorArguments[idx] = String.class;	
					} else if(o instanceof Integer) {
						constructorArguments[idx] = Integer.class;					
					} else if(o instanceof Long) {
						constructorArguments[idx] = Long.class;					
					} else if(o instanceof Float) {
						constructorArguments[idx] = Float.class;					
					} else if(o instanceof Double) {
						constructorArguments[idx] = Double.class;					
					} else if(o instanceof Boolean) {
						constructorArguments[idx] = Boolean.class;					
					} else if(o instanceof List) {
						constructorArguments[idx] = List.class;					
					} else if(o instanceof Map) {
						constructorArguments[idx] = Map.class;					
					} else if(o instanceof Set) {
						constructorArguments[idx] = Set.class;					
					} else constructorArguments[idx] = Object.class;
					idx++;
				}
				constructor = loadedMyClass.getConstructor(constructorArguments);
				myClassObject = constructor.newInstance(constructorParams);
			}

			// Getting the target method from the loaded class and invoke it using its name
			Method method;
			if(methodParams == null || methodParams.length==0) {
				method = loadedMyClass.getMethod(methodName);
				result = method.invoke(myClassObject);
			} else {
				Class[] arguments = new Class[methodParams.length];
				int idx = 0;
				for(Object o:methodParams) {
					if(o instanceof String) {
						arguments[idx] = String.class;	
					} else if(o instanceof Integer) {
						arguments[idx] = Integer.class;					
					} else if(o instanceof Long) {
						arguments[idx] = Long.class;					
					} else if(o instanceof Float) {
						arguments[idx] = Float.class;					
					} else if(o instanceof Double) {
						arguments[idx] = Double.class;					
					} else if(o instanceof Boolean) {
						arguments[idx] = Boolean.class;					
					} else if(o instanceof List) {
						arguments[idx] = List.class;					
					} else if(o instanceof Map) {
						arguments[idx] = Map.class;					
					} else if(o instanceof Set) {
						arguments[idx] = Set.class;
					} else arguments[idx] = Object.class;
					idx++;
				}
				method = loadedMyClass.getMethod(methodName, arguments);
				result = method.invoke(myClassObject, methodParams);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String removeComments(String str) {
		try {
			String tmp = str.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/", "");
	
			StringBuffer buffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new StringReader(tmp));
			String line;
			while((line=br.readLine()) != null) {
				if(line.trim().startsWith("#")) continue;
				buffer.append(line).append(System.lineSeparator());
			}
			return buffer.toString();
		} catch(Exception ex) {
			ex.printStackTrace();
			return str;
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(lpad("1", 3, '0'));
//		System.out.println(like("abc23def", "%1%"));
		Object result = invokeClassMethod("com.seculayer.seql.util.IPUtil", "ip2long", new Object[] { "192.168.1.1" });
		System.out.println("result======>" + result);
//		Object result2 = invokeClassMethod("com.seculayer.seql.util.KIDGenerator", "makeKey", new Object[] { "ABC" });
//		Object result2 = invokeClassMethod("com.seculayer.seql.util.cipher.AES256", "encrypt", new Object[] { "ABC" });
//		Object result2 = invokeClassMethod("com.seculayer.seql.util.cipher.AES256", "decrypt", new Object[] { "Xiiiiab1RRMP+B+DnBfbMw==" });
//		Object result2 = invokeClassMethod("com.seculayer.seql.util.cipher.AES256", new Object[] { "seculayer@123" }, "encrypt", new Object[] { "ABC" });
//		Object result2 = invokeClassMethod("com.seculayer.seql.util.DateTime", new Object[] { "seoul" }, "getDateString");
		Object result2 = invokeClassMethod("com.seculayer.seql.util.DateTime", "getDateString");
		System.out.println("result2======>" + result2);
	}
}
package com.seculayer.seql.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class IPUtil {
	public static long ip2long(String ip){
		String[] arr = ip.split("\\.");
		if(arr.length != 4){return 0;}

		long[] larr = new long[4];
		for(int i = 0; i < arr.length; i++){
			try{
				larr[i] = Long.parseLong(arr[i]);
			}
			catch(Exception e){return 0;}
		}

		return (larr[3]) | (larr[2] << 8) | (larr[1] << 16) | (larr[0] << 24);
	}

	public static String long2ip(long ip) {
		String res = "";
		for(int i = 0; i < 4; i++){
			long tmp = ip & 255;
			res = tmp + res;
			if(i != 3){res = "." + res;}
			ip = ip >> 8;
		}
		return res;
	}
	
	public static String ip2longString(String ip, int maxLen, String s) {
		String result = String.valueOf(ip2long(ip));
        for (int i = 0; i < (maxLen-result.length()); i++){
              result = s + result;
        }
        return result;
	}
	
	public static String extractDomain(String domain) {
		if(domain==null || domain.length()==0) return domain;
		//0. Cut Parameter
		String cutDomain = domain.replaceAll("(\\?.+)|([hHtTpPsS]{4,5}://)", "");
		//1. IP is return
		if(cutDomain.replaceAll("[0-9.]", "").length()==0) return cutDomain;		
		String[] arr = cutDomain.split("\\.");
		//2. if domain is .com/.net
		if(cutDomain.endsWith(".com")||cutDomain.endsWith(".net")) {
			return arr[arr.length-2] + "." + arr[arr.length-1];
		}
		//3. if .(dot) is one
		if(arr.length==2) {
			return cutDomain;
		}
		//4. if .(dot) is two
		if(arr.length==3) {
			return arr[arr.length-2] + "." + arr[arr.length-1];
		}
		//5. if .(dot) is three more
		if(arr.length>=4) {
			return arr[arr.length-3] + "." + arr[arr.length-2] + "." + arr[arr.length-1];
		}
		return cutDomain;
	}
	
	private static final int N_SHORTS = 8;
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$");
		
	/**
	 * ip를 BigInteger로 형변환
	 * ip를 byte화한 후 BigInteger로 변환
	 * @param addr
	 * @return ip를 BigInteger로 형변환
	 * @throws UnknownHostException
	 */
	public static BigInteger ipToBigInteger(String addr) {
	    InetAddress ia = null;
	    byte[] bytes = {};
		try {
			ia = InetAddress.getByName(addr);
			bytes = ia.getAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	    return new BigInteger(1, bytes);
	}
	
	/**
	 * BigInteger를 ip로 형변환
	 * @param bInt
	 * @return BigInteger를 ip로 형변환
	 * @throws UnknownHostException
	 */
	public static String BigIntegerToIp(BigInteger bInt) {
		byte[] bytes = bInt.toByteArray();
		int bytesLeg = bytes.length;
		
		int ipLeg = (bInt.compareTo(new BigInteger("4294967295")) != 1) ? 4 : 16;
		byte[] setBytes = new byte[ipLeg];
		for(int i=0; i<ipLeg; i++) {
			setBytes[i] = 0;
		}
		if((bytesLeg == 5 && ipLeg == 4) || (bytesLeg == 17 && ipLeg == 16)) {
			System.arraycopy(bytes, 1, setBytes, 0, bytesLeg-1);
		} else {
			System.arraycopy(bytes, 0, setBytes, ipLeg-bytesLeg, bytesLeg);
		}
		
	    InetAddress ia = null;	    
		try {
			ia = InetAddress.getByAddress(setBytes);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return ia.toString().replaceFirst("/", "");
	}
	
	public static String BigIntegerToStandardIp(BigInteger bInt) {
		return ipToStandardIp(BigIntegerToIp(bInt));
	}

	public static boolean checkIPformat(String str) {
		if (str == null) return false;
		
		if(IPV4_PATTERN.matcher(str).matches()){
			return true;
		} else {
			if(IPV6_PATTERN.matcher(str).matches()){
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static boolean isIPv4(String ip) {
		if (ip == null) return false;
		
		if(IPV4_PATTERN.matcher(ip).matches()){
			return true;
		}
		return false;
	}
	
	public static boolean isIPv6(String ip) {
		if (ip == null) return false;
		
		if(IPV6_PATTERN.matcher(ip).matches()){
			return true;
		}
		return false;
	}
	
	/**
	 * 사설 아이피 여부 검증
	 * @param ip
	 * @return true : is private ip, false : is Not private ip.
	 */
	//**** ipv6용 수정해야함.
	public static boolean isPrivateIP(String ip) {
		BigInteger ipl = IPUtil.ipToBigInteger(ip);
		
		//ipv4용
		if(ipl.compareTo(IPUtil.ipToBigInteger("167772160")) != -1 && ipl.compareTo(IPUtil.ipToBigInteger("184549375")) != 1) return true;
		if(ipl.compareTo(IPUtil.ipToBigInteger("2886729728")) != -1 && ipl.compareTo(IPUtil.ipToBigInteger("2887778303")) != 1) return true;
		if(ipl.compareTo(IPUtil.ipToBigInteger("3232235520")) != -1 && ipl.compareTo(IPUtil.ipToBigInteger("3232301055")) != 1) return true;	

		//ipv6용
		//empty.
		
		return false;
	}
	
	/**
	 * binary를 BigInteger로 형변환
	 * @param bInt, ip형식을 구분해주는 String형 ipType
	 * @return binary를 BigInteger로 형변환
	 * @throws UnknownHostException
	 */	
	public static List<Map<String,Object>> BinaryToBigInteger(List<Map<String,Object>> list, String [] columnList) {
		List<Map<String,Object>> cgList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> data : list){
			for(int i = 0; i < columnList.length; i++){
				if(data.get(columnList[i]).getClass().getName().equals("java.math.BigInteger")) continue;
				data.put(columnList[i], new BigInteger(1, (byte[])data.get(columnList[i])));
			}
			cgList.add(data);
		}

		return cgList;
	}
	
	/**
	 * binary를 BigInteger로 형변환
	 * @param bInt, ip형식을 구분해주는 String형 ipType
	 * @return binary를 BigInteger로 형변환
	 * @throws UnknownHostException
	 */	
	public static BigInteger BinaryToBigInteger(Map<String,Object> map, String column) {
		BigInteger data = null;
		if(map.get(column).getClass().getName().equals("java.math.BigInteger")) return (BigInteger) map.get("column");
		data = new BigInteger(1, (byte[])map.get(column));
		
		return data;
	}

    /**
     * Create an IPv6 address from its String representation. For example "1234:5678:abcd:0000:9876:3210:ffff:ffff" or "2001::ff" or even
     * "::". IPv4-Mapped IPv6 addresses such as "::ffff:123.456.123.456" are also supported.
     *
     * @param string string representation
     * @return IPUtil
     */
    public static String ipToStandardIp(String ip) {
        if (ip == null) return "";
        
        if(!ip.contains(":")) return ip;
        else if(!checkIPformat(ip)) return ip;
        
        final String withoutIPv4MappedNotation = rewriteIPv4MappedNotation(ip);
        final String longNotation = expandShortNotation(withoutIPv4MappedNotation);

        final long[] longs = tryParseStringArrayIntoLongArray(ip, longNotation);
        validateLongs(longs);
        
        return StringUtil.get(mergeLongArrayIntoIPv6Address(longs));
    }
    
    private static long[] tryParseStringArrayIntoLongArray(String string, String longNotation) {
        try {
            return parseStringArrayIntoLongArray(longNotation.split(":"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("can not parse [" + string + "]");
        }
    }
    
    private static String mergeLongArrayIntoIPv6Address(long[] longs) {
        long high = 0L;
        long low = 0L;

        for (int i = 0; i < longs.length; i++) {
            if (inHighRange(i))
                high |= (longs[i] << ((4 - i - 1) * 16));
            else
                low |= (longs[i] << ((4 - i - 1) * 16));
        }

        /**
         * Returns a string representation of the IPv6 address. It will use shorthand notation and special notation for IPv4-mapped IPv6
         * addresses whenever possible.
         *
         */
        if (isIPv4Mapped(high, low))
            return toIPv4MappedAddressString(high, low);
        else
            return toShortHandNotationString(high, low);
    }
    
    private static boolean inHighRange(int shortNumber) {
        return shortNumber >= 0 && shortNumber < 4;
    }
    
    private static void validateLongs(long[] longs) {
        if (longs.length != 8)
            throw new IllegalArgumentException("an IPv6 address should contain 8 shorts [" + Arrays.toString(longs) + "]");

        for (long l : longs) {
            if (l < 0) throw new IllegalArgumentException("each element should be positive [" + Arrays.toString(longs) + "]");
            if (l > 0xFFFF) throw new IllegalArgumentException("each element should be less than 0xFFFF [" + Arrays.toString(longs) + "]");
        }
    }
    
    private static long[] parseStringArrayIntoLongArray(String[] strings) {
        final long[] longs = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            longs[i] = Long.parseLong(strings[i], 16);
        }
        return longs;
    }
    
    private static final Pattern DOT_DELIM = Pattern.compile("\\.");
    
    /**
     * Replaces a w.x.y.z substring at the end of the given string with corresponding hexadecimal notation. This is useful in case the
     * string was using IPv4-Mapped address notation.
     */
    private static String rewriteIPv4MappedNotation(String string) {
        if (!string.contains(".")) {
            return string;
        } else {
            int lastColon = string.lastIndexOf(":");
            String firstPart = string.substring(0, lastColon + 1);
            String mappedIPv4Part = string.substring(lastColon + 1);

            if (mappedIPv4Part.contains("."))
            {
                String[] dotSplits = DOT_DELIM.split(mappedIPv4Part);
                if (dotSplits.length != 4)
                    throw new IllegalArgumentException(String.format("can not parse [%s]", string));

                StringBuilder rewrittenString = new StringBuilder();
                rewrittenString.append(firstPart);
                int byteZero = Integer.parseInt(dotSplits[0]);
                int byteOne = Integer.parseInt(dotSplits[1]);
                int byteTwo = Integer.parseInt(dotSplits[2]);
                int byteThree = Integer.parseInt(dotSplits[3]);

                rewrittenString.append(String.format("%02x", byteZero));
                rewrittenString.append(String.format("%02x", byteOne));
                rewrittenString.append(":");
                rewrittenString.append(String.format("%02x", byteTwo));
                rewrittenString.append(String.format("%02x", byteThree));

                return rewrittenString.toString();
            }else{
                throw new IllegalArgumentException(String.format("can not parse [%s]", string));
            }
        }
    }
    
    private static String expandShortNotation(String string)  {
        if (!string.contains("::")) {
            return string;
        } else if (string.equals("::")) {
            return generateZeroes(8);
        } else {
            final int numberOfColons = countOccurrences(string, ':');
            if (string.startsWith("::"))
                return string.replace("::", generateZeroes((7 + 2) - numberOfColons));
            else if (string.endsWith("::"))
                return string.replace("::", ":" + generateZeroes((7 + 2) - numberOfColons));
            else
                return string.replace("::", ":" + generateZeroes((7 + 2 - 1) - numberOfColons));
        }
    }
    
    private static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle)
            {
                count++;
            }
        }
        return count;
    }
    
    private static String generateZeroes(int number) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            builder.append("0:");
        }

        return builder.toString();
    }
    
    /**
     * Returns true if the address is an IPv4-mapped IPv6 address. In these addresses, the first 80 bits are zero, the next 16 bits are one,
     * and the remaining 32 bits are the IPv4 address.
     *
     * @return true if the address is an IPv4-mapped IPv6 addresses.
     */
    private static boolean isIPv4Mapped(long highBits, long lowBits) {
        return highBits == 0 // 64 zero bits
                && (lowBits & 0xFFFF000000000000L) == 0 // 16 more zero bits
                &&
                (lowBits & 0x0000FFFF00000000L) == 0x0000FFFF00000000L; // 16 one bits and the remainder is the IPv4 address
    }
    
    private static String toIPv4MappedAddressString(long highBits, long lowBits) {
        int byteZero = (int) ((lowBits & 0x00000000FF000000L) >> 24);
        int byteOne = (int) ((lowBits & 0x0000000000FF0000L) >> 16);
        int byteTwo = (int) ((lowBits & 0x000000000000FF00L) >> 8);
        int byteThree = (int) ((lowBits & 0x00000000000000FFL));

        final StringBuilder result = new StringBuilder("::ffff:");
        result.append(byteZero).append(".").append(byteOne).append(".").append(byteTwo).append(".").append(byteThree);

        return result.toString();
    }

    private static String toShortHandNotationString(long high, long low) {
        final String[] strings = toArrayOfShortStrings(high, low);

        final StringBuilder result = new StringBuilder();

        int[] shortHandNotationPositionAndLength = startAndLengthOfLongestRunOfZeroes(high, low);
        int shortHandNotationPosition = shortHandNotationPositionAndLength[0];
        int shortHandNotationLength = shortHandNotationPositionAndLength[1];

        boolean useShortHandNotation =
                shortHandNotationLength > 1; // RFC5952 recommends not to use shorthand notation for a single zero

        for (int i = 0; i < strings.length; i++) {
            if (useShortHandNotation && i == shortHandNotationPosition)
            {
                if (i == 0)
                    result.append("::");
                else
                    result.append(":");
            }
            else if (!(i > shortHandNotationPosition && i < shortHandNotationPosition + shortHandNotationLength))
            {
                result.append(strings[i]);
                if (i < N_SHORTS - 1)
                    result.append(":");
            }
        }

        return result.toString().toLowerCase();
    }
	
    private static String[] toArrayOfShortStrings(long high, long low) {
        final short[] shorts = toShortArray(high, low);
        final String[] strings = new String[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            strings[i] = String.format("%x", shorts[i]);
        }
        return strings;
    }
    
    private static int[] startAndLengthOfLongestRunOfZeroes(long high, long low) {
        int longestConsecutiveZeroes = 0;
        int longestConsecutiveZeroesPos = -1;
        short[] shorts = toShortArray(high, low);
        for (int pos = 0; pos < shorts.length; pos++) {
            int consecutiveZeroesAtCurrentPos = countConsecutiveZeroes(shorts, pos);
            if (consecutiveZeroesAtCurrentPos > longestConsecutiveZeroes)
            {
                longestConsecutiveZeroes = consecutiveZeroesAtCurrentPos;
                longestConsecutiveZeroesPos = pos;
            }
        }

        return new int[]{longestConsecutiveZeroesPos, longestConsecutiveZeroes};
    }
    
    private static int countConsecutiveZeroes(short[] shorts, int offset) {
        int count = 0;
        for (int i = offset; i < shorts.length && shorts[i] == 0; i++) {
            count++;
        }

        return count;
    }
    
    private static short[] toShortArray(long high, long low) {
        final short[] shorts = new short[N_SHORTS];

        for (int i = 0; i < N_SHORTS; i++) {
            if (inHighRange(i))
                shorts[i] = (short) (((high << i * 16) >>> 16 * (N_SHORTS - 1)) & 0xFFFF);
            else
                shorts[i] = (short) (((low << i * 16) >>> 16 * (N_SHORTS - 1)) & 0xFFFF);
        }

        return shorts;
    }
	
	public static void main(String[] args) {
		//System.out.println(">>> Long=["+IPUtil.ip2long("152.99.78.4")+"], IP=["+IPUtil.long2ip(Long.parseLong("2556644869"))+"]");
//		String ip = ipToStandardIp("fe80::ec4:7aff:fe32:878");
		String ip = ipToStandardIp("2041:0000:130F:0000:0000:07C0:853A:140B");
		System.out.println(">>> ip=>" + ip);
//		BigInteger bint = ipToBigInteger(ip);
//		System.out.println(">>> bint=>" + bint);
		BigInteger bint = new BigInteger("42872795166820420802676092457212056587");
		String ips = BigIntegerToStandardIp(bint);
		System.out.println(">>> ips=>" + ips);
		
		byte[] byteVal = bint.toByteArray();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sip", byteVal);
		list.add(map);
		
		List<Map<String,Object>> list2 = BinaryToBigInteger(list, new String[]{"sip"});
		for(Map<String,Object> m:list2) {
			System.out.println("===================>" + IPUtil.BigIntegerToStandardIp((BigInteger)m.get("sip")));
		}
	}
}

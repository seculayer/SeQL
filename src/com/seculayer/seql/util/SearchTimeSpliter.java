package com.seculayer.seql.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchTimeSpliter {
	public static List<Map<String, String>> makeSearchList(String start_date, String end_date, int interval) throws Exception {
		List<Map<String, String>> schList = new ArrayList<Map<String, String>>();
		
		try {
			Map<String, String> param = null;
			if(start_date==null && end_date==null) {
				param = new HashMap<String, String>();
				param.put("sdt", null);
				param.put("ddt", null);
				param.put("pSearch", "false");
				schList.add(param);
				return schList;
			}
			
			String last_ddt = null;
			String sdt   = null;
			String ddt   = null;
			
			String sec = end_date.substring(12);
			if(sec.equals("59")) {
				end_date = DateTime.addTimes(end_date, 1, "yyyyMMddHHmmss");
			}
			String min_sec = end_date.substring(10);
			if(!min_sec.equals("0000")) {
				last_ddt = end_date;
				if(end_date.substring(12).equals("00")) {
					ddt = DateTime.addTimes(last_ddt, -1, "yyyyMMddHHmmss");
				} else ddt = last_ddt;
				sdt = ddt.substring(0, 10) + "0000";
				if(Long.parseLong(sdt) < Long.parseLong(start_date)) {
					sdt = start_date;
				}
				//System.out.println("> sdt="+sdt+", ddt=" + ddt);
				
				param = new HashMap<String, String>();
				param.put("sdt", sdt);
				param.put("ddt", ddt);
				param.put("pSearch", "false");
				schList.add(param);
			}
			
			boolean isWorking = true;
			while(isWorking) {
				last_ddt = (last_ddt==null?end_date:sdt);
				ddt = DateTime.addTimes(last_ddt, -1, "yyyyMMddHHmmss");
				sdt = DateTime.addTimes(last_ddt, -1*interval*60*60, "yyyyMMddHHmmss");
				if(Long.parseLong(sdt) <= Long.parseLong(start_date)) {
					isWorking = false;
					sdt = start_date;
				}
				if(Long.parseLong(ddt) < Long.parseLong(start_date)) continue;
				//logger.debug("> sdt="+sdt+", ddt=" + ddt);
				
				param = new HashMap<String, String>();
				param.put("sdt", sdt);
				param.put("ddt", ddt);
				param.put("pSearch", String.valueOf(sdt.substring(10).equals("0000")));
				
				schList.add(param);
			}
		} catch(Exception e) {
			throw e;
		}
		return schList;
	}
	
	public static List<Map<String, String>> makeSearchListDay(String start_date, String end_date, int interval) throws Exception {
		List<Map<String, String>> schList = new ArrayList<Map<String, String>>();
		
		try {
			Map<String, String> param = null;
			if(start_date==null && end_date==null) {
				param = new HashMap<String, String>();
				param.put("sdt", null);
				param.put("ddt", null);
				param.put("pSearch", "false");
				schList.add(param);
				return schList;
			}
			
			String last_ddt = null;
			String sdt   = null;
			String ddt   = null;
			
			String sec = end_date.substring(10);
			if(sec.equals("5959")) {
				end_date = DateTime.addTimes(end_date, 1, "yyyyMMddHHmmss");
			}
			String min_sec = end_date.substring(8);
			if(!min_sec.equals("000000")) {
				last_ddt = end_date;
				if(end_date.substring(10).equals("0000")) {
					ddt = DateTime.addTimes(last_ddt, -1, "yyyyMMddHHmmss");
				} else ddt = last_ddt;
				sdt = ddt.substring(0, 8) + "000000";
				if(Long.parseLong(sdt) < Long.parseLong(start_date)) {
					sdt = start_date;
				}
				//System.out.println("> sdt="+sdt+", ddt=" + ddt);
				
				param = new HashMap<String, String>();
				param.put("sdt", sdt);
				param.put("ddt", ddt);
				param.put("pSearch", "false");
				schList.add(param);
			}
			
			boolean isWorking = true;
			while(isWorking) {
				last_ddt = (last_ddt==null?end_date:sdt);
				ddt = DateTime.addTimes(last_ddt, -1, "yyyyMMddHHmmss");
				sdt = DateTime.addTimes(last_ddt, -1*interval*60*60*24, "yyyyMMddHHmmss");
				if(Long.parseLong(sdt) <= Long.parseLong(start_date)) {
					isWorking = false;
					sdt = start_date;
				}
				if(Long.parseLong(ddt) < Long.parseLong(start_date)) continue;
				//logger.debug("> sdt="+sdt+", ddt=" + ddt);
				
				param = new HashMap<String, String>();
				param.put("sdt", sdt);
				param.put("ddt", ddt);
				param.put("pSearch", String.valueOf(sdt.substring(10).equals("0000")));
				
				schList.add(param);
			}
		} catch(Exception e) {
			throw e;
		}
		return schList;
	}
	
	public static void main(String[] args) {
		try {
			//Hour
			List<Map<String, String>> list = makeSearchList("20150410081000", "20150416095959", 3);
			//Day
			list = makeSearchListDay("20150410081000", "20150416095959", 1);
			
			for(Map<String, String> m:list) {
				System.out.println("> sdt="+m.get("sdt")+", ddt=" + m.get("ddt")+", pSearch=" + m.get("pSearch"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.seculayer.seql.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public final class KIDGenerator {
	
	private static volatile KIDGenerator instance = new KIDGenerator();

	private static String fmt = "yyyyMMddHHmmss";
	private static SimpleDateFormat sfmt = new SimpleDateFormat(fmt);
	private static String currSecs = sfmt.format(new Date());
	private static AtomicLong seq = new AtomicLong(0);

    public KIDGenerator() {
		//currTimeMillis = System.currentTimeMillis();
    }
    
    public static KIDGenerator getInstance(){
    	return instance;
    }

    public synchronized String makeKey(String CollectorUID) {
    	StringBuffer buffer = new StringBuffer();
    	String tempTimeSecs = sfmt.format(new Date());
		if( currSecs.equals(tempTimeSecs) ) 
			seq.addAndGet(1);
		else {
			currSecs = tempTimeSecs;
			seq.set(1);
		}
		buffer.append(CollectorUID);
		buffer.append(tempTimeSecs);
		writeZero(buffer, (int)seq.get(), 5);
        return buffer.toString();
    }
    
    public void writeZero(StringBuffer buffer, int value, int len) {
    	String subject = String.valueOf(value);
        for (int i = 0; i < (len - subject.length()); i++) {
            buffer.append("0");
        }
        buffer.append(subject);
    }
  
    public static void main(String args[]) {
    	long start = System.currentTimeMillis();
    	for(int i=0; i < 20000; i++) {
    		System.out.println(i + "="+KIDGenerator.getInstance().makeKey(""));
    	}
    	long end = System.currentTimeMillis();
    	System.out.println("dif : " + (end-start));
    	//System.out.println("..."+ UUID.randomUUID().toString());
    }
}

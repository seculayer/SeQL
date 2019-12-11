package com.seculayer.seql.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import org.apache.http.conn.HttpHostConnectException;

public class HttpClientAdapter {

	public final static String HTTP_METHOD_POST = "POST";
	public final static String HTTP_METHOD_GET = "GET";

	private int socketTimeout = 10000;
	private int connectTimeout = 10000;
	private int BUFFER_SIZE=1024;
	private String url;
	private String method = HTTP_METHOD_GET;

	public HttpClientAdapter(int timeout) {
		this.socketTimeout = timeout*1000;
		this.connectTimeout = timeout*1000;
	}
	
	public HttpClientAdapter(int connectTimeout, int socketTimeout) {
		this.socketTimeout = socketTimeout*1000;
		this.connectTimeout = connectTimeout*1000;
	}
	
	public HttpClientAdapter(String strUrl) {
		this.url = strUrl;
	}	
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	private SSLContext getSSLContext() {
		SSLContext sc = null;

        try {
        	TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
        		public X509Certificate[] getAcceptedIssuers() {
        			return new X509Certificate[] {};
        		}

				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					//System.out.println(">>> Check Client Certification!");
					
				}

				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					//System.out.println(">>> Check Server Certification!");
					
				}
        	} };
        	
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustManager, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sc;
    }
    
    final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
	
	public String execute(String strMethod) throws Exception {
		this.method = strMethod;
		return HttpURLConnection(url);
	}
	
	public void close() {}
	
	public String HttpURLConnection(String url) throws Exception {
		HttpURLConnection conn = null;
		HttpsURLConnection httpsConn = null;
		BufferedReader br  = null;
		StringBuilder builder = null;
		String[] urlParams = null;
		try {
			//HttpURLConnection.setFollowRedirects(false);
			if(this.method == HTTP_METHOD_POST) {
				urlParams = url.split("\\?");
				url = urlParams[0];
			}
			
			URL svrURL = new URL(url);
			String charSet = "UTF-8";
			
			if (svrURL.getProtocol().toLowerCase().equals("https")) {
				SSLContext ctx = getSSLContext();
				HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
				
				httpsConn = (HttpsURLConnection) svrURL.openConnection();
				httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
				httpsConn.setRequestMethod(this.method);
				httpsConn.setDoOutput(true);
				httpsConn.setConnectTimeout(connectTimeout);
				httpsConn.setReadTimeout(socketTimeout);
				if(urlParams != null && urlParams.length > 1) {
					httpsConn.getOutputStream().write(urlParams[1].getBytes(charSet));
				}
				httpsConn.connect(); 
				
				//charSet = getCharSet(url, httpsConn.getHeaderField("Content-Type"));
				br  = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), charSet));
            } else {
				conn = (HttpURLConnection) svrURL.openConnection();
				conn.setRequestMethod(this.method);
				conn.setDoOutput(true);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(socketTimeout);
				if(urlParams != null && urlParams.length > 1) {
					conn.getOutputStream().write(urlParams[1].getBytes(charSet));
				}
				conn.connect();
				
				//charSet = getCharSet(url, conn.getHeaderField("Content-Type"));
				br  = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
            }			
			
			builder = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null)
			{
				builder.append(line + "\n");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {  if(br!=null) br.close(); } catch(Exception e) {}
			if(conn!=null) {
				conn.getInputStream().close();
				conn.disconnect();
			}
			if(httpsConn!=null) {
				httpsConn.getInputStream().close();
				httpsConn.disconnect();
			}
		}
		return builder==null?"":builder.toString();
	}
	
	String getCharset(String contentType) {
		if (contentType == null) return "UTF-8";
		
		String charset = "";
		String[] values = contentType.split(";");
		for (String value : values) {
		    value = value.trim();

		    if (value.toLowerCase().startsWith("charset=")) {
		        charset = value.substring("charset=".length());
		    }
		}

		if ("".equals(charset)) {
		    charset = "UTF-8";
		}
		return charset;
	}
	
//	public String getCharSet(String url, String contentType) {
//		HttpURLConnection conn = null;
//		HttpsURLConnection httpsConn = null;
//		BufferedReader br  = null;
//		String charSet = "euc-kr";
//		
//		try {
//			//1. Header정보에서 CharSet 찾으면 리턴
//			String regex = "charset[\t\\s]?=[\t\\s]?([a-zA-Z0-9-_]{5,20})";
//			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//			Matcher m = p.matcher(contentType);
//			if(m.find()) {
//				charSet = m.group(1);
//				return charSet;
//			}
//			
//			//2. HTML내에서 CharSet 찾기
//			URL svrURL = new URL(url); 
//			if (svrURL.getProtocol().toLowerCase().equals("https")) {
//				SSLContext ctx = getSSLContext();
//				HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
//				
//				httpsConn = (HttpsURLConnection) svrURL.openConnection();
//				httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
//				httpsConn.setRequestMethod("GET");
//				httpsConn.setDoOutput(true);
//				httpsConn.setConnectTimeout(socketTimeout);
//				httpsConn.setReadTimeout(socketTimeout);
//				httpsConn.connect(); 
//				
//				br  = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
//            } else {
//            	conn = (HttpURLConnection) svrURL.openConnection();
//            	conn.setRequestMethod("GET");
//            	conn.setDoOutput(true);
//            	conn.setConnectTimeout(socketTimeout);
//            	conn.setReadTimeout(socketTimeout);
//            	conn.connect();
//            	
//            	br  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            }
//			
//			String line = null;
//			while ((line = br.readLine()) != null)
//			{
//				if(line.indexOf("charset") > 0) {
//					m = p.matcher(line);
//					if(m.find()) {
//						charSet = m.group(1);
//						break;
//					}
//				}
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		} finally {
//			try { 
//				if(br!=null) br.close();
//			} catch(Exception e) {}
//			if(conn!=null) conn.disconnect();
//			if(httpsConn!=null) httpsConn.disconnect();
//		}
//		return charSet;
//	}

	public String getDomainTail(String url){
		String result = null;
		//System.out.println(">>> IN URL : " + url);
		if(url.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*")){
			return "";
		}

		result = url.replaceAll("(?s)^http://|/.*$", "");		
		//System.out.println(">>> OUT URL : " + result);

		return result;
	}

	public String checkCookie(String html){
		String result = "";
		
		String[] arr = html.split("\\s+");
		for(int i = 0; i < arr.length; i++) {
			if(arr[i].indexOf("_accessKey2=") > -1) {				
				arr[i] = arr[i].replace("_accessKey2=", "");
				arr[i] = arr[i].replace("'", "");
				//System.out.println(arr[i]);
				result = arr[i];
				return result;
			}
		}
		return "";
		
	}
	
	//Use TLSv1.2
	//-Djavax.net.debug=ssl,handshake,record -Dhttps.protocols=TLSv1.2
	public static void main(String[] args) throws Exception {
		String url = "https://192.168.10.100:8443/ParserHandlerServlet?action_code=-1";
		url = "http://192.168.1.51:9092/select?q=eqp_dt%3A%5B20160217092700+TO+20160217092659%5D+AND+src_country_name%3A*+AND+eqp_ip%3A192.168.1.24+AND+sim_event_id%3A2016021709280700002&rows=2000&start=0&action=event";
		url = "http://www.daum.net";
		url = "http://118.193.197.115";
		String query = "CREATE TABLE TEMP_DATA_1 AS INDEX.RAW[ eqp_dt:[#{sdt} TO #{sdt}] AND  attack_nm:* AND prtc:(TCP UDP) | GROUP BY src_ip | LIMIT 1000 ];SHOW TABLES;";
		url = "http://localhost:9103/SearchServer?q="+URLEncoder.encode(query, "UTF-8")+"&action=script&sdt=20160217092700&ddt=20160217122700";
		query = "attack_nm:*";
		url = "http://192.168.1.51:9103/SearchServer?q="+URLEncoder.encode(query, "UTF-8")+"&action=count&sdt=20180115220000&ddt=20180115220500&start=0&rows=0";
		HttpClientAdapter adapter = new HttpClientAdapter(30);
		
		String newHtml = null;
		try {
			newHtml = adapter.HttpURLConnection(url);
			
			System.out.println("[HTML]----------------------------------------------------------------");
			System.out.println(newHtml);
			System.out.println("----------------------------------------------------------------------");

		} catch (Exception he) {
			he.printStackTrace();
			if (he instanceof SocketTimeoutException) {
				System.out.println(">>> Delay, SocketTimeoutException");
//			} else if (he instanceof HttpHostConnectException) {
//				System.out.println(">>> Cut-Off, HttpHostConnectException");
			} else if (he instanceof UnknownHostException) {
				System.out.println(">>> Cut-Off, UnknownHostException");
			} else {
				System.out.println(">>> Error, Unknown Exception!");
			}
		}
	}
	
}

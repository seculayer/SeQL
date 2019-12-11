package com.seculayer.seql;

import java.util.regex.Pattern;

public class Constants {
	
	public static final String DEFAULT_CHARSET  = "UTF-8";
	public static final String SHA_256 = "SHA-256";	
	public static final String SHA_1 = "SHA-1";	
	public static final String NUMERIC = "-?[0-9.]+";
	public static final String BOOLEAN = "true|false";
	public static final String NULL = "null";
	public static final int MAX_JOIN_VALUE_CNT = 1000;
	
	public static final Pattern PAT_FILE = Pattern.compile("FILE\\.([0-9a-zA-Z_]+)[\\s\\t]*\\[(.+)\\]", Pattern.DOTALL);
	public static final Pattern PAT_RDB  = Pattern.compile("RDB\\.([0-9a-zA-Z_]+)\\[(.+)\\]", Pattern.DOTALL);
	public static final Pattern PAT_TABLE = Pattern.compile("TABLE\\.LOCAL\\[(.+)\\]", Pattern.DOTALL);
	public static final Pattern PAT_JSON = Pattern.compile("JSON\\s+FILE\\s*['\"](.+)['\"]", Pattern.DOTALL);
	public static final Pattern PAT_SAVE_FILE = Pattern.compile("(TOJSON|TOCSV|TOFILE)\\s+\"([^\"]+)\"(?:\\s+SEPARATOR=\"([^\"]+)\")?(?:\\s+HEADER=(true|TRUE|false|FALSE))?", Pattern.DOTALL);
	public static final Pattern PAT_SAVE_DB1 = Pattern.compile("TODB\\s+dbId\\s*=\\s*`?([a-zA-Z0-9_]+)`?\\s+tableName\\s*=\\s*([a-zA-Z0-9_]+)", Pattern.DOTALL);
	public static final Pattern PAT_SAVE_DB2 = Pattern.compile("TODB\\s+dbId\\s*=\\s*`?([a-zA-Z0-9_]+)`?\\s+queryStr\\s*=\\s*\\[(.+)\\]", Pattern.DOTALL);
	public static final Pattern PAT_SAVE_TABLE = Pattern.compile("TOTABLE\\s+tableName\\s*=\\s*([a-zA-Z0-9_]+)\\s+REWRITE\\s*=\\s*(true|TRUE|false|FALSE)", Pattern.DOTALL);
	public static final Pattern PAT_DB_FIELD = Pattern.compile("@\\{([a-zA-Z0-9_]+)\\}", Pattern.DOTALL);
	
	public static final String FIELD_SEPARATOR  = " ";
	public static final String KEY_SEPARATOR    = "^";
	public static final String ESCAPE_CHAR      = "~";
	public static final String KEY_SPLIT_SEPARATOR = "\\^";
	public static final String FIELD_NULL_STR   = "(null)";
	public static final String REPLACE_NULL_STR = "-";
	
	public static final String PIPE_AND_SPACE = "| ";
	public static final String SPACE = " ";
}

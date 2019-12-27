# 1. Overview
<img src="https://user-images.githubusercontent.com/58262527/70760960-ccfe4980-1d8e-11ea-92d0-5fce99b6bdef.png" width="70%">

SeQL, abbreviated from Seculayer Query Language, is a query language developed by SecuLayer’s R&D center.
SeQL supports search for heterogeneous data such as file, RDB and search data, and union and join analysis for homogeneous and heterogenous data. In addition, it has about 220 built-in functions including but not limited to characters, numbers, dates, logics, arrays and sets for data manipulation and supports various calculation functions. 



# 2. Understanding SeQL
SeQL is installed and executed in ShovelServer process in the Analyzer of [eyeCloudSIM](http://www.seculayer.co.kr/en/?c=117) as shown in the architecture below. Basically, SeQL can search the data stored in eyeCloudSIM and can search the internal and external RDB and file data.

<img src="https://user-images.githubusercontent.com/58262527/70762374-a858a080-1d93-11ea-87ff-0cb571760186.png" width="70%">

SeQL consists of the structure of Data Search -> Data Processing-> Data Output Printing. 

<img src="https://user-images.githubusercontent.com/58262527/70762715-fa4df600-1d94-11ea-9b8d-84549d2b7abf.png" width="70%">

Each processing steps can be connected using | (pipe), and executed in order. SeQL’s use of | (pipe), is based on Linux command which is one of the core features. 

An analogy of liquid flowing in a single pipe is useful to understand the process of input, execution and output of commands. Output value is printed as shown below. 
  
Multiple pipes can be connected as shown below. Instead of printing the output value from first command, let it flow to the second pipe via connector, then it becomes the input value of the second pipe. 

<img src="https://user-images.githubusercontent.com/58262527/70762770-35502980-1d95-11ea-9b96-eef0e2addb7a.png" width="70%">
 
Various functions for data manipulation can be performed by using | (pipe), which enables to connect to the subordinate conditions. 
When an end-user requests a SeQL query, it operates in the form of query parsing -> query structure -> recursive processing as shown below.

<img src="https://user-images.githubusercontent.com/58262527/70762837-71838a00-1d95-11ea-837b-d1f28f577e64.png" width="90%">
 
 

# 3. SeQL Basic Grammar
SeQL consists of the structure of Data Search -> Data Processing-> Data Output Printing. Data search is performed from eyeCloudSIM search engine data, DB data, file data and more. 

### 3.1  Data Search
#### 1)	Data search from search engine
Search engine data can be retrieved using LuceneQL, the search language of the eyeCloudSIM search engine.
 
#### [ Grammar and example ]
<pre><code>prtc:TCP AND (dstn_port:80 OR dstn_port:90) AND attack_nm:*
| LAST 5 MINUTE
| STORAGE FROM RAW
| FIELDS `key`, src_ip, dstn_ip, prtc 
| LIMIT 0 100</code></pre>

#### [ Description ]
<pre><code>- prtc:TCP AND (dstn_port:80 OR dstn_port:90) AND attack_nm:*: LuceneQL search condition
- LAST n [SECOND/MINUTE/HOUR/DAY/MONTH/YEAR] : Query of the latest target time at the current time.
- STORAGE FROM : Set a storage to search from.
- FIELDS : Set fields to search from. List the fields using , (comma).
- LIMIT n1 n2 : Set offset of data to pull.</pre></code> 

#### 2)	Data search from RDB source
SeQL retrieves data from local and remote RDBs based on JDBC.

#### [ Grammar and example ]
<pre><code>RDB.DSN_01[
	SQL Query
]</code></pre>

#### [ Description ]
<pre><code>- RDB : Prefix for an RDB data source
- DSN_01 : ID of the RDB data source
- SQL Query : Database’s SQL query</code></pre>

The ID of the RDB data source can be retrieved from the icon, “get data source”  from the QueryBrowser window as shown below.

<img src="https://user-images.githubusercontent.com/58262527/70769745-114d1200-1dae-11ea-8ec8-3c8ec954e6f9.png" width="90%">
 
To register a data source, click the "Add" button in the "get data source" popup window to register the JDBC driver information.

#### 3)	File data source
File data registered on eyeCloudSIM is retrieved for data search. 

#### [ Grammar and example ]
<pre><code>FILE.file_01[
	*:*
]</code></pre>

#### [ Description ]
<pre><code>- FILE : Prefix for the File data source
- file_01 : ID of the File data source
- *:* : Reserved word for entire file contents</code></pre>

The ID of the File data source can be retrieved from the icon, “get file data”  from the QueryBrowser window as shown below.

<img src="https://user-images.githubusercontent.com/58262527/70769871-84ef1f00-1dae-11ea-9c17-9618cc2fb971.png" width="90%">

To register a file data source, use the "Add" button in the "get file data" popup., then register local files. JSON, CSV and more file formats are supported. 

#### 4)	JSON File Data Search
JSON file that exists on the server can be loaded on SeQL. 

#### [ Grammar and example ]
<pre><code>JSON FILE '/CloudESM/data/test.json'</code></pre>

#### [ Description ]
<pre><code>- JSON FILE : Prefix for JSON File
- /CloudESM/data/test.json : Absolute route of JSON File</code></pre>

### 3.2  Data Processing
The data processing step generates statistics on the retrieved data, or performs data conversion, filtering, and sorting.

#### [ Grammar and example ]
<pre><code>| STATS COUNT(*) AS cnt, DC(`key`) AS dip_cnt  BY src_ip, prtc
| WHERE cnt >= 1 AND prtc IN ('TCP', 'UDP')
| CONVERT src_ip AS "SOURCE IP", SUBSTR(prtc, 0, 1) AS "PROTOCOL", 
  LENGTH(src_ip) AS len, CONCAT(src_ip, '/', prtc, '/', 'cnt') AS concat 
| SORT cnt DESC</code></pre>
	
#### [ Description ]
<pre><code>- STATS [AGGREGATE_FUNCTION] BY [GROUP_FIELD] : Statistics statement for Group By. Aggregate functions such as COUNT, MAX, MIN, SUM, AVG, DC, VALUES and GROUP_CONTACT can be used.
- WHERE : A reserved word for filtering statement. Comparison operators such as >, >=, !=, =, <, <=, IS NULL, IS NOT NULL, LIKE and NOT LIKE can be used. 
- CONVERT : A reserved word for data conversion statement. Built-in functions such as numbers, characters, logic, dates and data type can be used. 
- SORT : A reserved word for sorting statement, such as ASC(ascending order) and DESC(descending order).</code></pre>

### 3.3  Data Output Printing
Data Output Printing after Data Search and Processing are conducted. 

#### [ Grammar and example ]
<pre><code>| PRINT seq, "SOURCE IP", len, cnt
| TO JSON “/CloudESM/data/test.json”
| TO CSV “/CloudESM/data/test.csv”</code></pre>

#### [ Description ]
<pre><code>- PRINT : A reserved word for fields to print. List the fields using comma, “,”.
- TO JSON : A reserved word for saving the query result as a JSON file. 
- TO CSV : A reserved word for saving the query result as a CSV file.</code></pre>

### 3.4  Other Rules 
SeQL consists of the structure of Data Search -> Data Processing-> Data Output Printing. 
SeQL commands can be combined and connected using the separator, | (pipe), and executed in order. 
- SeQL is case sensitive. Statement and function names must use capital letters. (For e.g., statements – STATS~BY, WHERE, CONVERT, SORT, LIMIT, HEAD, TAIL, TOP, BOTTOM, ETC and more and Function – MAX, MIN, CONTACT, DATE_FORMAT, ETC and more)  
- Commands for data processing such as STATS, WHERE, CONVERT, SORT, LIMIT, HEAD, TAIL and others can be used repetitively. 
- Aggregate functions such as COUNT, MAX, MIN, SUM, AVG, DC, VALUES and GROUP_CONTACT can be used for STATS statement in a form of aggregate function(string function()), number function(aggregate function()) and such.
- Approximately 220 built-in functions such as number, string, logic, date and data type can be used for CONVERT statement in a form of function(function()).
- Operators such as >, >=, !=, =, <, <=, IS NULL, IS NOT NULL, LIKE and NOT LIKE are available.
- For WHERE statement, AND, OR, NOT-IN, and grouping using () (brackets) are available. 
- Specific field value can be applied on CONVERT statement. For e.g., CONVERT 'AAA' AS data_type, 'BBB' AS log_type 
- JOIN functions such as Inner Join, [Left|Right|Full] Outer Join, Union [ALL], are available among dataset. 
- Statement such as SORT, LIMIT, TOP, BOTTOM, HEAD and TAIL are available for data processing.
- Search query for LAST time statement is available. For e.g., LAST n SECOND / MINUTE / HOUR / DAY / MONTH / YEAR. 
- Correlation analysis is available using IN filter result in the search condition. 
- In case that field name is the same as a reserved word, wrap the field name around ` (apostrophe).
- Java methods can be called for CONVERT statement. For e.g., to print the result by calling a method of the Java class, JAVA_METHOD(class, method[, arg1[, arg2 ...]]).
- Operations using EVAL function for CONVERT statement is available. For e.g., EVAL(1+2), EVAL(in_pkt_cnt*8), EVAL(in_pkt_cnt*cnt), EVAL((src_ip*2.0)/100) .
- Comments can be left and will be ignored when running. For e.g., # - line comment, /*comment*/ - section or multi-line comment.
- Line counting available using the statement of STATS COUNT(*) AS cnt BY ALL.
- JOIN analysis among heterogeneous data sources is available. For e.g., File and DB, File and Search, DB and Search, and etc…)



# 4. SeQL Usage Example
### 4.1  Basic Example
The table below shows the basic example of data search, processing and output printing.

#### [ Grammar and example ]
<pre><code>#---------------------------------------
# 1) Data Search
#---------------------------------------
eqp_dt:[20190822150000 TO 20190822153000] 
AND prtc:TCP
# Set a storage to search from
| STORAGE FROM RAW
# Set the output field to print results
| FIELDS `key`, src_ip, dstn_ip, prtc 
| LIMIT 0 100

#---------------------------------------
# 2) Data Processing 
#---------------------------------------
# Aggregate by src_ip and prtc 
| STATS COUNT(*) AS cnt, SUM(in_packet) AS in_pkts, 
        AVG(COUNT) AS "average", DC(`key`) AS dip_cnt  BY src_ip, prtc
# Apply Filter
| WHERE cnt >= 1 
  AND prtc IN ('TCP', 'UDP')
| WHERE dip_cnt >= 1
# Apply Alias and data conversion using built-in function
| CONVERT src_ip AS "source ip", SUBSTR(prtc, 0, 1) AS "protocol", 
          LENGTH(src_ip) AS len, CONCAT(src_ip, '/', prtc, '/', 'cnt') AS concat 
# Sort by descending order of cnt
| SORT cnt DESC
# Sequential numbers are applied by order
| CONVERT SEQ() AS seq 

#---------------------------------------
# 3) Data output printing
#---------------------------------------
# Set fields to print
| PRINT seq, "source ip", len, cnt
# Print 10 from the top in order
| HEAD 10</code></pre>

### 4.2  Index Data Search
The table below shows an example to search index data stored in eyeCloudSIM.

#### [ Grammar and example ]
<pre><code>#-----------------------
# 1. Search for certain dates
#-----------------------
eqp_dt:[20190822150000 TO 20190822153000] 
AND prtc:TCP AND (dstn_port:80 OR dstn_port:90) AND attack_nm:* 
# Choose storage (Raw storage is default if not chosen)
| STORAGE FROM RAW 
# Set fields to search (If not set, all fields are set for search which is impacting the search performance)
| FIELDS src_ip, dstn_ip, prtc 
| LIMIT 0 100;

#-----------------------
# 2. Recent Time Search
#-----------------------
prtc:TCP AND (dstn_port:80 OR dstn_port:90) AND attack_nm:* 
# Search for last 5 minutes
| LAST 5 MINUTE
| STORAGE FROM RAW 
| FIELDS src_ip, dstn_ip, prtc
| LIMIT 0 100;

#-----------------------
# 3. Search Groups
#-----------------------
eqp_dt:[20190822150000 TO 20190822153000] 
AND prtc:TCP AND attack_nm:*
# Choose storage (Raw storage is default if not chosen)
| STORAGE FROM RAW 
# Set group field (Place field with less distinctive field for faster processing.) 
| GROUP BY prtc, dstn_ip
| TOP 10</code></pre>

### 4.3  Query Browser Comments
Query Browser supports both line, multi-line and section comments, as shown below examples.

#### [ Grammar and example ]
<pre><code># Comment: Use # for commenting lines, and /**/ for multi-lines and specific section 
# Search
eqp_dt:[20190822150000 TO 20190822153000] 
AND prtc:TCP AND (dstn_port:80 OR dstn_port:90) AND attack_nm:* 
# Example of commenting a specific section
| STORAGE FROM RAW /*Set storage to search from*/
# Example of commenting a line
# | STORAGE FROM RAW
# Example of commenting multiple lines
/*
| FIELDS src_ip, dstn_ip, prtc 
| LIMIT 0 100;
*/
| FIELDS src_ip, dstn_ip, prtc 
| LIMIT 0 10</code></pre>

### 4.4  Sorting and Cutting Data
The table below shows examples of SORT for sorting data, TOP/BOTTOM and HEAD/TAIL for cutting data, and LIMIT to bring data to where desired. 
Basically, both TOP/BOTTOM and HEAD/TAIL are used to cut data from the beginning or the end, yet the purpose varies. TOP/BOTTOM is used to cut the data from eyeCloudSIM index data in search statements and HEAD/TAIL is used to cut the data while processing the data. 

#### [ Grammar and example ]
<pre><code>eqp_dt:[20190722160000 TO 20190722163000] 
AND prtc:TCP
| SEARCH FROM RAW STORAGE
| GROUP BY prtc, src_ip
# Using TOP/BOTTOM on eyeCloudSIM index search statement
| TOP 1000 
| STATS COUNT(*) AS cnt, SUM(total) AS total BY prtc, src_ip
| WHERE cnt>=1 AND prtc IN ('TCP', 'UDP')
| CONVERT src_ip AS "source ip", SUBSTR(prtc, 0, 1) AS "protocol", LENGTH(src_ip) AS len, CONCAT(src_ip, '/', prtc, '/', 'cnt') AS concat 
# 1) SORT Example
| SORT len DESC, "source ip" ASC
# 2) Using HEAD N, TAIL N for data processing statement
| HEAD 10
| TAIL 5
# 3) LIMIT Example
| LIMIT 0 3
| PRINT "source ip", len, concat</code></pre>

### 4.5  Search Filter
Below shows an example of search filter for correlation analysis when searching for eyeCloudSIM index data. It is following the form of | SEARCH src_ip IN [SeQL].
SeQL’s search result value is converted to the form of src_ip:(value1 value2 ...), then append this to the upper search condition. The max count for appending is 1000.

#### [ Grammar and example ]
<pre><code>eqp_dt:[20190822150000 TO 20190822153000] 
AND log_type:1
| STORAGE FROM RAW 
# Example of using search filter
| SEARCH src_ip IN [
		log_type:2 AND attack_nm:*
		| LAST 5 MINUTE
		| GROUP BY src_ip
		| TOP 10 
		| PRINT src_ip
	] 
| FIELDS eqp_dt, src_ip, dstn_ip, prtc 
| LIMIT 0 100</code></pre>

### 4.6  Filter Join 
The table below shows an example of filtering using IN/NOT-IN statement while data processing. 

#### [ Grammar and example ]
<pre><code>eqp_dt:[20190822150000 TO 20190822153000] 
AND prtc:TCP AND (dstn_port:80 OR dstn_port:90) AND attack_nm:* 
| STORAGE FROM RAW 
| FIELDS src_ip, dstn_ip, prtc 
| LIMIT 0 1000 
| STATS COUNT(*) AS cnt, SUM(in_packet) AS in_pkts, 
        AVG(COUNT) AS "hangul", DC(dstn_ip) AS dip_cnt  BY src_ip, prtc
| WHERE cnt>=1 
AND prtc IN ('TCP', 'UDP')
# Example of using IN filter
  AND (src_ip IN [ 
		eqp_dt:[20190822150000 TO 20190822153000] 
		AND prtc:TCP AND attack_nm:*
		| GROUP BY prtc, dstn_ip
		| TOP 100 
		| STATS COUNT(*) AS cnt, SUM(NO) AS nosum, AVG(NO) AS "average" BY src_ip, prtc 
		| WHERE src_ip='211.8.100.6' 
		| PRINT dstn_ip
	] OR cnt = 2)
  AND prtc IN ('TCP','UDP') 
  AND (cnt >= 0 AND (cnt=1 OR cnt=2 OR cnt=3)) 
| CONVERT src_ip AS "source ip", SUBSTR(prtc, 0, 1) AS "protocol", LENGTH(src_ip) AS len, 
          CONCAT(src_ip, '/', prtc, '/', 'cnt') AS concat 
| PRINT "source ip", len, concat</code></pre>

### 4.7  Inner Join
The table below shows an example of Inner Join where overlapping data is printed from two data sets on join key. 

<img src="https://user-images.githubusercontent.com/58262527/70772454-ee276000-1db7-11ea-8a41-c67ba9947d7d.png" width="40%">

#### [ Grammar and example ]
<pre><code>[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, dstn_ip 
    | TOP 100 
] a 
INNER JOIN 
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 20 
] b 
ON a.dstn_ip = b.src_ip 
| STATS COUNT(*) AS cnt BY a.dstn_ip
| WHERE cnt>=1  AND a.dstn_ip IS NOT NULL;</code></pre>

### 4.8  Outer Join
The table below shows examples of Left, Right and Full Outer Join.

<img src="https://user-images.githubusercontent.com/58262527/70774075-89bacf80-1dbc-11ea-9822-df3266298e59.png" width="80%">
 
#### [ Grammar and example ]
<pre><code>#-----------------------
# 1. Example of Left Outer Join 
#-----------------------
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, dstn_ip 
    | TOP 10 
] a 
LEFT OUTER JOIN 
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 2 
] b 
ON a.dstn_ip = b.src_ip 
| STATS COUNT(*) AS cnt BY a.dstn_ip 
| WHERE cnt>=1;

#-----------------------
# 2. Example of Right Outer Join 
#-----------------------
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, dstn_ip 
    | TOP 10 
] a 
RIGHT OUTER JOIN 
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 2 
] b 
ON a.dstn_ip = b.src_ip 
| STATS COUNT(*) AS cnt BY b.src_ip 
| WHERE cnt>=1;

#-----------------------
# 3. Example of Full Outer Join
#-----------------------
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, dstn_ip 
    | TOP 10 
] a 
FULL OUTER JOIN 
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 2 
] b 
ON a.dstn_ip = b.src_ip 
| STATS COUNT(*) AS cnt BY a.dstn_ip 
| WHERE cnt>=1;</code></pre>

### 4.9  Union
The table below shows examples of combining two data sets using Union and Unial All.

<img src="https://user-images.githubusercontent.com/58262527/70774193-da322d00-1dbc-11ea-90b0-ac9b29855ad3.png" width="90%">
 
#### [ Grammar and example ]
<pre><code>#-----------------------
# 1. Example of Union 
#-----------------------
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 10 
]  
UNION
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 2 
]  
| STATS COUNT(*) AS cnt BY src_ip
| WHERE cnt>=1 AND src_ip IS NOT NULL;

#-----------------------
# 2. Example of Union ALL
#-----------------------
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 10 
]  
UNION ALL
[ eqp_dt:[20190822150000 TO 20190822153000] 
    AND prtc:TCP AND attack_nm:* 
    | GROUP BY prtc, src_ip 
    | TOP 2 
]  
| STATS COUNT(*) AS cnt BY src_ip
| WHERE cnt>=1 AND src_ip IS NOT NULL;</code></pre>

### 4.10  Count Query
The table below shows an example of counting result of data search and processing. 

#### [ Grammar and example ]
<pre><code>eqp_dt:[20190822150000 TO 20190822153000] 
AND log_type:1
| STORAGE FROM RAW 
| SEARCH src_ip IN [
        eqp_dt:[20190822150000 TO 20190822153000] 
		AND log_type:2 AND attack_nm:*
		| GROUP BY src_ip
		| TOP 10 
		| PRINT src_ip
	] 
# Total count of above search result
| STATS COUNT(*) AS cnt, DC(src_ip) AS dc_cnt BY ALL</code></pre>

### 4.11  Calling Java Function
The table below shows an example of calling Java function within internal engine. When running Shovel engine, it can be used by calling Java function included in loaded jar library registered in CLASSPATH. The result is printed by calling method of Java Class - JAVA_METHOD(class, method[, arg1[, arg2 ..]]). 
Note: Class must be declared without parameter in a default form of constructor, className(). For e.g., public IPUtil(). Otherwise, constructor declaration should be omitted.

#### [ Grammar and example ]
<pre><code>eqp_dt:[20190822150000 TO 20190822153000] 
AND prtc:TCP
| SEARCH FROM RAW STORAGE
| FIELDS `key`, src_ip, dstn_ip, prtc 
| LIMIT 0 100
| STATS COUNT(*) AS cnt, SUM(in_packet) AS in_pkts, 
        AVG(COUNT) AS "average", DC(`key`) AS dip_cnt  BY src_ip, prtc
# Example of using Java Method : calling ip2long function included in IPUtil class
# 1. In case without parameter : JAVA_METHOD(class, method)
| CONVERT JAVA_METHOD('com.seculayer.seql.util.DateTime', 'getDateString') AS date_str
# 2. In case with parameter : JAVA_METHOD(class, method[, arg1[, arg2 ..]])
| CONVERT JAVA_METHOD('com.seculayer.seql.util.IPUtil', 'ip2long', '192.168.1.1') AS ip2long
| CONVERT JAVA_METHOD('com.seculayer.seql.util.cipher.AES256', 'encrypt', 'ABC') AS enc_str, JAVA_METHOD('com.seculayer.seql.util.cipher.AES256', 'decrypt', 'Xiiiiab1RRMP+B+DnBfbMw==') AS dec_str
| HEAD 10</code></pre>

### 4.12  Using Function within a Function
The table below shows an example of a nested fuction, using function within a function in a form of Function(function()).

#### [ Grammar and example ]
<pre><code>eqp_dt:[20190827160000 TO 20190827163000] 
AND prtc:TCP 
| STORAGE FROM RAW 
| FIELDS `src_ip`, dstn_ip, dstn_port, prtc 
| LIMIT 0 10 
| STATS COUNT(*) AS cnt,  DC(dstn_ip) AS dcnt, 
        /* Using logic function, IF() inside a group function, SUM() */
	SUM(IF(prtc='TCP', 1, 0)) AS scnt, 
        SUM(IF(SUBSTR(prtc,0,1)='T', 1, 0)) AS scnt2, 
        /* Using group function, STDEV() inside a number function, ROUND() */
	ROUND(STDEV(`no`), 2) AS rnd_std 
  BY prtc, dstn_port 
# Using date function, NOW() inside a date function, DATE_FORMAT()
| CONVERT DATE_FORMAT(NOW(), 'yyyyMMddHHmmss') AS now,
          TYPEOF(TODATE(now, 'yyyyMMddHHmmss')) AS now_type
| PRINT scnt, scnt2, rnd_std, now, now_type</code></pre>

### 4.13  Searching JSON File
The table below shows an example of searching JSON File existing on the server. 

#### [ Grammar and example ]
<pre><code>JSON FILE '/CloudESM/data/test.json'
| WHERE total = 2
| STATS COUNT(*) AS cnt, DC(`key`) AS dcnt  BY dstn_ip
| PRINT dstn_ip, dcnt, cnt;</code></pre>

### 4.14  Searching RDB Data Source
The table below shows an example of search using RDB data source registered on eyeCloudSIM.

#### [ Grammar and example ]
<pre><code>#-----------------------
# 1. Example of searching local DB
#-----------------------
RDB.LOCAL[ 
  SELECT * 
  FROM COM_CODE 
] 
| WHERE code_type = 'CS0004'
| STATS COUNT(*) AS cnt, DC(required) AS dcnt  BY code_type
| PRINT code_type, cnt, dcnt
| HEAD 3;

#-----------------------
# 2. Example of searching remote DB 
#-----------------------
RDB.db_01[ 
  SELECT * 
  FROM COM_CODE 
] 
| WHERE code_type = 'CS0004'
| STATS COUNT(*) AS cnt, DC(required) AS dcnt  BY code_type
| PRINT code_type, cnt, dcnt
| HEAD 3;</code></pre>

### 4.15  Searching File Data Source
The table below shows an example of search using file data source registered on eyeCloudSIM.

#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
| WHERE cnt >= 1 
  AND prtc IN ('TCP', 'UDP')
| PRINT src_ip, prtc, cnt, dcnt
| HEAD 3;</code></pre>

### 4.16  Join between Heterogeneous Data Sources
The table below shows examples of join analysis between heterogeneous data sources File and RDB, RDB and Search, and File and Search. 

#### [ Grammar and example ]
<pre><code>#-----------------------
# 1. Example of Union between File and DB
#-----------------------
[
	FILE.4[
		*:*
	]
	| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
	| WHERE cnt >= 1 
	  AND prtc IN ('TCP', 'UDP')
	| PRINT src_ip, cnt
]
UNION ALL
[
	RDB.LOCAL[ 
	  SELECT *
	  FROM   `TOP_5MIN_SRC_IP`
	  WHERE  eqp_dt >= '201908291500' AND eqp_dt < '201908291520'
	] 
	| PRINT src_ip, cnt
]
| WHERE cnt >= 20 AND src_ip LIKE '192.168.1.%'  
| STATS COUNT(*) AS ccnt, SUM(cnt) AS scnt BY src_ip
| WHERE ccnt > 1
;

#-----------------------
# 2. Example of Join between File and DB
#-----------------------
[
	FILE.4[
		*:*
	]
	| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
	| WHERE cnt >= 1 
	  AND prtc IN ('TCP', 'UDP')
	| PRINT src_ip, prtc, cnt, dcnt
] a
INNER JOIN 
[
	RDB.LOCAL[ 
	  SELECT *
	  FROM   TOP_5MIN_SRC_IP
	  WHERE  eqp_dt >= '201908291500' AND eqp_dt < '201908291520'
	] 
	| PRINT src_ip, eqp_dt, cnt
] b
ON a.src_ip = b.src_ip
| WHERE b.cnt >= 20
| print b.eqp_dt, a.src_ip, b.cnt</code></pre>

### 4.17  Save Results as a File
The table below shows examples of saving the SeQL query results as files such as JSON, CSV and delimiter separated file. For CSV and delimiter separated file, header and separator can be user defined, and if not chosen, , (comma) is used as default. 

#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
| WHERE cnt >= 1 
  AND prtc IN ('TCP', 'UDP')
| HEAD 3
| PRINT src_ip, prtc, cnt, dcnt
# Save as a JSON file
| TOJSON "/home/temp/test.json"
# Save as a CSV file
| TOCSV "/home/temp/test.csv" HEADER=true
# Save as a delimiter separated file
| TOFILE "/home/temp/test.txt" SEPARATOR="|" HEADER=true</code></pre>

### 4.18  Save Results on DB
The table below shows an example of saving the SeQL query results on RDB. There are two ways to save by 1) giving DB data source ID and table names, and 2) entering INSERT query explicitly. 

#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
| WHERE cnt >= 1 
  AND prtc IN ('TCP', 'UDP')
| HEAD 3
| PRINT dcnt, src_ip
# Saving on DB by giving table names when fields of tables and printing fields are the same 
| TODB dbId=TRUNK tableName=TEMP_DATA
# Saving on DB by using INSERT query
| TODB dbId=TRUNK query=[
  INSERT INTO TEMP_DATA (`index`, `data`) values (@{dcnt}, @{src_ip})
]</code></pre>

### 4.19  Using CASE~WHEN 
The table below shows an example of using CASE~WHEN Function between CONVERT and STATS statements. 

#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, 
        SUM(CASE WHEN prtc='TCP' THEN 1 ELSE 0 END) AS case_max  
  BY src_ip, prtc
| CONVERT (CASE WHEN prtc='TCP' THEN 'T' 
		        WHEN prtc='UDP' THEN 'U' 
		        ELSE '-' 
		   END) AS case_prtc
| PRINT prtc, src_ip, case_max, case_prtc
;</code></pre>

### 4.20  Using BETWEEN 
The table below shows an example of BETWEEN a AND b filtering to a WHERE statement.
 
#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
# BETWEEN Example
| WHERE cnt BETWEEN 1 AND 10
  AND prtc IN ('TCP', 'UDP')
| HEAD 3
| PRINT dcnt, src_ip</code></pre>

### 4.21  Using REGEXP 
The table below shows an example of REGEXP (regular expression) filtering to a WHERE statement.  

#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
# Using REGEXP 
| WHERE src_ip REGEXP '192\.168\..+\.'
| HEAD 3
| PRINT dcnt, src_ip</code></pre>

### 4.22  Creating QueryBrowser Table
The table below shows an example to create a QueryBrowser Table with analysis result. In case the same table exists already, existing table is deleted and new one is created when Rewrite=True, and data is added to the exisiting table when Rewrite=False. 
TOTABLE tableName=[table name] REWRITE=[true/false]

#### [ Grammar and example ]
<pre><code>FILE.4[
  	*:*
]
| STATS COUNT(*) AS cnt, DC(dstn_port) AS dcnt  BY src_ip, prtc
| WHERE cnt >= 1 
  AND prtc IN ('TCP', 'UDP')
| HEAD 3
| PRINT dcnt, src_ip
# Save to QueryBrowser Table
# - If REWRITE is true then the table is recreated. if false then data is appended.
| TOTABLE tableName=TEMP_DATA REWRITE=true
;</code></pre>

### 4.23  Searching QueryBrowser Table
The table below shows an example of searching data from QueryBrowser Table. TABLE.LOCAL is a reserved word for QueryBrowser Table. 
TABLE.LOCAL[	  Spark-SQL search Query   ]

#### [ Grammar and example ]
<pre><code>TABLE.LOCAL[ 
  SELECT * FROM TEMP_DATA_1
]
| WHERE total >= 10
| STATS SUM(total) AS total BY src_ip
| PRINT src_ip, total
| SORT total DESC
| HEAD 10;</code></pre>


### 4.24  SeQL File processing example
SeQL basically provides fast performance by processing data on a memory basis. 
However, it offers a separate option for handling large amounts of data beyond memory.
If you declare @PROCESS_BY_FILE at the top of SeQL statement as below, all query(Group-By, Sort, Filter, Join) is processed based on file.
At this time, the path to the processing file used internally is created in the /CloudESM/data/cache directory.
the path can change in the configuration file(/CloudESM/app/shovel_server/conf/shovel-server-conf.xml).
A detailed usage example is shown below.

#### [ Grammar and example ]
<pre><code>@PROCESS_BY_FILE
eqp_dt:[20190919090000 TO 20190919091000] 
AND prtc:TCP
| STORAGE FROM RAW
| FIELDS `key`, src_ip, dstn_ip, prtc 
| LIMIT 0 100
| STATS COUNT(*) AS cnt BY src_ip, prtc
| PRINT src_ip, prtc, cnt
| HEAD 10
;</code></pre>

### 4.25  Parallel Query example
SeQL supports parallel query on 1: N type multiple collector structures. Use parallel query like this:
<pre><code>Grammar> CLUSTER.PARALLEL[   SeQL Query  ]</code></pre>
Based on the idea that "moving calculation results rather than moving data" is beneficial to performance, the SeQL statements declared as CLUSTER.PARALLEL are processed by the collector server's search engine and transmitted to the Shovel server. It works by collecting all the data and returning the result.
@PROCESS_BY_FILE declaration for file processing is supported in CLUSTER.PARALLEL statement. Within CLUSTER.PARALLEL statement, search / group search-> processing syntax is available, but Join / Union / Search-Filter is not meaningful at collector node. Therefore, use is limited.

#### [ Grammar and example ]
<pre><code>#-----------------------
# 1. Search Example
#-----------------------
CLUSTER.PARALLEL[
	@PROCESS_BY_FILE
	eqp_dt:[20190919090000 TO 20190919091000] 
	AND prtc:TCP
	| STORAGE FROM RAW
	| FIELDS `key`, src_ip, dstn_ip, prtc 
	| LIMIT 0 100
	| STATS COUNT(*) AS cnt BY src_ip, prtc
	| PRINT src_ip, prtc, cnt
	| HEAD 10
]
| STATS SUM(cnt) AS scnt BY src_ip, prtc
| SORT scnt DESC
| HEAD 10;

#-----------------------
# 2. Group Search Example
#-----------------------
CLUSTER.PARALLEL[
	@PROCESS_BY_FILE
	eqp_dt:[20190919090000 TO 20190919091000] 
	AND prtc:TCP
	| STORAGE FROM RAW
	| GROUP BY prtc, src_ip 
	| STATS COUNT(*) AS cnt BY src_ip
	| PRINT src_ip, cnt
	| HEAD 1000
]
| STATS SUM(cnt) AS scnt BY src_ip
| SORT scnt DESC
| HEAD 10;</code></pre>



# 5. SeQL Built-in Functions
SeQL supports about 200 built-in functions related to letters / numbers / dates / logics / ARRAYs / sets / files.
It is basically used in the form of <code>function_name(parameter1, parameter2, …) AS alias</code>.
<pre><code>eqp_dt:[20190919090000 TO 20190919091000] 
AND prtc:TCP 
| STORAGE FROM RAW 
| FIELDS `src_ip`, dstn_ip, dstn_port, prtc 
| LIMIT 0 10 
| STATS COUNT(*) AS cnt,  DC(dstn_ip) AS dcnt, 
        /* Example of using logical function IF () in group function SUM () */
	SUM(IF(prtc='TCP', 1, 0)) AS scnt, 
        SUM(IF(SUBSTR(prtc,0,1)='T', 1, 0)) AS scnt2, 
        /* Example of using group function STDEV () in numeric function ROUND () */
	ROUND(STDEV(`no`), 2) AS rnd_std 
  BY prtc, dstn_port 
# Example of using date function NOW () in date function DATE_FORMAT ()
| CONVERT DATE_FORMAT(NOW(), 'yyyyMMddHHmmss') AS NOW,
          TYPEOF(TODATE(NOW, 'yyyyMMddHHmmss')) AS now_type
| PRINT scnt, scnt2, rnd_std, NOW, now_type;
</code></pre>
Functions can be nested as above.
As shown in the above example, aggregate function can be used and nested use in STATS ~ BY statement, and other functions except aggregate function can be used in CONVERT / WHERE statement.

  
### 5.1  Functions Related to Numbers
- ABS(number) prints the absolute value.
- CEILING(number) prints the smallest number among integers bigger than the entered number.
- FLOOR(number) prints the biggest number among integers smaller than the entered number. Decimals are omitted for positive numbers. This includes negative numbers but decimals.
- ROUND(number,decimal place) prints the number rounded up to the decimal place that can have positive and negative numbers and zero.
- TRUNC(number, decimal place) prints the number omitted below the entered decimal place.
- POW(X,Y) prints X to the power of Y. 
- MOD (numerator, denominator) prints the remainder of the numerator divided by the denominator, which is the same as operator %.
- SEQ() prints the list of results with counting numbers with field name, no. 
- SEQ(number) prints the list of results with counting numbers from the entered number with field name, no.
- RAND() prints a floating-point value.
- RAND(number) prints a positive integer value corresponding to the number of digits entered. 
- UUID() prints the UUID(Universal Unique Identifier).
- UUID_SHORT() prints the universal identifier of the integer value.
- SQRT(Double) prints the root value of the entered double value.
- SIN(Double) prints the sine value of the entered Double value.
- COS(Double) prints the cosine value of the entered Double value.
- TAN(Double) prints the tangent value of the entered Double value.
- SIGN(number) prints the sign of the entered number. Negative number prints (-1), positive number (1) and 0(0).
- PI() prints the value of pi. 
- PLUS(number1, number2) prints the sum of number1 plus number2.
- ADD(number1, number2) prints the sum of number1 plus number2.
- ADDITION(number1, number2) prints the sum of number1 plus number2.
- MINUS(number1, number2) prints the difference of number1 minus number2.
- SUB(number1, number2) prints the difference of number1 minus number2.
- SUBTRACTION(number1, number2) prints the difference of number1 minus number2.
- TIMES(number1, number2) prints the product of number1 times number2
- MULTIPLICATION(number1, number2) prints the product of number1 times number2
- DIVIDED_BY(number1, number2) prints the quotient of number1 divided by number2. 
- DIVISION(number1, number2) prints the quotient of number1 divided by number2.
- SEQUENCE(start, stop) prints automatically the value between start and stop.
- SEQUENCE(start, stop, step) prints the value between start and stop while skipping step. 
- FORMAT_NUMBER(number, format) prints numbers separated by , (comma) every 3 digits, with a format of #,### or #,###.### or #,###.00. For e.g., 123456789  123,456,789 
- LEAST(number1, number2, ...) prints the smallest value entered. 
- MOST(number1, number2, ...) prints the largest value entered. 
- SORT(number1, number2, ...) prints the entered value in ascending order. 
- NEGATIVE(number) prints the entered number in negative number. If negative number is entered, it prints as entered.
- POSITIVE(number) prints the entered number in positive number. If positive number is entered, it prints as entered.
- EVAL(regexp) prints the result of the entered REGEXP operations (+, -, *, /, %). For e.g., EVAL(1+2), EVAL(in_pkt_cnt*8), EVAL(in_pkt_cnt*cnt), and EVAL((src_ip*2.0)/100).

### 5.2  Functions Related to Strings 
- CONCAT('string1',' string2',' string3',...) concatenates(merges) entered strings.
- REPLACE('string','existing string','string to replace') replaces exsiting string to string to replace from the entered string.
- REPLACE_ALL('string','regexp','string to replace') replaces strings that matches regexp to string to replace from the entered string. 
- INDEXOF('string','string to find') prints the index(position) of the string to find from the entered string. 
- LEFTSTR('string',number) extracts string from left up to the number.
- RIGHTSTR('string',number) prints string from right up to the number.
- MIDSTR('string',position,number) prints string from the position up to the number.
- SUBSTR('string',position,number) prints string from the position up to the number.
- SUBSTRING('string',position,number) prints string from the position up to the number.
- LTRIM('string') removes left space of the string.
- RTRIM('string') removes right space of the string.
- TRIM('string') remove space from both sides of the string.
- LOWER('string') changes string to lower case.
- UPPER('string') changes string to upper case.
- LENGTH(field name) prints the length of the field value.
- LPAD('string1', digit, 'string2') prints the result of adding string2 to the left of string1 by the insufficient amount of entered digit.
- RPAD('string1', digit, 'string2') prints the result of adding string2 to the right of string1 by the insufficient amount of entered digit.
- REGEXP_MATCH(string, regexp) prints the result of true(1) and false(0), whether string and regexp match.
- REGEXP_COUNT(string, regexp) prints count of field name that matches with regexp.
- REGEXP_GET(string, regexp) extracts values of field name that matches with regexp.
- HEX_RMHDR(string) removes header value of HEX payload.
- HEX_TO_STR(string) converts HEX payload to Unicode string.
- STR_TO_MD5(string) converts string to MD5 hash value.
- ENCODE_BASE64(string) prints the result of encoded string with BASE64. 
- DECODE_BASE64(string) prints the result of decoded string with BASE64.
- LONG_TO_IP(Long) prints the result of IPv4 converted from Long data. 
- IP_TO_LONG(IP) prints the result of Long data converted from IPv4. 
- GET_DOMAIN(URL) prints the Domin only extracted from URL.
- LCASE() converts to lower case.
- UCASE() converts to upper case.
- REPEAT(string,number) prints the string repeated n times.
- REPEAT(string,number,separator) prints the string repeated n times with separator.
- REVERSE(string) prints string with reverse order.
- SPACE(number) prints space repeated n times.
- ENC_AES256(string) prints the result of encoded string using encryiption alogirthm, AES256, and internal fixed key.
- DEC_AES256(string) prints the result of decoded string using encryiption alogirthm, AES256, and internal fixed key.
- ENC_AES256(string, key) prints the result of encoded string using encryiption alogirthm, AES256.
- DEC_AES256(string, key) prints the result of decoded string using encryiption alogirthm, AES256.
- ENC_SEED128(string) prints the result of encoded string using encryiption alogirthm, SEED128, and internal fixed key.
- DEC_SEED128(string) prints the result of decoded string using encryiption alogirthm, SEED128, and internal fixed key.
- ENC_SEED128(string, key) prints the result of encoded string using encryiption alogirthm, SEED128.
- DEC_SEED128(string, key) prints the result of decoded string using encryiption alogirthm, SEED128.
- ENC_ARIA256(string, key) prints the result of encoded string using encryiption alogirthm, ARIA256, and internal fixed key.
- DEC_ARIA256(string, key) prints the result of decoded string using encryiption alogirthm, ARIA256, and internal fixed key.
- ENC_ARIA256(string) prints the result of encoded string using encryiption alogirthm, ARIA256.
- DEC_ARIA256(string) prints the result of decoded string using encryiption alogirthm, ARIA256.
- STR_TO_SHA256(string) converts the entered string to SHA256 hash.
- STR_TO_SHA1(string) converts the entered string to SHA1 hash. 
- IP_VER(IP) prints the version of the entered IP. For e.g., IPv4(4), IPv6(6) or etc(0). 
- OK_IPV4(IP) prints the result of true(1) or false(0) whether entered IP is valid IPv4.
- OK_IPV6(IP) prints the result of true(1) or false(0) whether entered IP is valid IPv6.
- IPV6_TO_BINT(IPv6) prints the result in BigInteger type converted from entered IPv6.
- BINT_TO_IPV6(bigint) prints the result in IPv6 converted from entered BigInteger.
- TO_STD_IPV6(IPv6) prints the result in standard IPv6 format converted from entered IPv6. 
- HASHCODE(string) prints the result of Java hash code of integer type converted from entered string.
- INITCAP(string) prints the result of the first letters of words separated by space changed to upper case.
- CHANGE_CHARSET(string, toCharset) prints the string with converted charset entered. For e.g., UTF-8, KSC5601, ISO8859-1 and etc.
- CHANGE_CHARSET(string, fromCharset, toCharset) prints the string with converted charset entered. For e.g., UTF-8, KSC5601, ISO8859-1 and etc.
- ENCODE_URL(url) prints the result of encoded URL.
- DECODE_URL(url) prints the result of decoded URL.
- PRINTF(strfmt, obj, ...) prints the standardized string from strfmt string format. For e.g., - PRINTF('Hello World %s %s', 100, 'days')  Hello World 100 days. 
- SPLIT(string) prints the arrays from the entered string separated by , (comma). Escape string is / (slash).
- SPLIT(string, regexp) prints the arrays from the entered string separated by regexp. 
- SPLIT(string, separator char, escape char) prints the arrays from the entered string separated by the entered separator char. Escape char is user-defined. For e.g., SPLIT('test|123', '|', '\\') 

### 5.3  Functions Related to Logic
- IF(logical operation,true value,false value) prints the result of either true or false value of logical operation. 
- IFNULL(value1,value2) prints value1 in default unless it is NULL.
- ISNULL(value1) prints the result of true(1) when value1 is NULL, otherwise false(0).
- ISNOTNULL(value) prints the result of true(1) when value is NOT NULL, otherwise false(0).

### 5.4  Aggregate Functions
- COUNT(field name) prints the counts of the rows. 
- SUM(field name) prints the sum of field names.
- AVG(field name) prints the average value of field names within each group. 
- MAX(field name) prints the maximum value. 
- MIN(field name) prints the minimum value.
- GROUP_CONCAT(field name) encloses group field values with , (comma). 
- GROUP_CONCAT(field name, 'char') encloses group field values with entered string, a character. 
- DC(field name) prints the count of unique strings of group field values. (DC: acronym of Distinct Count).
- VALUES(field name) prints the list of unique string of group field values. For e.g., [field value 1, field value 2, field value 3, …] 
- STDEV(field name) prints the standard deviation of field values.

### 5.5  Functions Related to Dates
- NOW() prints the current date and time. 
- SYSDATE() prints the current date and time.
- DATE_ADD(date,INTERVAL,value) adds the value to the entered date. (value: YEAR, MONTH, DAY, HOUR, MINUTE and SECOND)
- DATE_SUB(date,INTERVAL,value) subtract the value from the entered date. (value: YEAR, MONTH, DAY, HOUR, MINUTE and SECOND)
- YEAR(date) prints the year(yyyy) of entered date.
- MONTH(date) prints the month(MM) of entered date.
- MONTHNAME(date) prints the month(MMMM) of entered date, in English.
- DAYNAME(date) prints the day(EEEE) of entered date, in English.
- DATE_FORMAT('format') prints the current date in entered format.
- DATE_FORMAT(date,'format') prints the entered date in entered format.
- TODATE(string, 'format') prints the result of parsed string in entered format.
- UNIXTIME_TO_STR(unixtime) prints Unix Timestamp in the form of yyyy-MM-dd HH:mm:ss.
- UNIXTIME_TO_STR(unixtime, format) prints Unix Timestamp in the entered format. For e.g., yyyyMMddHHmmss or yyyy-MM-dd HH:mm:ss.
- UNIXTIME_TO_DATE(unixtime) converts Unix Timestamp to Date type. 
- YEAR_DIFF(date1, date2) prints the year difference between date1 and date2. 
- YEARSTR_DIFF(date1, date1 format, date2, date2 format) prints the year difference between date1 and date2.
- MONTH_DIFF(date1, date2) prints the month difference between date1 and date2. 
- MONTHSTR_DIFF(date1, date1 format, date2, date2 format) prints the month difference between date1 and date2. 
- DATE_DIFF(date1, date2) prints the date difference between date1 and date2. 
- DATESTR_DIFF(date1, date1 format, date2, date2 format) prints the date difference between date1 and date2.
- HOUR_DIFF(date1, date2) prints the hour difference between date1 and date2.
- HOURSTR_DIFF(date1, date1 format, date2, date2 format) prints the hour difference between date1 and date2.
- MIN_DIFF(date1, date2) prints the minute difference between date1 and date2. 
- MINSTR_DIFF(date1, date1 format, date2, date2 format) prints the minute difference between date1 and date2.
- SEC_DIFF(date1, date2) prints the second difference between date1 and date2. 
- SECSTR_DIFF(date1, date1 format, date2, date2 format) prints the second difference between date1 and date2.
- DATE() prints the current date in the form of yyyy-MM-DD.
- TIME() prints the current time in the form of HH:mm:ss.
- UNIX_TIMESTAMP() prints Unix Timestamp.
- UTC_DATE() prints date of UTC(Coordinated Universal Time) in the form of yyyy-MM-dd.
- UTC_TIME() prints time of UTC(Coordinated Universal Time) in the form of HH:mm:ss.
- UTC_TIMESTAMP() prints date and time of UTC(Coordinated Universal Time) in the form of yyyy-MM-dd HH:mm:ss.
- UTC_TO_LOCAL(utc_time) prints the local date and time converted from UTC in the form of yyyy-MM-dd HH:mm:ss.
- UTC_TO_LOCALSTR(utc_time, Local date format) prints the local date and time in the form of entered Local date format converted from UTC in the form of yyyy-MM-dd HH:mm:ss.
- LOCAL_TO_UTC(local_time) prints UTC date and time in the form of yyyy-MM-dd HH:mm:ss converted from the local date and time in the form of yyyy-MM-dd HH:mm:ss.
- LOCAL_TO_UTCSTR(local_time, UTC date format) prints UTC date and time in the form of entered UTC date format converted from local date and time in the form of yyyy-MM-dd HH:mm:ss.
- DAY(date) prints the day(dd) of entered date.
- HOUR(date) prints the hour(HH) of entered date.
- MINUTE(date) prints the minute(mm) of entered date.
- SEC(date) prints the second(ss) of entered date.
- SECOND(date) prints the second(ss) of entered date.
- LAST_DAY(date) prints the last day of entered date.
- LAST_DAY(string, date format) prints the last day of entered date in the form of entered date format.

### 5.6  Functions Related to Data Types
- CAST(field name, type) converts the value of field name to entered data type. Available data types are String, Long, Double, Float and Integer. For e.g., CAST(dstn_port, Integer). 
- TYPEOF(field name) prints the data type of the entered field name. Available data types are String, Long, Double, Float and Integer.

### 5.7  Functions Related to Files
- FILE(absolute file path) prints the result of true(1) or false(0) for the existance of file
- DIR(absolute file path) prints the result of true(1) or false(0) for the existance of directory.
- DIRECTORY(absolute file path) prints the result of true(1) or false(0) for the existance of directory.
- LOAD_FILE(absolute file path) prints the contents of the file in string.
- FILE_LENGTH(absolute path) prints the size of the file. -1 is printed if file does not exist.
- FILE_LINES(absolute path) prints the number of lines of the file. -1 is printed if file does not exist.
- FILE_NOTNULL_LINES(absolute path) prints the number of lines, omitting the empty lines, of the file. -1 is printed if file does not exist. 
- FILE_NAME(absolute path) prints the file name. null is printed if file does not exist.
- FILES_COUNT(absolute path) prints the number of files in the directory. -1 is printed if directory does not exist.
- FILES(absolute path) prints the array of file names in the directory. null is printed if directory does not exist.
- FILES_EXCLUDE(absolute path, string) prints the array of file names without string in the directory. null is printed if directory does not exist.
- FILES_INCLUDE(absolute path, string) prints the array of file names with string in the directory. null is printed if directory does not exist.
- FILES_PREFIX(absolute path, string) prints the array of file names that begin with the entered string in the directory. null is printed if directory does not exist.
- FILES_EXT(absolute path, file extension) prints the array of file names that match with the entered file extension string in the directory. null is printed if directory does not exist.

### 5.8  Operators
- <code>+</code> is addition operator.
- <code>-</code> is subtraction operator.
- <code>*</code> is multiplication operator.
- / is division operator.
- % is remainder operator. 
- value1 < value2 prints the result of true(1) when value1 is smaller than value2, otherwise false(0) 
- value1 <= value2 prints the result of true(1) when value1 is equal or smaller than value2, otherwise false(0).
- value1 > value2 prints the result of true(1) when value1 is greater than value2, otherwise false(0).
- value1 >= value2 prints the result of true(1) when value1 is equal or greater than value2, otherwise false(0).
- value1 = value2 prints the result of true(1) when value1 and value2 are the same, otherwise false(0).
- value1!= value2 prints the result of true(1) when value1 and value2 are not the same, otherwise false(0).
- AND is AND logical operator.
- OR is OR logical operator.
- IS NULL checks whether it is NULL.
- IS NOT NULL checks whether it is NOT NULL.
- IN checks for inclusion.
- NOT IN checks for exclusion.
- LIKE is LIKE operator
- NOT LIKE is NOT LIKE operator. 

### 5.9  Functions Related to Array
- ARRAY(number1, number2, number3) prints the array of number 1, 2 and 3. 
- TOARRAY(number1, number2, number3) prints the array of number 1, 2 and 3.
- ARRAY_LENGTH(array) prints the size of the array. 
- ARRAY_SIZE(array) prints the size of the array.
- ARRAY_CONTAINS(array, value) prints the result of true(1) or false(0) whether value is included in the array. 
- ARRAY_DISTINCT(array) prints the distinctive array after omitting duplicate values.
- ARRAY_EXCEPT(array1, array2) prints the array after omitting the duplicate values of array2 from array1.
- ARRAY_INTERSECT(array1, array2) prints the array that intersects array1 and array2 without duplicates.
- ARRAY_JOIN(array, separator) prints array as a string connected with separator. Ignore NULL  if exists.
- ARRAY_JOIN(array, separator, null replacement) prints array as a string connected with separator. Replace NULL with null replacement if exists.
- ARRAY_MAX(array) prints the maximum values from array. Ignore NULL.
- ARRAY_MIN(array) prints the minimum values from array. Ignore NULL.
- ARRAY_POSITION(array, element) prints the position(index) of first element from array 
- ARRAY_REMOVE(array, element) prints array omitting the element. 
- ARRAY_REPEAT(element, count) prints array in the size of count after duplicating element count times.
- ARRAY_SORT(array) prints array in ascending order.
- ARRAY_UNION(array1, array2) prints the result of addition of array1 and array2 wihtout duplicates.
- ARRAY_UNIONALL(array1, array2) prints the result of addition of array1 and array2.
- ARRAY_COALESCE(array) prints the first value of array except null, unless all value is null.
- ARRAY_ELEMENT_AT(array, element) prints the position(index) of element from array. 
- ARRAY_SHUFFLE(array) prints the array in a randomly shuffled order. 
- ARRAY_LAST(array, regexp) prints the last value that matches regexp for the group of columns.
- ARRAY_LAST(array, regexp, IgnoreNull) prints the last value that matches regexp for the group of columns. Only non-null values are returned if isIgnoreNull is true.
- ARRAY_FIRST(array, regexp) prints the first value that matches regexp for the group of columns.
- ARRAY_FIRST(array, regexp, IgnoreNull) prints the first value that matches regexp for the group of columns. Only non-null values are returned if isIgnoreNull is true
- DISTINCT(value1, value2, ...) prints array with distinctive values from entered. 

### 5.10  Calling External Functions 
- JAVA_METHOD(class, method[, arg1[, arg2 ..]]) prints the result from calling method of Java class.  
E.g. 1) calling functions without parameter: JAVA_METHOD('com.seculayer.seql.util.DateTime', 'getDateString')  
E.g. 2) calling functions with parameter: JAVA_METHOD('com.seculayer.seql.util.cipher.AES256', 'encrypt', 'ABC')

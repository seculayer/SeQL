grammar SeQL;

mainQ
	: (proc=PROC_BY_FILE)? query=pipe_query SEMI?
	;

pipe_query
	: ds=ds_query proc=pipe_proc_group?
	;
	
ds_query
	:
	search=pipe_search_group	#dsSearch
	| file=ds_file				#dsFile
	| rdb=ds_rdb				#dsRdb
	| join=join_clause			#dsJoin
	| union=union_list			#dsUnion
	| json=json_file			#dsJson
	| tbl=ds_table				#dsTable
	| parallel=cluster_parallel	#dsCluster
	| show=show_reputations		#dsReputations
	| show=show_reput			#dsReputGroup
	| drop=drop_reput			#dsDropReput
	;
ds_file
	:
	FILE_PHRASE
	;
ds_rdb
	:
	RDB_PHRASE
	;
ds_table
	:
	TABLE_PHRASE
	;
json_file
	:
	JSON_FILE (DQUOTE_PHRASE|SQUOTE_PHRASE)
	;

join_clause
	: LBRACK left=pipe_query RBRACK leftAlias=ID
	  joinType=join_type LBRACK right=pipe_query RBRACK rightAlias=ID 
	  ON expr=op_eval_expr
	;

union_list
	:
	union_clause+
	;

union_clause
	:  
	  joinType=union_type? LBRACK right=pipe_query RBRACK
	;

join_type
	:
	JOIN_DIRECTION? JOIN_TYPE 
	;

union_type
	:
	UNION_TYPE
	;
	
cluster_parallel
	:
	CLUSTER_PARALLEL LBRACK (proc=PROC_BY_FILE)? query=pipe_query RBRACK
	;
	
show_reputations
	:
	SHOW_REPUTATIONS
	;
	
show_reput
	:
	SHOW_REPUTATION table=ID (PIPE filter=pipe_proc_filter)* (PIPE limit=pipe_proc_limit)?
	;
	
drop_reput
	:
	DROP_REPUTATION table=ID
	;


/**--------------------------
** 1. Search
**--------------------------**/    
pipe_search_group
	:
	searchClause (PIPE pipe_search)* (PIPE pipe_search_limit)?
	| searchClause (PIPE pipe_group_search)*
	;
pipe_search
    : pipe_search_time
    | pipe_search_in_clause
    | pipe_search_storage
    | pipe_search_fields
    | pipe_search_sort
//    | pipe_search_limit
    ;
pipe_group_search
    : pipe_search_time
    | pipe_search_in_clause
    | pipe_search_storage
    | pipe_search_groupby
    | pipe_search_top_bottom
    ;
pipe_search_time
	: LAST span=eval_expr timeType=TIME_TYPE
	;
pipe_search_in_clause :
	SEARCH id=join_id inNot=in_notin LBRACK query=pipe_query RBRACK
	;
pipe_search_storage
	: STORAGE FROM storage=ID
	;
pipe_search_groupby
    : GROUP BY idList=id_list
    ;
pipe_search_fields
    : FIELDS idList=id_list
    ;
id_list :
	ids (COMMA ids)*
	;
ids :
	ID
	| ID COLON ('avg'|'sum'|'max'|'min'|UINT)
	| APOSTRO ids APOSTRO
	;
	
pipe_search_sort
    : SORT sort_clause (COMMA sort_clause)*
    ;
pipe_search_top_bottom
    : topBot=top_bottom evExpr=UINT
    ;
pipe_search_limit
    : limitType=LIMIT expr1=UINT expr2=UINT
    | limitType=UNLIMIT
    ;
    
sort_clause
	: sid=alias_name sort=ASC_DES
	;
top_bottom
	: (TOP|BOTTOM)
	;
head_tail
	: (HEAD|TAIL)
	;

/**--------------------------
** 2. Processing(Stat, Convert...) 
**--------------------------**/
pipe_proc_group
	:
	(PIPE proc=pipe_proc)+
	;
pipe_proc
    : pipe_proc_stats
	| pipe_proc_filter
    | pipe_proc_convert
    | pipe_proc_sort
    | pipe_proc_limit
    | pipe_proc_head_tail
    | pipe_output_print
    | pipe_output_file
    | pipe_output_db
    | pipe_output_table
    | pipe_output_reput
    ;
//--------------------------
// Statistic statement
//--------------------------	
pipe_proc_stats
    : 
    STATS stats_func_alias (COMMA stats_func_alias)* BY alias_name (COMMA alias_name)*
	;
	
stats_func_alias :
	(statFunc=stats_function|func=function) AS alias=alias_name
	;
	
stats_function
	: funcNm=GROUP_FUNC (LPAREN args=arguments? RPAREN)?
	;
	
//--------------------------
// Convert statement
//--------------------------	
pipe_proc_convert :
	CONVERT func_alias (COMMA func_alias)*
	;
	
func_alias :
	func=function AS alias=alias_name
	;
	
function
	: funcNm=functionName (LPAREN args=arguments? RPAREN)?
	| LPAREN caseFunc=case_function RPAREN
	| caseFunc=case_function
	| text=SQUOTE_PHRASE
	;
	
functionName
	:
	ID
	| TIME_TYPE
	| ID DOT ID
	;
	
case_function
	: 'CASE' ('WHEN' op_eval_expr 'THEN' arg_expr)+ ('ELSE' elsVal=arg_expr)? 'END'
	;
	
arguments
	: arg_expr (COMMA arg_expr )*
	;
 
arg_expr
	: left=arg_expr op=OPER right=arg_expr	#argOper
	| left=arg_expr op=AND right=arg_expr	#argAnd
	| left=arg_expr op=OR right=arg_expr	#argOr
//	| LPAREN expr=arg_expr RPAREN           #argParen
	| bool=BOOL								#argBool
	| text=SQUOTE_PHRASE					#argText
	| number=numeric						#argNumber
	| id=join_id							#argId
	| func=stats_function					#argStatFunc
	| func=function							#argFunc
	| eval=eval_expr						#argEval
	| STAR									#argAsterik
	;
 
op_eval_expr : 
	 left=eval_expr op=AND right=eval_expr		#opEvalAnd
	| left=eval_expr op=OR right=eval_expr		#opEvalOr
	| LPAREN expr=op_eval_expr RPAREN           #opParen
    | left=eval_expr							#opEval
	| left=op_eval_expr op=AND right=eval_expr	#opEvalExprAnd
	| left=op_eval_expr op=OR right=eval_expr	#opEvalExprOr
    ;
 
eval_expr  : 
	left=eval_expr op=mathOp right=eval_expr 	#evalCalc
	| left=eval_expr op=OPER right=eval_expr	#evalOper
    | LPAREN eval_expr RPAREN                   #evalParen
    | trunc=truncated							#evalTrunc
    | number=numeric                   			#evalAtomr
//    | id=ID                           			#evalId
    | id=join_id                           		#evalId
    | text=SQUOTE_PHRASE               			#evalText
    | text=DQUOTE_PHRASE               			#evalDquoteText
    | func=function		               			#evalFunction
    ;

join_id :
	ID '.' ID
	| ID
	;

alias_name :
	join_id
	| DQUOTE_PHRASE
	;
	
mathOp:
  (PLUS | MINUS | STAR | SLASH | PER)
  ;
	
//--------------------------
// Filter statement
//--------------------------	
pipe_proc_filter
	: 
	WHERE filter=filter_logical
	;

filter_logical :
    LPAREN filter=filter_logical RPAREN								#filterParen
    | left=filter_logical fOper=OPER right=filter_logical			#filterOper
	| left=filter_logical op=AND right=filter_logical				#filterAnd
	| left=filter_logical op=OR  right=filter_logical				#filterOR
    | left=eval_expr BETWEEN from=eval_expr AND to=eval_expr		#filterBetween
    | left=eval_expr REGEXP right=eval_expr							#filterRegexp
    | left=eval_expr fOper=like_oper right=eval_expr				#filterLikeOper
    | left=eval_expr fOper=null_oper								#filterNullOper
    | left=eval_expr inNot=in_notin LBRACK query=pipe_query RBRACK	#filterInSearch
	| left=eval_expr inNot=in_notin LPAREN exprs=expr_list RPAREN	#filterInClause
    | left=eval_expr												#filterSingle
//    | left=function												#filterFunction
	;
expr_list :
	eval_expr (COMMA eval_expr)*
	;
like_oper :
	(LIKE | NOT LIKE)
	;
null_oper :
	(IS NULL | IS NOT NULL)
	;
in_notin :
	(IN | NOT IN)
	;
//--------------------------
// Sort/Limit/Tail
//--------------------------	
pipe_proc_sort
	: SORT sort_clause (COMMA sort_clause)*
	;
pipe_proc_limit
	: LIMIT expr1=UINT expr2=UINT
	;
pipe_proc_head_tail
	: topBot=head_tail evExpr=UINT
	;

/**--------------------------
** 3. Output 
**--------------------------**/
pipe_output_print
	: PRINT alias_name (COMMA alias_name)*
	;
pipe_output_file
	: fopt=OUTPUT_FILE_OPT (rewrite=REWRITE)?
	;
pipe_output_db :
	type1=SAVE_DB
	| type2=SAVE_DB_QUERY
	;
pipe_output_table 
	: table=OUTPUT_TABLE (rewrite=REWRITE)?
	;
pipe_output_reput 
	: reput=OUTPUT_REPUT
	;

FILE_TYPE :
	(TOJSON|TOCSV|TOFILE)
	;
//OUTPUT_FILE_OPT :
//	DQUOTE_PHRASE WS SEPARATOR_PHRASE? (WS* 'HEADER' EQ BOOL)?
//	| DQUOTE_PHRASE WS ('HEADER' EQ BOOL)? (WS* SEPARATOR_PHRASE)?
//	;
OUTPUT_FILE_OPT :
	FILE_TYPE WS DQUOTE_PHRASE (WS SEPARATOR_PHRASE)? (WS 'HEADER' EQ BOOL)?
	| FILE_TYPE WS DQUOTE_PHRASE (WS 'HEADER' EQ BOOL)? (WS SEPARATOR_PHRASE)?
	;
	
OUTPUT_TABLE :
	'TOTABLE' WS 'tableName' WS? EQ WS? ID
	;
	
OUTPUT_REPUT :
	'TOREPUT' WS 'tableName' WS? EQ WS? (DQUOTE_PHRASE|SQUOTE_PHRASE) WS 'KEY' WS? EQ WS? (DQUOTE_PHRASE|SQUOTE_PHRASE) WS 'keepTime' WS? EQ WS? ID WS TIME_TYPE
	;

REWRITE :
	'REWRITE' WS? EQ WS? BOOL
	;

/* ================================================================
 * =                     LuceneQL                                 =
 * ================================================================
 */
searchClause
	:
	clauseBasicList	
	;

clauseBasicList
	:
	clauseBasic (oper clauseBasic)*
	;
	
clauseBasic
  :
  clauseGroup
  | atom
  | systemVariables
  | systemList
  ;

clauseGroup
  :
  modifier? LPAREN search=clauseBasicList RPAREN term_modifier?
  ;

atom
  :
  modifier? mfld=field mval=multi_value term_modifier?
  | modifier? fld=field? val=value term_modifier?
  ;
  
//SYSVAR(BLACK_IP)
systemVariables
  : 'SYSVAR' LPAREN ID RPAREN
  ;

//SYSLIST(ATT_IP,src_ip=attack_src_ip,dst_ip=victim_dst_ip)
systemList
  : 'SYSLIST' LPAREN ID RPAREN
  ;
  
field
  :
  ID COLON
  ;

value
  :
  range=range_term
  | ip=ip_type
  | norm=normal
  | trunc=truncated
  | quot=quoted
  | qmark=QMARK
  | reg=regex
  | any=anything
  | star=STAR
  ;

anything
  :
  STAR COLON STAR
  ;

range_term
  :
  (LBRACK|LCURLY) left=range_value TO right=range_value (RBRACK|RCURLY)
  ;

range_value
  :
  truncated
  | quoted
  | ip_type
  | normal
  | STAR
  ;

multi_value
  :
  LPAREN vals=value+ RPAREN
  ;

normal
  :
  ID
  | numeric
  ;

truncated
  :
  TERM_TRUNCATED

//	(STAR|QMARK) (TERM_CHAR+ (QMARK|STAR))+ (TERM_CHAR)*
//	| TERM_START_CHAR (TERM_CHAR* (QMARK|STAR))+ (TERM_CHAR)*
//	| (STAR|QMARK) TERM_CHAR+
  ;

quoted  :
  DQUOTE_PHRASE
  ;

regex  :
  REGEX
  ;

modifier:
  PLUS
  | MINUS;


term_modifier :
  boost fuzzy?
  | fuzzy boost?
  ;

boost :
  (CARAT) // set the default value
  (numeric)? //replace the default with user input
  ;

fuzzy :
  (TILDE) // set the default value
  (numeric)? //replace the default with user input
  ;

not_  :
  AND NOT
  | NOT
  ;

oper :
	and_
	| or_
	| not_
	;

and_  :
  AND
  ;

or_   :
  OR
  ;

ip_type :
	ip_v4 
//	| ip_v6
	;
ip_v4
	: IPV4
	;
	
numeric :
	MINUS UINT '.' UINT
	| UINT '.' UINT
	| MINUS UINT
	| UINT
	;


/* ================================================================
 * =                     LEXER                                    =
 * ================================================================
 */
fragment INT		: '0' .. '9';
fragment ID_CHAR	: ('a' .. 'z' | 'A' .. 'Z' | INT | '_');
fragment CHAR   	: ('a' .. 'z' | 'A' .. 'Z');
fragment IP_CHAR    : ('a' .. 'f' | 'A' .. 'F');
fragment ESC_CHAR	:  '\\' .;
fragment EQ			: '=';
fragment NEQ		: '!=';
fragment LESS		: '<';
fragment LESS_EQ	: '<=';
fragment GREATER	: '>';
fragment GREATER_EQ	: '>=';

DOT		: '.';
PIPE    : '|';
SQUOTE  : '\'';
APOSTRO : '`';
DQUOTE  : '"';
LPAREN  : '(';
RPAREN  : ')';
LBRACK  : '[';
RBRACK  : ']';
LCURLY  : '{' ;
RCURLY  : '}' ;
COMMA   : ',';
COLON   : ':' ;
SEMI    : ';' ;
PLUS    : '+' ;
MINUS   : '-';
STAR    : '*' ;
QMARK   : '?'+ ;
SLASH   : '/';
PER     : '%' ;
CARAT   : '^' (INT+ ('.' INT+)?)?;
TILDE   : '~' (INT+ ('.' INT+)?)?;
BETWEEN : 'BETWEEN' ;
REGEXP  : 'REGEXP' ;
AND     : 'AND' ;
OR      : 'OR';
NOT     : 'NOT';
ON      : 'ON';
SEARCH  : 'SEARCH';
IN      : 'IN';
LAST    : 'LAST';
STORAGE : 'STORAGE';
FROM    : 'FROM';
GROUP   : 'GROUP';
BY      : 'BY';
FIELDS  : 'FIELDS';
JSON_FILE    : 'JSON' WS 'FILE';
//FILE    : 'FILE';
RDB     : 'RDB';
LOCAL   : 'LOCAL';
SORT    : 'SORT';
TOP     : 'TOP';
BOTTOM  : 'BOTTOM';
HEAD    : 'HEAD';
TAIL    : 'TAIL';
LIMIT   : 'LIMIT';
UNLIMIT   : 'UNLIMIT';
STATS   : 'STATS';
IS      : 'IS';
LIKE    : 'LIKE';
WHERE   : 'WHERE';
CONVERT : 'CONVERT';
ASC_DES : ('ASC'|'DESC');
AS      : 'AS';
PRINT   : 'PRINT';
TOJSON  : 'TOJSON';
TOCSV   : 'TOCSV';
TOFILE  : 'TOFILE';
TODB    : 'TODB';
TO      : 'TO';
NULL    : 'NULL';
SEPARATOR : 'SEPARATOR';

JOIN_TYPE 
	: 'INNER' WS 'JOIN'
	| 'OUTER' WS 'JOIN'
	;
JOIN_DIRECTION	: ('LEFT'|'RIGHT'|'FULL');
UNION_TYPE		: 'UNION' (WS 'ALL')?;
BOOL			: ('true'|'false'|'TRUE'|'FALSE');
TIME_TYPE 		: ('SECONDS'|'MINUTE'|'HOUR'|'DAY'|'MONTH'|'YEAR');
OPER			: (EQ | NEQ | LESS | LESS_EQ | GREATER | GREATER_EQ);
//CALC			: (PLUS | MINUS | STAR | SLASH | PER);
UINT : INT+;
//NUMBER
//	: MINUS? INT+ (DOT INT+)?
//	;
GROUP_FUNC :
	'COUNT'
	| 'MAX' 
	| 'MIN' 
	| 'SUM' 
	| 'AVG' 
	| 'VALUES' 
	| 'DC' 
	| 'GROUP_CONCAT' 
	| 'STDEV' 
	;
ID	: (APOSTRO ID APOSTRO | ID_CHAR+);
DQUOTE_PHRASE 
	: DQUOTE (ESC_CHAR|~('"'|'\\'))+ DQUOTE
	;
SQUOTE_PHRASE
	: SQUOTE (ESC_CHAR|~('\''|'\\'))* SQUOTE
	;
SAVE_DB_QUERY :
	TODB WS 'dbId' WS? EQ WS? ID WS 'queryStr' WS? EQ WS? LBRACK (ESC_CHAR|~('['|']'|'\\'))+ RBRACK
	;
SAVE_DB :
	TODB WS 'dbId' WS? EQ WS? ID WS 'tableName' WS? EQ WS? ID
	;
SEPARATOR_PHRASE :
	SEPARATOR WS* EQ WS* DQUOTE_PHRASE
	;
FILE_PHRASE
	: 'FILE' DOT ID WS* LBRACK WS* '*:*' WS* RBRACK
	;
RDB_PHRASE
	: RDB DOT ID WS* LBRACK (ESC_CHAR|~('['|']'|'\\'))+ RBRACK
	;
TABLE_PHRASE
	: 'TABLE' DOT 'LOCAL' WS* LBRACK (ESC_CHAR|~('['|']'|'\\'))+ RBRACK
	;
REGEX
	: SLASH (ESC_CHAR|~('/'|'\\'))+ SLASH
	;
IPV4 :
	UINT '.' UINT '.' UINT '.' UINT (SLASH UINT)?
	;
PROC_BY_FILE
	:
	'@PROCESS_BY_FILE'
	;
CLUSTER_PARALLEL
	:
	'CLUSTER' DOT 'PARALLEL'
	;
SHOW_REPUTATIONS
	:
	'SHOW' WS 'REPUTATIONS'
	;
SHOW_REPUTATION
	:
	'SHOW' WS 'REPUTATION' WS
	;
DROP_REPUTATION
	:
	'DROP' WS 'REPUTATION' WS
	;
//IPV6 :
//	(IPv6_CHAR|COLON)+ (SLASH INT+)?
//	;
//HEXDIG
// : INT
// | IP_CHAR
// ;
TERM_TRUNCATED:
	(STAR|QMARK) (TERM_CHAR+ (QMARK|STAR))+ (TERM_CHAR)*
	| TERM_START_CHAR (TERM_CHAR* (QMARK|STAR))+ (TERM_CHAR)*
	| (STAR|QMARK) TERM_CHAR+
  ;
TERM_CHAR 	: (TERM_START_CHAR | '-' | '+');
TERM_START_CHAR
	: (~(' ' | '\t' | '\n' | '\r' | '\u3000'
        | '"'
        | '(' | ')' | '[' | ']' | '{' | '}'
        | '+' | '-' | '!' | ':' | '~' | '^'
        | '?' | '*' | '\\'
        )
   | ESC_CHAR );

WS: [ \t\r\n]+ -> skip;

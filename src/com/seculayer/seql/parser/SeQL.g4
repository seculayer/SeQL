grammar SeQL;

mainQ
	: pipe_query SEMI?
	;

pipe_query
	: (search=pipe_search_group|join=join_clause) proc=pipe_proc_group?
	;

join_clause
	: LBRACK left=pipe_query RBRACK (leftAlias=ID)? 
	  joinType=join_type LBRACK right=pipe_query RBRACK (rightAlias=ID)? 
	  (ON expr=op_eval_expr)?
	;

join_type
	:
	JOIN_DIRECTION? JOIN_TYPE
	| UNION_TYPE 
	;


/**--------------------------
** 1. Search
**--------------------------**/    
pipe_search_group
	:
	searchClause (PIPE pipe_search)*
	;
pipe_search
    : pipe_search_time
    | pipe_search_in_clause
    | pipe_search_storage
    | pipe_search_groupby
    | pipe_search_fields
    | pipe_search_sort
    | pipe_search_top_bottom
    | pipe_search_limit
    ;
pipe_search_time
	: LAST span=eval_expr timeType=TIME_TYPE
	;
pipe_search_in_clause :
	SEARCH id=ID inNot=in_notin LBRACK query=pipe_query RBRACK
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
	ID (COMMA ID)*
	;
pipe_search_sort
    : SORT sort_clause (COMMA sort_clause)*
    ;
pipe_search_top_bottom
    : topBot=top_bottom evExpr=NUMBER
    ;
pipe_search_limit
    : LIMIT expr1=NUMBER expr2=NUMBER
    ;
    
sort_clause
	: sid=alias_name sort=ASC_DES
	;
top_bottom
	: (HEAD|TAIL|TOP|BOTTOM)
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
//    | pipe_output_file
//    | pipe_output_db
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
	: funcNm=ID (LPAREN args=arguments? RPAREN)?
	;
 
arguments
	: arg_expr (COMMA arg_expr )*
	;
 
arg_expr
	: left=arg_expr op=OPER right=arg_expr	#argOper
	| left=arg_expr op=AND right=arg_expr	#argAnd
	| left=arg_expr op=OR right=arg_expr	#argOr
	| bool=BOOL								#argBool
	| text=SQUOTE_PHRASE					#argText
	| number=NUMBER							#argNumber
	| id=ID									#argId
//	| id=APOSTRO_PHRASE						#argApostorId
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
    ;
 
eval_expr  : 
	left=eval_expr op=CALC right=eval_expr 		#evalCalc
	| left=eval_expr op=OPER right=eval_expr	#evalOper
//	| left=eval_expr op=AND right=eval_expr		#evalAnd
//	| left=eval_expr op=OR right=eval_expr		#evalOr
    | LPAREN eval_expr RPAREN                   #evalParen
    | number=NUMBER                    			#evalAtomr
    | id=ID                           			#evalId
//    | id=APOSTRO_PHRASE							#evalApostorId
    | text=SQUOTE_PHRASE               			#evalText
    | text=DQUOTE_PHRASE               			#evalDquoteText
    ;
    
alias_name :
	ID
	| DQUOTE_PHRASE
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
    | left=eval_expr fOper=like_oper right=eval_expr				#filterLikeOper
    | left=eval_expr fOper=null_oper								#filterNullOper
    | left=eval_expr inNot=in_notin LBRACK query=pipe_query RBRACK	#filterInSearch
	| left=eval_expr inNot=in_notin LPAREN exprs=expr_list RPAREN	#filterInClause
    | left=eval_expr												#filterSingle
	;
//pipe_filter_single :
//	left=eval_expr fOper=filter_oper right=eval_expr
//	;
//proc_filter_in_join :
//	left=eval_expr inNot=in_notin LBRACK query=pipe_query RBRACK
//	;
//proc_filter_in_clause :
//	left=eval_expr inNot=in_notin LPAREN exprs=expr_list RPAREN
//	;
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
	: LIMIT expr1=NUMBER expr2=NUMBER
	;
pipe_proc_head_tail
	: topBot=top_bottom evExpr=NUMBER
	;

/**--------------------------
** 3. Output 
**--------------------------**/
pipe_output_print
	: PRINT alias_name (COMMA alias_name)*
	;
//pipe_output_file
//	: (TOJSON|TOCSV) (DQUOTE_PHRASE|SQUOTE_PHRASE)
//	;
//pipe_output_db
//	: TODB ID DOT ID
//	;



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
  | modifier? fld=field val=value term_modifier?
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
  | NUMBER
  ;

truncated
  :
  TERM_TRUNCATED
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
  (NUMBER)? //replace the default with user input
  ;

fuzzy :
  (TILDE) // set the default value
  (NUMBER)? //replace the default with user input
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


/* ================================================================
 * =                     LEXER                                    =
 * ================================================================
 */
fragment INT		: '0' .. '9';
fragment ID_CHAR	: ('a' .. 'z' | 'A' .. 'Z' | INT | '_' | DOT);
fragment CHAR   	: ('a' .. 'z' | 'A' .. 'Z');
fragment IP_CHAR    : ('a' .. 'f' | 'A' .. 'F');
fragment ESC_CHAR	:  '\\' .;
fragment EQ			: '=';
fragment NEQ		: '!=';
fragment LESS		: '<';
fragment LESS_EQ	: '=<';
fragment GREATER	: '>';
fragment GREATER_EQ	: '>=';
fragment TERM_CHAR 	: (TERM_START_CHAR | '-' | '+');
fragment TERM_START_CHAR
	: (~(' ' | '\t' | '\n' | '\r' | '\u3000'
        | '"'
        | '(' | ')' | '[' | ']' | '{' | '}'
        | '+' | '-' | '!' | ':' | '~' | '^'
        | '?' | '*' | '\\'
        )
   | ESC_CHAR );

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
SORT    : 'SORT';
TOP     : 'TOP';
BOTTOM  : 'BOTTOM';
HEAD    : 'HEAD';
TAIL    : 'TAIL';
LIMIT   : 'LIMIT';
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
TODB    : 'TODB';
TO      : 'TO';
NULL    : 'NULL';

JOIN_TYPE 
	: 'INNER' WS+ 'JOIN'
	| 'OUTER' WS+ 'JOIN'
	;
JOIN_DIRECTION	: ('LEFT'|'RIGHT'|'FULL');
UNION_TYPE		: 'UNION' (WS+ 'ALL')?;
BOOL			: ('true'|'false');
TIME_TYPE 		: ('SECOND'|'MINUTE'|'HOUR'|'DAY'|'MONTH'|'YEAR');
OPER			: (EQ | NEQ | LESS | LESS_EQ | GREATER | GREATER_EQ);
//LOGC_OPER		: (IS NOT NULL | IS NULL | LIKE | NOT LIKE);
CALC			: (PLUS | MINUS | STAR | SLASH | PER);
//IN_NOTIN		: (IN|NOT IN);
NUMBER
	: MINUS? INT+ (DOT INT+)?
	;
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
ID				: (APOSTRO ID APOSTRO | ID_CHAR+);
DQUOTE_PHRASE 
	: DQUOTE (ESC_CHAR|~('"'|'\\'))+ DQUOTE
	;
SQUOTE_PHRASE
	: SQUOTE (ESC_CHAR|~('\''|'\\'))+ SQUOTE
	;
//APOSTRO_PHRASE
//	: APOSTRO (ESC_CHAR|~('`'|'\\'))+ APOSTRO
//	;
REGEX
	: SLASH (ESC_CHAR|~('/'|'\\'))+ SLASH
	;
IPV4 :
	INT+ '.' INT+ '.' INT+ '.' INT+ (SLASH INT+)?
	;
//IPV6 :
//	(IPv6_CHAR|COLON)+ (SLASH INT+)?
//	;
HEXDIG
 : INT
 | IP_CHAR
 ;
TERM_TRUNCATED:
	(STAR|QMARK) (TERM_CHAR+ (QMARK|STAR))+ (TERM_CHAR)*
	| TERM_START_CHAR (TERM_CHAR* (QMARK|STAR))+ (TERM_CHAR)*
	| (STAR|QMARK) TERM_CHAR+
  ;

WS: [ \t\r\n]+ -> skip;

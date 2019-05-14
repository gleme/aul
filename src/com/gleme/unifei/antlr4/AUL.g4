//
//      AUL Programming Language
//      Another Useless Language
//        Gustavo de Godoi Leme 
//        <gleme@nevada.unr.edu>
//                  & 
//          Luiz Goulart Costa
//      <lhgoulart92@yahoo.com.br>


grammar AUL;

@header {
    package com.gleme.unifei.antlr4;
    import java.util.HashMap;
}

@members {
    private HashMap<String, String> methodMap = new HashMap<>();
    private HashMap<String, String> idMap = new HashMap<>();
}

/* * * * * * * * * * * *
* SYNTACTICAL ANALYSIS *
* * * * * * * * * * * */

/* AST Root */
program @init {
    methodMap.put("cout", "void");
    methodMap.put("cin", "String");
    methodMap.put("main", "void");
}: root_var* root_method* root_main;

/* Method declaration */

root_method: FUNCTION (method_void | method_int | method_double | method_string | method_char);

method_void: VOID_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN L_BRACE block R_BRACE { methodMap.put($ID.text, "void"); };

method_int: INT_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN L_BRACE ret_block["0"] R_BRACE { methodMap.put($ID.text, "int"); };

method_double: DOUBLE_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN L_BRACE ret_block["0"] R_BRACE { methodMap.put($ID.text, "double"); };

method_char: CHAR_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN L_BRACE ret_block["' '"] R_BRACE { methodMap.put($ID.text, "char"); };

method_string: STRING_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN L_BRACE ret_block["\"\""] R_BRACE { methodMap.put($ID.text, "String"); };

/* Main Function declaration */

root_main: FUNCTION VOID_TYPE MAIN L_PAREN R_PAREN L_BRACE block R_BRACE;

/* Variable declarations */

root_var: VAR (int_var | double_var | char_var | string_var) breakline;

int_var: ID { idMap.get($ID.text) == null }? COLON INT_TYPE (EQUALS int_expr)? { idMap.put($ID.text, "int"); };

double_var: ID { idMap.get($ID.text) == null }?  COLON DOUBLE_TYPE (EQUALS double_expr)? { idMap.put($ID.text, "double"); };

char_var: ID { idMap.get($ID.text) == null }?  COLON CHAR_TYPE (EQUALS char_expr)? { idMap.put($ID.text, "char"); };

string_var: ID { idMap.get($ID.text) == null }?  COLON STRING_TYPE (EQUALS str_expr)? { idMap.put($ID.text, "String"); };

/* Commands declarations */

root_cmd: (cmd_if | cmd_for | cmd_while | stmt_assign | stmt_method | stmt_var | java_cmd | stmt_return);

cmd_if: if_c elseif_c* else_c?;

if_c: IF cond L_BRACE root_cmd* R_BRACE;

elseif_c: ELSIF cond L_BRACE root_cmd* R_BRACE;

else_c: ELSE L_BRACE root_cmd* R_BRACE;

cmd_for: FOR for_assign SEMI_COLON cond SEMI_COLON for_assign L_BRACE root_cmd* R_BRACE;

cmd_while: WHILE cond L_BRACE root_cmd* R_BRACE;

stmt_assign: ID EQUALS expr breakline;

for_assign: ID EQUALS expr;

stmt_method: ID L_PAREN void_args R_PAREN SEMI_COLON;

stmt_var: root_var;

java_cmd: JAVA STRING_LIT breakline;

stmt_return: RETURN expr breakline;

/* Expressions declarations */

expr: (ID | ID L_PAREN caller_args R_PAREN | int_expr | double_expr | char_expr | str_expr | cond);

int_expr:   L_PAREN int_expr R_PAREN
    |       INT_LIT
    |       ID L_PAREN caller_args R_PAREN
    |       ID
    |       int_expr MULT_DIV int_expr
    |       int_expr MOD int_expr
    |       int_expr PLUS_MINUS int_expr;

double_expr: L_PAREN double_expr R_PAREN
    |       DOUBLE_LIT
    |       INT_LIT
    |       ID L_PAREN caller_args R_PAREN
    |       ID
    |       double_expr MULT_DIV double_expr
    |       double_expr MOD double_expr
    |       double_expr PLUS_MINUS double_expr;

char_expr:  CHAR_LIT
    |       ID L_PAREN caller_args R_PAREN
    |       ID;
    
str_expr:   STRING_LIT
    |       ID L_PAREN caller_args R_PAREN
    |       ID
    |       str_expr PLUS_MINUS str_expr;

cond:       L_PAREN cond R_PAREN
    |       NOT cond
    |       ID L_PAREN caller_args R_PAREN
    |       ID
    |       ID (EQUALITY | RELATIONAL) expr
    |       double_expr (EQUALITY | RELATIONAL) double_expr
    |       int_expr (EQUALITY | RELATIONAL) int_expr
    |       char_expr EQUALITY char_expr
    |       str_expr EQUALITY str_expr
    |       cond (EQUALITY | LOGICAL) cond;

/* Other grammar rules */

breakline: SEMI_COLON;

var_type:   INT_TYPE
    |       DOUBLE_TYPE
    |       CHAR_TYPE
    |       STRING_TYPE;

method_args: (ID COLON var_type (COMMA ID COLON var_type)*)?;

void_args: (expr (COMMA expr)*)?;

caller_args: (expr (COMMA expr)*)?;

block: root_cmd*;

ret_block[String ret]: root_cmd* stmt_return; 

/* * * * * * * * * * * 
*  LEXICAL ANALYSIS  *
* * * * * * * * * * */

/* Keywords */
FOR:            'for';
WHILE:          'while';
IF:             'if';
ELSIF:          'elsif';
ELSE:           'else';
INT_TYPE:       'int';
DOUBLE_TYPE:    'double';
CHAR_TYPE:      'char';
STRING_TYPE:    'string';
VOID_TYPE:      'void';
MAIN:           'main';
VAR:            'var';
FUNCTION:       'func';
RETURN:         'return';
JAVA:           'Java';

/* Non-functional symbols */
L_PAREN:        '(';
R_PAREN:        ')';
L_BRACE:        '{';
R_BRACE:        '}';
COLON:          ':';
SEMI_COLON:     ';';
COMMA:          ',';

/* Functional symbols */
EQUALITY:       '==' | '!=';
RELATIONAL:     '>=' | '<=' | '>' | '<';
EQUALS:         '=';
LOGICAL:        '&&' | '||';
NOT:            '!';
PLUS_MINUS:     '+' | '-';
MULT_DIV:       '*' | '/';
MOD:            '%';


/* Variable literals */
INT_LIT:          [+-]?[0-9]+;
DOUBLE_LIT:       [0-9]+'.'[0-9]+([eE][+-]?[0-9]+)?;
CHAR_LIT:         '\'' ~[\r\n'] '\'';
STRING_LIT:       '"' (~[\r\n"] | '""')* '"';

/* Identifiers */
ID:           [a-zA-Z_][a-zA-Z0-9_]*;

/* White space */
WHITESPACE:     [\r\t\n ]+ -> skip;
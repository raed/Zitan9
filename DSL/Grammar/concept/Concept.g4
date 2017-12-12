grammar Concept;


concepts : concept* ;

concept :  compilationUnit ;


compilationUnit : (packageSpec)? conceptDefinition ;

packageSpec : 'package' Identifier ';'  ;

conceptDefinition : 'concpet' Identifier (relationClausel)? (impelementsClausel)? classBody ;

relationClausel  : inheritance
                 | composition
                 ;

 inheritance : 'extends' Identifier ;
 composition : 'includes' Identifier (',' 'includes' Identifier) * ;

 impelementsClausel : 'implements' Identifier (',' Identifier) * ;

 classBody : '{' features '}' ;


features : feature* ;

feature : attribute
        | operation
        ;



attribute: attributeArt (',' 'default' '=' VALUE)? ;


attributeArt : dataAttribute
             | conceptAttribute
             | chainAttribute
             | functionAttribute
             | aggregatingAttribute
             ;


dataAttribute :  ':' type ('property' '=' 'functional') ;

type : javaType
     | concreteDomain
     | enumeration
     ;

javaType :  Identifier ;      // toDo allow java types
concreteDomain : atomicType | setType ; // all wrapper types from the concept framework
atomicType : 'AbsoluteTimePoint'
           | 'BooleanObject'
           | 'BooleanType'
           | 'ConstantObject'
           | 'Duration'
           | 'EnumerationObject'
           | 'EnumerationType'
           | 'ExtensionConcept'
           | 'ExtensionLength'
           | 'ExtensionUnit'
           | 'FloatObject'
           | 'IntegerObject'
           | 'NumberObject'
           | 'PhysicalHost'
           | 'RelativeTimePoint'
           | 'StringObject'
           ;

setType    : 'BoundedTimeInterval'
           | 'FloatInterval'
           | 'FloatList'
           | 'IntegerInterval'
           | 'IntegerList'
           | 'Interval'
           | 'ListObject'
           | 'PhysicalHostList'
           | 'StringList'
           | 'TimeInterval'
           ;


 enumeration :  Identifier ; // @toDo grammar for enumeration definition



conceptAttribute : Identifier ':' ('domain' '=' Identifier)? '->' ('range' '=' Identifier) ('is' property)? ;
property : 'functional'
         | 'transitive'
         | 'semmetric'
         | 'reflexiv'
         | 'inverse'
         ;


chainAttribute : conceptAttribute 'concat' conceptAttribute ('concat' conceptAttribute)*  calculableAttribute ;

calculableAttribute : functionAttribute
                    | aggregatingAttribute
                    ;


functionAttribute : expr ;
aggregatingAttribute : 'start' '=' VALUE 'aggregatier' '=' expr  'finalizer' '=' expr ;


// toDo expr must be extended

expr:   Identifier '(' exprList? ')'    // func call like f(), f(x), f(1,2)
    |   Identifier '[' expr ']'         // array index like a[i], a[i][j]
    |   '-' expr                // unary minus
    |   '!' expr                // boolean not
    |   expr '*' expr
    |   expr ('+'|'-') expr
    |   expr '==' expr          // equality comparison (lowest priority op)
    |   Identifier                      // variable reference
    |   INT
    |   '(' expr ')'
    ;
exprList : expr (',' expr)* ;   // arg list






operation : 'op' returnType Identifier '(' formalParameters? ')' block  ; // "void f(int x) {...}"
returnType : 'void'
           | type
           ;


formalParameters
    :   formalParameter (',' formalParameter)*
    ;

formalParameter
        :   type Identifier
        ;


block:  '{' stat* '}' ;   // possibly empty statement block

stat:   block
    |   attribute
    |   'if' expr 'then' stat ('else' stat)?
    |   'return' expr? ';'
    |   expr '=' expr ';' // assignment
    |   expr ';'          // func call
    ;


VALUE :  STRING
        | NUMBER
        ;

/** "any double-quoted string ("...") possibly containing escaped quotes" */
STRING      :   '"' ('\\"'|.)*? '"' ;

/** "a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? )" */
NUMBER      :   '-'? ('.' DIGIT+ | DIGIT+ ('.' DIGIT*)? ) ;
fragment
DIGIT       :   [0-9] ;



/** "Any string of alphabetic ([a-zA-Z\200-\377]) characters, underscores
 *  ('_') or digits ([0-9]), not beginning with a digit"
 */


Identifier
	:	JavaLetter JavaLetterOrDigit*
	;

fragment
JavaLetter
	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment
JavaLetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;




WS  :   [ \t\n\r]+ -> skip ;

SL_COMMENT
    :   '//' .*? '\n' -> skip
    ;


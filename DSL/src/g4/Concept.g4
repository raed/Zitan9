grammar Concept;


concepts : concept* ;

concept :  ID
        |  compilationUnit ;


compilationUnit : (packageSpec)? conceptDefinition ;

packageSpec : 'package' ID ';'  ;

conceptDefinition : 'class' ID (relationClausel)? (impelementsClausel)? classBody ;

relationClausel  : inheritance
                 | composition
                 ;

 inheritance : 'extends' ID ;
 composition : 'includes' ID (',' 'includes' ID) * ;

 impelementsClausel : 'implements' ID (',' ID) * ;

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


dataAttribute : ID ':' type ('property' '=' 'functional') ;

type : javaType
     | concreteDomain
     | enumeration
     ;

javaType :  ;      // toDo allow java types
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


 enumeration :  ; // @toDo grammar for enumeration definition



conceptAttribute : ID ':' ('domain' '=' ID)? '->' ('range' '=' ID) ('is' property)? ;
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

expr:   ID '(' exprList? ')'    // func call like f(), f(x), f(1,2)
    |   ID '[' expr ']'         // array index like a[i], a[i][j]
    |   '-' expr                // unary minus
    |   '!' expr                // boolean not
    |   expr '*' expr
    |   expr ('+'|'-') expr
    |   expr '==' expr          // equality comparison (lowest priority op)
    |   ID                      // variable reference
    |   INT
    |   '(' expr ')'
    ;
exprList : expr (',' expr)* ;   // arg list






operation : 'op' returnType ID '(' formalParameters? ')' block  ; // "void f(int x) {...}"
returnType : 'void'
           | type
           ;


formalParameters
    :   formalParameter (',' formalParameter)*
    ;

formalParameter
        :   type ID
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

 ID : LETTER (LETTER|DIGIT)*;
 fragment
 LETTER      :   [a-zA-Z\u0080-\u00FF_] ;


WS  :   [ \t\n\r]+ -> skip ;

SL_COMMENT
    :   '//' .*? '\n' -> skip
    ;


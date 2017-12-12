grammar Concepts;
import ConceptLexerRules;

concepts : concept* ;

concept
    : compilationUnit
    ;


compilationUnit
    : packageDeclaration? importDeclaration* typeDeclaration* EOF
    ;


packageDeclaration
    :   'package' qualifiedName ';'
    ;


importDeclaration
    :   'import' 'static'? qualifiedName ('.' '*')? ';'
    ;


typeDeclaration
    : conceptDeclaration
 // | enumDeclaration

    ;



// normalClassDeclaration
//    : classModifier* 'base'? 'concept' Identifier typeParameters? superclass? superinterfaces? classBody
//    ;

conceptDeclaration
    : conceptModifier* 'concept' 'base'? Identifier relationalClausel?  classBody
    ;


relationalClausel
    : superConcept
    | includeConcept
    ;



superConcept
    : 'extends' conceptType
    ;


includeConcept
    : 'includes' conceptType (',' 'includes' conceptType) *
    ;



// classType
//     :	annotation* Identifier typeArguments?
//     |	classOrInterfaceType '.' annotation* Identifier typeArguments?
//     ;

conceptType
        : Identifier
        ;


// toDo
classBody
     : '{' attributeList? '}'
     ;

attributeList
    : attribute (';' attribute)*
    |
    ;


attribute
    : dataAttribute
    | conceptAttribute
    | chainAttribute
    | functionAttribute
    | aggregatingAttribute
    ;



dataAttribute
    : Identifier ':' type ('attrDAG')?  ('is' property)?    // an attrDAG will be also saved in DAG<attribute>
    ;


conceptAttribute
    :
    ;


// conceptAttribute
//    : Identifier ':' 'range' conceptType ('is' property)?
//    ;


 property : 'functional'
          | 'transitive'
          | 'semmetric'
          | 'reflexiv'
          | 'inverse'
          ;



//toDo
chainAttribute
    :
    ;

//toDo
functionAttribute
    :
    ;

//toDo
aggregatingAttribute
    :
    ;


type
    : primitiveType
    | referenceType
//  | enumeration       // allow later
    ;

primitiveType
	:	numericType
	|	'boolean'
	;


numericType
	:	integralType
	|	floatingPointType
	;

integralType
	:	'byte'
	|	'short'
	|	'int'
	|	'long'
	|	'char'
	;

floatingPointType
	:	'float'
	|	'double'
	;






//toDo define javatype

referenceType
    : Identifer
    | concreteDomain
    ;


concreteDomain
    : atomicType
    | setType  // all wrapper types from the concept framework
    ;


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

property
    : 'functional'
    | 'transitive'
    | 'semmetric'
    | 'reflexiv'
    | 'inverse'
    ;



// toDo
assignment
    :
    ;



 conceptModifier
    :   'public'
    |	'protected'
    |	'private'
    |	'abstract'
    |	'static'
    |	'final'
    ;




qualifiedName
    :   Identifier ('.' Identifier)*
    ;


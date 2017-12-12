grammar Concept ;
import JavaLexerRules;

compilationUnit
		:  packageDeclaration? importDeclaration* conceptDeclaration* EOF
		;


packageDeclaration
	: 'package' packageName ';'
	;



packageName
	:	Identifier
	|	packageName '.' Identifier
	;


importDeclaration
		:   'import' 'static'? qualifiedName ('.' '*')? ';'
		;


conceptDeclaration
		:  conceptModifier* 'concept' Identifier 'base'? relationalClausel?  conceptBody
		;



conceptModifier
		:	'public'
		|	'protected'
		|	'private'
		|	'static'
		|	'final'
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


conceptBody
		: '{' attribute* '}'
		;


 attribute
		: dataAttribute
		| conceptAttribute
		;


dataAttribute
		: Identifier ':' type  propertyList? 'dag'?
		;


conceptAttribute
         : Identifier ':' range
		 ;

range
	: 'range' referenceType
	;




propertyList
	: '(' property (',' property)* ')'
	;

 property
	:  'functional'
	|  'transitive'
	|  'semmetric'
	|  'reflexiv'
	|  'inverse'
	;


//toDo add referenceType

type
		: primitiveType
		| referenceType
		;


referenceType
        : conceptType
        | concreteDomain
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



//toDo
conceptType
				: Identifier
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

qualifiedName
		:   Identifier ('.' Identifier)*
		;
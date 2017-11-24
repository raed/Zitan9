The RQLJ Rule-Engine
---------------------

The Rule Engine consists of a number of 'processors' which operate on a so called query array, which is an Object[]-array.
The purpose of the query array is to store variable bindings of a rule language.

Example: Person(X) with X.owns (Car(Y) with Y.price > 10000 & Z = Y.price)
In this case one would need a query array with three cells to store the bindings for X,Y,Z.

Each processor has a definition-class and a processor-class.
The definition-class stores all parameters for controlling the operation of the processor.
With the makeProcessor method in the definition-class one can construct an arbitrary number of processors,
which can even work in parallel.
Each processor has an inputStream and an outputStream.
The processor operates by transforming the inputStream into the outputStream.
Once the outputStream has been worked off, one can set a new inputStream, and gets a new outputStream.
Thus, a processor can process an arbitrary number of queries.

The input to the inputStream is always a stream of the above mentioned query arrays.
It can be just one query array, or there can be many query arrays in sequence.
The processors may extend a single query array into a longer stream of query arrays.
Thus, the output for a single query array may be an arbitrary long sequence of answer arrays.

However, in order to save memory, the query arrays are usually not copied, but their cells are just modified.
Although the outputStream delivers a sequence of query arrays, they are usually always the same array,
just with different contents in their cells. There are, however, exceptions, where it is necessary
to create new query arrays during processing.


Processor Sequences
------------------
A particular kind of processor is a SequenceProcessor.
It consists of a sequence of processors, which themselves can again be sequence processors.
The sequence of processors operates on the query stream by chaining the output stream
of processor i into the input stream of processor i+1.
Form the outside, there is no difference between a SequenceProcessor or a basic processor.


Parameters for the Processors
-----------------------------
All processors need certain control parameters.
Usually theses are defined in the processor definition.
Some of the parameters, however, can also be taken from the query array.
This makes the processors very flexible.


Basic Processor Classes
-----------------------
There are three different classes of processors:

- PipelineProcessors, for general manipulations of the stream pipeline
- HierarchyProcessors, for accessing the concept hierarchy and the attribute values
- LogicProcessors, for logical operations and quantifications.


PipelineProcessors
------------------
- AssignmentProcessor
  for assignments like X = Y. They transfer values form one query cell to another query cell.
- FilterProcessors
  they apply a filter predicate to the entire query array, and therefore can block a particular query array.
- PeekProcessors
  they apply a consumer function to the entire query.
  The consumer function may even change the contents of the query array, but cannot block it.
- TransferProcessors
  they can change the structure of the query array.
  If, for example, one processor requires three cells, and a subsequent processor requires five cells,
  a TransferProcessor can turn the three-cell array into a five-cell array and transfer the contents
  to the new array.

HierarchyProcessors
-------------------
These processors do the actual work. They access the concept hierarchy and the attribute values
and fill the query array with concrete values.

- ConceptProcessors
  they can
  - fill the query array with concepts taken from the concept hierarchy.
    Example: Person(X) fills the X-cell with persons
    Here we can distinguish whether only individuals are to be produced,
    or sub- or superconcepts in the hierarchy. Even the traversal through the DAG can be
    influenced (breadth-first or depth-first).
  - check if a given concept is a sub-concept of another concept and block the query if it is not the case.

- DataAttribute Processors
  They access for a given concept and a given DataAttribute (predefined or taken from the query) the
  attribute values, filter them, or do other computations with them, and store them in the query array.

  There are
  - DataAttributeFunctionalSingle processors for accessing a single attribute value
  - DataAttributeFunctionalMultiple processors for accessing attribute values
    for several functional attributes and processing them simultaneously.
  - DataAttributeRelational processors for accessing relational data attributes and put them
    into the query stream.

- ConceptAttribute Processors.
  Since the values for concept attributes are again concepts, there may be a need for further processing them.
  Therefore each ConceptAttribute processor may have a sub-processor for processing the attribute values.
  Example:
    Person(X) with X.owns(Car(Y) with Y.color == "red") and X.age = 20,
  Here the part (Car(Y) with Y.color == "red") would be treated by a ConceptAttribute processor
  that accesses all cars of Y and calls a sub-processor to filter out the red cars.

  There are
  - ConceptAttributeFunctional processors for functional concept attributes, and
  - ConceptAttributeRelational processors for relational concept attributes.


LogicProcessors
---------------
These are responsible for dealing with logical connectives (junctors) and quantifications.

- JunctorProcessors for NOT,AND,OR,XOR,IMPL,EQUIV
  Each one works as a filter for a query.
  They have one or more sub-processors for producing a true/false decision for a filter.

  Example:
  Person(X) with (X.age > 20 AND X.owns(CAR(Y) with Y.color = "red")
  The AND-processor would have the two sub-processors, one for X.age > 20 and the other one (actually a sequence)
  for X.owns(CAR(Y) with Y.color = "red")

- QuantificationProcessors for SOME, ATLEAST n, ATMOST n, EXACT n, RANGE min,max

  These ones can work in three different modes:
  - filter mode:
    Example: Student(X) with ATLEAST 3 X.mark < 2
    would filter out the students with atleast three marks < 2.
    The marks themselves would not appear in the query stream.
  - MIN-mode
    Example: Student(X) with ATLEAST 3 X.mark < 2
    It would again filter out the students with atleast three marks < 2,
    but would in addition put THE FIRST THREE marks < 2 into the query stream.
  - MAX-mode
    Example: Student(X) with ATLEAST 3 X.mark < 2
    It would again filter out the students with atleast three marks < 2,
    but would in addition put ALL marks < 2 into the query stream.

  The MIN- and MAX-mode might require the sub-processors to generate a longer sequence
  of answers before the filter decision can be made. In this case the answers need to be stored,
  which in turn causes that the query-array might be copied several times.

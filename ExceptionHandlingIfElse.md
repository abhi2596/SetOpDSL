# Exception Handling and IF Else Construct Implementation

HW4 implementation is in src/main/scala/SetExIf and the testcases are in test/scala/ExceptionIfElse

## Then(op*),Else(op*)
Then() and Else() are functions which takes variable number of arguments which can contain different operations which
can be of type SetDSL.SetOp1 or SetClass.SetClassImp or it can take another IF in it which is of type
SetExIf.ExIf. If then contains another IF in it then we can have nested If,which is also implemented in this
implementation.
Then(),Else() are functions which have the same implementation the only difference is if the condition is true
then the statements in then will be executed but if condition is false then statements in else will be
executed.
Then() and Else() can only be used inside IF construct they cannot be used in isolation if used this 
implementation will return an error message
If we call Then() directly it returns an error message "Cannot use then without if".
If we call Else() directly it returns an error message "Cannot use else without if".

## IF(condition,Then(),Else())
IF construct takes three arguments one is condition, second is Then function and the other is Else function
condition is of Type SetDSL.SetOp1 expressions it can contain two expressions Check() and Delete() which can be used as
conditions as they return boolean. For the documentation of SetDSL.SetOp1 refer to this 
#### [link](SetOpDSL.md).


### Example 
IF(
Check(SetName("b"),Value(1)),
Then(Insert(SetName("b"),Valset(3,4))),
Else(Insert(SetName("b"),Valset(7,8)))
)

An example here the condition will check if 1 is in SetName("b") if yes then the statements in then
will be executed otherwise the statements in else will be executed 
So if condition is true then 3,4 will be inserted into Set "b" , Otherwise 7,8 will be inserted into Set "b"

## ExceptionClass()
ExceptionClass takes one argument which is ClassDef which contains the class name and field and the Constructor which contains the 
error message which is the error message which should be thrown if the exception is encountered of this class.

### Example
ExceptionClass(ClassDef("exception",Field("a"),Constructor(Assign(SetName("a"),Valset("error message")))))

## Throw(NewObject("Exceptionclassname","Anyobject"))
Throw takes only one operation which is NewObject().
So in this implementation throw will create a NewObject of the exception classname and the exception classname constructor contains the error
message. The throw implementation will take the msg and assign it to a variable which can be retrieved by Catch() 

For ClassDef and NewObject documentation refer to this 
##### [link](ClassOperation.md)

### Example
Throw(NewObject("exception","z"))
 

## Try()
Try contains all the code we want to execute which might contain an exception. Try takes variable number of arguments which can be of 
type which can be SetDSL.SetOp1 or SetClass.SetClassImp or SetExIf.ExIf
In this implementation Try cannot exist in isolation if declared without CatchException() then it will return an error message "try cannot exist without catch".

### Example
Try(
Assign(SetName("b"),Valset(1,2)),
IF(Check(SetName("b"),Value(2)),
Then(Throw(NewObject("exception","z")),
Insert(SetName("b"),Valset(3,4))),
Else())
)
In this try example Set b is assigned with 1,2 then the IF construct is executed so in if the condition is 
evaluated as Set b contains 2 hence the then statement is evaluated which throws the exception.


## Catch()
Catch can take variable number of arguments but the last argument should be getMsg() which contains the error message which is thrown by throw()
function. In this implementation Catch cannot exist in isolation it will return error message "cannot use catch without try".

#### Example of simple Catch
Catch(getMsg())
it will retrieve the error message which will be raised by Throw expression\

## CatchException()

CatchException takes three arguments one is the exception class name and the other two are Try and Catch functions. 
So in this implementation Try can have CatchException in it and if CatchException inside Try results in exception then the exception will travel to 
the outer Catch as well and ignore all statements in Try.

#### Example 
ExceptionClass(ClassDef("exception",Field("a"),Constructor(Assign(SetName("a"),Valset("Exception encountered")))))
CatchException("exception",
Try(
Assign(SetName("b"),Valset(1,2)),
IF(Check(SetName("b"),Value(1)),
Then(Throw(NewObject("exception","z")),
Insert(SetName("b"),Valset(3,4))),
Else(
Insert(SetName("b"),Valset(5))
))
),
Catch(
getMsg()
))

the exception class name is exception and the try statement as different operations first operations executes and Assigns 1,2 to Set b
then the IF construct checks if Set b contains 1 as it is True it throws and exception and the Catch will catch the exception and the return 
message will be the error message and the error message will be "Exception encountered"





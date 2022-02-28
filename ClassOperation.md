# ClassOperation 

# To run 
Navigate from src to main In main SetClassImp.scala is the file which contains the code For testing navigate to test in test SetClassImptest.scala

# Field()
Field takes one argument which is a string this string is the name of field variable
Field operation has a value which acts as instance variable of class which will be initialized in Constructor of that class
Example : Field("a") it creates a field in class like Class setop{ val a; }

# Constructor()
Constructor can take multiple operations as parameters and All the operations in Constructor should be on Field Variables defined in the class and 
Assign Operation in SetDSL as constructor is used to initialize variables defined.
Constructor operation is used to initialize the field variables declared by Field("a") 
Example: Constructor(Assign(SetName("a"),Valset(1,2))) this assigns value 1,2 to set "a" where a is the field variable declared in the class

# Method()
Method can take many arguments but the first argument is always methodname and the operations after that can be any SetOperations defined by SetDSL and we can pass multiple 
parameters
Example : Method(name,Insert(SetName("a"),Valset(3,4))) this method operation when called using InvokeMethod assigns values 3,4 to the set "a"

# ClassDef()
ClassDef is used to create a class with fields,constructors,Methods and it may also contain ClassDef inside it.
Example : ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2)),Method(name,Insert(SetName("a"),Valset(3,4))))

# NewObject()
NewObject takes two arguments one is name of the class for which we are creating an object and the other is object name 
NewObject when called evaluates the Constructor of the class and returns the last statement evaluated value 

Example NewObject("setop","z") this creates a object with name "z" and the object is of class setop and by referring to ClassDef in above Example the return value will be
Set(1,2) as Assign(SetName("a"),Valset(1,2) returns Set(1,2)

# InvokeMethod()
InvokeMethod takes two parameters one is the object name of the class which second parameter in NewObject and the other is Methodname of the class which we want to call
InvokeMethod returns the value of the last operation in Method()

Example: InvokeMethod("z","name") referring to NewObject and ClassDef example above we can see that object z belongs to setop class and there is a method in ClassDef called name 
as the Constructor initializes Set "a" to 1,2 and after evaluating the method Set "a" becomes 1,2,3,4 as the operation is Insert in SetDSL it inserts values into already 
existing set

# Extends()
Extends is used to implement Inheritance so for this to work first we need to define two classes using ClassDef 
Extends takes two parameters both are classnames one is classname which will act as parentClass and other is class which will inherit from parentClass

Example : Extends("setop","setop1") here class setop1 inherits properties from class setop

# NestedClassObject()
this operation takes three operations the outer Object name which is bound to outerclass then the innerclass name and the Object name which we want to give to this
this operation also evaluates the constructor in nested class

Example : ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),
      ClassDef("setop1",Field("b"),Constructor(Assign(SetName("b"),Valset(3,4))),
        Method("name",Insert(SetName("b"),Valset(1,2))),Method("name1",Insert(SetName("b"),Valset(5,6))))) example of Nested Class implementation
NewObject("setop","z") the object z is bound to class setop
NestedClassObject("z","setop1","y") it creates an Object y which points to setop1 which is nested in class which is bound to object "z" and it returns Set(1,2)

# NestedClassMethod()
this operation takes two parameters one is object name of nested class and name of the method which we want to call it returns the last expression 

Example : referring to the above example 
NestedClassMethod("y","name1") which will return Set(1,2,5,6) 

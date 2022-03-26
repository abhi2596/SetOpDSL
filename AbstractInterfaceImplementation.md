# HW3 Documentation

## AbstractMethod()
AbstractMethod takes one parameter which is the name of the method and it is a string
#### Example: AbstractMethod("name")

## AbstractClassDef()
Abstract Class Def can take multiple parameters but the first parameter should be Abstract Class name and the rest of the parameters can be Field, Method or an
AbstractMethod. 

#### Example: AbstractClassDef("abc2",Field("b"),Constructor(Assign(SetName("b"),Valset(3,4))),Method("name",Insert(SetName("a"),Valset(5,6))),AbstractMethod("name1"))
In this example an Abstract class with name abc2 is defined with fields b and a constructor which is used to initialize the field variable, Method which is Inserting values into the Setname b which was created by Constructor and a Abstract Method which just has its name and should be implemented by concrete class which 
inherits this abstract class

## InterfaceDecl(name,List of operations)
InterfaceDecl takes many parameters but the first parameter is the name of the interface and the rest of the parameters can be Field,Method or AbstractMethod
as in java InterfaceDecl can have default implementation hence Method is also allowed inside Interface it acts as default method.

#### Example: InterfaceDecl("setop",Method("name",Assign(SetName("x"),Valset(1,2))),AbstractMethod("abm"))
In this example an interface with name setop is created and it contains a Method which acts as default method in interface and an Abstract method with name abm

## Implements()
Implements also takes multiple parameters and the first parameter is the class name which is implementing the interfaces and the rest of parameters can be interface name which the class is implementing. So in this implementation a class can implement multiple interfaces.

#### Example: Implements("minfclass","minf1","minf2")
in this example minfclass is the classname which implements minf1 and minf2 interfaces

### Can a class/interface inherit from itself?
No, a class/interface cannot inherit itself. In this implementation,an error message will be printed on screen if class tries to inherit itself same with interface 
the error message will be as follows for class "Class cannot inherit itself" and for interface the error message is "interface cannot implement another interface"

### Can an interface inherit from an abstract class with all pure methods?
No a interface can only inherit from an interface. In this implementation if a interface tries to extend a class an error message is returned "Interface cannot inherit from a class"

### Can an interface implement another interface?
No an interface cannot implement another interface. In this implementation an error will be returned that "interface cannot implement another interface".

### Can a class implement two or more different interfaces that declare methods with exactly the same signatures?
Yes, a class can implement two interfaces with the same method signature but that method should be implemented only once in the class.In this implementation we dont have method signatures like int or double so a class can implement two or more interfaces with same method as return type is always a Set

### Can an abstract class inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures?
An abstract class can inherit from another abstract class and as already explained the interfaces and abstract class methods have same signature here as we are not mentioning the return type of the method. So in this implementation if we declare Methods with same name then the abstract class methods will override the interface
methods.

### Can an abstract class implement interfaces?
Yes a abstract class can implement interfaces it can just implement interface without providing implementation. If a abstract class implements an interface partially i.e. it did not implement all the methods in the interface then if a class inherits the abstract class then this class should implement the remaining methods of interface which abstract class did not implement.
In this implementation abstract class can implement an interface and follows the rules as mentioned above

### Can a class implement two or more interfaces that have methods whose signatures differ only in return types?
No, its an error.If two interfaces contain a method with the same signature but different return types, then it is impossible to implement both the interface simultaneously.
But in this implementation as the return type is always a set so the signature does not change so two interfaces can have method with same name and a class can implement this 
methods

### Can an abstract class inherit from a concrete class?
Yes an abstract class can inherit from a concrete class this is supported by this implementation
### Can an abstract class/interface be instantiated as anonymous concrete classes?
It is a compile-time error if an attempt is made to create an instance of an abstract class using a class instance creation expression
In this implementation it will give an error and the error message is as follows "Abstract class cannot be instantiated"
Interfaces may not be directly instantiated.
In this implementation it will give an error and the error message is as follows "Interface cannot be instantiated"

Not implemented nested interfaces and nested abstract classes 
Not implemented access modifiers in classes,interfaces,abstract classes

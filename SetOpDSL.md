# To run this DSL
run navigate from src to main 
In main SetDSL.scala is the file which contains the code 
For testing navigate to test in test SetDSLtest.scala


# SetOpDSL

Created a Domain Specific Language for Set Operations using Scala

## Value()
takes a single argument and the single argument passed can be a number or a string 
#### Example: Value(1), Value("string"), Value(3.0)

## Variable() 
takes a single argument and the single argument should be variable name which exists in the Language if not present it assigns a value of 0
#### Example: Variable("var")

## SetName() 
takes a single argument and the single argument should be a name of the set it should be enclosed in quotation marks 
#### Example: SetName("someSetName")

## Create() 
takes two arguments either Value or Variable and it creates a Set with these two values 
#### Example: Create(Value(1),Variable("string")) 

## ValSet()
this can used if we want to pass multiple arguments to the Assign operation
#### Example: ValSet(1,2,3,4)

## Insert() 
takes two arguments one is SetName and the other is Create Create should contain the values which needs to be inserted in the SetName specified and SetName should contain the SetName in which the values are inserted. For Insert to work the SetName should be present the system if not present it may throw error
#### Example: Insert(SetName("someSetName"),Create(Value(1),Variable("string")))  

## Assign() 
takes two arguments like Insert() the only difference between Insert and Assign creates a set and assigns the values specified in Create to the a set which can later be referenced by the SetName() 
#### Example: Assign(SetName("setName"),Create(Value(1),Variable("string")))

## Check() 
takes two arguments one is SetName() and the other is Value or Variable it checks whether the Value() or the Variable is present in the SetName if present it return True if not it returns False
#### Example: Check(SetName("someSetName"),Value(1)) , Check(SetName("someSetName"),Variable(1))

## Delete() 
takes two arguments one is SetName() and the other is Value or Variable it deletes the Value or Variable specified from the SetName it does not return anything
#### Example: Delete(SetName("someSetName"),Value(1)), Delete(SetName("someSetName"),Value(1))

## Union(), Intersection(), SetDifference(), SymmetricDifference(), CartesianProduct()
takes two arguments both are SetName() which contain the setnames for which we want to calculate the union 
#### Example: Union(SetName("someSetName"),SetName("SetName"))

## bindingMacro() 
it takes two arguments one is the name which we want to assign to this Macro and the other is the SetDSL operation from the above displayed so this binds Macro to the SetOperation
#### Example: bindingMacro("name",Union(SetName("someSetName"),SetName("SetName")))

## Macro()
the above operation assigns a SetDSL operation to a macro and to refer to it we can access it using the string used above 
Example: Macro("name") this replaces the operation Macro("name") with whatever operation was specified using bindingMacro so if bindingMacro("name",Union(SetName("someSetName"),SetName("SetName"))) was the command used for binding then in the program if we call Macro("name") it will be replaced by Union(SetName("someSetName"),SetName("SetName"))

## Scope()
the above operation can be used if the user wants to implement scoping in their SetDSL it works like these
Example: Scope("name",Scope("name1",Assign(SetName("SetName"),ValSet(1,2)),Value(1)),Assign(SetName("SetName"),ValSet(3,4)) the SetName in name1 contains 1,2 whereas the SetName in name scope contains 3,4 this is how a user can implement Scoping

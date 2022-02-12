# SetOpDSL

Created a Domain Specific Language for Set Operations using Scala

## Value()
takes a single argument and the single argument passed can be a number or a string Example: Value(1), Value("string"), Value(3.0)

## Variable() 
takes a single argument and the single argument should be variable name which exists in the Language if not present it assigns a value of 0

# SetName() 
takes a single argument and the single argument should be a name of the set it should be enclosed in quotation marks 
Example: SetName("someSetName")

# Create() 
takes two arguments either Value or Variable and it creates a Set with these two values 
Example: Create(Value(1),Variable("string")) 

# Insert() 
takes two arguments one is SetName and the other is Create Create should contain the values which needs to be inserted in the SetName specified and SetName should contain the SetName in which the values are inserted. For Insert to work the SetName should be present the system if not present it may throw error
Example: Insert(SetName("someSetName"),Create(Value(1),Variable("string")))

# Assign() 
takes two arguments like Insert() the only difference between Insert and Assign is Assign checks if the SetName is present or not if not present it creates a new one 
and assigns the values specified in Create to the a set which can later be referenced by the SetName() 
Example: Assign(SetName("setName"),Create(Value(1),Variable("string")))

# Check() 
takes two arguments one is SetName() and the other is Value or Variable it checks whether the Value() or the Variable is present in the SetName if present it return True if not it returns False
Example: Check(SetName("someSetName"),Value(1)) , Check(SetName("someSetName"),Variable(1))

# Delete() 
takes two arguments one is SetName() and the other is Value or Variable it deletes the Value or Variable specified from the SetName it does not return anything
Example: Delete(SetName("someSetName"),Value(1)), Delete(SetName("someSetName"),Value(1))





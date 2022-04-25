# Partial Implementation and Optimization

## Partial Evaluation implementation is done in individual files in SetDSL.scala and SetExIf.scala and optimization is implemented in a seperate
## folder src/main/Optimization.scala and the test cases can be found src/test/parOp.scala


## Partial Evaluation

1. Assign() operation assigns values to a set and it takes Create and Valset input to create a Set so if in Create a variable is passed 
of which there are no bindings then the set returned would contains Variable() 
Example: Assign(SetName("x"),Create(Variable("x"),Value(1))) the resulting expression would be Set(Variable("x"),1) wherein x does not have a 
binding
2. Insert() operation has two different implementations so if set exists then it will act like Assign i.e. if element passed in Create does not 
have a binding then the return will be Set(Variable()) so in other case if SetBinding does not exist then the return will contain the SetName 
but the second parameter will be replaced by the Set which was supposed to be inserted
Example: Insert(SetName("x"),Valset(1,2)) return will be Insert(SetName("x"),Set(1,2))
3. Union,Intersection, Difference, SetDifference, Symmetric Product all these implementations expect SetName() as its parameters so if one of the
binding is not there then the other binding will be replaced with the Set and the name which is not in binding will be sent as SetName()
Example: Union(SetName("x"),SetName("y")) returns Union(Set(1,2),SetName("y")) here y does not have a binding does not exist 


## Optimization


Following optimizations are implemented:
1.For Intersection:
If Intersection(SetName("x"), SetName("x")) gives SetName("x")
Intersection(SetName("x"), SetName("y")) here if SetName("y") is empty then the Intersection with empty set gives SetName("x")
if both the sets are empty then Set() will be returned
2.For Union:
If Union(SetName("x"), SetName("x")) gives SetName("x")
Union(SetName("x"), SetName("y")) here if SetName("y") is empty then Union with empty set gives SetName("x")
3.For Difference:
If Difference(SetName("x"), SetName("x")) gives Set()
Difference(SetName("x"), SetName("y")) here if SetName("y") is empty then Difference with empty set gives SetName("x")
Difference(SetName("x"), SetName("y")) here if SetName("x") is empty then Difference with empty set gives Set()
4.For Symmetric difference:
If SymmetricDifference(SetName("x"), SetName("x")) gives Set()
5.For If-Else:
If-else has been optimized such that if the condition is true, then thenExp evaluation is returned, else elseExp evaluation is returned.
object SetDSL:
  // this a map with variable binding it contains variable name and the value of variable
  private val binding : collection.mutable.Map[String,Any]= collection.mutable.Map("var"-> 1)
  // this is map with SetBinding it contains the setname and its contents
  private val SetBinding : collection.mutable.Map[String,Set[Any]]= collection.mutable.Map("someSetName" -> Set(3))
  // this is map of Macro operation binding it will contains the Macro operation name and the operation binded to it
  private val MacroBinding : collection.mutable.Map[String,SetOp1]= collection.mutable.Map[String,SetOp1]()
  // ScopeBinding map it contains the scope name and the sets contained in it
  val ScopeBinding : collection.mutable.Map[String,collection.mutable.Map[String,Set[Any]]] = collection.mutable.Map()
  // this variable is used to resolve which scope we are present in
  private val name = scala.collection.mutable.Stack[String]()
  // global is pushed into the scope so all operation will act on global unless specified
  name.append("global")
  type A = SetOp1|Any
  // enum defining all the SetOperation
  enum SetOp1:
    case Variable(Name:String)
    case Value(Val:Any)
    case SetName(Name: String)
    case Check[A](op1:A,op2:A) // partial implemented
    case Valset(x:Any*)
    case Create(x:SetOp1,y:SetOp1) // partial implemented
    case Insert[A](op:A, op1:A) // partial implemented
    case Assign(x:SetOp1,y:SetOp1) // partial implemented
    case Delete[A](x:A,y:A) // partial implemented
    case Union[A](x:A,y: A) // partial implemented
    case Intersection[A](x:A,y:A) // partial implemented
    case SetDifference[A](x:A,y:A) // partial implemented
    case CartesianProduct[A](x:A,y:A) // partial implemented
    case SymmetricDifference[A](x:A,y:A) // partial implemented
    case Scope(x:String,y:SetOp1*)
    case bindingMacro(x:String,y:SetOp1)
    case Macro(x:String) // partial implemented
    case getSet(name:String)
    case getScopeSet(scope:String,name:String)

    // this is the evaluation function for the SetOperations
    def eval: Any=
      this match{
        case getSet(name)=>
          if(SetBinding.contains(name)) SetBinding(name)
          else getSet(name)
        case getScopeSet(scope,name) =>
          if(ScopeBinding.contains(scope)) {
            if(ScopeBinding(scope).contains(name)) ScopeBinding(scope)(name)
          }
          else getScopeSet(scope,name)
        // Value(x) just returns the value passed
        case Value(x)=> x
        // Variable(x) takes a string and checks if it is present in the binding if yes then returns it else assigns a value of 0
        // and returns it
        case Variable(x)=>
          if (binding.contains(x))
            binding(x)
          else
            Variable(x)
        // SetName(x) just return the name which is passed
        case SetName(x) => x
        // Check takes two arguments one is the name of the set and the second is the value which is to be checked
        // it return True if the value is found else returns False
        case Check(op1,op2) =>
          if(name.top=="global"){
            if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String])){
              val set = SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String])
              if(op2.asInstanceOf[SetOp1].eval.getClass.getSimpleName == "Variable") Check(set,op2)
              else if(set.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[Any])) true
              else false
            }
            else{
              if(op2.asInstanceOf[SetOp1].eval.getClass.getSimpleName == "Variable") Check(op1,op2)
              else Check(op1,op2.asInstanceOf[SetOp1].eval)
            }
          }
          else{
            val set = op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]
            if (ScopeBinding(name.top)(set).contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[Any]))
              true
            else
              false
          }
        // Assign takes two arguments one is setname and the other is create which creates a set
        // assign assigns the setname to the set created by create it basically binds the setname to a set
        // assign creates a set if not present and assign the set if present it just assigns the set created by create
        case Assign(op1,op2) =>
          if(name.top=="global"){
            val setName = op1.eval.asInstanceOf[String]
            val insert = op2.eval
            SetBinding += (setName -> Set())
            val set = SetBinding(setName).union(insert.asInstanceOf[Set[Any]])
            SetBinding += (setName -> set)
            SetBinding(setName)
          }
          else{
            val setName = op1.eval.asInstanceOf[String]
            val insert = op2.eval
            val setBinding = ScopeBinding(name.top)
            ScopeBinding(name.top) += (setName -> Set())
            ScopeBinding(name.top)+= (setName->insert.asInstanceOf[Set[Any]])
            ScopeBinding(name.top)(setName)
          }
        // Valset is used we want to pass in multiple arguments to creates a set
        case Valset(x*)=>
          Set(x *)
        case Create(x,y)=>
          if((x.getClass.getSimpleName != "Variable" && x.getClass.getSimpleName != "Value") ||
          (x.getClass.getSimpleName != "Variable" && x.getClass.getSimpleName != "Value")) return "Prohibited use of language"
          Set(x.eval,y.eval)

        //Insert takes two arguments one is SetName to which we want to insert values and the other argument is Create which contains the
        // values we want to insert
        case Insert(op1,op2) =>
          if(name.top=="global") {
            val setname = op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]
            val insert = op2.asInstanceOf[SetOp1].eval
            if (SetBinding.contains(setname)){
              val set = SetBinding(setname).union(insert.asInstanceOf[Set[Any]])
              SetBinding += (setname -> set)
              SetBinding(setname)
            }
            else{
              Insert(op1,insert)
            }
          }
          else{
            val setName = op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]
            val insert = op2.asInstanceOf[SetOp1].eval
            val setBinding = ScopeBinding(name.top)
            if(ScopeBinding(name.top).contains(setName)){
              val set = ScopeBinding(name.top)(setName).union(insert.asInstanceOf[Set[Any]])
              ScopeBinding(name.top)+= (setName -> set)
              ScopeBinding(name.top)(setName)
            }
            else{
              Insert(op1,insert)
            }
          }
        // Delete takes two arguments setname and the value to be deleted
        case Delete(op1,op2) =>
          if (name.top=="global") {
            val setname = op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]
            if(SetBinding.contains(setname.asInstanceOf[String])) {
              if(SetBinding(setname).contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[Any])) {
                SetBinding(setname.asInstanceOf[String]) -= op2.asInstanceOf[SetOp1].eval.asInstanceOf[Any]
                SetBinding(setname.asInstanceOf[String])
              }
              else{
                if(op2.asInstanceOf[SetOp1].eval.getClass.getSimpleName == "Variable"){
                  return Delete(SetBinding(setname.asInstanceOf[String]),op2.asInstanceOf[SetOp1].eval)
                }
                SetBinding(setname.asInstanceOf[String])
              }
            }
            else{
              if(op2.asInstanceOf[SetOp1].eval.getClass.getSimpleName == "Variable"){
                return Delete(op1,op2)
              }
              Delete(op1,op2.asInstanceOf[SetOp1].eval)
            }
          }
          else {
            val setname = op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]
            ScopeBinding(name.top)(setname) -= op2.asInstanceOf[SetOp1].eval.asInstanceOf[Any]
            ScopeBinding(name.top)(setname)
          }
        // it takes two arguments setname and then calculates Union for these two sets
        case Union(op1,op2) =>
          if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]) && SetBinding.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]).union(SetBinding(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            Union(SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]),op2)
          else if(SetBinding.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            Union(op1,SetBinding(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else Union(op1,op2)
        // it takes two arguments setname and then calculates Intersection for these two sets
        case Intersection(op1,op2)=>
          if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]) && SetBinding.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]).intersect(SetBinding(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            Intersection(SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]),op2)
          else if(SetBinding.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            Intersection(op1,SetBinding(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else Intersection(op1,op2)
        // it takes two arguments setname and then calculates SetDifference for these two sets
        case SetDifference(op1,op2)=>
          if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]) && SetBinding.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]).diff(SetBinding(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else if(SetBinding.contains(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SetDifference(SetBinding(op1.asInstanceOf[SetOp1].eval.asInstanceOf[String]),op2)
          else if(SetBinding.contains(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SetDifference(op1,SetBinding(op2.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else SetDifference(op1,op2)
        // it takes two arguments setname and then calculates SymmetricDifference for these two sets
        case SymmetricDifference(x,y) =>
          if(SetBinding.contains(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]) && SetBinding.contains(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SetBinding(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]).diff(SetBinding(y.asInstanceOf[SetOp1].eval.asInstanceOf[String])).union(SetBinding(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]).diff(SetBinding(x.asInstanceOf[SetOp1].eval.asInstanceOf[String])))
          else if(SetBinding.contains(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SymmetricDifference(SetBinding(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]),y)
          else if(SetBinding.contains(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            SymmetricDifference(x,SetBinding(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else SymmetricDifference(x,y)
        // it takes two arguments setname and then calculates CartesianProduct for these two sets
        case CartesianProduct(x,y) =>
          if(SetBinding.contains(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]) && SetBinding.contains(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            val Set1 = SetBinding(x.asInstanceOf[SetOp1].eval.asInstanceOf[String])
            val Set2 = SetBinding(y.asInstanceOf[SetOp1].eval.asInstanceOf[String])
            val SetCp = collection.mutable.Set[Any]()
            for (i<-Set1) {
              for (j<-Set2){
                SetCp += ((i,j))
              }
            }
            SetCp
          else if(SetBinding.contains(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            CartesianProduct(SetBinding(x.asInstanceOf[SetOp1].eval.asInstanceOf[String]),y)
          else if(SetBinding.contains(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
            CartesianProduct(x,SetBinding(y.asInstanceOf[SetOp1].eval.asInstanceOf[String]))
          else CartesianProduct(x,y)
        // It takes two arguments one is the name and the other is the Setoperation of type SetOp1
        // it binds the name to SetOperation
        case bindingMacro(x,y) =>
          MacroBinding += (x-> y)
        // this is used to resolve the Macro created by bindingMacro it finds the binding with name passed to it and evaluate and returns it
        case Macro(x) =>
          if (MacroBinding.contains(x)) MacroBinding(x).eval
          else Macro(x)

        // this is used to implement Scope internally if the user wants
        case Scope(x,y*) =>
          val z = scala.collection.mutable.Stack[Any]()
          name.push(x)
          if (!ScopeBinding.contains(name.top)){
            ScopeBinding(name.top) = collection.mutable.Map()
          }
          for(n<-y){
            z.push(n.eval)
          }
          name.pop()
          z.pop()
      }

  @main def runSetOp: Unit =
    import SetOp1.*
    var expression = Assign(SetName("x"),Valset(1,2,3)).eval
//    expression = Insert(SetName("z"),Create(Variable("x"),Value(1))).eval
//    println(expression)
//    expression = Delete(SetName("y"),Variable("x")).eval
//    println(expression)
    expression = Assign(SetName("y"),Valset(4,5,6)).eval
    expression = Union(SetName("x"),SetName("y")).eval
    println(expression)








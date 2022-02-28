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

  // enum defining all the SetOperation
  enum SetOp1:
    case Variable(Name:String)
    case Value(Val:Any)
    case SetName(Name: String)
    case Check(op1:SetOp1,op2:SetOp1)
    case Valset(x:Any*)
    case Create(x:SetOp1,y:SetOp1)
    case Insert(op: SetOp1, op1:SetOp1)
    case Assign(x:SetOp1,y:SetOp1)
    case Delete(x:SetOp1,y:SetOp1)
    case Union(x:SetOp1 , y: SetOp1)
    case Intersection(x:SetOp1,y:SetOp1)
    case SetDifference(x:SetOp1,y:SetOp1)
    case CartesianProduct(x:SetOp1,y:SetOp1)
    case SymmetricDifference(x:SetOp1,y:SetOp1)
    case Scope(x:String,y:SetOp1*)
    case bindingMacro(x:String,y:SetOp1)
    case Macro(x:String)

    // this is the evaluation function for the SetOperations
    def eval: Any=
      this match{
        // Value(x) just returns the value passed
        case Value(x)=> x
        // Variable(x) takes a string and checks if it is present in the binding if yes then returns it else assigns a value of 0
        // and returns it
        case Variable(x)=>
          if (binding.contains(x))
            binding(x)
          else
            binding += (x -> 0)
            binding(x)
        // SetName(x) just return the name which is passed
        case SetName(x) => x
        // Check takes two arguments one is the name of the set and the second is the value which is to be checked
        // it return True if the value is found else returns False
        case Check(op1,op2) =>
          if (name.top=="global"){
            val set = SetBinding(op1.eval.asInstanceOf[String])
            if (set.contains(op2.eval.asInstanceOf[Any]))
              true
            else
              false
          }
          else{
            val set = op1.eval.asInstanceOf[String]
            if (ScopeBinding(name.top)(set).contains(op2.eval.asInstanceOf[Any]))
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
            //            if (SetBinding.contains(setName)) {
            //              val set = SetBinding(setName).union(insert.asInstanceOf[Set[Any]])
            //              SetBinding += (setName -> set)
            //            }
            //            else {
            SetBinding += (setName -> Set())
            val set = SetBinding(setName).union(insert.asInstanceOf[Set[Any]])
            SetBinding += (setName -> set)
            //            }
            SetBinding(setName)
          }
          else{
            val setName = op1.eval.asInstanceOf[String]
            val insert = op2.eval
            val setBinding = ScopeBinding(name.top)
            //            if (setBinding.contains(setName)) {
            //              val set = ScopeBinding(name.top)(setName).union(insert.asInstanceOf[Set[Any]])
            //              ScopeBinding(name.top)+= (setName->set)
            //            }
            //            else {
            ScopeBinding(name.top) += (setName -> Set())
            ScopeBinding(name.top)+= (setName->insert.asInstanceOf[Set[Any]])
            //            }
            ScopeBinding(name.top)(setName)
          }
        // Valset is used we want to pass in multiple arguments to creates a set
        case Valset(x*)=>
          Set(x *)
        case Create(x,y)=>
          Set(x.eval,y.eval)

        //Insert takes two arguments one is SetName to which we want to insert values and the other argument is Create which contains the
        // values we want to insert
        case Insert(op1,op2) =>
          if(name.top=="global") {
            val setname = op1.eval.asInstanceOf[String]
            val insert = op2.eval
            val set = SetBinding(setname).union(insert.asInstanceOf[Set[Any]])
            SetBinding += (setname -> set)
            SetBinding(setname)
          }
          else{
            val setName = op1.eval.asInstanceOf[String]
            val insert = op2.eval
            val setBinding = ScopeBinding(name.top)
            val set = ScopeBinding(name.top)(setName).union(insert.asInstanceOf[Set[Any]])
            ScopeBinding(name.top)+= (setName -> set)
            ScopeBinding(name.top)(setName)
          }
        // Delete takes two arguments setname and the value to be deleted
        case Delete(op1,op2) =>
          if (name.top=="global") {
            val setname = op1.eval.asInstanceOf[String]
            if (SetBinding.contains(setname.asInstanceOf[String])) {
              if(SetBinding(setname).contains(op2.eval.asInstanceOf[Any])) {
                SetBinding(setname.asInstanceOf[String]) -= op2.eval.asInstanceOf[Any]
                return SetBinding(setname.asInstanceOf[String])
              }
              else{
                return SetBinding(setname.asInstanceOf[String])
              }
            }

          }
          else {
            val setname = op1.eval.asInstanceOf[String]
            ScopeBinding(name.top)(setname) -= op2.eval.asInstanceOf[Any]
            ScopeBinding(name.top)(setname)
          }
        // it takes two arguments setname and then calculates Union for these two sets
        case Union(op1,op2) =>
          SetBinding(op1.eval.asInstanceOf[String]).union(SetBinding(op2.eval.asInstanceOf[String]))
        // it takes two arguments setname and then calculates Intersection for these two sets
        case Intersection(op1,op2)=>
          SetBinding(op1.eval.asInstanceOf[String]).intersect(SetBinding(op2.eval.asInstanceOf[String]))
        // it takes two arguments setname and then calculates SetDifference for these two sets
        case SetDifference(op1,op2)=>
          SetBinding(op1.eval.asInstanceOf[String]).diff(SetBinding(op2.eval.asInstanceOf[String]))
        // it takes two arguments setname and then calculates SymmetricDifference for these two sets
        case SymmetricDifference(x,y) =>
          (SetBinding(x.eval.asInstanceOf[String]).diff(SetBinding(y.eval.asInstanceOf[String]))).union(SetBinding(y.eval.asInstanceOf[String]).diff(SetBinding(x.eval.asInstanceOf[String])))
        // it takes two arguments setname and then calculates CartesianProduct for these two sets
        case CartesianProduct(x,y) =>
          val Set1 = SetBinding(x.eval.asInstanceOf[String])
          val Set2 = SetBinding(y.eval.asInstanceOf[String])
          val SetCp = collection.mutable.Set[Any]()
          for (i<-Set1) {
            for (j<-Set2){
              SetCp += ((i,j))
            }
          }
          SetCp
        // It takes two arguments one is the name and the other is the Setoperation of type SetOp1
        // it binds the name to SetOperation
        case bindingMacro(x,y) =>
          MacroBinding += (x-> y)
        // this is used to resolve the Macro created by bindingMacro it finds the binding with name passed to it and evaluate and returns it
        case Macro(x) =>
          MacroBinding(x).eval

        // this is used to implement Scope internally if the user wants
        case Scope(x,y*) =>
          val z = scala.collection.mutable.Stack[Any]()
          name.push(x)
          if (ScopeBinding.contains(name.top)==false) {
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
    var expression = Scope("name",Scope("name1",Assign(SetName("x"),Valset(1,2))),Assign(SetName("x"),Valset(2,3))).eval
    println(expression)
    println(ScopeBinding)







object SetDSL:
  // Variable binding are stored in binding
  private val binding : collection.mutable.Map[String,Any]= collection.mutable.Map("var"-> 1)
  // SetName and its contents are stored in SetBinding
  private val SetBinding : collection.mutable.Map[String,Set[Any]]= collection.mutable.Map("someSetName" -> Set(3))
  private val MacroBinding : collection.mutable.Map[String,String]= collection.mutable.Map()
  private val ScopeBinding : collection.mutable.Map[String,Any] = collection.mutable.Map()
  enum SetOp1:
    case Variable(Name:String)
    case Value(Num:Any)
    case SetName(Name: String)
    case Check(op1:String,op2:SetOp1)
    case Insert(op: SetOp1, arg: SetOp1 , arg1: SetOp1)
    case Assign(x:SetOp1,y:SetOp1)
    case Union(x:SetOp1 , y: SetOp1)
    case Intersection(x:SetOp1,y:SetOp1)
    case SetDifference(x:SetOp1,y:SetOp1)
    case CartesianProduct(x:String,y:String)
    case SymmetricDifference(x:String,y:String)
    case Macro(x:String,y:String)
    case Scope(x:String,y:SetOp1)

    def eval: Any=
      this match{
        // Returning the value of x passed as a set
        case Value(x)=> x
        // Resolving the binding of variable
        case Variable(x)=>
          if (binding.contains(x))
            binding(x)
          else
            binding += (x->0)
            binding(x)
        // just returning the string passed to method SetName
        case SetName(x) => x
        // Checking whether a particular value is present in a set or not
        case Check(op1,op2) => val set = SetBinding(op1.asInstanceOf[String])
          println(set)
          println(op2.eval)
          if (set.contains(op2.eval.asInstanceOf[Any]))
            true
          else
            false
        //      case Macro(x,y)=>   MacroBinding += (x->y)
        //      MacroBinding(x)
        // Creating a set with the given operands
        case Insert(op1,op2,op3) =>
          Set(op1.eval,op2.eval,op3.eval)
        case Union(op1,op2) =>
          SetBinding(op1.eval.asInstanceOf[String]).union(SetBinding(op2.eval.asInstanceOf[String]))

        // Intersection of two sets
        case Intersection(op1,op2)=>
          SetBinding(op1.eval.asInstanceOf[String]).intersect(SetBinding(op2.eval.asInstanceOf[String]))
        // Computes the SetDifference between two Sets whose names are passed to the method
        case SetDifference(op1,op2)=>
          SetBinding(op1.eval.asInstanceOf[String]).diff(SetBinding(op2.eval.asInstanceOf[String]))
        // Assign creates a set if it is not present in the SetBinding
        // and assigns the output of the second argument to the coressponding Set is available
        // Else creates a new set with the name specified in op1 and assigns the output of
        // second argument
        case Assign(op1,op2) =>
          val setName = op1.eval.asInstanceOf[String]
          if (SetBinding.contains(setName))
            val insert = op2.eval.asInstanceOf[Set[Any]].union(SetBinding(setName))
            SetBinding += (setName->insert)
            SetBinding(setName)
          else
            val insert = op2.eval.asInstanceOf[Set[Any]]
            SetBinding += (setName -> insert)
            SetBinding(setName)
        // Computes the Symmetric Difference between two sets mentioned
        case SymmetricDifference(x:String,y:String) =>
          (SetBinding(x).union(SetBinding(y))).diff(SetBinding(y).intersect(SetBinding(x)))
          // Computes the cartesian Product between two sets whose names are passed as argument     case CartesianProduct(x,y) =>
          val Set1 = SetBinding(x)
          val Set2 = SetBinding(y)
          val SetCp = collection.mutable.Set[Any]()
          for (i<-Set1) {
            for (j<-Set2){
              SetCp += Set(i,j)
            }
          }
          SetCp
        case Scope(x,y) => ???
      }
  @main def runDSL : Unit =
    import SetOp1.*
    var expression=Assign(SetName("someSetName"), Insert(Variable("var"), Value(2), Value("somestring"))).eval
    expression = Check("someSetName",Value(1)).eval
    println(SetBinding)
    //    expression = Scope("scopename", Assign(SetName("someSetName"), Insert(Variable("var"), Value(1), Value("somestring")))).eval
    println(expression)




object SetDSL:
  private val binding : collection.mutable.Map[String,Any]= collection.mutable.Map("var"-> 1)
  private val SetBinding : collection.mutable.Map[String,Set[Any]]= collection.mutable.Map("someSetName" -> Set(3))
  private val MacroBinding : collection.mutable.Map[String,SetOp1]= collection.mutable.Map[String,SetOp1]()
  private val ScopeBinding : collection.mutable.Map[String,collection.mutable.Map[String,Set[Any]]] = collection.mutable.Map()
  enum SetOp1:
    case Variable(Name:String)
    case Value(Val:Any)
    case SetName(Name: String)
    case Check(op1:SetOp1,op2:SetOp1)
    case Create(x:SetOp1,y:SetOp1)
    case Insert(op: SetOp1, op1:SetOp1) // used when setname is present in the set
    case Assign(x:SetOp1,y:SetOp1) // used when setname is not present in the set binding
    case Delete(x:SetOp1,y:SetOp1)
    case Union(x:SetOp1 , y: SetOp1)
    case Intersection(x:SetOp1,y:SetOp1)
    case SetDifference(x:SetOp1,y:SetOp1)
    case CartesianProduct(x:SetOp1,y:SetOp1)
    case SymmetricDifference(x:SetOp1,y:SetOp1)
    case Scope(x:String,y:SetOp1,z:SetOp1,a:SetOp1)
    case NestedScope(x:String,y:SetOp1,z:SetOp1,a:SetOp1,b:SetOp1)
    case bindingMacro(x:String,y:SetOp1)
    case Macro(x:String)


    def eval: Any=
      this match{
        case Value(x)=> x
        case Variable(x)=>
          if (binding.contains(x))
            binding(x)
          else
            binding += (x->0)
            binding(x)
        case SetName(x) => x
        case Check(op1,op2) => val set = SetBinding(op1.eval.asInstanceOf[String])
          if (set.contains(op2.eval.asInstanceOf[Any]))
            true
          else
            false
        case Assign(op1,op2) =>
          val setName = op1.eval.asInstanceOf[String]
          val insert = op2.eval
          if (SetBinding.contains(setName)) {
            val set = SetBinding(setName).union(insert.asInstanceOf[Set[Any]])
            SetBinding += (setName -> set)
          }
          else {
            SetBinding += (setName -> Set())
            val set = SetBinding(setName).union(insert.asInstanceOf[Set[Any]])
            SetBinding+= (setName -> set)
          }
          SetBinding(setName)

        case Create(x,y) =>
          Set(x.eval,y.eval)

        case Insert(op1,op2) =>
          val setname = op1.eval.asInstanceOf[String]
          val insert = op2.eval
          val set = SetBinding(setname).union(insert.asInstanceOf[Set[Any]])
          SetBinding += (setname -> set)

        case Delete(op1,op2) =>
          val name = op1.eval
          if (SetBinding.contains(name.asInstanceOf[String])) {
            SetBinding(name.asInstanceOf[String]) -= op2.eval.asInstanceOf[Any]
            return SetBinding(name.asInstanceOf[String])
          }
        case Union(op1,op2) =>
          SetBinding(op1.eval.asInstanceOf[String]).union(SetBinding(op2.eval.asInstanceOf[String]))
        case Intersection(op1,op2)=>
          SetBinding(op1.eval.asInstanceOf[String]).intersect(SetBinding(op2.eval.asInstanceOf[String]))
        case SetDifference(op1,op2)=>
          SetBinding(op1.eval.asInstanceOf[String]).diff(SetBinding(op2.eval.asInstanceOf[String]))
        case SymmetricDifference(x,y) =>
          (SetBinding(x.eval.asInstanceOf[String]).diff(SetBinding(y.eval.asInstanceOf[String]))).union(SetBinding(y.eval.asInstanceOf[String]).diff(SetBinding(x.eval.asInstanceOf[String])))
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
        case bindingMacro(x,y) =>
          MacroBinding += (x-> y)
        case Macro(x) =>
          MacroBinding(x).eval
      }
  @main def runDSL : Unit =
    import SetOp1.*
//    var expression = bindingMacro("name",Create(Variable("string"),Value(1))).eval
//    expression = Assign(SetName("somesetname"),Macro("name")).eval
//    println(expression)
//    expression = Assign
//    expression = bindingMacro("name",Variable("var")).eval
//    expression = Delete(SetName("somesetname"),Macro("name")).eval
//    print(expression)
//    Assign(SetName("SomeSetName"),Create(Value(2),Value(6))).eval
//    Assign(SetName("SomeSetName1"),Create(Value(2),Value(4))).eval
//    bindingMacro("UnionSetOp",Union(SetName("SomeSetName"),SetName("SomeSetName1"))).eval
//    val macrounion = Assign(SetName("SomeSetName3"),Macro("UnionSetOp")).eval
//    println(macrounion)
    Assign(SetName("someSetName"),Create(Value(1),Value(3))).eval
    Insert(SetName("someSetName"),Create(Value("string"),Value(5))).eval
    Assign(SetName("someSetName1"),Create(Value(2),Value("string"))).eval
    Insert(SetName("someSetName1"),Create(Value(1),Value("somestring"))).eval
    val setDiff = SetDifference(SetName("someSetName"),SetName("someSetName1")).eval
    println(setDiff)



object SetDSL:
  private val binding : collection.mutable.Map[String,Any]= collection.mutable.Map("var"-> 1)
  private val SetBinding : collection.mutable.Map[String,Set[Any]]= collection.mutable.Map("someSetName" -> Set(3))
  private val MacroBinding : collection.mutable.Map[String,String]= collection.mutable.Map()
  private val ScopeBinding : collection.mutable.Map[String,collection.mutable.Map[String,Set[Any]]] = collection.mutable.Map()
  enum SetOp1:
    case Variable(Name:String)
    case Value(Val:Any)
    case SetName(Name: String)
    case Check(op1:SetOp1,op2:SetOp1)
    case Insert(op: SetOp1, arg: SetOp1 , arg1:SetOp1) // used when setname is present in the set
    case Assign(x:SetOp1,y:SetOp1,z:SetOp1) // used when setname is not present in the set binding
    case Delete(x:SetOp1,y:SetOp1)
    case Union(x:SetOp1 , y: SetOp1)
    case Intersection(x:SetOp1,y:SetOp1)
    case SetDifference(x:SetOp1,y:SetOp1)
    case CartesianProduct(x:SetOp1,y:SetOp1)
    case SymmetricDifference(x:SetOp1,y:SetOp1)
    case Macro(x:String,y:String)
    case Scope(x:String,y:SetOp1,z:SetOp1,a:SetOp1)

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
        case Assign(op1,op2,op3) =>
          val setName = op1.eval.asInstanceOf[String]
          val insert = Set(op2.eval,op3.eval)
          if (SetBinding.contains(setName)) {
              val set = SetBinding(setName) ++ insert
              SetBinding += (setName -> set)
            }
          else {
              SetBinding += (setName -> insert)
            }
          SetBinding(setName)


        case Insert(op1,op2,op3) =>
          val setname = op1.eval.asInstanceOf[String]
          val insert = Set(op2.eval,op3.eval) ++ SetBinding(setname)
          SetBinding += (setname -> insert)
          SetBinding(setname)

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
              SetCp += Set(i,j)
            }
          }
          SetCp

        case Scope(x,y,z,a) =>
          val setname = y.eval.asInstanceOf[String]
          SetBinding += (setname-> Set(z.eval,a.eval))
          if (ScopeBinding.contains(x)){
            ScopeBinding(x)(setname) = ScopeBinding(x)(setname) ++ SetBinding(setname)
            Delete(SetName(setname),z).eval
            Delete(SetName(setname),y).eval
            ScopeBinding
          }
          else {
            ScopeBinding += (x -> collection.mutable.Map(setname -> SetBinding(setname)))
            Delete(SetName(setname),z).eval
            Delete(SetName(setname),y).eval
            ScopeBinding
          }
      }
  @main def runDSL : Unit =
    import SetOp1.*
//    var expression = Insert(SetName("someSetName"),Value(1),Variable("x")).eval
//    println(expression)
//    expression = Assign(SetName("someSetName1"),Value(2),Variable("var")).eval
//    expression = Assign(SetName("someSetName2"),Union(SetName("someSetName"),SetName("someSetName1"))).eval
//    expression = Union(SetName("someSetName1"),SetName("someSetName")).eval
//    println(expression)
//    expression = Assign(SetName("someSetName1"),Variable("var"),Value("string")).eval
//    println(expression)
//    println(Check(SetName("someSetName1"), Value("string")).eval)
//    var expression = Assign(SetName("someSetName"),Value(1),Value("somestring")).eval
//    println(expression)
//    println(SetBinding)
//    expression = Insert(SetName("someSetName"),Variable("var"),Value(3)).eval
//    expression = Union(SetName("someSetName1"),SetName("someSetName")).eval
//    expression = Delete(SetName("someSetName"),Value(1)).eval
//    var expression = Scope("name",SetName("someSetName"),Value(1),Value("somestring")).eval
//    println(expression)
//    expression = Scope("name",SetName("someSetName"),Value(2),Value("string")).eval
//    expression = Scope("othername",SetName("someSetName"),Value(2),Value("string")).eval
//    Scope("scopename", Scope("othername", Assign(Variable("someSetName"),Variable("var"), Value(1)), Value("somestring"))))
//    println(expression)
//    print(ScopeBinding)
    var expression=Assign(SetName("SetName"),Value(2),Value(6)).eval
    expression=Insert(SetName("SetName"),Value(7),Value(9)).eval
    expression=Assign(SetName("SetName1"),Value(2),Value(4)).eval
    expression = Insert(SetName("SetName1"),Value(6),Value(10)).eval
    expression = SymmetricDifference(SetName("SetName"),SetName("SetName1")).eval
    print(expression)



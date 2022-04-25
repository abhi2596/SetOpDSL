import SetDSL.SetOp1.*
import SetClass.SetClassImp.*
import SetDSL.SetOp1
import SetExIf.ExIf.*

object Optimization:
  // transformer function for SetDSL.SetOp1 operations 
  // it checks if both the sets have same names if yes it returns just that set 
  // it also checks if one of the set or both the sets are empty
  def function(x:SetDSL.SetOp1):Any = x match{
    case Union(x,y) =>
      if (x.asInstanceOf[SetDSL.SetOp1].eval == y.asInstanceOf[SetDSL.SetOp1].eval) x
      else if (getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set() && getSet(y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval == Set()) Set()
      else if(getSet(y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval == Set()) x
      else if(getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set()) y
      else Union(x,y).eval
    case Intersection(x,y) =>
      if (x.asInstanceOf[SetDSL.SetOp1].eval == y.asInstanceOf[SetDSL.SetOp1].eval) x
      else if (getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set() || getSet(y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval == Set()) Set()
      else Intersection(x,y)
    case SetDifference(x,y) =>
      if (x.asInstanceOf[SetDSL.SetOp1].eval == y.asInstanceOf[SetDSL.SetOp1].eval) Set()
      else if (getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set() && getSet(y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval == Set()) Set()
      else if(getSet(y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval == Set()) x
      else if(getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set()) Set()
      else SetDifference(x,y).eval
    case SymmetricDifference(x,y) =>
      if (x.asInstanceOf[SetDSL.SetOp1].eval == y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[SetDSL.SetOp1].eval) Set()
      else if (getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set() && getSet(y.asInstanceOf[String]).eval == Set()) Set()
      else if(getSet(y.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval == Set()) x
      else if(getSet(x.asInstanceOf[SetDSL.SetOp1].eval.asInstanceOf[String]).eval ==  Set()) y
      else SymmetricDifference(x,y).eval
  }
  // transformer function for if else construct 
  // it checks for if condition if it is true then returns only then expression 
  // if condition is false return only else expression 
  def function1(y:SetExIf.ExIf): Any = y match{
    case IF(condition,statements,els) =>
      val condition1 = condition.asInstanceOf[SetDSL.SetOp1].eval
      if(condition1 == true) function1(statements.asInstanceOf[SetExIf.ExIf])
      else els
    case Then(x) => x
    case Else(x) => x
  }

  trait SetExpression:
    def map(f: SetExpression => SetExpression): SetExpression

  @main def Run:Unit =
    import Optimization.*
    val expression = Assign(SetName("ife"),Valset(1,2)).eval
    val ifelse = Seq(IF(Check(SetName("ife"),Value(1)),Then(Insert(SetName("ife"),Valset(3,4))),Else(Assign(SetName("ife"),Valset(1,2)))))
    println(ifelse.map(function1))


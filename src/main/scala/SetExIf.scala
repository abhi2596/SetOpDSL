import SetDSL.SetOp1.*
import SetClass.SetClassImp.*

import collection.*
import scala.collection.mutable.ArrayBuffer


object SetExIf:
  // this value stores the exception message
  private val Msg : mutable.ArrayBuffer[Any] = mutable.ArrayBuffer()
  // this flag is used to skip all the expression after Throw statement
  private val flag: mutable.Stack[Any] = mutable.Stack(0)
  // this flags are used so as to not execute only then class or Else class or Try class or Catch class
  private val ifelseflag : mutable.Stack[Any] = mutable.Stack(0)
  private val trycatchflag : mutable.Stack[Any] = mutable.Stack(0)
  private val nestedflag : mutable.Map[String,Int] = mutable.Map()

  // enum class used to implement Exception handling and If else
  enum ExIf:
    case Conversion(op:Any)
    case LoopEvaluate(op:Any*)
    case Then(op:Any*)
    case Else(op:Any*)
    case IF(condition:SetDSL.SetOp1,Then:ExIf,Else:ExIf)
    case ExceptionClass(classdef:SetClass.SetClassImp)
    case Try(op:Any*)
    case Catch(op:Any*)
    case Throw(op:SetClass.SetClassImp)
    case getMsg()
    case CatchException(name:String,Try:ExIf,Catch:ExIf)



    def eval: Any =
      this match {
        // in this implementation we have statements from SetClass, SetOp, SetExIf
        // to evaluate them this function is used to convert the operation any to the
        // class which it belongs to
        case Conversion(op)=>
          val variable: mutable.Stack[Any] = mutable.Stack()
          if(op.isInstanceOf[SetDSL.SetOp1]){
            variable.push(op.asInstanceOf[SetDSL.SetOp1].eval)
          }
          else if(op.isInstanceOf[SetClass.SetClassImp]){
            variable.push(op.asInstanceOf[SetClass.SetClassImp].evaluate)
          }
          else if(op.isInstanceOf[SetExIf.ExIf]){
            variable.push(op.asInstanceOf[SetExIf.ExIf].eval)
          }
          variable.pop()
        // in this implementation we are accepting multiple arguments in every case class
        // so instead of writing a loop in every case class written this function which resolves
        // all the operands
        case LoopEvaluate(op*)=>
          val z: mutable.ArrayBuffer[Any] = mutable.ArrayBuffer[Any]()
          for(i<- op){
            if(flag.top==1){
              i
            }
            else{
              z.append(Conversion(i).eval)
            }
          }
          z
        // this is Exception class declaration
        case ExceptionClass(classDef) =>
          classDef.evaluate
        // this case class evaluate Then statement in If construct
        // Then class can take variable number of arguments which can be of SetClass.SetClassImp.*
        // and SetDSL.SetOp1.* and SetExIf.ExIf.*
        case Then(op*)=>
          if(ifelseflag.top ==1){
            LoopEvaluate(op*).eval.asInstanceOf[ArrayBuffer[Any]].last
          }
          else{
            "Cannot use then without if"
          }
        // this case class evaluate Else statement in If construct
        // Else class can take variable number of arguments which can be of SetClass.SetClassImp.*
        // and SetDSL.SetOp1.* and SetExIf.ExIf.*
        case Else(op*) =>
          if(ifelseflag.top==1){
            if(LoopEvaluate(op*).eval.asInstanceOf[ArrayBuffer[Any]].length >0){
              LoopEvaluate(op*).eval.asInstanceOf[ArrayBuffer[Any]].last
            }
            else{
              LoopEvaluate(op*).eval.asInstanceOf[ArrayBuffer[Any]]
            }
          }
          else{
            "Cannot use else without if"
          }
        // this case class evaluates the IF construct
        // it takes three arguments one is condition, other is Then case class
        // and the other is Else case class
        case IF(condition,statements,els) =>
          ifelseflag.push(1)
          val boolExp: Boolean = condition.eval.asInstanceOf[Boolean]
          var variable: Any = ""
          if(boolExp){
            variable = statements.eval
          }
          else{
            variable = els.eval
          }
          ifelseflag.pop()
          variable
        // this case class is used to evaluate the try condition in CatchException
        // Try class can take variable number of arguments which can be of SetClass.SetClassImp.*
        // and SetDSL.SetOp1.* and SetExIf.ExIf.*
        case Try(op*) =>
          if (trycatchflag.top ==1){
            val variable = LoopEvaluate(op*).eval.asInstanceOf[ArrayBuffer[Any]].last
            variable
          }
          else{
            "try cannot exist without catch"
          }
        // this case class is used to evaluate the Catch condition in CatchException
        // Catch class can take variable number of arguments but the last argument should be getMsg()
        case Catch(op*) =>
          if(trycatchflag.top ==1){
            LoopEvaluate(op*).eval.asInstanceOf[ArrayBuffer[Any]].last
          }
          else{
            "cannot use catch without try"
          }
        // this case evaluates the throw statement in Try
        // it takes NewObject() operation so that it can return the Constructor of exception class
        // the constructor contains the error message
        case Throw(op) =>
          flag.push(1)
          val z: mutable.ArrayBuffer[Any] = mutable.ArrayBuffer[Any]()
          for(i<-op.evaluate.asInstanceOf[Set[Any]]){
            z.append(i)
          }
          Msg.append(z.last.asInstanceOf[String])
          Msg
        // getMsg() which is called in Catch Block which contains the message to be
        // returned if there is a exception
        case getMsg()=>
          Msg.last
        // this case class evaluates the CatchException which takes two operands one is try block
        // the other operand is catch block
        case CatchException(name,op,op1)=>
          trycatchflag.push(1)
          if(nestedflag.contains(name)){
            nestedflag(name) += 1
          }
          else{
            nestedflag += (name -> 0)
          }
          val a:mutable.Stack[Any] = mutable.Stack()
          a.push(op.eval)
          if(flag.top==1){
            flag.pop()
            a.push(op1.eval)
            if(nestedflag(name) >0){
              flag.push(1)
            }
          }
          nestedflag(name) -=1
          trycatchflag.pop()
          a.pop()
      }



  @main def runIt:Unit =
    import ExIf.*
    var expression = ExceptionClass(ClassDef("exception",Field("a"),Constructor(Assign(SetName("a"),Valset("exception"))))).eval
    expression = Assign(SetName("a"),Valset(1,2)).eval
    expression = CatchException("exception",Try(
      CatchException("exception",Try(
        Throw(NewObject("exception","z"))),
        Catch(getMsg())),Insert(SetName("a"),Valset(3,4))),
      Catch(getMsg())).eval
    println(expression)
    expression = getSet("a").eval
//    var expression = Catch(getMsg()).eval
    println(expression)
//    var expression = Assign(SetName("b"),Valset(3,4)).eval
//    expression = Then(Assign(SetName("x"),Valset(1,2))).eval
//    println(expression)
//    expression = IF(Check(SetName("b"),Value(1)),Then(Insert(SetName("b"),Valset(1,2))),Else()).eval
//    println(expression)
//    println(expression)
//    expression = ExceptionClass(ClassDef("exception",Field("a"),Constructor(Assign(SetName("a"),Valset("exception"))))).eval
//    expression = Assign(SetName("b"),Valset(1,2)).eval
//    expression = Check(SetName("b"),Value(3)).eval
//    expression = Try(IF(Check(SetName("b"),Value(1)),Then(Insert(SetName("b"),Valset(3,4))),Else(Insert(SetName("b"),Valset(3,4))))).eval
//    println(expression)
//    expression = CatchException(
//      Try(IF(Check(SetName("b"),Value(1)),
//        Then(Throw(NewObject("exception","z")),
//          Insert(SetName("b"),Value(4))),
//        Else(Insert(SetName("b"),Valset(1,2))))),
//      Catch(getMsg())).eval
//    println(expression)
//    println(getSet("b").eval)
//    println(getScopeSet("z","a").eval)
//    println(expression)
//    println(getMsg().eval)
//    var expression = Catch(getMsg()).eval
//    println(expression)
//    println(getSet("a"))
//    expression = Assign(SetName("c"),Valset(3,4)).eval
//    expression = CatchException("exception",Try(Union(SetName("b"),SetName("c")),Throw(NewObject("exception","z"))),Catch(getSet("a"))).eval
//    println(expression)
//    var expression = Assign(SetName("a"),Valset(1,2,3)).eval
//    expression = IF(Check(SetName("a"),Value(3)),Then(Insert(SetName("a"),Valset(4)),Insert(SetName("a"),Valset(5))),Else(Insert(SetName("a"),Valset(3)))).eval
//    expression = getSet("a").eval
//    println(expression)
//    var expression = Assign(SetName("a"),Valset(1,2)).eval
//    println(expression)
//    expression = Check(SetName("a"),Value(1)).eval
//    println(expression)
//    expression =   IF(Check(SetName("a"),Value(1)),IF(Check(SetName("a"),Value(2)),Insert(SetName("a"),Valset(3,4)),Assign(SetName("a"),Valset(1))),Assign(SetName("a"),Valset(1,2))).eval
//    expression = Check(SetName("a"),Value(4)).eval
//    expression = getSet("a").eval
//    println(expression)
//    var expression = ExceptionClass(ClassDef("exception",Field("a"),Constructor(Assign(SetName("a"),Valset("exception"))))).eval
//    expression = NewObject("exception","z").evaluate
//    for(i<-expression.asInstanceOf[Set[Any]]){
//      println(i)
//    }
//    expression = Assign(SetName("b"),Valset(1,2)).eval
//    expression = Union(SetName("c"),SetName("b")).eval
//    expression = CatchException("exception",Union(SetName("c"),SetName("b")),NewObject("exception","z")).eval
//    println(expression)
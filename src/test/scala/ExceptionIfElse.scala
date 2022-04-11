import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetDSL.SetOp1.*
import SetClass.SetClassImp.*
import SetExIf.ExIf.*

import scala.collection.mutable

class ExceptionIfElse extends AnyFlatSpec with Matchers {
  behavior of "If else and Exception handling"

  it should "If else construct with both if and else statements" in {
    Assign(SetName("a"),Valset(1,2)).eval
    Assign(SetName("b"),Valset(5,6)).eval
    IF(Check(SetName("a"),Value(1)),
      Then(Insert(SetName("a"),Valset(3,4))),
      Else(Insert(SetName("a"),Valset(1)))).eval
    getSet("a").eval shouldEqual Set(1,2,3,4)
    IF(Check(SetName("b"),Value(1)),
      Then(Insert(SetName("b"),Valset(3,4))),
      Else(Insert(SetName("b"),Valset(7,8)))).eval
    getSet("a").eval shouldEqual Set(1,2,3,4)
  }

  it should "IF construct with only if expression" in {
    Assign(SetName("a"),Valset(1,2,3,4)).eval
    IF(Check(SetName("a"),Value(1)),
      Then(Delete(SetName("a"),Value(1))),
      Else()).eval
    getSet("a").eval shouldEqual Set(2,3,4)
    IF(Check(SetName("a"),Value(1)),
      Then(Delete(SetName("a"),Value(1))),
      Else()).eval
    getSet("a").eval shouldEqual Set(2,3,4)
  }

  it should "Nested IF in if else" in {
    Assign(SetName("b"),Valset(1,2,3,4)).eval
    IF(Check(SetName("b"),Value(1)),
      IF(Check(SetName("b"),Value(2)),
        Then(Delete(SetName("b"),Value(2))),
        Else(Insert(SetName("b"),Value(1)))),
      Else(Insert(SetName("b"),Valset(5)))).eval
    getSet("b").eval shouldEqual Set(1,3,4)
  }

  it should "simple Exception handling execution" in {
    ExceptionClass(ClassDef("exception",Field("a"),
      Constructor(Assign(SetName("a"),Valset("An exception is thrown"))))).eval
    CatchException( "exception",
      Try(Throw(NewObject("exception","z"))),
      Catch(getMsg())
    ).eval shouldEqual "An exception is thrown"
  }

  it should "Exception handling with IF construct checking the condition" +
    "IF true it will throw an exception" in {
    ExceptionClass(ClassDef("exception",Field("a"),
      Constructor(Assign(SetName("a"),Valset("exception is thrown as if is executed to true"))))).eval
    CatchException( "exception",
      Try(
        Assign(SetName("b"),Valset(1,2)),
        IF(Check(SetName("b"),Value(2)),
          Then(Throw(NewObject("exception","z")),
            Insert(SetName("b"),Valset(3,4))),
          Else())
      ),
      Catch(
        getMsg()
      )).eval shouldEqual "exception is thrown as if is executed to true"
    getSet("b").eval shouldEqual Set(1,2)
  }

  it should "Exception handling with IF construct checking the condition" +
    "Throw Exception statements after throw are skipped" in {
    ExceptionClass(ClassDef("exception",Field("a"),
      Constructor(Assign(SetName("a"),Valset("exception"))))).eval
    CatchException( "exception",
      Try(
        Assign(SetName("b"),Valset(1,2)),
        IF(Check(SetName("b"),Value(1)),
          Then(Throw(NewObject("exception","z")),
            Insert(SetName("b"),Valset(3,4))),
          Else(
            Insert(SetName("b"),Valset(5))
          ))
      ),
      Catch(
        getMsg()
      )).eval
    getSet("b").eval shouldEqual Set(1,2)
  }
  it should "Nested Try wherein exception travels to outer try as well" in {
    ExceptionClass(ClassDef("exception",Field("a"),Constructor(Assign(SetName("a"),Valset("exception travelling to outward catch"))))).eval
    Assign(SetName("a"),Valset(1,2)).eval
    CatchException("exception",Try(
      CatchException("exception",Try(
        Throw(NewObject("exception","z"))),
        Catch(getMsg())),Insert(SetName("a"),Valset(3,4))),
      Catch(getMsg())).eval shouldEqual "exception travelling to outward catch"
    getSet("a").eval shouldEqual Set(1,2)
  }
}

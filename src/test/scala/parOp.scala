import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetDSL.SetOp1.*
import SetClass.SetClassImp.*
import SetExIf.ExIf.*
import Optimization.*

import scala.collection.mutable

class parOp extends AnyFlatSpec with Matchers {
  behavior of "partial evaluation and optimization"

  it should "testing partial evaluation for operations like Assign and Insert" in {
    Variable("z") shouldEqual Variable("z")
    Assign(SetName("x"),Create(Variable("z"),Value(1))).eval shouldEqual Set(1,Variable("z"))
    Insert(SetName("z"),Valset(1,2,3)).eval shouldEqual Insert(SetName("z"),Set(1,2,3))
    Assign(SetName("y"),Valset(1,2,3)).eval
    Insert(SetName("y"),Create(Variable("y"),Value(4))).eval shouldEqual Set(1,2,3,Variable("y"),4)
  }

  it should "Delete partial evaluation with one binding missing" in {
    Variable("a").eval shouldEqual Variable("a")
    Assign(SetName("b"),Valset(1,2,3)).eval
    Delete(SetName("b"),Variable("a")).eval  shouldEqual Delete(Set(1,2,3),Variable("a"))
    Delete(SetName("c"),Value(1)).eval shouldEqual Delete(SetName("c"),1)
  }

  it should "Check partial evaluation" in {
    Variable("d").eval shouldEqual Variable("d")
    Check(SetName("e"),Value(1)).eval shouldEqual Check(SetName("e"),1)
    Assign(SetName("e"),Valset(1,2,3)).eval
    Check(SetName("e"),Variable("d")).eval shouldEqual Check(Set(1,2,3),Variable("d"))
  }

  it should "Union,Intersection,SetDifference,CartesianProduct and Symmetric Difference partial evaluation for all these operations is " +
    "similar" in {
    Assign(SetName("f"),Valset(1,2)).eval
    Assign(SetName("g"),Valset(3,4)).eval
    Union(SetName("f"),SetName("h")).eval shouldEqual Union(Set(1,2),SetName("h"))
    Intersection(SetName("i"),SetName("g")).eval shouldEqual Intersection(SetName("i"),Set(3,4))
  }

  it should "IF Else partial evaluation" in {
    IF(Check(SetName("ie"),Variable("var")),
      Then(Insert(SetName("ie"),Create(Value(2),Value(1)))),
      Else(Assign(SetName("ie"),Valset(1,2)))).eval shouldEqual IF(Check(SetName("ie"),1),Insert(SetName("ie"),Set(2, 1)),Set(1, 2))
  }

  it should "Using optimization on Union and other set operations which are partially evaluated" in {
    Assign(SetName("po1"),Valset()).eval
    val collection = Seq(Union(SetName("po"),SetName("po")),Union(SetName("po"),SetName("po1")))
    val collection1 = Seq(SetDifference(SetName("po"),SetName("po")),SetDifference(SetName("po"),SetName("po1")))
    collection.map(function) shouldEqual List(SetName("po"),SetName("po"))
    collection1.map(function) shouldEqual List(Set(),SetName("po"))
  }
  it should "If else construct optimization" in {
    Assign(SetName("ife"),Valset(1,2)).eval
    val ifelse = Seq(IF(Check(SetName("ife"),Value(1)),Then(Insert(SetName("ife"),Valset(3,4))),Else(Assign(SetName("ife"),Valset(1,2)))))
    ifelse.map(function1) shouldEqual List(Insert(SetName("ife"),Valset(3, 4)))
  }
}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetDSL.SetOp1.*

import scala.collection.mutable

class YourSetTheoryLanguageTest extends AnyFlatSpec with Matchers {
  behavior of "my first language for set theory operations"

  it should "create a set and insert objects into it" in {
    Insert(SetName("someSetName"),Create(Value(1),Value("somestring"))).eval
    Assign(SetName("someSetName1"),Create(Variable("var"),Value("string"))).eval
    Check(SetName("someSetName"), Value(1)).eval shouldBe true
    Check(SetName("someSetName1"), Value("string")).eval shouldBe true
  }

  it should "Delete elements in a Set" in {
    Insert(SetName("someSetName"),Create(Value(1),Value("somestring"))).eval
    Delete(SetName("someSetName"),Value("somestring")).eval
    Check(SetName("someSetName"), Value("somestring")).eval shouldBe false
    Assign(SetName("someSetName1"),Create(Value(3.0),Value("somestring"))).eval
    Delete(SetName("someSetName"),Value(3.0)).eval
    Check(SetName("someSetName"), Value(3.0)).eval shouldBe false
  }

  it should "test different set operations" in {
    Assign(SetName("someSetName"),Create(Value(1),Value(4))).eval
    Insert(SetName("someSetName"),Create(Value("string"),Value(5))).eval
    Assign(SetName("someSetName1"),Create(Value(2),Value("string"))).eval
    Insert(SetName("someSetName1"),Create(Value(1),Value("somestring"))).eval
    val union = Union(SetName("someSetName"),SetName("someSetName1")).eval
    union.asInstanceOf[Set[Any]] should contain allOf (1,2,4,"string","somestring",5)
    val setDiff = SetDifference(SetName("someSetName"),SetName("someSetName1")).eval
    setDiff.asInstanceOf[Set[Any]] should contain allOf (4,5)
    val intersection = Intersection(SetName("someSetName"),SetName("someSetName1")).eval
    intersection.asInstanceOf[Set[Any]] should contain allOf (1,"string")
  }

  it should "test Set Operations Symmeteric Difference and Cartesian Product" in {
    Assign(SetName("SetName"),Create(Value(2),Value(6))).eval
    Insert(SetName("SetName"),Create(Value(7),Value(9))).eval
    Assign(SetName("SetName1"),Create(Value(2),Value(4))).eval
    Insert(SetName("SetName1"),Create(Value(6),Value(10))).eval
    val symmetricDifference = SymmetricDifference(SetName("SetName"),SetName("SetName1")).eval
    symmetricDifference.asInstanceOf[Set[Any]] should contain allOf (4,7,9,10)
    Assign(SetName("SomeSetName"),Create(Value(2),Value(6))).eval
    Assign(SetName("SomeSetName1"),Create(Value(2),Value(4))).eval
    val cartesianProduct = CartesianProduct(SetName("SomeSetName"),SetName("SomeSetName1")).eval
    cartesianProduct shouldEqual  mutable.HashSet((2, 2), (6, 4), (6, 2), (2, 4))
  }

  it should "Macros are tested" in {
    bindingMacro("name",Create(Variable("string"),Value(1))).eval
    Assign(SetName("someSetName"),Macro("name")).eval
    Check(SetName("someSetName"), Value(1)).eval shouldBe true
    bindingMacro("name1",Value(1)).eval
    Delete(SetName("someSetName"),Macro("name1")).eval
    Check(SetName("someSetName"), Value(1)).eval shouldBe false
    Assign(SetName("SomeSetName"),Create(Value(2),Value(6))).eval
    Assign(SetName("SomeSetName1"),Create(Value(2),Value(4))).eval
    bindingMacro("UnionSetOp",Union(SetName("SomeSetName"),SetName("SomeSetName1"))).eval
    val macrounion = Assign(SetName("SomeSetName3"),Macro("UnionSetOp")).eval
    macrounion.asInstanceOf[Set[Any]] should contain allOf (2,6,4)
  }

}
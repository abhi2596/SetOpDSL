import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetDSL.SetOp1.*

import scala.collection.mutable

class YourSetTheoryLanguageTest extends AnyFlatSpec with Matchers {
  behavior of "my first language for set theory operations"

  it should "create a set and insert objects into it" in {
    Insert(SetName("someSetName"),Value(1),Value("somestring")).eval
    Assign(SetName("someSetName1"),Variable("var"),Value("string")).eval
    Check(SetName("someSetName"), Value(1)).eval shouldBe true
    Check(SetName("someSetName1"), Value("string")).eval shouldBe true
  }

  it should "Delete elements in a Set" in {
    Insert(SetName("someSetName"),Value(1),Value("somestring")).eval
    Delete(SetName("someSetName"),Value("somestring")).eval
    Check(SetName("someSetName"), Value("somestring")).eval shouldBe false
  }

  it should "test different set operations" in {
    Assign(SetName("someSetName"),Value(1),Value(3)).eval
    Insert(SetName("someSetName"),Value("string"),Value(5)).eval
    Assign(SetName("someSetName1"),Value(2),Value("string")).eval
    Insert(SetName("someSetName1"),Value(1),Value("somestring")).eval
    val union = Union(SetName("someSetName"),SetName("someSetName1")).eval
    union.asInstanceOf[Set[Any]] should contain allOf (1,2,3,"string","somestring",5)
    val setDiff = SetDifference(SetName("someSetName"),SetName("someSetName1")).eval
    setDiff.asInstanceOf[Set[Any]] should contain allOf (3,5)
    val intersection = Intersection(SetName("someSetName"),SetName("someSetName1")).eval
    intersection.asInstanceOf[Set[Any]] should contain allOf (1,"string")
//    symmetricDifference.asInstanceOf[Set[Any]] should contain allOf ()
    Assign(SetName("SetName"),Value(2),Value(6)).eval
    Insert(SetName("SetName"),Value(7),Value(9)).eval
    Assign(SetName("SetName1"),Value(2),Value(4)).eval
    Insert(SetName("SetName1"),Value(6),Value(10)).eval
    val symmetricDifference = SymmetricDifference(SetName("SetName"),SetName("SetName1")).eval
    symmetricDifference.asInstanceOf[Set[Any]] should contain allOf (4,7,9,10)
//    val cartesianProduct = CartesianProduct(SetName("setName"),SetName("setName1")).eval
//    cartesianProduct should contain allOf mutable.HashSet(Set(1,3),Set(1,4),Set(2,3),Set(2,4))
  }



}
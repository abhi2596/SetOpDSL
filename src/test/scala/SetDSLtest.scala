import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetDSL.SetOp1.*

class YourSetTheoryLanguageTest extends AnyFlatSpec with Matchers {
  behavior of "my first language for set theory operations"

  it should "create a set and insert objects into it" in {
    Assign(SetName("someSetName"), Insert(Variable("var"), Value(2), Value("somestring"))).eval
    Check("someSetName", Value(2)).eval shouldBe true
  }
}
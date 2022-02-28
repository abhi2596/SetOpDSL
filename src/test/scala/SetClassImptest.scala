import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetClass.SetClassImp.*
import SetDSL.SetOp1.*

import scala.collection.mutable

class ClassImplementation extends AnyFlatSpec with Matchers {
  behavior of "Class implementation on set theory operations"

  it should "Creating an Object and calling multiple methods in the class using that Object" in {
    ClassDef("setop",Field("a"),Field("b"),Constructor(Assign(SetName("a"),Valset(5,6)),Assign(SetName("b"),Valset(1,2))),Method("name",Insert(SetName("b"),Valset(3,4))),Method("name1",Insert(SetName("a"),Valset(7,8)))).evaluate
    NewObject("setop","z").evaluate shouldEqual Set(1,2)
    InvokeMethod("z","name").evaluate shouldEqual Set(1,2,3,4)
    InvokeMethod("z","name1").evaluate shouldEqual Set(5,6,7,8)
  }

  it should "Create an Object of a class and executing methods in that object scope" in {
    ClassDef("SetOp",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
    ClassDef("SetOp1",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name1",Insert(SetName("a"),Valset(7,8)))).evaluate
    NewObject("SetOp","z").evaluate shouldEqual Set(1,2)
    InvokeMethod("z","name").evaluate shouldEqual Set(1,2,3,4)
    NewObject("SetOp1","y").evaluate shouldEqual  Set(5,6)
    InvokeMethod("y","name1").evaluate shouldEqual Set(5,6,7,8)
  }
  //  //
  it should "Nested Class Implementation" in {
    ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),
      ClassDef("setop1",Field("b"),Constructor(Assign(SetName("b"),Valset(3,4))),
        Method("name",Insert(SetName("b"),Valset(1,2))),Method("name1",Insert(SetName("b"),Valset(5,6))))).evaluate
    NewObject("setop","z").evaluate shouldEqual Set(1,2)
    NestedClassObject("z","setop1","y").evaluate shouldEqual Set(3,4)
    NestedClassMethod("y","name").evaluate shouldEqual Set(1,2,3,4)
    NestedClassMethod("y","name1").evaluate shouldEqual Set(1,2,3,4,5,6)
  }
  //  ////
  it should "Multiple Inheritance is not allowed" in {
    ClassDef("SetOp",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
    ClassDef("SetOp1",Field("b"),Constructor(Assign(SetName("b"),Valset(5,6))),Method("name1",Insert(SetName("b"),Valset(7,8)))).evaluate
    ClassDef("SetOp2",Field("c"),Constructor(Assign(SetName("c"),Valset(5,6))),Method("name1",Insert(SetName("c"),Valset(7,8)))).evaluate
    Extends("SetOp","SetOp2").evaluate
    Extends("SetOp1","SetOp2").evaluate shouldEqual "Multiple Inheritance not allowed"
  }
  //
  it should "Object is calling method in parent as there is no method with that name in this class" in {
    ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name1",Insert(SetName("a"),Valset(7,8)),Insert(SetName("a"),Valset(7,8)))).evaluate
    ClassDef("setop1",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
    Extends("setop","setop1").evaluate
    NewObject("setop1","z").evaluate shouldEqual Set(1,2)
    InvokeMethod("z","name").evaluate shouldEqual Set(1,2,3,4)
    InvokeMethod("z","name1").evaluate shouldEqual Set(1,2,3,4,7,8)
  }

  it should "Overriding Method of Parent" in {
    ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name",Insert(SetName("a"),Valset(7,8)),Insert(SetName("a"),Valset(9,10)))).evaluate
    ClassDef("setop1",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
    Extends("setop","setop1").evaluate
    NewObject("setop","y").evaluate
    InvokeMethod("y","name").evaluate shouldEqual Set(5,6,7,8,9,10)
    NewObject("setop1","z").evaluate
    InvokeMethod("z","name").evaluate shouldEqual Set(1,2,3,4)
  }

}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetDSL.SetOp1.*
import SetClass.SetClassImp.*

import scala.collection.mutable

class AbstractClassInterfaceImplementation extends AnyFlatSpec with Matchers {
  behavior of "Abstract Class and Interface"

  it should "Class cannot have abstract methods" in {
    val expression = ClassDef("cabm",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),AbstractMethod("name1")).evaluate
    expression shouldEqual "Class cannot have abstract methods in it"
  }

  it should "Abstract Class and Interface cannot be instantiated" in {
    AbstractClassDef("abc",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),AbstractMethod("name")).evaluate
    NewObject("abc","z").evaluate shouldEqual "Abstract class cannot be instantiated"
    InterfaceDecl("inf",AbstractMethod("name"),AbstractMethod("name1")).evaluate
    NewObject("inf","z").evaluate shouldEqual "Interface cannot be instantiated"
  }

  it should "Child Class should implement all abstract methods of parent abstract class" in {
    var expression = AbstractClassDef("abcm",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),AbstractMethod("name")).evaluate
    expression = ClassDef("ciabcm",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name1",Assign(SetName("a"),Valset(1,2)))).evaluate
    Extends("abcm","ciabcm").evaluate shouldEqual "Class should implement all abstract methods"
  }

  it should "Class should Implement all the methods of interface" in {
    InterfaceDecl("pinf",AbstractMethod("name"),AbstractMethod("name1")).evaluate
    ClassDef("cpinf",Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
    Implements("cpinf","pinf").evaluate shouldEqual "Class should implement all the methods of interface"
  }

  it should "interface cannot implement another interface" in {
    InterfaceDecl("inf1",AbstractMethod("name"),AbstractMethod("name1")).evaluate
    InterfaceDecl("inf2",AbstractMethod("name2"),AbstractMethod("name3")).evaluate
    Implements("inf1","inf2").evaluate shouldEqual "interface cannot implement another interface"
  }

  it should "interface cannot inherit from a class" in {
    InterfaceDecl("iinf",AbstractMethod("name"),AbstractMethod("name1")).evaluate
    AbstractClassDef("abinf",AbstractMethod("name2"),AbstractMethod("name3")).evaluate
    Extends("abinf","iinf").evaluate shouldEqual "Interface cannot inherit from a class"
  }

  it should "Abstract class inherits from another abstract class and Class inherits from this abstract class" in {
    AbstractClassDef("abc1",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),AbstractMethod("name")).evaluate
    AbstractClassDef("abc2",Field("b"),Constructor(Assign(SetName("b"),Valset(3,4))),Method("name",Insert(SetName("a"),Valset(5,6))),AbstractMethod("name1")).evaluate
    Extends("abc1","abc2").evaluate
    ClassDef("ciabc",Field("a"),Constructor(Assign(SetName("a"),Valset(7,8))),Method("name1",Insert(SetName("b"),Valset(9,10)))).evaluate
    Extends("abc2","ciabc").evaluate
    NewObject("ciabc","a").evaluate shouldEqual Set(7,8)
    InvokeMethod("a","name").evaluate shouldEqual Set(5,6,7,8)
    InvokeMethod("a","name1").evaluate shouldEqual Set(3,4,9,10)
  }

  it should "Abstract class implementing partially implementing interface and Class which inherits the abstract class implements the method" +
    "interface" in {
    InterfaceDecl("setop",AbstractMethod("name"),AbstractMethod("name1")).evaluate
    AbstractClassDef("setop1",Method("name",Insert(SetName("a"),Valset(1,2)))).evaluate
    Implements("setop","setop1").evaluate
    ClassDef("classname",Field("b"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name1",Insert(SetName("a"),Valset(3,4)))).evaluate
    Extends("setop1","classname").evaluate
    NewObject("classname","z").evaluate shouldEqual Set(5,6)
    InvokeMethod("z","name").evaluate shouldEqual Set(1,2,5,6)
    InvokeMethod("z","name1").evaluate shouldEqual Set(1,2,3,4,5,6)
  }

  it should "class implementing multiple interfaces" in {
    InterfaceDecl("minf1",AbstractMethod("name")).evaluate
    InterfaceDecl("minf2",AbstractMethod("name1")).evaluate
    ClassDef("minfclass",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4))),Method("name1",Insert(SetName("a"),Valset(5,6)))).evaluate
    Implements("minfclass","minf1","minf2").evaluate
    NewObject("minfclass","minf").evaluate shouldEqual Set(1,2)
    InvokeMethod("minf","name").evaluate shouldEqual Set(1,2,3,4)
    InvokeMethod("minf","name1").evaluate shouldEqual Set(1,2,3,4,5,6)
  }

}

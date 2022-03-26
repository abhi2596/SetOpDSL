import SetDSL.SetOp1.*
import collection.*


object SetClass:
  // this Map is used to bind classname to a Map of methodname and the sequence of operations in it
  private val MethodMapping: mutable.Map[String,mutable.Map[String,Seq[SetDSL.SetOp1]]] = mutable.Map()
  // this Map is used to create Abstract method mapping it maps a classname to abstract method name
  private val AbstractMethodMapping: mutable.Map[String,Set[String]] = mutable.Map()
  // this Map is used to bind classname to sequence of Operation defined inside Constructor
  private val ConstructorMapping: mutable.Map[String,Seq[SetDSL.SetOp1]] = mutable.Map()
  // this Map binds Objectname to a classname
  private val ObjectMapping : mutable.Map[String,String] = mutable.Map()
  // this Map is similar to ConstructorMapping but this one is used for NestedClass Implementation
  // this Map has outerClassname which maps to InnerClassName which further maps to MethodName and its Sequence Of operations
  private val NestedConstructorMapping: mutable.Map[String,Any] = mutable.Map()
  // this Mapping is used to implement NestedMethodMapping
  private val NestedMethodMapping: mutable.Map[String,Any] = mutable.Map()
  // this variable is a stack which has the classname which is executed in ClassDef Operation
  private val classname = mutable.Stack[String]()
  // this mapping is used to implement NestedClass implementation it is same as methodmapping
  private val MMApping : mutable.Map[String,Map[String,Seq[SetDSL.SetOp1]]] = mutable.Map()
  // this mapping is used to implement NestedScoping it contents are
  private val NestedObjectMapping : mutable.Map[String,List[String]] = mutable.Map()
  // this mapping is used to keeptrack of inheritance it is map of ChildClassName which points to ParentClassName
  private val InheritMapping : mutable.Map[Any,Any] = mutable.Map()
  // this Mapping is used to check multiple inheritance
  // it Maps classname and number of times it is inherited
  private val InheritStatus : mutable.Map[String,Int] = mutable.Map()
  // this set contains abstract class names
  private val abstractclassname : mutable.Set[String] = mutable.Set()
  // this set contains interface names
  private val interfacename : mutable.Set[String] = mutable.Set()

  // enum class which contains the case classes used in this Implementation
  enum SetClassImp:
    case Value(a:String)
    case Field(x:String)
    case Constructor(x:SetDSL.SetOp1*)
    case Method(name:String,y:SetDSL.SetOp1*)
    case AbstractMethod(name:String)
    case ClassDef(name:String,y:SetClassImp*)
    case AbstractClassDef(name:String,y:SetClassImp*)
    case InterfaceDecl(name:String,y:SetClassImp*)
    case NewObject(name:String,obj:String)
    case InvokeMethod(name:String, obj:String)
    case Extends(name:String,name1:String)
    case Implements(name:String,name1:String*)
    case NestedClassObject(parentObj:String,name:String,obj:String)
    case NestedClassMethod(obj:String,name:String)

    // this function is used to evaluate the case Classes defined in the enum
    def evaluate: Any =
      this match {
        // the evaluation just returns the contents in it
        case Field(a) => a
        // this operation takes multiple SetDSL.SetOp1 operations as parameter and stores them in a Map
        // this implementation as both NestedClass Implementation and Class Implementation
        case Constructor(op*) =>
          if(classname.length>1){
            val innerclass = Map(classname.top -> op)
            NestedConstructorMapping+= classname(1) -> innerclass
          }
          else{
            ConstructorMapping += classname.top-> op
          }

        // this operation takes two parameters one of them is the MethodName and the other is sequence of operation which can be
        // defined by language created by SetDSL
        // this operation creates a Map with classname pointing to MethodName which will inturn point to Sequence of operations defined in it
        // for nestedclass it creates a Map with outerclass pointing to innerclass which will inturn point to Methodname and
        // Sequence of Operations in it
        case Method(name,op*) =>
          if (classname.length>1){
            if(MMApping.contains(classname.top)){
              MMApping(classname.top) += name->op
            }
            else{
              MMApping+= classname.top -> Map()
              MMApping(classname.top) += name->op
            }
            val innerMethod : Map[String,Seq[SetDSL.SetOp1]] = MMApping(classname.top)
            NestedMethodMapping += classname(1) -> Map(classname.top -> innerMethod)
          }
          else{
            if (MethodMapping.contains(classname.top)){
              MethodMapping(classname.top) += name -> op
            }
            else{
              MethodMapping += classname.top-> mutable.Map()
              MethodMapping(classname.top) += name -> op
            }
          }

        // this operation is used to create a NewObject it takes two parameters one is name and the other is objectname
        // It also evaluates Constructor within the class and returns it
        // It also checks if the class is inherited if yes then evaluates parentClass Constructor as well
        case NewObject(name,obj) =>
          if (abstractclassname.contains(name)){
            return "Abstract class cannot be instantiated"
          }
          if (interfacename.contains(name)){
            return "Interface cannot be instantiated"
          }
          val z : mutable.Stack[Any] = mutable.Stack()
          ObjectMapping += obj-> name
          val setop = ConstructorMapping(name)
          for (n<-setop){
            z.push(Scope(obj,n).eval)
          }
          z.pop()

        // this method is used to create an Object of nestedclass
        case NestedClassObject(parent,name,obj)=>
          NestedObjectMapping += obj -> List(ObjectMapping(parent),name)
          val classname= ObjectMapping(parent)
          val innerconstructor: Map[String,Seq[SetDSL.SetOp1]]= NestedConstructorMapping(classname).asInstanceOf[Map[String,Seq[SetDSL.SetOp1]]]
          val z : mutable.Stack[Any] = mutable.Stack()
          for (n<-innerconstructor(name)){
            z.push(Scope(obj,n).eval)
          }
          z.pop()

        // this method is used to invoke a method with obj name and the name of the method
        case InvokeMethod(obj,name) =>
          val classname = ObjectMapping(obj)
          val z: mutable.Stack[Any] = mutable.Stack()
          val method: Seq[SetDSL.SetOp1] = MethodMapping(classname)(name)
          for (n<-method){
            z.push(Scope(obj,n).eval)
          }
          z.pop()

        // this method is used to invoke NestedClassmethod
        case NestedClassMethod(obj,name) =>
          val nestedmethod: Map[String,Map[String,Seq[SetDSL.SetOp1]]] = NestedMethodMapping(NestedObjectMapping(obj)(0)).asInstanceOf[Map[String,Map[String,Seq[SetDSL.SetOp1]]]]
          val z: mutable.Stack[Any] = mutable.Stack()
          for(n<- nestedmethod(NestedObjectMapping(obj)(1))(name)){
            z.push(Scope(obj,n).eval)
          }
          z.pop()

        // this method is used to implement Inheritance
        case Extends(name,name1) =>
          InheritStatus(name1) += 1
          if(InheritStatus(name1) == 2){
            return "Multiple Inheritance not allowed"
          }
          if(interfacename.contains(name1)){
            if(!interfacename.contains(name)){
              return "Interface cannot inherit from a class"
            }
          }
          val MethodMapping3: mutable.Map[String,mutable.Map[String,Seq[SetDSL.SetOp1]]] = mutable.Map()
          MethodMapping3(name1) = mutable.Map()
          if (MethodMapping.contains(name)){
            if (MethodMapping.contains(name1)){
              for(n<-MethodMapping(name)){
                MethodMapping3(name1) += n
              }
              for(n<- MethodMapping(name1)){
                MethodMapping3(name1) += n
              }
              for(n<- MethodMapping3(name1)){
                MethodMapping(name1) += n
              }
            }
            else{
              MethodMapping += name1-> mutable.Map()
              for(n<- MethodMapping(name1)){
                MethodMapping3(name1) += n
              }
              for(n<- MethodMapping3(name1)){
                MethodMapping(name1) += n
              }
            }
          }
          if(AbstractMethodMapping.contains(name)){
            if (AbstractMethodMapping.contains(name1)){
              for(n<-AbstractMethodMapping(name)){
                AbstractMethodMapping(name1) += n
              }
            }
            else{
              if(abstractclassname.contains(name1)){
                AbstractMethodMapping += name1-> Set()
                for(n<-AbstractMethodMapping(name)){
                  AbstractMethodMapping(name1) += n
                }
              }
              else{
                for(n<-AbstractMethodMapping(name)){
                  if(!MethodMapping(name1).contains(n)){
                    return "Class should implement all abstract methods"
                  }
                }
              }
            }
          }
          if(ConstructorMapping.contains(name)){
            if(ConstructorMapping.contains(name1)){
              ConstructorMapping(name1) =  ConstructorMapping(name) ++ ConstructorMapping(name1)
            }
            else{
              ConstructorMapping += name1 -> ConstructorMapping(name)
            }
          }
          InheritMapping(name1) = name

        // this method is used to implement interfaces
        case Implements(className, intername*) =>
          if(interfacename.contains(className)){
            return "interface cannot implement another interface"
          }
          for(name<- intername) {
            if (interfacename.contains(name)) {
              if (!abstractclassname.contains(className)) {
                for (n <- AbstractMethodMapping(name)) {
                  if (!MethodMapping(className).contains(n)) {
                    return "Class should implement all the methods of interface"
                  }
                }
              }
              else {
                if (AbstractMethodMapping.contains(className)) {
                  for (n <- AbstractMethodMapping(name)) {
                    AbstractMethodMapping(className) += n
                  }
                }
                else {
                  AbstractMethodMapping += className -> Set()
                  for (n <- AbstractMethodMapping(name)) {
                    AbstractMethodMapping(className) += n
                  }
                }
              }
            }
          }

        // this method creates Abstract method Mapping
        case AbstractMethod(name) =>
          if (!abstractclassname.contains(classname.top) && !interfacename.contains(classname.top)){
            return 1
          }
          if(AbstractMethodMapping.contains(classname.top)){
            AbstractMethodMapping(classname.top) += name
          }
          else{
            AbstractMethodMapping += classname.top -> Set()
            AbstractMethodMapping(classname.top) += name
          }
          if(AbstractMethodMapping.contains(classname.top)){
            AbstractMethodMapping(classname.top) += name
          }
          else{
            AbstractMethodMapping += classname.top -> Set()
            AbstractMethodMapping(classname.top) += name
          }
          AbstractMethodMapping(classname.top)

        // this method used to implement interface declaration
        case InterfaceDecl(name,y*) =>
          classname.push(name)
          if(classname.length>1){
            println(1)
          }
          interfacename += name
          InheritStatus+= (name-> 0)
          for(n<-y){
            n.evaluate
          }
          MMApping.remove(classname.top)
          classname.pop()
        // this method is used to create AbstractClass Definitions
        case AbstractClassDef(name,y*) =>
          classname.push(name)
          abstractclassname += name
          InheritStatus+= (name-> 0)
          for(n<-y){
            n.evaluate
          }
          MMApping.remove(classname.top)
          classname.pop()
        // this method is used to create Class Defintions
        case ClassDef(name,y*) =>
          classname.push(name)
          InheritStatus+= (name-> 0)
          val z: mutable.Set[Any] = mutable.Set()
          for(n<-y){
            z.add(n.evaluate)
          }
          MMApping.remove(classname.top)
          classname.pop()
          if (z.contains(1)){
            return "Class cannot have abstract methods in it"
          }
      }



  @main def runDSL: Unit =
    import SetClassImp.*
    var expression = InterfaceDecl("setop",InterfaceDecl("inf",AbstractMethod("name")),AbstractMethod("name1")).evaluate
    //    println(NestedMethodMapping)
    println(expression)
//    var expression = AbstractClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),AbstractMethod("name")).evaluate
//    expression = ClassDef("setop2",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name1",Assign(SetName("a"),Valset(1,2)))).evaluate
//    expression = Extends("setop","setop2").evaluate
//    println(expression)
//    expression = Interface("setop1",AbstractMethod("name2"),AbstractMethod("name3")).evaluate
//    expression = Implements("setop","setop1").evaluate
//    println(expression)
//    expression = AbstractClassDef("setop1",Method("name",Assign(SetName("a"),Valset(1,2)))).evaluate
//    expression = Implements("setop","setop1").evaluate
//      var expression = ClassDef("setop2",Field("b"),Constructor(Assign(SetName("b"),Valset(1,2))),Method("name1",Insert(SetName("b"),Valset(3,4)))).evaluate
//      expression = NewObject("setop2","z").evaluate
//      println(FieldMapping)
//      println(expression)
//    expression = Extends("setop1","setop2").evaluate
//    println(MethodMapping("setop2"))
//    println(expression)
//    var expression = Interface("setop",AbstractMethod("name"),AbstractMethod("name1")).evaluate
//    expression = AbstractClassDef("setop1",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name",Assign(SetName("x"),Valset(1,2)))).evaluate
//    expression = Implements("setop","setop1")
//    expression = ClassDef("setop2",Field("b"),Constructor(Assign(SetName("b"),Valset(3,4))),)
//    expression = Interface("setop1",AbstractMethod("name3")).evaluate
//    expression = Implements("setop","setop1").evaluate
//    println(AbstractMethodMapping)
//    println(expression)
//    var expression = AbstractClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),AbstractMethod("name")).evaluate
//    expression = AbstractClassDef("setop1",Field("b"),Constructor(Assign(SetName("b"),Valset(5,6))),Method("name",Insert(SetName("a"),Valset(4,5))),AbstractMethod("name1")).evaluate
//    expression = Extends("setop","setop1").evaluate
//    expression = ClassDef("setop2",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name1",Insert(SetName("a"),Valset(1,2)))).evaluate
//    expression = NewObject("setop","z").evaluate
//    println(expression)
//    expression = Extends("setop1","setop2").evaluate
//    println(expression)
//    expression = AbstractClassDef("setop1",Field("b"),Constructor(Assign(SetName("b"),Valset(5,6))),Method("name1",Insert(SetName("b"),Valset(1,2)))).evaluate
//    expression = Extends("setop","setop1").evaluate
//    println(ConstructorMapping("setop1"))
//    println(MethodMapping)
//    println(AbstractMethodMapping)
//    println(expression)
//    var expression = ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),AbstractMethod("name1")).evaluate
//    println(expression)
//    expression = ClassDef("setop2",Field("c"),Constructor(Assign(SetName("c"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
//    expression = Extends("setop1","setop2").evaluate
//    expression = NewObject("setop1","z").evaluate
//    println(expression)
//    println(ConstructorMapping("setop2"))
//    println(MethodMapping("setop2"))
//    expression = InvokeMethod("z","name1").evaluate
//    expression = InvokeMethod("z","name").evaluate
//    println(expression)
//    println(abstractmethodname)
//    println(MethodMapping)
//    println(ConstructorMapping)
//    println(AbstractMethodMapping)
//    expression = NewObject("setop","z").evaluate
//    var expression = Interface("setop",Method("name"),Method("name1"))
//    expression = NewObject("setop","z")
//    println(expression)
//    println(expression)


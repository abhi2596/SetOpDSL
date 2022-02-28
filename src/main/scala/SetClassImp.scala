import SetDSL.SetOp1.*
import collection.*


object SetClass:
  // this Map is used to bind classname to a Map of methodname and the sequence of operations in it
  private val MethodMapping: mutable.Map[String,Map[String,Seq[SetDSL.SetOp1]]] = mutable.Map()
  // this Map is used to bind classname to sequence of Operation defined inside Constructor
  private val ConstructorMapping: mutable.Map[String,Seq[SetDSL.SetOp1]] = mutable.Map()
  // this Map binds Objectname to a classname
  private val ObjectMapping : mutable.Map[String,String] = mutable.Map()
  // this Map is similar to ConstructorMapping but this one is used for NestedClass Implementation
  // this Map has outerClassname which maps to InnerClassName which further maps to MethodName and its Sequence Of operations
  private val NestedConstructorMapping: mutable.Map[String,Any] = mutable.Map()
  //
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
  // enum class which contains the case classes used in this Implementation
  enum SetClassImp:
    case Value(a:String)
    case Field(x:String)
    case Constructor(x:SetDSL.SetOp1*)
    case Method(name:String,y:SetDSL.SetOp1*)
    case ClassDef(name:String,y:SetClassImp*)
    case NewObject(name:String,obj:String)
    case InvokeMethod(name:String, obj:String)
    case Mapping(name:String,y:SetClassImp)
    case Extends(name:String,name1:String)
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
              MethodMapping += classname.top->Map()
              MethodMapping(classname.top) += name -> op
            }
          }

        // this operation is used to create a NewObject it takes two parameters one is name and the other is objectname
        // It also evaluates Constructor within the class and returns it
        // It also checks if the class is inherited if yes then evaluates parentClass Constructor as well
        case NewObject(name,obj) =>
          val status = InheritStatus(name)
          val z : mutable.Stack[Any] = mutable.Stack()
          ObjectMapping += obj-> name
          if ( status == 0){
            val setop = ConstructorMapping(name)
            for (n<-setop){
              z.push(Scope(obj,n).eval)
            }
            z.pop()
          }
          else{
            val setop = ConstructorMapping(InheritMapping(name).asInstanceOf[String])
            for (n<-setop) {
              z.push(Scope(obj, n).eval)
            }
            val setop1 = ConstructorMapping(name)
            for (n<-setop1){
              z.push(Scope(obj,n).eval)
            }
            z.pop()
          }

        //
        case NestedClassObject(parent,name,obj)=>
          NestedObjectMapping += obj -> List(ObjectMapping(parent),name)
          val classname= ObjectMapping(parent)
          val innerconstructor: Map[String,Seq[SetDSL.SetOp1]]= NestedConstructorMapping(classname).asInstanceOf[Map[String,Seq[SetDSL.SetOp1]]]
          val z : mutable.Stack[Any] = mutable.Stack()
          for (n<-innerconstructor(name)){
            z.push(Scope(obj,n).eval)
          }
          z.pop()

        //
        case InvokeMethod(obj,name) =>
          val classname = ObjectMapping(obj)
          val z: mutable.Stack[Any] = mutable.Stack()
          if (MethodMapping(classname).contains(name)){
            val method: Seq[SetDSL.SetOp1] = MethodMapping(classname)(name)
            for (n<-method){
              z.push(Scope(obj,n).eval)
            }
            z.pop()
          }
          else{
            val inheritclassname = InheritMapping(classname)
            val method: Seq[SetDSL.SetOp1] = MethodMapping(inheritclassname.asInstanceOf[String])(name)
            for (n<-method){
              z.push(Scope(obj,n).eval)
            }
            z.pop()
          }

        //
        case NestedClassMethod(obj,name) =>
          val nestedmethod: Map[String,Map[String,Seq[SetDSL.SetOp1]]] = NestedMethodMapping(NestedObjectMapping(obj)(0)).asInstanceOf[Map[String,Map[String,Seq[SetDSL.SetOp1]]]]
          val z: mutable.Stack[Any] = mutable.Stack()
          for(n<- nestedmethod(NestedObjectMapping(obj)(1))(name)){
            z.push(Scope(obj,n).eval)
          }
          z.pop()

        //
        case Extends(name,name1) =>
          InheritStatus(name1) += 1
          if(InheritStatus(name1) == 2){
            return "Multiple Inheritance not allowed"
          }
          InheritMapping(name1) = name

        //
        case ClassDef(name,y*) =>
          classname.push(name)
          InheritStatus+= (name-> 0)
          for(n<-y){
            n.evaluate
          }
          MMApping.remove(classname.top)
          classname.pop()
      }



  @main def runDSL: Unit =
    import SetClassImp.*
    var expression = ClassDef("setop",Field("a"),Constructor(Assign(SetName("a"),Valset(5,6))),Method("name",Insert(SetName("a"),Valset(7,8)),Insert(SetName("a"),Valset(7,8)))).evaluate
    //    expression = ClassDef("setOp2",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2)))).evaluate
    //    expression = ClassDef("setop1",Field("a"),Constructor(Assign(SetName("a"),Valset(1,2))),Method("name",Insert(SetName("a"),Valset(3,4)))).evaluate
    //    expression = Extends("setop","setop1").evaluate
    //    expression = Extends("setOp2","setop1").evaluate
    //    println(expression)
    expression = NewObject("setop","z").evaluate
    expression = InvokeMethod("z","name").evaluate
    println(expression)
//    expression = InvokeMethod("z","name").evaluate
//    println(expression)
//    println(expression)
//    println(ConstructorMapping("setop1"))
//    println(MethodMapping("setop1"))



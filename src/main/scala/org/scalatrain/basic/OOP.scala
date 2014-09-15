package org.scalatrain.basic

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer

object OOP {
  def main(args: Array[String]) {
    classes()
    traits()
    generics()
    typeMembers()
  }

  def classes() = {

    abstract class AbstractDbService(val dbUrl: String, private var retryCount: Int = 0) {

      println(s"I'm ${getClass} constructor")

      def this() = {
        this("default", 1)
        println("Auxiliary constructor")
      }

      lazy val cache = {
        //calculate()
      }

      protected def doConnect(): Unit

      private[basic] val b = 2
      private val a = b + 1
      println(s"a = $a, b = $b, $name")
      val name = "Martin"
    }

    val anon = new AbstractDbService("") {
      override protected def doConnect(): Unit = println(s"Connect $b")
    }

    class OracleDbService extends AbstractDbService("oracle") {

      private val oracle = 1

      private[this] val thisOracle = 2

      def privacyTest(service: OracleDbService) = {
        service.oracle + thisOracle
        //        service.thisOracle
      }

      override protected def doConnect(): Unit = ???
    }

    val oracle = new OracleDbService

    class User {
      private var _name: String = _

      @BeanProperty def name: String = _name

      def name_=(n: String) = {
        if (n.nonEmpty) _name = n
      }

      @BeanProperty var age: Int = _

      object gender

    }

    val u = new User
    println(s"${u.name} ${u.age}")
    u.name = "Alex"
    u.name = ""
    u.setAge(30)
    println(s"${u.name} ${u.age} ${u.gender}")

  }

  class Animal {
    println(s"I'm Animal constructor")
  }

  trait Furry extends Animal {
    println(s"I'm Furry constructor")
  }

  trait HasLegs extends Animal {
    println(s"I'm HasLegs constructor")
  }

  trait HasHands {
    println(s"I'm HasHands constructor")
  }

  trait FourLegged extends HasLegs {
    println(s"I'm FourLegged constructor")
  }

  trait TwoLegged extends HasLegs {
    println(s"I'm TwoLegged constructor")
  }

  class Cat extends Animal with Furry with FourLegged {
    println(s"I'm Cat constructor")
  }

  def traits() = {
    // Linearization

    // Cat ￼ ￼ FourLegged ￼ ￼ HasLegs ￼ ￼ Furry ￼ ￼ Animal ￼ ￼ AnyRef ￼ ￼ Any
    val cat = new Cat
    val man = new Animal with TwoLegged with HasHands

    def compound(animal: HasLegs with HasHands) = {}
    //    compound(cat)   // Won't compile
    compound(man)



    // Self type

    trait User { def name: String = getClass.getSimpleName }
    trait Tweeter {
      user: User =>
      def tweet(msg: String) = println(s"$name: $msg")
    }
    // trait Wrong extends Tweeter
    val user = new User with Tweeter


    // Stackable

    abstract class IntQueue {
      def get(): Int

      def put(x: Int)
    }

    class BasicIntQueue extends IntQueue {
      private val buf = new ArrayBuffer[Int]

      def get() = buf.remove(0)

      def put(x: Int) {
        buf += x
      }
    }
    val queue = new BasicIntQueue
    queue.put(10)
    println(queue.get())

    trait Doubling extends IntQueue {
      abstract override def put(x: Int) {
        super.put(2 * x)
      }
    }

    val doublequeue = new BasicIntQueue with Doubling
    doublequeue.put(10)
    println(doublequeue.get())

    trait Incrementing extends IntQueue {
      abstract override def put(x: Int) {
        super.put(x + 1)
      }
    }
    trait Filtering extends IntQueue {
      abstract override def put(x: Int) {
        if (x >= 10) super.put(x)
      }
    }

    val compQ = new BasicIntQueue with Incrementing with Filtering
    compQ.put(7)
    compQ.put(10)
    compQ.put(20)
    println(compQ.get())
    println(compQ.get())
  }

  def generics() = {
    import java.{util=>ju}


//    val j: ju.Collection[ju.Collection[Int]] = new ju.ArrayList[ju.ArrayList[Int]])()
    val ls: Traversable[Traversable[Int]] = List(List(1))

    class Base[+A, B] {
      def op(arg: B) = println(arg)
    }
//    val a: Base[Animal, Animal] = new Base[Cat, Animal]

    // Self type and generics
    trait User { def name: String = getClass.getSimpleName }
    trait Tweeter[A <: User] {
      user: A =>
      def tweet(msg: String) = println(s"${user.name}: $msg")
    }
    // trait Wrong extends Tweeter
    val user = new User with Tweeter[User]
  }

  def typeMembers() = {

    trait List {
      type Item <: Animal
      type Mapping[A] = Map[A, Item]

      class Dependent // Path dependent type
      def create = new Dependent

      def printDependent(i: Dependent) = println(i)

    }

    class CatList extends List {
      type Item = Cat
      type IntMapping = Mapping[Int]
      def getItem = new Cat
    }

    val l1 = new CatList
    val l2 = new CatList
    l2.printDependent(l2.create)
//    l2.printDependent(l1.create)
  }
}

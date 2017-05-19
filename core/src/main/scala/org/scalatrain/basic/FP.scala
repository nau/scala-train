package org.scalatrain.basic

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object FP {

  def identity[A](x: A) = x

  val doubleToString = (d: Double) => d.toString

  val intToDouble  = (i: Int) => i.toDouble

  val intToString  = (i: Int) => doubleToString(intToDouble(i))

  val intToString2 = doubleToString compose intToDouble

  val intToString3 = intToDouble andThen doubleToString

  def compose2[A, B, C](g: B => C, f: A => B): A => C = (a: A) => g(f(a))

  def hof() = {

    List(1, 2, 3).map(i => i * 27).filter(i => i > 3) // List(4, 6)



    val l = List(1, 2, 3)

    val result = ListBuffer.empty[Int]

    for (i <- 0 to l.size - 1) { // bug!
      val el = l(i)
      val newEl = el * 2
      if (newEl > 3) result.append(newEl)
    }

    val it = l.iterator
    while(it.hasNext) {
      val el = it.next()
      val newEl = el * 2
      if (newEl > 3) result.append(newEl)
    }

    List(1, 2, 3).foldLeft("")({ case (acc, n) => acc + n.toString })
    // "123"
  }

  def functor(): Unit = {
    List(1, 2, 3).map(d => d.toString) // List("1", "2", "3")

    def calculate(): Int = {Thread.sleep(1000000); 1}

    Future(calculate()).map(d => toString) // FUture[String]


    trait Functor[T[_]] {
      def map[A, B](f: A => B): T[A] => T[B]
    }

    // Functor Laws
    // 1. Identity
    List(1, 2, 3).map(identity) == List(1, 2, 3)

    // 2. Associativity
    List(1, 2, 3).map(intToDouble).map(doubleToString) ==
      List(1, 2, 3).map(doubleToString compose intToDouble)
  }

  def flatMapExample() = {
    val words = List("Test", "Hello World")

    // List(List(4), List(5, 5))
    words.map(s => s.split("\\s").map(w => w.size).toSeq)

    words.flatMap(s => s.split("\\s").map(w => w.size).toSeq)
    // List(4, 5, 5)
  }

  def monad(): Unit = {
    trait Monad[T[_]] {
      // also called unit, or return in Haskell
      def pure[A](a: A): T[A] // constructor

      // also called bind
      def >>=[A, B](f: A => T[B]): T[A] => T[B]

      def flatMap[A, B](m: T[A])(f: A => T[B]): T[B]
    }


    val intToListDouble    = (x: Int) => List(x.toDouble)
    val doubleToListString = (x: Double) => List(x.toString)

    // Monad Laws
    // 1. Left Identity
    // pure(x) >>= f === f(x)
    List(1).flatMap(intToListDouble) == intToListDouble(1)

    // 2. Right Identity
    // m >>= pure === m
    List(1).flatMap(d => List(d)) == List(1)

    // 3. Associativity
    // m >>= f >>= g === m >>= (x => f(x) >>= g)
    List(1).flatMap(intToListDouble).flatMap(doubleToListString) ==
      List(1).flatMap(x => intToListDouble(x).flatMap(doubleToListString))
  }

  def optionMonad(): Unit = {
    val valueExist: Option[Int] = Some(123)
    val valueDoesntExist: Option[Int] = None

    class User {
      val age = 25
    }

    def getUserFromDbOrNull(id: Int): User = null

    def getUserFromDb(id: Int): Option[User] = if (id == 0) Some(new User) else None

    val user0 = getUserFromDb(0)
    val user1 = getUserFromDb(1)

    user0.flatMap(u0 =>
      user1.flatMap(u1 =>
        Some(u0.age + u1.age)
      )
    )
    // None

    // syntactic sugar
    for (u0 <- user0; u1 <- user1) yield u0.age + u1.age

    // show Haskell do-notation

    def getLastName(u: User): Option[String] = ???
    def getSalary(surname: String): Option[Double] = ???

    for {
      u0 <- getUserFromDb(0)
      u1 <- getUserFromDb(1)
      last0 <- getLastName(u0)
      last1 <- getLastName(u1)
      salary0 <- getSalary(last0)
      salary1 <- getSalary(last1)
    } yield {???}
  }

  def futureMonad(): Unit = {
    class User {
      val age = 25
    }

    def getUserFromDb(id: Int): Future[User] =
      if (id == 0) Future(new User) else Future.failed(new RuntimeException("No such user"))

    val user0 = getUserFromDb(0)
    val user1 = getUserFromDb(1)

    user0.flatMap(u0 =>
      user1.flatMap(u1 =>
        Future(u0.age + u1.age)
      )
    )

    // syntactic sugar
    for (u0 <- user0; u1 <- user1) yield u0.age + u1.age

    val result = for {
      u0 <- user0
      u1 <- user1
    } yield u0.age + u1.age
    // Future[Int]
    result.onComplete {
      case Success(sum) => println(sum)
      case Failure(error) => println(error.getMessage)
    }
  }

  def patterns() = {

  }

  def main(args: Array[String]): Unit = {
    futureMonad()
  }
}

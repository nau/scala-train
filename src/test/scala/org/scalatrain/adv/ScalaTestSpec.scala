package org.scalatrain.adv

import java.io.File

import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Inspectors, OptionValues, Matchers, FlatSpec}


abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with PropertyChecks

class ScalaTestSpec extends UnitSpec with Inspectors {
  "I" should "show examples of matchers" in {
    val result = 3
    result should equal (3) // can customize equality
    result should === (3)   // can customize equality and enforce type constraints
    result should be (3)    // cannot customize equality, so fastest to compile
    result shouldEqual 3    // can customize equality, no parentheses required
    result shouldBe 3       // cannot customize equality, so fastest to compile, no parentheses required

    7.0 should be (6.9 +- 0.2)

    // String
    val string = "Hello world"
    string should startWith ("Hello")
    string should endWith ("world")
    string should startWith regex "Hel*o"
    string should endWith regex "wo.ld"
    string should include regex "wo.ld"

    // Options
    val opt = Some(2)
    opt.value should be < (7)
    opt should be ('defined)

    // Exceptions
    val thrown = the [IndexOutOfBoundsException] thrownBy "".charAt(-1)
    thrown.getMessage should startWith ("String")
    noException should be thrownBy 1 + 1

    // Containers
    Nil should be (empty)
    Set(1) should ((not be (empty)) and contain(1))
    // noneOf, atLeastOneOf, atMostOneOf, allOf
    Set(1, 2, 3) should contain oneOf(1, 5, 7)
    Set(1, 2, 3) should contain noneOf(4, 5, 7)
    Set(1, 2, 3) should contain allOf(1, 2)
    Set(1, 2, 3) should contain theSameElementsAs (List(3, 1, 2))

    // Type
    Some(2) shouldBe an [Option[_]] // type params erased
    None should not be a [String]

    // Inspectors
    val xs = List(1, 2, 3)
    forAll (xs) { x => x should be < 10 }
    all (xs) should be < 10
//    all (List(1, 10, 20)) should be < 10
//    every (List(1, 10, 20)) should be < 10
    atLeast(2, xs) should be < 3 // atMost, exactly, between

    // Composing matchers
    val beScreenshot = startWith("Screen") and endWith("png")
    "Screenshot.png" should beScreenshot
    val beFileScreenshot = beScreenshot compose { (f: File) => f.getPath }
    new File("Screenshot.png") should beFileScreenshot

    // Compile
    "trololo" shouldNot compile
    "val a: String = 1" shouldNot typeCheck
    "val a: Int = 1" should compile
  }
}

class PropertiesSpec extends UnitSpec with PropertyChecks {
  trait Fixture {
    val evenInts = for (n <- Gen.choose(1, 1000)) yield 2 * n
    val oneTwoThree = Gen.oneOf(1, 2, 3)
    val composition = for (i <- evenInts; j <- oneTwoThree) yield i -> j
  }

  "I" should "show examples to properties" in new Fixture {

    forAll { (a: String, b: String) => {
//      println(a, b)
      a.length + b.length should equal ((a + b).length)
    } }

    // Generators
    forAll (evenInts) { (n) => n % 2 should be (0) }
    forAll(composition) { case t@(i, j) => println(t); i * j should be > (0) }
  }

  it should "show table example" in {
    val users = Table(("user",               "passwords"),
      ("admin", ""),
      ("guest", "guest")
    )

    def checkPwd(user: String, pwd: String) = true

    forAll(users) { (u: String, p: String) => checkPwd(u, p) should be (true) }
  }
}
package org.scalatrain.adv

import org.scalatrain.basic.task.{JsBinOp, JsNum}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
 * http://henkelmann.eu/2011/01/13/an_introduction_to_scala_parser_combinators
 * http://www.codecommit.com/blog/scala/the-magic-behind-parser-combinators
 */
class ParsersSpec extends UnitSpec {

  sealed trait Result[+A] {
    val success: Boolean
    def map[B](f: A => B): Result[B]
  }

  case class Success[+A](value: A, rem: String) extends Result[A] {
    override val success: Boolean = true
    override def map[B](f: (A) => B): Result[B] = Success(f(value), rem)
  }

  case class Failure(msg: String) extends Result[Nothing] {
    override val success: Boolean = false
    override def map[B](f: (Nothing) => B): Result[B] = this
  }

  trait Parser[+A] extends (String => Result[A]) {
    def map[B](f: A => B): Parser[B] = parser(in => this(in) map f)

    def ~[B](that: => Parser[B]): Parser[(A, B)] = parser { in =>
      this(in) match {
        case Success(a, rem) => that(rem) match {
          case Success(b, rem) => Success((a, b), rem)
          case f: Failure => f
        }
        case f: Failure => f
      }
    }

    def |[U >: A](that: Parser[U]): Parser[U] = parser { in =>
      val r = this(in)
      if (r.success) r else that(in)
    }

    def * : Parser[List[A]] = parser { in =>
      val elems = ListBuffer[A]()
      @tailrec def applyParser(rest: String): Result[List[A]] = {
        this(rest) match {
          case Success(v, r) =>
            elems += v
            applyParser(r)
          case f => Success(elems.toList, rest)
        }
      }
      applyParser(in)
    }

    def ~>[B](that: => Parser[B]): Parser[B] = this ~ that map {
      case (a, b) => b
    }

    def + : Parser[List[A]] = this ~ this.* map {
      case (a, b) => a :: b
    }

    def ? : Parser[Option[A]] = map(Some(_)) | parser(in => Success(None, in))
  }

  implicit def keyword(str: String): Parser[String] = new Parser[String] {
    def apply(s: String) = {
      val trunc = s take str.length
      lazy val errorMessage = "Expected '%s' got '%s'".format(str, trunc)
      if (trunc == str) Success(str, s drop str.length) else Failure(errorMessage)
    }
  }

  def parser[A](p: String => Result[A]): Parser[A] = new Parser[A] {
    override def apply(v1: String): Result[A] = p(v1)
  }

  "Parsers" should "be useful" in {
    val clsParser = keyword("class")
    clsParser("class").success should be(true)
  }

  class SequenceParser[+A, +B](l: => Parser[A],
                               r: => Parser[B]) extends Parser[(A, B)] {
    lazy val left = l
    lazy val right = r

    def apply(s: String) = left(s) match {
      case Success(a, rem) => right(rem) match {
        case Success(b, rem) => Success((a, b), rem)
        case f: Failure => f
      }
      case f: Failure => f
    }
  }

  "Sequence combinator" should "be useful" in {
    val clsParser = "class" ~ " " ~ "Parser"
    val result = clsParser("class Parser")
    (result match {
      case Success((("class", " "), "Parser"), _) => true
      case _ => false
    }) should be(true)
  }

  "Disjoint combinator" should "be useful" in {
    val digit = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | "0"
    digit("2").success should be(true)
    val intNum = digit map (_.toInt)
    intNum("1") match {
      case Success(1, _) =>
      case _ => fail()
    }
  }

  "*, +, ? combinators" should "work" in {
    val digit = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | "0"
    val plusMinus = "+" | "-"

    val num = (digit +) map { ns => ns.mkString.toInt }

    val signedNum = plusMinus.? ~ num map {
      case (sign, num) => val koef = sign.map(s => if (s == "-") -1 else 1).getOrElse(1)
        koef * num
    }

    val nums = signedNum ~ ("," ~> signedNum).* map { case (n, ns) => n :: ns }

    num("112233") match {
      case Success(v, _) => v should be(112233)
      case _ => fail()
    }
    signedNum("-112233") match {
      case Success(v, _) => v should be(-112233)
      case _ => fail()
    }
    nums("1,+2,-3") match {
      case Success(1 :: 2 :: -3 :: Nil, _) =>
      case _ => fail()
    }
  }

  "simple expression parser" should "work" in {
    val digit = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | "0"

    val op = "+" | "-"

    val num = (digit +) map { ns => ns.mkString.toInt }

    def jsNum: Parser[JsNum] = num map JsNum

    def term = jsNum

    def nums: Parser[Option[JsNum]] = jsNum.?

    def binop: Parser[JsBinOp] = term ~ op ~ expr map { case ((l, op), r) => JsBinOp(op, l, r) }

    def expr = binop | term

    expr.apply("1+2-3") match {
      case Success(JsBinOp("+", JsNum(1), JsBinOp("-", JsNum(2), JsNum(3))), _) =>
      case r => fail()
    }
  }

}

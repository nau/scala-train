package org.scalatrain.basic.task

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.parsing.combinator.RegexParsers
import scala.util.{Failure, Success}

object ParsersAndFutures {
  def main(args: Array[String]) {
//    futures()
    parsers()
  }

  def futures() = {
    def calc(i: Int) = {
      Thread.sleep(i)
      println(s"Calculated $i!")
      i
    }
    val f1 = Future {
      calc(1000)
    }
    val result = Await.result(f1, Duration("1 min"))
    println(result)

    // You can define your own thread pool
//    implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

    val fs = for (i <- 1 to 300 reverse) yield Future(calc(i))
    Thread.sleep(5000)

    println("==================")

    val f = Future.sequence(fs)

    f.onComplete {
      case Success(results) =>
      case Failure(e) => e.printStackTrace
    }

    f.onFailure { case e => e.printStackTrace }
    f.onSuccess { case res => println(res) }

    val newF = f andThen {
      case Success(results) =>
      case Failure(e) => e.printStackTrace
    }

    // Monad composition

  }


  class JsExprParser extends RegexParsers {
    def plus: Parser[String] = literal("+")

    def minus: Parser[String] = "-"

    def op: Parser[String] = plus | minus

    def numFull: Parser[Int] = regex("""\d+""".r).map(s => s.toInt)

    def num: Parser[Int] = """\d+""".r ^^ (_.toInt)

    def stringLit1: Parser[~[~[String, String], String]] = "\"" ~ "\\w+".r ~ "\""

    def stringLit2: Parser[String ~ String ~ String] = "\"" ~ "\\w+".r ~ "\""

    def stringLit: Parser[String] = "\"" ~> "[^\"]+".r <~ "\""

    def jsString1 = stringLit map (s => JsString(s))

    def jsString = stringLit ^^ JsString

    def jsNum: Parser[JsNum] = num ^^ JsNum

    def term = jsNum | jsString

    def binop: Parser[JsBinOp] = term ~ op ~ expr ^^ { case l ~ op ~ r => JsBinOp(op, l, r) }

    def expr = binop | term

    def parseExpr(s: String) = parseAll(expr, s)
  }

  def parsers() = {
    val parser = new JsExprParser
    println(parser.parseExpr("1"))
    println(parser.parseExpr("\"string\""))
    println(parser.parseExpr(""" 1 - 2 + 3"""))
    println(parser.parseExpr(""" 1 - 2 + 3 + " is two" """))
  }

}

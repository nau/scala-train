package org.scalatrain.adv.macros

import scala.language.experimental.macros
import scala.language.dynamics
import scala.reflect.macros.whitebox

/**
 * http://docs.scala-lang.org/overviews/macros/overview.html
 * http://docs.scala-lang.org/overviews/quasiquotes/syntax-summary.html
 * http://infoscience.epfl.ch/record/185242/files/QuasiquotesForScala.pdf
 */



object MacroExample  {
  def macro1(code: Any): String = macro MacroExampleImpl.macro1
  def timed[A](code: => A): A = macro MacroExampleImpl.timed[A]
  def asrt(code: Boolean): Unit = macro MacroExampleImpl.asrt

  implicit class Ops[A](c: A) {
    def lens = new FieldGen(c)
  }
  class FieldGen[A](c: A) extends   Dynamic {
    def selectDynamic(propName: String): String = macro MacroExampleImpl.selectDynamic
  }

}

object MacroExampleImpl {
  def macro1(c: whitebox.Context)(code: c.Expr[Any]): c.Expr[String] = {
    import c.universe._
    val tree = code.tree
    val str = showCode(tree)
    val ast = showRaw(tree)
    val strast = s"$str\n$ast"
    c.Expr(q"$strast")
  }

  def timed[A: c.WeakTypeTag](c: whitebox.Context)(code: c.Expr[A]): c.Expr[A] = {
    import c.universe._
    val tree = code.tree
    val str = showCode(tree)
    c.Expr(q"""
           {
           val start = System.nanoTime()
           val r = $tree
           val diff = (System.nanoTime() - start) / 1000000
           println($str + " run in " + diff + "ms")
           r
      }""")
  }

  def asrt(c: whitebox.Context)(code: c.Expr[Boolean]): c.Expr[Unit] = {
    import c.universe._

    val tree = code.tree
    println(showRaw(tree))
    val msg = tree match {
      case q"$lhs == $rhs" => s"$lhs should be $rhs"
      case _ => "false"
    }

    val str = showCode(tree)
    c.Expr(q"""
      if (!($tree)) throw new RuntimeException($msg)
      """)
  }

  def selectDynamic(c: whitebox.Context)(propName: c.Expr[String]): c.Expr[String] = {
    import c.universe._
    val name = propName.tree
    val caller = c.prefix.tree
    c.Expr(q"""
      {println("Called " + $caller + $name); $name}
      """)
  }
}
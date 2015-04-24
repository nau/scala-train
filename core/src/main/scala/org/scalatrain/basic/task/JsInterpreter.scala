package org.scalatrain.basic.task

abstract class JsExpr
case class JsString(value: String) extends JsExpr
case class JsNum(value: Int) extends JsExpr
case class JsBinOp(operator: String, lhs: JsExpr, rhs: JsExpr) extends JsExpr

object JsInterpreter {
  def run(js: JsExpr): String = ???
}

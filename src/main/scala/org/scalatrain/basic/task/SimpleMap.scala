package org.scalatrain.basic.task

class SimpleMap {
  def size: Int = ???
  def filterKey(p: String => Boolean): SimpleMap = ???
  def filter(p: ((String, String)) => Boolean): SimpleMap = ???
  def map(p: ((String, String)) => (String, String)): SimpleMap = ???
  def flatMap(p: ((String, String)) => SimpleMap): SimpleMap = ???
  def fold(zero: String)(acc: (String, (String, String)) => String): String = ???
  def collect(pf: PartialFunction[(String, String), (String, String)]): SimpleMap = ???
  def get(k: String): Option[String] = ???
}

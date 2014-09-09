package org.scalatrain.basic.task

class SimpleMap {
  def size: Int = ???
  def filterKey(p: String => Boolean): this.type = ???
  def filter(p: ((String, String)) => Boolean): this.type = ???
  def map(p: ((String, String)) => (String, String)): this.type = ???
  def flatMap(p: ((String, String)) => SimpleMap): this.type = ???
  def fold(zero: String)(acc: (String, (String, String)) => String): this.type = ???
}

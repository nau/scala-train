package org.scalatrain.basic

import org.scalatest.FunSuite
import org.scalatrain.basic.task.SimpleMap

class SimpleMapTest extends FunSuite {

  test("An empty Map should have size 0") {
    val sm = new SimpleMap
    assert(sm.size == 0)
  }
}

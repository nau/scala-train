package org.scalatrain.basic

object OOP {
  def main(args: Array[String]) {
    classes()
  }

  def classes() = {

    abstract class AbstractDbService(val dbUrl: String, private var retryCount: Int = 0) {

      println(s"I'm ${getClass.getSimpleName} constructor")

      def this() = {
        this("default", 1)
        println("Auxiliary constructor")
      }


      protected def doConnect(): Unit

      private val a = b + 1
      println(s"a = $a, b = $b")
      private[basic] lazy val b = 2
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
  }
}

package org.scalatrain.basic

object Syntax {

  def main(args: Array[String]): Unit = {
    keywords(args)
    basicTypesAndLiterals()
    // Questions and Coffee-break with
    sugar()
  }

  def keywords(args: Array[String]) = {
    import java.util.Date
    import java.lang.Integer.bitCount
    bitCount(1)

    import java.io._
    import File._
    pathSeparator

    // final int constant = 1
    val constant: Int = 1

    // long i = 0
    var i = 0L

    //var j: Int = i // Won't compile

    val xml = <bean>Can't live without Spring</bean>; println(xml)

    def calc() = {
      println("Calculating the truth")
      Thread.sleep(1000)
      true
    }

    lazy val how = if (calc()) "hehe" else "haha"

    while (i < 10) i += 1

    do i -= 1 while (i > 0)

    // for comprehension
    for (arg <- args)
      println(arg)

    if (how.equals("hehe") || (how == "haha") || (how eq null)) println("bugaga")

    val result = try {
       how match {
        case "hehe" => 1
        case "haha" => 2
        case any => any.toInt
      }
    } catch {
      case e: Exception => 0
    } finally {
      4
    }
    println(result)
  }

  def basicTypesAndLiterals() = {
    val a: Byte = 1
    val b: Short = 2
    val c: Int = 0x3
    val d: Long = 4
    val e: Char = '\u0041'
    val f: Float = 0.5f
    val g: Double = .6e8d
    val h: Boolean = true || false
    val i: Array[String] = new Array[String](5)


    val j: String = s"a = $a, b = ${b.toString} \n"
    val k: String = """Multi
        line \n"""

    val l: String =
      """|10 LET A = 10
         |20 LET B = 20
         |30 PRINT A+B""".stripMargin


    println(j)
    println(k)
    println(l)


    val m: Symbol = 'OK
    val n: Symbol = 'WTF
    val o = Symbol("OK")
    println(s"m == n ${m == n}, m == o ${m == o}")

    val q: Function1[String, Boolean] = (s: String) => s.isEmpty()

    val x: Unit = ()
    val y: Null = null
    val z: Nothing = throw new RuntimeException("It's never gonna happen.")
  }

  def sugar() = {
    // Infix
    1 + 2 == 1.+(2)
    1 == 1 == 1.==(1)

    // Postfix, not recommended
    val one = 1 toString // newline is required here!

    // one.concat("0").charAt(0).toInt
    val i = one concat "0" charAt 0

    println(i)

    // Apply
    val f = (s: String) => s.isEmpty()
    f.apply("Not empty") // false
    f("") // true

    class Callable {
      def apply() = println("Called!")
    }

    val c = new Callable
    c.apply()
    c()

    // Update
    class Updatable {
      def update(i: Int, value: String): Unit = println(s"Update $i with $value")
    }
    val u = new Updatable
    u.update(5, "Test")
    u(5) = "Test"
  }

  def functions(): Unit = {
    // Functions & Lambdas
    def isEmpty(arg: String): Boolean = arg.isEmpty // type is Function1[String, Boolean]
    val isEmpty2 = (arg: String) => arg.isEmpty
  }

}

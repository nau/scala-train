package org.scalatrain.akka

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart, Resume}
import akka.actor._
import akka.pattern._
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Random

case class Trade(id: Int, notional: Int)

/**
 * Trades should be processed concurrently.
 * Trades with same id should be `process`ed sequentially
 */
class TradingSupervisor extends Actor {
  import context.dispatcher
  implicit val timeout = Timeout(1.second)
  val trades = mutable.HashMap[Int, ActorRef]()

  def getOrCreate(id: Int) =
    trades.getOrElseUpdate(id, context.actorOf(Props(classOf[TradingActor]), id.toString))

  def receive = {
    case t: Trade =>
      val s = sender()
      getOrCreate(t.id) ? t onComplete(r => s ! r)
  }
}

class ServiceActor extends Actor {
  def receive = {
    case t: Trade =>
      if (Random.nextBoolean()) sys.error(s"Ops! $t") else sender() ! t
  }
}

class TradingActor extends Actor {
  import context.dispatcher
  implicit val timeout = Timeout(1.second)
  var service1 = context.actorOf(Props(classOf[ServiceActor]))
  var service2 = context.actorOf(Props(classOf[ServiceActor]))

  def process(t: Trade): Trade = {
    Thread.sleep(1000)
    t
  }


  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(10, 1.minute) {
    case _: RuntimeException => Restart
    case _: Exception => Escalate
  }

  def receive = {
    case t: Trade =>
      val p = process(t)
      val f = Future.firstCompletedOf(Seq(service1 ? p, service2 ? p))
      val s = sender()
      f onComplete(r => s ! r)
  }
}

object TradingApp extends App {

  val system = ActorSystem()

  import system.dispatcher
  implicit val timeout = Timeout(1.second)

  val trader = system.actorOf(Props(classOf[TradingSupervisor]))

  val f = trader ? Trade(Random.nextInt(5), 1)
//  val f = Future.traverse(1 to 10)(n => trader ? Trade(Random.nextInt(5), n))

  f.onComplete {
    case r => println(r)
  }

  Await.result(f, 10.seconds)

}

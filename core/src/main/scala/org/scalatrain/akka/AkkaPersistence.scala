package org.scalatrain.akka

import akka.actor._
import akka.persistence._

/**
 * PersistentActor
 * PersistentView
 * AtLeastOnceDelivery
 * Snapshot/Recover
 *
 * Event sourcing
 */


case class Cmd(data: String)
case class Evt(data: String)

case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)
  def size: Int = events.length
  override def toString: String = events.reverse.toString
}

class EventSourcingActor extends PersistentActor {
  override def persistenceId = "sample-id-1"
  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def numEvents =
    state.size

  val receiveCommand: Receive = {
    case c@Cmd(data) =>
      if (validate(c)) {
        persist(Evt(s"$data-$numEvents"))(updateState)
        persist(Evt(s"$data-${numEvents + 1}")) { event =>
          updateState(event)
          context.system.eventStream.publish(event)
        }
      }
    case "snap"  => saveSnapshot(state)
    case SaveSnapshotSuccess(_) => println(s"Snapshot saved! Sequence number $lastSequenceNr")
    case SaveSnapshotFailure(_, t) => println(s"Snapshot failed! $t")
    case "print" => println(state)
    case "clean" =>
      deleteMessages(lastSequenceNr)
      println("Deleted messages!")
      deleteSnapshots(SnapshotSelectionCriteria.Latest)
      println("Deleted snapshots!")
  }

  def validate(c: Cmd) = true

  val receiveRecover: Receive = {
    case evt: Evt => println(s"Recovering... $evt"); updateState(evt)
    case SnapshotOffer(meta, snapshot: ExampleState) =>
      println("Got a Snapshot for SN:" + meta.sequenceNr)
      state = snapshot
    case RecoveryCompleted => println(s"Recovery completed!")
  }

  override protected def onRecoveryFailure(cause: Throwable, event: Option[Any]): Unit = super.onRecoveryFailure(cause, event)


  /**
   *  Uncomment to disable automatic recovery on start/restart,
   *  but need to recover manually
   */
//  @throws[Exception](classOf[Exception])
//  override def preStart(): Unit = {
//     self ! Recover(SnapshotSelectionCriteria(123L, 11111111L))
//  }
}


object AkkaPersistence extends App {

  val system = ActorSystem("example")
  val persistentActor = system.actorOf(Props[EventSourcingActor], "persistentActor-4-scala")
//  val viewActor = system.actorOf(Props[EventViewActor], "viewActor-4-scala")


//  persistentActor ! Cmd("foo")
//  persistentActor ! Cmd("baz")
//  persistentActor ! Cmd("bar")
//  persistentActor ! "snap"
//  persistentActor ! Cmd("buzz")
//  persistentActor ! Recover()
//  persistentActor ! "print"
//  persistentActor ! "clean"



//  val doorActor = system.actorOf(Props[PersistentDoor], "doorActor-4-scala")
//  doorActor ! Persistent("open")
//  doorActor ! Persistent("close")
//  doorActor ! Persistent("open")
//  doorActor ! "print"

  Thread.sleep(1000)
  system.terminate()
}

package nephtys.loom.frontend

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import angulate2.std.Injectable
import nephtys.dualframe.cqrs.client.{IDBConfig, IDBPersistenceService}
import nephtys.loom.protocol.vanilla.solar.SolarProtocol.SolarCommand
import upickle.default._

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by nephtys on 12/16/16.
  */
@Injectable
class VanillaCommandQueueService(idb: IDBPersistenceService) {

  println("VanillaCommandQueueService instantiated")

  implicit val idbConfig: IDBConfig = VanillaConstants.idbConfig

  private val onItsWayQueue : mutable.Queue[VanillaCommandPersistencePack] = mutable.Queue()
  private val inMemoryQueue : mutable.Queue[VanillaCommandPersistencePack] = mutable.Queue()

  //fill with values from indexedDB async after start
  idb.getAllAsync(commandStore = true).map(seq => seq.map(s => read[VanillaCommandPersistencePack](s)).sortBy(_.uniqueTimestamp)).foreach(
    seq => seq.foreach(e => inMemoryQueue.:+(e))
  )

  /**
    * takes a timestamp and a new UUID of the given command and returns it after writing the combined
    *
    * @param command
    * @return
    */
  def store(command : SolarCommand) : Future[UUID] = {
    val c = VanillaCommandPersistencePack(UUID.randomUUID(), newUniqueTimestamp(), command)
    inMemoryQueue.:+(c)
    idb.setAsync(c.uuid, write(c), commandStore = true).map(f => c.uuid)
  }

  def setAsTransmittedAndRemove(seq : Seq[UUID]) : Future[Unit] = {
    val s : Seq[VanillaCommandPersistencePack] = onItsWayQueue.dequeueAll(e => seq.contains(e.uuid))
    idb.removeSetAsync(seq, commandStore = true)
  }

  /**
    * takes the in-memory copy and moves it to a pool of sent commands ("on its way")
    * @return
    */
  def retreiveOpenCommands : (Seq[SolarCommand], Seq[UUID]) = {
    val s : Seq[VanillaCommandPersistencePack] = inMemoryQueue.dequeueAll(e => true)
    s.foreach(c => onItsWayQueue.enqueue(c))
    (s.map(_.command), s.map(_.uuid))
  }

  /**
    * moves the given commands back into the open queue (as head list)
    * @param seq
    * @return
    */
  def failedTransmission(seq : Seq[UUID]) : Unit = {
    val s : Seq[VanillaCommandPersistencePack] = onItsWayQueue.dequeueAll(e => seq.contains(e.uuid))

    //the following isn't needed anymore in scala 2.12, as there is a prepend function to queue (but you'd still have to reverse s)
    val therest  : Seq[VanillaCommandPersistencePack] = inMemoryQueue.dequeueAll(e => true)
    s.foreach(c => inMemoryQueue.enqueue(c))
    therest.foreach(c => inMemoryQueue.enqueue(c))
  }


  private var lastTimestamp : Long = 0
  private def newUniqueTimestamp() : Long = {
    val c = System.currentTimeMillis()
    if (c == lastTimestamp) {
      lastTimestamp = c+1
      c+1
    } else {
      lastTimestamp = c
      c
    }
  }
}

package nephtys.loom.frontend


import angulate2.std.Injectable
import nephtys.dualframe.cqrs.client.{IDBConfig, IDBPersistenceService, TokenService}
import nephtys.loom.frontend.IncrementalChanges.{Deletion, Insertion, Update}
import nephtys.loom.protocol.vanilla.solar.Solar
import nephtys.loom.protocol.zprotocols.ZSolarProtocol
import nephtys.loom.protocol.zprotocols.ZSolarProtocol.{SolarCommand, SolarEvent}
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxscalajs.subjects.BehaviorSubject
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

/**
  * Created by nephtys on 12/16/16.
  */
@Injectable
class VanillaInMemoryService(idb: IDBPersistenceService, tokenService: TokenService) {


  println("VanillaInMemoryService instantiated")

  implicit val idbConfig: IDBConfig = VanillaConstants.idbConfig

  var allCharacters : js.Array[Solar] = js.Array()

  private var aggregateMap : Map[ID[Solar], Solar] = Map()

  val changes : BehaviorSubject[Option[IncrementalChanges.Change]] = BehaviorSubject[Option[IncrementalChanges.Change]](None)


  idb.getAllAsync(commandStore = false).map(seq => seq.map(s => (Try(read[Solar](s)), s)).filter(t => t._1 match {
    case s : Success[Solar] => true
    case Failure(f) => {
      org.scalajs.dom.window.alert(s"Could not read stored Solar Character from JSON format, \nerror: ${f.getMessage}\nJSON: ${t._2}")
      false
    }
  }).map(_._1.get).map(s => (s.id, s)).toMap[ID[Solar], Solar]).foreach(newmap => {
    aggregateMap = newmap
    allCharacters = newmap.values.toJSArray
    newmap.keys.foreach(k => changes.next(Some(Insertion(k.id))))
  })

  def keys : IndexedSeq[ID[Solar]] = aggregateMap.keys.toIndexedSeq


  def get(id : ID[Solar]) : Option[Solar] = {
    //println("Aggregate map is : " + aggregateMap.toString)
    aggregateMap.get(id)
  }

  def checkCommandLocally(command : SolarCommand) : Boolean = command.validate(tokenService.getCurrentEmail, aggregateMap).isSuccess
  def applyCommandLocally(command : SolarCommand) : Future[Try[SolarEvent]] = {
    val eventTry : Try[SolarEvent] = command.validate(tokenService.getCurrentEmail, aggregateMap).asInstanceOf[Try[SolarEvent]]
    eventTry match {
      case Success(event) => {
        val diff = findDiffSingle(event)
        val newmap = event.commit(aggregateMap)
        aggregateMap = newmap
        changes.next(Some(diff))

        diff match {
          case Deletion(id) => {
            val index : Int = (0 until allCharacters.length).find(i => allCharacters(0).id.id == id).getOrElse(0)
            allCharacters.splice(index, 1)
            idb.removeAsync(id, commandStore = false).map(f => Success(event))
          }
          case Insertion(id) => {
            allCharacters.push(newmap(ID[Solar](id)))
            idb.setAsync(id, write(newmap(ID[Solar](id))), commandStore = false).map(f => Success(event))
          }
          case Update(id) => {
            val index : Int = (0 until allCharacters.length).find(i => allCharacters(0).id.id == id).getOrElse(0)
            allCharacters(index) = newmap(ID[Solar](id))
            idb.setAsync(id, write(newmap(ID[Solar](id))), commandStore = false).map(f => Success(event))
          }
        }
      }
      case Failure(e) => Future.successful(Failure(e))
    }
  }

  def refreshWithValuesFromRemote(seq : Seq[Solar]) : Future[Unit] = {
    val newmap : Map[ID[Solar], Solar] = seq.map(s => (s.id, s)).toMap
    aggregateMap = newmap
    allCharacters = newmap.values.toJSArray
    idb.setSetAsync(newmap.map(a => (a._1.id, write(a._2))), commandStore = false)
  }

  protected def findDiffSingle(event : SolarEvent) : IncrementalChanges.Change = event match {
    case ZSolarProtocol.Creation(owner, id) => IncrementalChanges.Insertion(id.id)
    case ZSolarProtocol.Deletion(id) => IncrementalChanges.Deletion(id.id)
    case _ => IncrementalChanges.Update(event.id.id)
  }

  protected def findDiff(from : Map[ID[Solar], Solar], to : Map[ID[Solar], Solar]) : (Seq[Insertion], Seq[Deletion], Seq[Update]) = {
    val newkeys = to.keySet
    val oldkeys = from.keySet
    val addedkeys = newkeys.diff(oldkeys)
    val removedkeys = oldkeys.diff(newkeys)
    val updatedkeys = newkeys.diff(addedkeys).filter(k => from.contains(k) && to.contains(k) && ! from(k).equals(to(k)))

    (addedkeys.map(id => Insertion(id.id)).toSeq, removedkeys.map(id => Deletion(id.id)).toSeq, updatedkeys.map(id => Update(id.id)).toSeq)
  }
}

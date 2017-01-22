package nephtys.loom.frontend


import java.util.concurrent.TimeUnit

import angulate2.std.Injectable
import nephtys.dualframe.cqrs.client.HttpService
import nephtys.dualframe.cqrs.client.httphelper.HttpResults
import nephtys.loom.protocol.vanilla.solar.{Solar, SolarProtocol}
import nephtys.loom.protocol.vanilla.solar.SolarProtocol.{SolarCommand, SolarEvent}
import org.nephtys.loom.generic.protocol.InternalStructures.{EndpointRoot, FailableList}
import rxscalajs.Observable
import upickle.default._
import rxscalajs.subjects.BehaviorSubject

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by nephtys on 12/16/16.
  */
@Injectable
class VanillaControlService(aggregateService: VanillaInMemoryService, httpService: HttpService, commandService: VanillaCommandQueueService) {

  println("VanillaControlService instantiated")

  protected val pushSubject : BehaviorSubject[Boolean] = BehaviorSubject[Boolean](true)
  protected val debounced: Observable[Boolean] = pushSubject.debounceTime(FiniteDuration(3, TimeUnit.SECONDS))

  protected def refreshFromRemote() : Future[Boolean] = {
    println("Refreshing from remote")
    val p = Promise[Boolean]()
    val f = httpService.get("aggregates", endpoint).map(a => read[Seq[Solar]](a.body))
    f.onComplete {
      case Failure(e) => {
        p.trySuccess(false)
      }
      case Success(t) => {
        aggregateService.refreshWithValuesFromRemote(t)
        p.trySuccess(true)
      }
    }
    p.future
  }
  protected def endpoint : Option[String] = Some(SolarProtocol.endpointRoot.prefix)
  protected def pushOpenChangesToRemote() : Future[Boolean] = {
    val seqf = commandService.retreiveOpenCommands
    seqf.flatMap(seq => {
      if (seq._1.isEmpty) {
        println("No commands to push")
        Future.successful(true)
      } else {
        val p = Promise[Boolean]()
        println(s"Found commands to push, #${seq._1.size}")
        //try to send over http
        val json: String = write(seq._1)
        val f = httpService.post("commands", json, endpoint)
        f.onComplete {
          case Failure(e) => {
            //if http fails, revert command service, and just fail
            commandService.failedTransmission(seq._2)
            p.trySuccess(false)
          }
          case Success(t) => {
            //if http succeeds, delete commands locally
            commandService.setAsTransmittedAndRemove(seq._2)
            p.trySuccess(true)
          }
        }

        p.future
      }
    })
  }

  debounced.subscribe(onNext = (b : Boolean) => {
    println("Debounced in VanillaControl Triggered")
    pushOpenChangesToRemote().flatMap(a => if(a) {
      refreshFromRemote()
    } else { Future.successful(false)}).foreach(b =>  println(if(b) "Refreshed content by remote data call" else "couldn't update from remote (offline?)"))
  })

  def checkCommand(command : SolarCommand) : Boolean = aggregateService.checkCommandLocally(command)
  def enqueueCommand(command : SolarCommand): Future[Seq[Try[SolarEvent]]] = enqueueCommands(Seq(command))
  def enqueueCommands(commands : Seq[SolarCommand]) : Future[Seq[Try[SolarEvent]]] = {
    //add to queue
    commands.foreach(c => commandService.store(c))

    //apply locally
    val f : Future[Seq[Try[SolarEvent]]] = Future.sequence(commands.map(c => aggregateService.applyCommandLocally(c)))
    f
  }

  /**
    * sends all open changes to the server and refreshes from remote if possible
    */
  def externalTimerTick() : Unit = pushSubject.next(true)

}

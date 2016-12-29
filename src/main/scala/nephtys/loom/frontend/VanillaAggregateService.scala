package nephtys.loom.frontend

import java.util.UUID

import angulate2.std.Injectable
import nephtys.loom.protocol.vanilla.solar.Misc.{Dawn, Twilight, Zenith}
import nephtys.loom.protocol.vanilla.solar.{Characters, Solar}
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxscalajs.Observable
import rxscalajs.subjects.BehaviorSubject

/**
  * Created by nephtys on 12/8/16.
  */
@Injectable
class VanillaAggregateService {

  private def ownermock = Email("christopher.kaag@gmail.com")
  private def mocks = Seq(
    Characters.emptySolar(ID[Solar](UUID.fromString("2d246cc1-77ac-406a-ae7c-f84d9cee16c8")), ownermock).copy(caste = Some(Zenith), name = ("Thrice-Holy Magnus")),
    Characters.emptySolar(ID[Solar](UUID.fromString("1d216cc1-76ac-406a-ae7c-f84d9cee16c8")), ownermock).copy(caste = Some(Dawn)),
    Characters.emptySolar(ID[Solar](UUID.fromString("4d246bd3-77ac-406a-ae7c-f84d9cee16c8")), ownermock)
  ).map(a => (a.id, a)).toMap

  def createNew(id : ID[Solar], name : String) : Unit = internalMap.next(store.+((id, Characters.emptySolar(id, ownermock).copy(name = (name)))))

  private var store : Map[ID[Solar], Solar] = Map.empty
  private val internalMap = BehaviorSubject[Map[ID[Solar], Solar]](mocks)
  internalMap.subscribe(m => store = m)
  internalMap.subscribe(m => println(s"Internal datastore changed to $m"))
  val allInstances : Observable[Map[ID[Solar],Solar]] = internalMap.map(a => a)
  def getInstance(id : ID[Solar]) : Option[Solar] = mocks.get(id)

}

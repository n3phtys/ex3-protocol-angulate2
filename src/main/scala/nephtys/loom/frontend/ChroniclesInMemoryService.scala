package nephtys.loom.frontend

import java.util.UUID

import angulate2.std.Injectable
import nephtys.loom.protocol.chronicles.solar.{CharacterFactory, Solar}
import org.nephtys.loom.generic.protocol.InternalStructures.ID
import rxscalajs.subjects.BehaviorSubject

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

/**
  * Created by Christopher on 25.01.2017.
  */
@Injectable
class ChroniclesInMemoryService {
  val changes : BehaviorSubject[Option[IncrementalChanges.Change]] = BehaviorSubject[Option[IncrementalChanges.Change]](None)

  def get(id: ID[Solar]) : Option[Solar] = {
    //TODO: improve
    val o = allCharacters.find(_.id.equals(id))
    println(s"found in $allCharacters with get $id: $o")
    o
  }


  var allCharacters : js.Array[Solar] = Seq[Solar](CharacterFactory.emptyChroniclesSolar(UUID.fromString("4e7fa7e8-9e6d-43db-9376-0837988fc747"), CharacterFactory.defaultEmail)).toJSArray

  //println("Characters of Chronicles:")
  //allCharacters.foreach(println)




}

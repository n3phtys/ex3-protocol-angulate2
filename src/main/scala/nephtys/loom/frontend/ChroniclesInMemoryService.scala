package nephtys.loom.frontend

import angulate2.std.Injectable
import nephtys.loom.protocol.chronicles.solar.{CharacterFactory, Solar}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

/**
  * Created by Christopher on 25.01.2017.
  */
@Injectable
class ChroniclesInMemoryService {

  var allCharacters : js.Array[Solar] = Seq[Solar](CharacterFactory.emptyChroniclesSolar).toJSArray

  //println("Characters of Chronicles:")
  //allCharacters.foreach(println)

}

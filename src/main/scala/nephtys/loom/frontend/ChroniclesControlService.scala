package nephtys.loom.frontend

import angulate2.std.Injectable
import nephtys.loom.protocol.chronicles.solar.ChroniclesSolarProtocol.{SolarCommand, SolarEvent}

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by Christopher on 25.01.2017.
  */
@Injectable
class ChroniclesControlService {

  def enqueueCommands(commands : Seq[SolarCommand]) : Future[Seq[Try[SolarEvent]]] = {
    ???
  }

}

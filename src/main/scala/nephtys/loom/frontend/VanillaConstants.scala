package nephtys.loom.frontend

import nephtys.dualframe.cqrs.client.IDBConfig

/**
  * Created by nephtys on 12/16/16.
  */
object VanillaConstants {

  val idbConfig = IDBConfig("VanillaSolarDatabase", "Aggregates", "Commands", 1)
}

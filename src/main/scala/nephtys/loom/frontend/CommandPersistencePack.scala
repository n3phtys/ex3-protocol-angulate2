package nephtys.loom.frontend

import java.util.UUID


/**
  * Created by nephtys on 12/16/16.
  */
final case class CommandPersistencePack[T](uuid : UUID, uniqueTimestamp : Long, command : T) {
//it could be possible to use the uniqueTimestamp as unique key and remove the UUID completely, BUT:
  //imagine a case where the clock on the client is turned back between app starts
  //this way the unique timestamp could suddenly be a little less unique
}
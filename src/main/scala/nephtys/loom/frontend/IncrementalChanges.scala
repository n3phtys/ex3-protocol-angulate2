package nephtys.loom.frontend

import java.util.UUID

/**
  * Created by nephtys on 12/16/16.
  */
object IncrementalChanges {

  sealed trait Change {
    def id : UUID
  }
  final case class Insertion(id : UUID) extends Change
  final case class Deletion(id : UUID) extends Change
  final case class Update(id : UUID) extends Change
}

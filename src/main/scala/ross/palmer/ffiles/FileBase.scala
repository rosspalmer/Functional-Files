package ross.palmer.ffiles

import cats.Monoid
import cats.implicits.*

import io.circe.Json
import io.circe._

import scala.io.Source

import ross.palmer.ffiles.CatsUtils

case class FileInfo(file: Source, metadata: Json = Json.Null) {
  def mergeMetadata(data: Json): FileInfo = withMetadata(metadata.deepMerge(data))
  def withMetadata(data: Json): FileInfo = FileInfo(file, data)
}
case class FileBase(infoSet: Set[FileInfo])

abstract class FileBaseMonoid extends Monoid[FileBase] {
  override def empty: FileBase = FileBase(Set.empty)
}

object FileBaseMonoids {

  val merge: Monoid[FileBase] = new FileBaseMonoid {
    override def combine(x: FileBase, y: FileBase): FileBase = {
      FileBase(CatsUtils.combineAll(List(x.infoSet, y.infoSet)))
    }
  }

  def loadMetaFromFilePath(metaRegex: String, merge: Boolean = true): Monoid[FileBase] = new FileBaseMonoid {

    val r = metaRegex.r

    def runRegex(f: FileInfo): FileInfo = {
      val m = r.findFirstMatchIn(f.file.descr)
      if (m.isDefined) {
        val newData = Json.fromFields(m.get.groupNames.map(n => (n, Json.fromString(m.get.group(n)))))
        if (merge) f.mergeMetadata(newData) else f.withMetadata(newData)
      } else f
    }

    override def combine(x: FileBase, y: FileBase): FileBase = {

      implicit val m: Monoid[FileBase] = FileBaseMonoids.merge
      val yWithTags = FileBase(y.infoSet.map(runRegex))
      CatsUtils.combineAll(List(x, yWithTags))

    }
  }

}


package ross.palmer.ffiles

import cats.Monoid
import cats.implicits.*
import io.circe.Json
import io.circe.*

import java.io.File
import ross.palmer.ffiles.CatsUtils

import scala.util.matching.Regex

case class FileInfo(file: File, metadata: Json = Json.Null) {
  def mergeMetadata(data: Json): FileInfo = withMetadata(metadata.deepMerge(data))
  def withMetadata(data: Json): FileInfo = FileInfo(file, data)
}
case class FileBase(infoSet: Seq[FileInfo])

abstract class FileBaseMonoid extends Monoid[FileBase] {
  override def empty: FileBase = FileBase(Seq.empty)
}

object FileBaseMonoids {

  val merge: Monoid[FileBase] = new FileBaseMonoid {
    override def combine(x: FileBase, y: FileBase): FileBase = {
      FileBase(CatsUtils.combineAll(List(x.infoSet, y.infoSet)))
    }
  }

  def loadMetaFromFilePath(metaRegex: String, metaNames: Seq[String],
                           merge: Boolean = true): Monoid[FileBase] = new FileBaseMonoid {

    val regex = new Regex(metaRegex, metaNames: _*)

    def runRegex(f: FileInfo): FileInfo = {
      val m = regex.findFirstMatchIn(f.file.getPath)
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


package ross.palmer.ffiles

import cats.Monoid
import cats.implicits.*
import io.circe.Json
import io.circe.*

import java.io.File
import ross.palmer.ffiles.CatsUtils

import scala.util.matching.Regex

//case class FileInfo(file: File, metadata: Json = Json.Null) {
//  def mergeMetadata(data: Json): FileInfo = withMetadata(metadata.deepMerge(data))
//  def withMetadata(data: Json): FileInfo = FileInfo(file, data)
//}
case class FileSet(pointers: Seq[FilePointer])

abstract class FileSetMonoid extends Monoid[FileSet] {
  override def empty: FileSet = FileSet(Seq.empty)
}
//
//object FileSetMonoids {
//
//  val merge: Monoid[FileSet] = new FileSetMonoid {
//    override def combine(x: FileSet, y: FileSet): FileSet = {
//      FileSet(CatsUtils.combineAll(List(x.infoSet, y.infoSet)))
//    }
//  }
//
//  def loadMetaFromFilePath(metaRegex: String, metaNames: Seq[String],
//                           merge: Boolean = true): Monoid[FileSet] = new FileSetMonoid {
//
//    val regex = new Regex(metaRegex, metaNames: _*)
//
//    def runRegex(f: FileInfo): FileInfo = {
//      val m = regex.findFirstMatchIn(f.file.getPath)
//      if (m.isDefined) {
//        val newData = Json.fromFields(m.get.groupNames.map(n => (n, Json.fromString(m.get.group(n)))))
//        if (merge) f.mergeMetadata(newData) else f.withMetadata(newData)
//      } else f
//    }
//
//    override def combine(x: FileSet, y: FileSet): FileSet = {
//
//      implicit val m: Monoid[FileSet] = FileSetMonoids.merge
//      val yWithTags = FileSet(y.infoSet.map(runRegex))
//      CatsUtils.combineAll(List(x, yWithTags))
//
//    }
//  }
//
//}
//

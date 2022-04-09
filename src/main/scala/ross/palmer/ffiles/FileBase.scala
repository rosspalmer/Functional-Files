package ross.palmer.ffiles

import scala.io.Source

import cats.Monoid
import cats.implicits._

case class FileInfo(file: Source,
                    hashTags: Set[String] = Set.empty,
                    namedTags: Map[String, String] = Map.empty) {

  def mergeTags(tags: Set[String]): FileInfo = withTags(hashTags.concat(tags))
  def mergeTags(tags: Map[String, String]): FileInfo = withTags(tags)
  def withTags(tags: Set[String]): FileInfo = FileInfo(file, tags, namedTags)
  def withTags(tags: Map[String, String]): FileInfo = FileInfo(file, hashTags, tags)

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

  def splitByRegex(file: Source, splitRegex: String): Set[String] = file.descr.split(splitRegex).toSet
  def mapByRegex(lines: Set[String], mapRegex: String): Map[String, String] = {
    val r = mapRegex.r
    lines.map(l => r.findFirstMatchIn(l))
         .filter(_.isDefined)
         .map(m => (m.get.group(1), m.get.group(2))).toMap
  }

  def hashTagBySplit(splitRegex: String, merge: Boolean = true): Monoid[FileBase] = new FileBaseMonoid {
    override def combine(x: FileBase, y: FileBase): FileBase = {

      implicit val m = FileBaseMonoids.merge
      val withFunc: FileInfo => Set[String] = f => {
        val tags = f.file.descr.split(splitRegex).toSet
        if (merge) f.hashTags.concat(tags) else tags
      }

      val yWithTags = FileBase(y.infoSet.map(f => f.withTags(withFunc(f))))
      CatsUtils.combineAll(List(x, yWithTags))

    }
  }

  def namedTagByRegex(splitRegex: String, nameRegex: String, merge: Boolean = true): Monoid[FileBase] = new FileBaseMonoid {
    override def combine(x: FileBase, y: FileBase): FileBase = {

      def addNamedTags(f: FileInfo): FileInfo = {
        val newTags = mapByRegex(splitByRegex(f.file, splitRegex), nameRegex)
        f.withTags(if (merge) CatsUtils.combineAll(List(f.namedTags, newTags)) else newTags)
      }

      implicit val m = FileBaseMonoids.merge
      CatsUtils.combineAll(List(x, FileBase(y.infoSet.map(addNamedTags))))

    }
  }

  def namedTagByOrder(splitRegex: String, nameRegex: String, merge: Boolean = true): Monoid[FileBase] = new FileBaseMonoid {
    override def combine(x: FileBase, y: FileBase): FileBase = {

      def addNamedTags(f: FileInfo): FileInfo = {
        val newTags = mapByRegex(splitByRegex(f.file, splitRegex), nameRegex)
        f.withTags(if (merge) CatsUtils.combineAll(List(f.namedTags, newTags)) else newTags)
      }

      implicit val m = FileBaseMonoids.merge
      CatsUtils.combineAll(List(x, FileBase(y.infoSet.map(addNamedTags))))

    }
  }

}


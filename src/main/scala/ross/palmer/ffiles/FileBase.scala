package ross.palmer.ffiles

import scala.io.Source

import cats.Monoid

def combineAll[A: Monoid](as: List[A]): A =
  as.foldLeft(Monoid[A].empty)(Monoid[A].combine)

case class FileBaseInfo(val stuff: String)
case class FileBase(val files: Set[Source], val info: FileBaseInfo)


object FileBaseMonads {

  implicit val concat: Monoid[FileBaseInfo] = new Monoid[FileBaseInfo] {
    override def empty: FileBaseInfo = FileBaseInfo("")
    override def combine(x: FileBaseInfo, y: FileBaseInfo): FileBaseInfo = {
      FileBaseInfo(x.stuff + y.stuff)
    }
  }

  val concatInfo: Monoid[FileBase] = new Monoid[FileBase] {
    override def empty: FileBase = FileBase(Set.empty, concat.empty)
    override def combine(x: FileBase, y: FileBase): FileBase = {
      FileBase(x.files.concat(y.files), CatsUtils.combineAll(List(x.info, y.info)))
    }
  }

}


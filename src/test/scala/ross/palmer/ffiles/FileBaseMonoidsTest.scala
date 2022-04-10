package ross.palmer.ffiles

import org.scalatest.wordspec.AnyWordSpec

import java.io.File
import java.net.URL
import scala.io.Source

class FileBaseMonoidsTest extends AnyWordSpec {

  def getFile(path: String): File = {
    val resource: URL = getClass().getClassLoader().getResource(path)
    new File(resource.toURI())
  }

  lazy val fileBaseA = FileBase(Seq(
    FileInfo(getFile("file-base-a/part=1/fileA.txt")),
    FileInfo(getFile("file-base-a/part=2/fileB.txt")),
    FileInfo(getFile("file-base-a/part=2/fileC.txt"))
  ))

  lazy val fileBaseB = FileBase(Seq(
    FileInfo(getFile("file-base-b/part=4/fileD.txt")),
    FileInfo(getFile("file-base-b/part=4/fileX.xxx")),
    FileInfo(getFile("file-base-b/part=y/fileE.txt"))
  ))

  "FileBaseMonoids" should {

    "merge" in {

      implicit val m = FileBaseMonoids.merge
      val combined = CatsUtils.combineAll(List(fileBaseA, fileBaseB))

      assert(combined.infoSet.size == 6)
      assert(".+/file-base-a/part=1/fileA.txt$".r.findFirstMatchIn(combined.infoSet(0).file.getPath).isDefined)
      assert(".+/file-base-a/part=2/fileB.txt$".r.findFirstMatchIn(combined.infoSet(1).file.getPath).isDefined)
      assert(".+/file-base-a/part=2/fileC.txt$".r.findFirstMatchIn(combined.infoSet(2).file.getPath).isDefined)
      assert(".+/file-base-b/part=4/fileD.txt$".r.findFirstMatchIn(combined.infoSet(3).file.getPath).isDefined)
      assert(".+/file-base-b/part=4/fileX.xxx$".r.findFirstMatchIn(combined.infoSet(4).file.getPath).isDefined)
      assert(".+/file-base-b/part=y/fileE.txt$".r.findFirstMatchIn(combined.infoSet(5).file.getPath).isDefined)

    }

    "loadMetaFromFilePath" in {

      implicit val m = FileBaseMonoids.loadMetaFromFilePath(
        ".+/file-base-[ab]/part=([0-9])/(.+\\.txt)$", Seq("part-num", "file-name")
      )
      val combined = CatsUtils.combineAll(List(fileBaseA, fileBaseB))

      assert(combined.infoSet.size == 6)

      val fileA = combined.infoSet(0)
      assert(".+/file-base-a/part=1/fileA.txt$".r.findFirstMatchIn(fileA.file.getPath).isDefined)
      assert(fileA.metadata.spaces2 ==
        """{
          |  "part-num" : "1",
          |  "file-name" : "fileA.txt"
          |}""".stripMargin)

      val fileB = combined.infoSet(1)
      assert(".+/file-base-a/part=2/fileB.txt$".r.findFirstMatchIn(fileB.file.getPath).isDefined)
      assert(fileB.metadata.spaces2 ==
        """{
          |  "part-num" : "2",
          |  "file-name" : "fileB.txt"
          |}""".stripMargin)

      val fileC = combined.infoSet(2)
      assert(".+/file-base-a/part=2/fileC.txt$".r.findFirstMatchIn(fileC.file.getPath).isDefined)
      assert(fileC.metadata.spaces2 ==
        """{
          |  "part-num" : "2",
          |  "file-name" : "fileC.txt"
          |}""".stripMargin)

      val fileD = combined.infoSet(3)
      assert(".+/file-base-b/part=4/fileD.txt$".r.findFirstMatchIn(fileD.file.getPath).isDefined)
      assert(fileD.metadata.spaces2 ==
        """{
          |  "part-num" : "4",
          |  "file-name" : "fileD.txt"
          |}""".stripMargin)

      val fileX = combined.infoSet(4)
      assert(".+/file-base-b/part=4/fileX.xxx$".r.findFirstMatchIn(fileX.file.getPath).isDefined)
      assert(fileX.metadata.isNull)

      val fileE = combined.infoSet(5)
      assert(".+/file-base-b/part=y/fileE.txt$".r.findFirstMatchIn(fileE.file.getPath).isDefined)
      assert(fileE.metadata.isNull)


    }
  }
}

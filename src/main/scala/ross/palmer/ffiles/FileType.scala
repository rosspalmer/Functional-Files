
package ross.palmer.ffiles

import java.io.File


trait FileType {

  // TODO Return cats based results on actions
  def copy(inputPath: String, outputPath: String): Unit
  def move(inputPath: String, outputPath: String): Unit
  def remove(filePath: String): Unit

  // FIXME what is the best string buffer to return?
  def readBuffer(filePath: String): Buffer[String]
  def readNLines(filePath: String, n: Int): Iterator[String]
  def readAll(filePath: String): Iterator[String]


}

object LocalFileType extends FileType {
  
}

object FtpFileType extends FileType {

}

object SshFileType extends FileType {

}

object HdfsFileType extends FileType {

}

object S3FileType extends FileType {

}


package ross.palmer.ffiles.types

import java.io.File

sealed abstract class FileType(name: String)

object FileType {
  case object Local extends FileType("local")
  case object FTP extends FileType("ftp")
  case object SSH extends FileType("ssh")
  case object HDFS extends FileType("hdfs")
  case object S3 extends FileType("s3")
}


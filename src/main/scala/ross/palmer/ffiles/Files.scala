package ross.palmer.ffiles

case class FileInput(filePath: String)
case class FileOutput(fileInput: FileInput, outputPath: String)
case class FileWithTags(fileInput: FileInput, fileTags: Set[String])
case class FileWithNamedTags(fileInput: FileInput, fileTags: Map[String, String])

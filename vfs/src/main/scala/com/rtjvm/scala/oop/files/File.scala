package com.rtjvm.scala.oop.files

class File (override val parentPath: String,
            override val name: String,
            val contents: String = "") extends DirEntry(parentPath, name) {

  def setContents(contents: String): DirEntry = new File(parentPath, name, contents)
  def appendContent(contents: String): DirEntry = new File(parentPath, name, this.contents + "\n" + contents)


  override def asDirectory: Directory = throw new RuntimeException("File cannot be converted to directory")
  override def prettyString: String = s"$name[File]"
  override def asFile: File = this
  override def isDirectory: Boolean = false
  override def isFile: Boolean = true
}

object File {
  def empty(parentPath: String, fileName: String) =
    new File(parentPath, fileName, "")
}

package com.rtjvm.scala.oop.files

class Directory (override val parentPath: String,
                 override val name: String,
                 val contents: List[DirEntry]) extends DirEntry (parentPath, name) {
}

object Directory {
  val SEPERATOR = "/"
  val ROOT_PATH = "/"

  def empty(parentPath: String, directoryName: String) =
    new Directory(parentPath, directoryName, List())

  def ROOT:Directory = Directory.empty(null, "")
}

package com.rtjvm.scala.oop.files

import scala.annotation.tailrec

class Directory (override val parentPath: String,
                 override val name: String,
                 val contents: List[DirEntry]) extends DirEntry (parentPath, name) {

  def findEntry(entryName: String): DirEntry = {
    @tailrec
    def findEntryHelper(name: String, contentList :List[DirEntry]): DirEntry = {
      if (contentList.isEmpty) null
      else if (contentList.head.name.equals(name)) contentList.head
      else findEntryHelper(name, contentList.tail)
    }

    findEntryHelper(entryName, contents)
  }

  def replaceEntry(name: String, newEntry: DirEntry): Directory =
    new Directory(parentPath, name, contents.filter(x => !x.name.equals(name)) :+ newEntry)

  def addEntry(newDir: DirEntry) =
    new Directory(parentPath, name, contents :+ newDir)

  @tailrec
  final def findDescendant(path: List[String]): Directory = {
    if (path.isEmpty) this
    else findEntry(path.head).asDirectory.findDescendant(path.tail)
  }

  def getAllFoldersinPath(): List[String] = {
     if (!path.isEmpty()) path.substring(1).split(Directory.SEPERATOR).toList.filter(x => !x.isEmpty)
    else List()
  }

  def hasEntry(name: String): Boolean = findEntry(name) != null

  def path: String =
    if (name.isEmpty) Directory.SEPERATOR
    else if (Directory.SEPERATOR.equals(parentPath)) s"$parentPath$name"
    else s"$parentPath${Directory.SEPERATOR}$name"

  def isRoot:Boolean = (parentPath == null)

  override def asDirectory: Directory = this

  override def prettyString: String = s"$name[Directory]"

  override def asFile: File = throw new RuntimeException("Directory cannot be converted to File")

  override def isDirectory: Boolean = true

  override def isFile: Boolean = false
}

object Directory {
  val SEPERATOR = "/"
  val ROOT_PATH = "/"

  def empty(parentPath: String, directoryName: String) =
    new Directory(parentPath, directoryName, List())

  def ROOT:Directory = Directory.empty(null, "")
}

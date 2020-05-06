package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

class CreateEntry (name: String) extends Command {
  def doCreateDirEntry(state: State, name: String):DirEntry = ???
  def checkIllegal(name: String): Boolean = name.contains(".")

  def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
    if (path.isEmpty) currentDirectory.addEntry(newEntry)
    else {
      val oldEntry = currentDirectory.findEntry(path.head).asDirectory
      currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry ))
    }
  }

  def doCreateEntry(state:State, name: String): State = {
    val wd = state.wd
    val addDirsinPath  = wd.getAllFoldersinPath
    val newRoot = updateStructure(state.root, addDirsinPath, doCreateDirEntry(state, name))
    val newSD = newRoot.findDescendant(addDirsinPath)
    new State(newRoot, newSD, "")
  }

  override def apply(state: State): State = {
    val wd = state.wd
    if (wd.hasEntry(name)) {
      state.setMessage(s"$name already exists")
    } else if (name.contains(Directory.SEPERATOR)) {
      state.setMessage(s"$name must not contain seperators")
    } else if (checkIllegal(name)) {
      state.setMessage(s"$name: illegal entry name!")
    } else {
      doCreateEntry(state, name)
    }
  }
}

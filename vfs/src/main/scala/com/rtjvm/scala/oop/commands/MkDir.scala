package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.filesystem.State
import com.rtjvm.scala.oop.files.{Directory,DirEntry}

class MkDir(name: String) extends Command {

  def checkIllegal(name: String): Boolean = name.contains(".")

  def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
    if (path.isEmpty) currentDirectory.addEntry(newEntry)
    else {
      val oldEntry = currentDirectory.findEntry(path.head).asDirectory
      currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry ))
    }
  }

  def doMkDir(state:State, name: String): State = {
    val wd = state.wd
    val addDirsinPath  = wd.getAllFoldersinPath
    val newDir = Directory.empty(wd.path, name)
    val newRoot = updateStructure(state.root, addDirsinPath, newDir)
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
      doMkDir(state, name)
    }
  }
}

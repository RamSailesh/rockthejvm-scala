package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.filesystem.State
import com.rtjvm.scala.oop.files.Directory

class Rm(name: String) extends Command {
  def doRm(state: State, absPath:String): State = {

    def rmUtil(currentDirectory: Directory, path: List[String]): Directory = {
      if (path.isEmpty) currentDirectory
      else if(path.tail.isEmpty) currentDirectory.removeEntry(path.head)
      else {
        val nextDir = currentDirectory.findEntry(path.head)
        if (!nextDir.isDirectory) currentDirectory
        else {
          val newNextDirectory = rmUtil(nextDir.asDirectory, path.tail)
          if (newNextDirectory == nextDir) currentDirectory
          else currentDirectory.replaceEntry(path.head, newNextDirectory)
        }
      }
    }

    // find the entry to remove
    val tokens = absPath.substring(1).split(Directory.SEPERATOR).toList
    val newRoot = rmUtil(state.root, tokens)

    if (newRoot == state.root) {
      state.setMessage(absPath + ": no such file or directory")
    } else {
      State(newRoot, newRoot.findDescendant(state.wd.path.substring(1)))
    }
  }

  override def apply(state: State): State = {
    // get working dir
    val wd = state.wd
    // get abspath
    val absPath = if (name.startsWith(Directory.SEPERATOR)) name
    else if (wd.isRoot) wd.path + name
    else wd.path + Directory.SEPERATOR + name
    // do some checks
    if (Directory.ROOT_PATH == absPath) {
      state.setMessage("Cannot delete root")
    } else {
      doRm(state, absPath)
    }

  }
}

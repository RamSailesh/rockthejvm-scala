package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.files.{Directory, File}
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

class Echo(args: Array[String]) extends Command {

  def createContent(args: Array[String], topIndex: Int): String = {
    @tailrec
    def createContentUtil(currIndex:Int, accumulator: String): String = {
      if (currIndex >= topIndex) accumulator
      else createContentUtil(currIndex+1, accumulator + " " + args(currIndex))
    }
    createContentUtil(0,"")
  }

  def getNewRootAfterEcho(currentDir: Directory, path: List[String],
                          contents: String, appendMode: Boolean): Directory = {
    if (path.isEmpty) currentDir
    else if (path.tail.isEmpty) {
      val dirEntry = currentDir.findEntry(path.head)
      if (dirEntry == null) currentDir.addEntry(new File(currentDir.path, path.head, contents))
      else if (dirEntry.isDirectory) dirEntry.asDirectory
      else if (appendMode) currentDir.replaceEntry(path.head, dirEntry.asFile.appendContent(contents))
      else currentDir.replaceEntry(path.head, dirEntry.asFile.setContents(contents))
    } else {
      val nextDir = currentDir.findEntry(path.head).asDirectory
      val newNextDir = getNewRootAfterEcho(nextDir, path.tail, contents, appendMode)

      if (newNextDir == nextDir) currentDir
      else currentDir.replaceEntry(path.head, newNextDir)
    }

  }

  def doEcho(state: State, contents: String, fileName: String, appendMode: Boolean): State = {
      if (fileName.contains(Directory.SEPERATOR))
        state.setMessage("FileName must not contain Seperators")
      else {
        val newRoot: Directory = getNewRootAfterEcho(state.root, state.wd.getAllFoldersinPath() :+ fileName, contents, appendMode)
        if (newRoot == state.root) state.setMessage(fileName + " No such file")
        else State(newRoot,  newRoot.findDescendant(state.wd.getAllFoldersinPath()))
      }
  }

  override def apply(state: State): State = {
   if (args.isEmpty) state
   else if (args.tail.isEmpty) state.setMessage(args.head)
   else {
     val operator = args(args.length - 2)
     val filename = args(args.length - 1)
     val contents = createContent(args, args.length - 2)

     if (">>".equals(operator)) doEcho(state, contents, filename, true)
     else if (">".equals(operator)) doEcho(state, contents, filename, false)
     else state.setMessage(contents)
   }
  }
}

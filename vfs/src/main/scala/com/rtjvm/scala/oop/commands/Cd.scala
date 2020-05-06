package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

class Cd (path: String) extends Command {

  def doCd(state: State, newPath:String ):State = {
    // find the root
    val root = state.root
    val wd = state.wd
    // find the absolutepath
    val absolutePath = if (path.startsWith(Directory.SEPERATOR)) path
    else if (wd.isRoot) wd.path + path
    else wd.path + Directory.SEPERATOR + path

    // find the dir to cd to
    val destDir = findEntry(root, absolutePath)

    // change the state
    if (destDir == null || !destDir.isDirectory) {
      state.setMessage(path + " No such Directory")
    } else {
      State(root, destDir.asDirectory)
    }
  }

  def findEntry(root: Directory, path: String): DirEntry = {
    @tailrec
    def collapseRelativeTokens(tokens: List[String], accumulator: List[String]): List[String] = {
      if(tokens.isEmpty) accumulator
      else if(".".equals(tokens.head)) collapseRelativeTokens(tokens.tail, accumulator)
      else if ("..".equals(tokens.head)) {
        if (accumulator.isEmpty) null
        else collapseRelativeTokens(tokens.tail, accumulator.init)
      } else  collapseRelativeTokens(tokens.tail, accumulator :+ tokens.head)
    }

    @tailrec
    def findEntryUtil(dir: Directory, tokens: List[String]): DirEntry = {
      if (tokens.isEmpty || tokens.head.isEmpty) dir
      else if (tokens.tail.isEmpty) dir.findEntry(tokens.head)
      else {
        val nextDir = dir.findEntry(tokens.head)
        if (nextDir != null && nextDir.isDirectory) {
          findEntryUtil(nextDir.asDirectory, tokens.tail)
        } else null
      }
    }

    val tokens: List[String] = path.substring(1).split(Directory.SEPERATOR).toList
    val newTokens = collapseRelativeTokens(tokens, List())
    if (newTokens == null) null
    else findEntryUtil(root, newTokens)
  }

  override def apply(state: State): State = {
    doCd(state, path)
  }
}


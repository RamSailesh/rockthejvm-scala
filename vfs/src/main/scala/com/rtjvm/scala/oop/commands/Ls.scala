package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

class Ls extends Command {
  def prettyMessage(workingDir: Directory): String = {
    @tailrec
    def prettyMessageUtil(contents: List[DirEntry], accumulator: List[String]): List[String] = {
      if (contents.isEmpty) accumulator
      else prettyMessageUtil(contents.tail, accumulator :+ contents.head.prettyString)
    }
    prettyMessageUtil(workingDir.contents, List()).mkString("\n")
  }

  override def apply(state: State): State = {
    state.setMessage(prettyMessage(state.wd))
  }
}

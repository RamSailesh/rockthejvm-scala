package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.filesystem.State

class Cat(name: String) extends Command {
  override def apply(state: State): State = {
    val wd = state.wd
    val dirEntry = wd.findEntry(name)
    if (dirEntry == null || !dirEntry.isFile)
      state.setMessage("No such file")
    else
      state.setMessage(dirEntry.asFile.contents)
  }
}

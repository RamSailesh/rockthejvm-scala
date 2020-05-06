package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.filesystem.State
import com.rtjvm.scala.oop.files.{Directory,DirEntry}

class MkDir(name: String) extends CreateEntry(name) {
  override def doCreateDirEntry(state: State, name: String):DirEntry = Directory.empty(state.wd.path, name)
}

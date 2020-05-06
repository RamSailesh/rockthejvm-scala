package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

trait Command {
  def apply (state: State): State
}

object Command {

  val MKDIR = "mkdir"
  val LS = "ls"
  val PWD = "pwd"
  val TOUCH = "touch"

  def emptyCommand: Command = new Command {
    override def apply(state: State): State = state
  }
  def incompleteCommand(name: String) : Command = new Command {
    override def apply(state: State): State = state.setMessage(s"$name is incomplete" )
  }

  def from(input:String):Command = {
    val tokens = input.split(" ")
    if (input.isEmpty || tokens.isEmpty) emptyCommand
    else if (MKDIR.equals(tokens(0))) {
      if (tokens.length < 2) incompleteCommand(MKDIR)
      else new MkDir(tokens(1))
    }
    else if (TOUCH.equals(tokens(0))) {
      if (tokens.length < 2) incompleteCommand(MKDIR)
      else new Touch(tokens(1))
    }
    else if (LS.equals(tokens(0))) {
      if (tokens.length != 1) incompleteCommand(LS)
      else new Ls()
    }
    else if (PWD.equals(tokens(0))) {
      if (tokens.length != 1) incompleteCommand(PWD)
      else new Pwd()
    }
    else new UnknownCommand()
  }

}

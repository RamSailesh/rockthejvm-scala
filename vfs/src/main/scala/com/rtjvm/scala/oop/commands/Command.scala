package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

trait Command extends (State => State){
}

object Command {

  val MKDIR = "mkdir"
  val LS = "ls"
  val PWD = "pwd"
  val TOUCH = "touch"
  val CD = "cd"
  val RM = "rm"
  val ECHO = "echo"
  val CAT = "cat"

  def emptyCommand: Command = new Command {
    override def apply(state: State): State = state
  }
  def incompleteCommand(name: String) : Command = new Command {
    override def apply(state: State): State = state.setMessage(s"$name is incomplete" )
  }

  def from(input:String):Command = {
    val tokens = input.split(" ")
    if (input.isEmpty || tokens.isEmpty) emptyCommand
    else {
      tokens(0) match {
        case MKDIR => if (tokens.length < 2) incompleteCommand(MKDIR)
        else new MkDir(tokens(1))
        case LS => if (tokens.length != 1) incompleteCommand(LS)
        else new Ls()
        case PWD => if (tokens.length != 1) incompleteCommand(PWD)
        else new Pwd()
        case TOUCH => if (tokens.length < 2) incompleteCommand(MKDIR)
        else new Touch(tokens(1))
        case CD => if (tokens.length < 2) incompleteCommand(MKDIR)
        else new Cd(tokens(1))
        case RM => if (tokens.length < 2) incompleteCommand(RM)
        else new Rm(tokens(1))
        case PWD => if (tokens.length != 1) incompleteCommand(PWD)
        else new Pwd()
        case ECHO => new Echo(tokens.tail)
        case CAT => if (tokens.length != 2) incompleteCommand(CAT)
        else new Cat(tokens(1))
        case _ => new UnknownCommand()
      }
    }
  }
}

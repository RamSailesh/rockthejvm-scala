package com.rtjvm.scala.oop.filesystem

import java.util.Scanner

import com.rtjvm.scala.oop.commands.Command
import com.rtjvm.scala.oop.files.Directory

object FileSystem extends App {
  val scanner = new Scanner(System.in)
  val root = Directory.ROOT
  var state = State(root, root)

  while(true) {
    state.show
    val input = scanner.nextLine()
    state = Command.from(input)(state)
  }
}
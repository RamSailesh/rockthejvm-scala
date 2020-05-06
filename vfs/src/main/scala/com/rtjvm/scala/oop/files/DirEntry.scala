package com.rtjvm.scala.oop.files

abstract class DirEntry (val parentPath: String, val name: String) {
  def asDirectory: Directory = ???
  def prettyString: String = ???
}

package me.home.shell.command
import me.home.tools.afrp._

trait Output
case class Message(val msg: String) extends Output
case class Query(val msg: String, val respond: E[String]) extends Output
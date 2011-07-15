package me.home.tools.resource

import scalaz.Validation

trait Resource {
	def dimension[T](name: String): Validation[String, Dimension[T]]
}

package me.home.tools.resource

import scalaz.Validation

trait Resource {
  def model: ResourceModel
	def dimension[T](name: String): Validation[String, Dimension[T]]
}

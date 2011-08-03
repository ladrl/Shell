package me.home.tools.resource

// Defines the structure of a certain resource
// Can be used to generate such a resource (has the interface to do it)
// Delivers a Validation[String, Resuorce]
// Must be some sort of model structure built from dimensions and dimension transformations
// --> A resource should be some sort of generic abstract syntax tree (but not necessarily a tree)
// Can contain phases of analysis, i.e. file size, structure, 


import scalaz._
import Scalaz._

trait ResourceModel {
  def dimensionCount: Int
}
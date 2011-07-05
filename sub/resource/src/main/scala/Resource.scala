package me.home.tools.resource


trait Resource {
  def contains(dim: Dimension[_]): Boolean
  // Both dims must be member of this
  def dot(dim1: Dimension[_], dim2: Dimension[_]): Double = {
    require(this contains dim1)
    require(this contains dim2)
    dotProduct(dim1, dim2)
  }
  protected def dotProduct(dim1: Dimension[_] , dim2: Dimension[_]): Double
}

object Resource {

}
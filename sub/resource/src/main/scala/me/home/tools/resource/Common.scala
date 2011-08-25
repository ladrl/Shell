package me.home.tools.resource


trait Rank
trait Complexity

trait Actor // --> connect to Co and Contra

trait CanCreate[A] {
  def create: A
}

trait CanCreateFrom[M[_], A, B] {
  def from(a: A) : CanCreate[M[B]]
}

trait CanExtract[A] {
  def extract: A
}

trait CanExtractFrom[M[_], A, B] {
  def from(a: M[A]): CanExtract[B]
}
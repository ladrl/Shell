package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

class ResourceTest extends FlatSpec with MustMatchers {
  "A resource of a Byte array" must "accept dimensions processing Bytes" in {
    pending
  }
}

class DimensionTest extends FlatSpec with MustMatchers {
  "A matcher function" must "allow to encapsulate the functionality of pattern matching" in {
    val m: DimensionTemplate.Matcher[Int] = { i =>
      i match {
        case 10 :: 20 :: x => true
        case _ => false
      }
    }
    
    m(Nil) must be (false)
    m(10 :: 20 :: Nil) must be (true)
    m(10 :: 20 :: 30 :: Nil) must be (true)
  }
  
  "Two impact map functions" must "allow to calculate the orthogonality" in {
    val im1: DimensionTemplate.ImpactMap[Int] = { i => i % 3 == 0 }
    val im2: DimensionTemplate.ImpactMap[Int] = { i => i % 6 == 1 }
    
    val indices = 0 to 50
    
    val map1 = indices.map { im1(_) }
    val map2 = indices.map { im2(_) }
    
    val collision = (map1 zip map2).map { t => t._1 && t._2 }
    val orthogonal = collision.foldLeft(false) { (l, r) => l || r }
    
    orthogonal must be (false)
  }
  
  "A projection" must "create a container from an instance" in {
    val proj: DimensionTemplate.Projection[Int, Int, List] = { i => 0 to i toList }
    
    proj(10) must be (List(0,1,2,3,4,5,6,7,8,9,10))
  }
  it must "create a container from a container" in {
    val proj: DimensionTemplate.Projection[List[Double], Int, List] = { ds => ds.map { _.toInt } }
    
    proj((0 until 10).toList map { _ + 0.2}) must be (0 until 10 toList)
  }
  it must "create a container of containers from a container" in {
    
    val proj: DimensionTemplate.Projection[List[Int], List[Int], List] = { i => i.map { 0 to _ toList } }
    
    proj(0 until 3 toList) must be (List(List(0), List(0,1), List(0,1,2)))
  }
  
  "A reverse projection" must "create an instance form a container" in {
    val rev_proj: DimensionTemplate.ReverseProjection[Int, Int, List] = { i => i.foldLeft(0) { _ + _ }}
    
    rev_proj(0 until 100 toList) must be (100 * 99 / 2)
  }
  
  it must "create a container from a container" in {
    val rev_proj: DimensionTemplate.ReverseProjection[List[Double], Int, List] = { is => is.map { _ + 0.2 } }
    rev_proj(0 until 100 toList) must be ((0 until 100).toList map { _ + 0.2 })
  }
  
  it must "create a container from a container of containers" in {
    val rev_proj: DimensionTemplate.ReverseProjection[List[Int], List[Int], List] =
    	{ i => i.map { _.foldLeft(0){_+_} } }
    
    rev_proj((0 until 100).toList.map { 0 until _ toList }) must be (
        (0 until 100).toList.map { i => i * (i-1) / 2 })
  }
}
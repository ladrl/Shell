package me.home.tools

package object testUtil {

import org.scalatest.matchers.BePropertyMatcher
import org.scalatest.matchers.BePropertyMatchResult

  def anInstanceOf[T](implicit manifest: Manifest[T]) = {
    val clazz = manifest.erasure.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) = BePropertyMatchResult(clazz.isAssignableFrom(left.getClass), "an instance of " + clazz.getName)
    }
  }
}

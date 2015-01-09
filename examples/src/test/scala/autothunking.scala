package de.bstudios
package laziness

import org.scalatest._

class AutothunkingTest extends FlatSpec {

  var count = 0;

  val x: Lazy[Int] = 42

  val f: Lazy[Int] = () => { count += 1; 42 }

  assert(f.force + f.force == 84)
  assert(count == 1)


  count = 0;
  implicit val impl_f: Lazy[Int] = () => { count += 1; 42 }
  def foo(implicit ev: Lazy[Int]): Lazy[Int] = ev

  foo;

  assert(foo.force == 42)
  assert(foo.force == 42)
  assert(count == 1)
}

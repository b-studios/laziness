package de.bstudios
package object laziness {

  import scala.language.experimental.macros
  import scala.reflect.macros.Context
  import scala.language.implicitConversions

  trait Lazy[A] {
    def thunk(): A
    private var cache: Option[A] = None
    def force: A = cache getOrElse { cache = Some(thunk()); cache.get }
  }

  object Lazy {

    def apply[A](a: => A): Lazy[A] = new Lazy[A] {
      def thunk() = a
    }

    def impl[A: c.WeakTypeTag](c: Context)(a: c.Expr[A]): c.Expr[Lazy[A]] = {
      import c.universe._
      c.Expr[Lazy[A]](q"""Lazy($a)""")
    }

    // this is an implicit macro in order to avoid strict evaluation of `a`
    implicit def thunkIt[A](a: A): Lazy[A] = macro impl[A]

    // to allow auto thunking on implicit lookup, transitively
    implicit def thunkItTrans[A](implicit a: A): Lazy[A] = macro impl[A]
  }


  // Every zero argument function can be seen as a thunk.
  implicit def funToThunk[A](f: () => A): Lazy[A] = Lazy(f())
}

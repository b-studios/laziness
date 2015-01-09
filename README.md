Laziness
========
The combination of by-name parameters and implicit arguments is not allowed (cf. [Scala Language Specification, Section 4.6.1](http://www.scala-lang.org/docu/files/ScalaReference.pdf)). That is, the following Scala code is invalid:

~~~scala
trait X
def foo(implicit ev: => X) { ... ev ... }
~~~

However, sometimes this is necessary in rare cases. For these cases, the `laziness` library can be used. Just replace the by-name parameter by `Lazy[X]` and explicitly force the evaluation like:

~~~scala
import de.bstudios.laziness
def foo2(implicit ev: Lazy[X]) { ... ev.force ... }
~~~

That's it. Expressions that compute `X` in positions where a `Lazy[X]` is required will automatically be thunked to avoid strict evaluation.


How does it work?
-----------------
It is no magic, just a simple implicit macro.

Let us assume a term of type `T`.
~~~
<Term>: T
~~~

In a context where a lazy term of type `Lazy[T]` is expected the following code transformation automatically takes place:

~~~
<Term> --transform--> new Lazy[T] { def thunk = <Term> }
~~~

Forcing the term will evaluate the thunk and cache the result, such that repeated forcing will only return the cached result.

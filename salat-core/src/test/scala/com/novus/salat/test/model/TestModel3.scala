/*
 * Copyright (c) 2010 - 2012 Novus Partners, Inc. (http://www.novus.com)
 *
 * Module:        salat-core
 * Class:         TestModel3.scala
 * Last modified: 2012-10-15 20:40:59 EDT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *           Project:  http://github.com/novus/salat
 *              Wiki:  http://github.com/novus/salat/wiki
 *      Mailing list:  http://groups.google.com/group/scala-salat
 *     StackOverflow:  http://stackoverflow.com/questions/tagged/salat
 */

package com.novus.salat.test.model

import com.novus.salat.annotations._
import com.novus.salat.Context

package object suppress_default_args {
  implicit val ctx = new Context {
    val name = "suppres-default args"
    override val suppressDefaultArgs = true
  }
}

package sda {

  object Foo {
    val empty = Foo(0)
  }

  case class Foo(x: Int = 0)

  object Bar {
    val S = "xyz"
  }

  case class Bar(foo: Foo = Foo.empty, s: String = Bar.S, o: Option[String] = None, baz: Baz = Qux.empty)

  @Salat
  trait Baz {
    def thingy: String
  }

  object Qux {
    val Thingy = "thingy"
    val empty = Qux(Thingy)
  }

  case class Qux(thingy: String = Qux.Thingy) extends Baz

}

package object case_object_override {
  import com.novus.salat.test.model.coo._

  implicit val ctx = {
    val ctx = new Context {
      val name = "case_object_override"
    }
    ctx.registerCaseObjectOverride[Foo, Bar.type]("B")
    ctx.registerCaseObjectOverride[Foo, Baz.type]("Z")
    ctx
  }
}

package coo {

  @Salat
  trait Foo
  case object Bar extends Foo
  case object Baz extends Foo
  case object Qux extends Foo
  case class Thingy(foo: Foo)
  case class Thingy2(foo: Option[Foo])
  case class Thingy3(foo: List[Foo])
}
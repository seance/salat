/*
 * Copyright (c) 2010 - 2012 Novus Partners, Inc. (http://www.novus.com)
 *
 * Module:        salat-core
 * Class:         Extractors.scala
 * Last modified: 2012-10-15 20:40:58 EDT
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
package com.novus.salat.transformers

import scala.tools.scalap.scalax.rules.scalasig._

import com.novus.salat._
import com.mongodb.casbah.Imports._
import com.novus.salat.util.Logging
import com.novus.salat.annotations.util._

package object out {
  def select(t: TypeRefType, hint: Boolean = false)(implicit ctx: Context): Transformer = {
    t match {
      case IsOption(t @ TypeRefType(_, _, _)) => t match {
        case TypeRefType(_, symbol, _) if ctx.caseObjectHierarchy.contains(symbol.path) => {
          new Transformer(t.symbol.path, t)(ctx) with OptionExtractor with CaseObjectExtractor
        }

        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with OptionExtractor with BigDecimalExtractor

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with OptionExtractor with BigIntExtractor

        case TypeRefType(_, symbol, _) if isChar(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with OptionExtractor with CharToString

        case TypeRefType(_, symbol, _) if isFloat(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with OptionExtractor with FloatToDouble

        case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
          new Transformer(prefix.symbol.path, t)(ctx) with OptionExtractor with EnumStringifier
        }

        case TypeRefType(_, symbol, _) if hint || ctx.lookup_?(symbol.path).isDefined =>
          new Transformer(symbol.path, t)(ctx) with OptionExtractor with InContextToDBObject {
            val grater = ctx.lookup_?(symbol.path)
          }

        case TypeRefType(_, symbol, _) => new Transformer(symbol.path, t)(ctx) with OptionExtractor
      }

      case IsTraversable(t @ TypeRefType(_, _, _)) => t match {
        case TypeRefType(_, symbol, _) if ctx.caseObjectHierarchy.contains(symbol.path) => {
          new Transformer(t.symbol.path, t)(ctx) with CaseObjectExtractor with TraversableExtractor
        }

        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with BigDecimalExtractor with TraversableExtractor

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with BigIntExtractor with TraversableExtractor

        case TypeRefType(_, symbol, _) if isFloat(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with FloatToDouble with TraversableExtractor

        case TypeRefType(_, symbol, _) if isChar(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with CharToString with TraversableExtractor

        case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
          new Transformer(prefix.symbol.path, t)(ctx) with EnumStringifier with TraversableExtractor
        }

        case TypeRefType(_, symbol, _) if hint || ctx.lookup_?(symbol.path).isDefined =>
          new Transformer(symbol.path, t)(ctx) with InContextToDBObject with TraversableExtractor {
            val grater = ctx.lookup_?(symbol.path)
          }

        case TypeRefType(_, symbol, _) =>
          new Transformer(symbol.path, t)(ctx) with TraversableExtractor
      }

      case IsMap(_, t @ TypeRefType(_, _, _)) => t match {
        case TypeRefType(_, symbol, _) if ctx.caseObjectHierarchy.contains(symbol.path) => {
          new Transformer(t.symbol.path, t)(ctx) with CaseObjectExtractor with MapExtractor
        }

        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with BigDecimalExtractor with MapExtractor

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with BigIntExtractor with MapExtractor

        case TypeRefType(_, symbol, _) if isChar(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with CharToString with MapExtractor

        case TypeRefType(_, symbol, _) if isFloat(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with FloatToDouble with MapExtractor

        case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" =>
          new Transformer(prefix.symbol.path, t)(ctx) with EnumStringifier with MapExtractor

        case TypeRefType(_, symbol, _) if hint || ctx.lookup_?(symbol.path).isDefined =>
          new Transformer(symbol.path, t)(ctx) with InContextToDBObject with MapExtractor {
            val grater = ctx.lookup_?(symbol.path)
          }

        case TypeRefType(_, symbol, _) => new Transformer(symbol.path, t)(ctx) with MapExtractor
      }
      case pt if ctx.caseObjectHierarchy.contains(pt.symbol.path) => {
        new Transformer(pt.symbol.path, pt)(ctx) with CaseObjectExtractor
      }
      case TypeRefType(_, symbol, _) => t match {
        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with BigDecimalExtractor

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with BigIntExtractor

        case TypeRefType(_, symbol, _) if isChar(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with CharToString

        case TypeRefType(_, symbol, _) if isFloat(symbol.path) =>
          new Transformer(symbol.path, t)(ctx) with FloatToDouble

        case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
          new Transformer(prefix.symbol.path, t)(ctx) with EnumStringifier
        }

        case TypeRefType(_, symbol, _) if hint || ctx.lookup_?(symbol.path).isDefined =>
          new Transformer(symbol.path, t)(ctx) with InContextToDBObject {
            val grater = ctx.lookup_?(symbol.path)
          }
        case TypeRefType(_, symbol, _) => new Transformer(symbol.path, t)(ctx) {}
      }
    }
  }
}

package out {

  trait CaseObjectExtractor extends Transformer with Logging {
    self: Transformer =>

    override def transform(value: Any)(implicit ctx: Context): Any = {
      val name = value.asInstanceOf[AnyRef].getClass.getName
      ctx.caseObjectOverrides.get(name).getOrElse {
        MongoDBObject(ctx.typeHintStrategy.typeHint -> ctx.typeHintStrategy.encode(name))
      }
    }
  }

  trait BigDecimalExtractor extends Transformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context): Any = ctx.bigDecimalStrategy.out(value)
  }

  trait BigIntExtractor extends Transformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context): Any = value match {
      case bi: BigInt               => ctx.bigIntStrategy.out(bi)
      case bi: java.math.BigInteger => ctx.bigIntStrategy.out(bi)
      case l: Long                  => ctx.bigIntStrategy.out(l)
      case i: Int                   => ctx.bigIntStrategy.out(i)
    }
  }

  trait FloatToDouble extends Transformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context) = value match {
      case f: Float           => f.toDouble
      case f: java.lang.Float => f.doubleValue()
    }
  }

  trait CharToString extends Transformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context) = value match {
      case c: Char                => c.toString
      case c: java.lang.Character => c.toString
    }
  }

  trait InContextToDBObject extends Transformer with InContextTransformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context): Any = value match {
      case cc: CaseClass => ctx.lookup(path, cc).asInstanceOf[Grater[CaseClass]].asDBObject(cc)
      case _             => MongoDBObject("failed-to-convert" -> value.toString)
    }
  }

  trait OptionExtractor extends Transformer {
    self: Transformer =>

    // ok, Some(null) should never happen.  except sometimes it does.
    override def before(value: Any)(implicit ctx: Context): Option[Any] = value match {
      case Some(value) if value != null => Some(super.transform(value))
      case _                            => None
    }
  }

  trait TraversableExtractor extends Transformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context): Any = value

    override def after(value: Any)(implicit ctx: Context): Option[Any] = value match {
      case traversable: Traversable[_] =>
        Some(MongoDBList(traversable.map {
          case el => super.transform(el)
        }.toList: _*))
      case _ => None
    }
  }

  trait MapExtractor extends Transformer {
    self: Transformer =>
    override def transform(value: Any)(implicit ctx: Context): Any = value

    override def after(value: Any)(implicit ctx: Context): Option[Any] = value match {
      case map: scala.collection.Map[_, _] => {
        val builder = MongoDBObject.newBuilder
        map.foreach {
          case (k, el) =>
            builder += (k match {
              case s: String => s
              case x         => x.toString
            }) -> super.transform(el)
        }
        Some(builder.result)
      }
      case _ => None
    }
  }

  trait EnumStringifier extends Transformer {
    self: Transformer =>

    val strategy = {
      val s = getClassNamed_!(path).annotation[com.novus.salat.annotations.raw.EnumAs].
        map(_.strategy())
      if (s.isDefined) s.get else ctx.defaultEnumStrategy
    }

    override def transform(value: Any)(implicit ctx: Context): Any = value match {
      case ev: Enumeration#Value if strategy == EnumStrategy.BY_VALUE => ev.toString
      case ev: Enumeration#Value if strategy == EnumStrategy.BY_ID    => ev.id
    }
  }

}


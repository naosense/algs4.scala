package collection

import java.util.NoSuchElementException

class Stack[E] extends Iterable[E] {
  sealed trait Node
  case class Elm(e: E, next: Node) extends Node
  case object End extends Node

  private var first: Node = End
  private var n = 0

  def push(e: E): Unit = {
    val oldfirst = first
    first = Elm(e, oldfirst)
    n = n + 1
  }

  def pop(): E = {
    if (isEmpty) throw new NoSuchElementException
    val item = (first: @unchecked) match {
      case Elm(e, next) => first = next; e
    }
    n = n - 1
    item
  }

  override def isEmpty: Boolean = n == 0

  override def knownSize: Int = n

  override def className: String = "Stack"

  override def iterator: Iterator[E] = new LinkedIterator(first)

  private class LinkedIterator(var current: Node) extends Iterator[E] {
    override def hasNext: Boolean = current ne End

    override def next(): E = {
      if (!hasNext) throw new NoSuchElementException
      val item = (current: @unchecked) match {
        case Elm(e, next) => current = next; e
      }
      item
    }
  }
}

object Stack {
  def apply[E](elements: E*): Stack[E] = {
    val s = new Stack[E]
    elements.foreach(s.push)
    s
  }

  def main(args: Array[String]): Unit = {
    val s = Stack(1, 2, 3)
    s.foreach(println)
  }
}

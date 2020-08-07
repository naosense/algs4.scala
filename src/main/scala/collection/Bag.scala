package collection

import java.util.NoSuchElementException

class Bag[E] extends Iterable[E] {
  sealed trait Node
  case class Elm(e: E, next: Node) extends Node
  case object End extends Node

  private var first: Node = End
  private var n = 0

  def add(e: E): Unit = {
    val oldfirst = first
    first = Elm(e, oldfirst)
    n = n + 1
  }

  override def isEmpty: Boolean = n == 0

  override def knownSize: Int = n

  override def className: String = "Bag"

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

object Bag {
  def apply[E](elements: E*): Bag[E] = {
    val bag = new Bag[E]()
    elements.foreach(bag.add)
    bag
  }

  def main(args: Array[String]): Unit = {
    val bag = Bag(1, 2, 3)
    bag.foreach(println)
  }
}

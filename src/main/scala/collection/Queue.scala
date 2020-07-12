package collection

import java.util.NoSuchElementException

import util.StdIn

class Queue[E] extends Iterable[E] {
  sealed trait Node
  case class Elm(e: E, var next: Node) extends Node
  case object End extends Node

  // 避免与父类中的变量重名
  private var fst: Node = End
  private var lst: Node = End
  private var n = 0

  def enqueue(e: E): Unit = {
    val oldlast = lst
    lst = Elm(e, End)
    if (isEmpty) fst = lst
    else oldlast.asInstanceOf[Elm].next = lst
    n = n + 1
  }

  def dequeue(): E = {
    val item = (fst: @unchecked) match {
      case Elm(e, _) => e
    }
    fst = fst.asInstanceOf[Elm].next
    if (isEmpty) lst = End
    n = n - 1
    item
  }

  override def isEmpty: Boolean = n == 0

  override def knownSize: Int = n

  override def className: String = "Queue"

  override def iterator: Iterator[E] = new LinkedIterator(fst)

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

object Queue {
  def apply[E](elements: E*): Queue[E] = {
    val q = new Queue[E]
    elements.foreach(q.enqueue)
    q
  }

  def main(args: Array[String]): Unit = {
    val q = Queue[String]()
    while (!StdIn.isEmpty) {
      val item = StdIn.readString()
      if (item != "-") q.enqueue(item)
      else if (!q.isEmpty) print(q.dequeue() + " ")
    }
    println("(" + q.size + " left on queue")
  }
}

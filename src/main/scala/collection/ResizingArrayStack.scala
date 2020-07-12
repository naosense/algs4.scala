package collection

import scala.reflect.ClassTag

class ResizingArrayStack[E >: Null : ClassTag] extends Iterable[E] {
  private var a = new Array[E](2)
  private var n = 0

  override def isEmpty: Boolean = n == 0

  override def knownSize: Int = n

  override def className: String = "ResizingArrayStack"

  private def resize(capacity: Int): Unit = {
    assert(capacity >= n)
    a = Array.copyOf(a, capacity)
  }

  def push(e: E): Unit = {
    if (n == a.length) resize(2 * a.length)
    a(n) = e
    n = n + 1
  }

  def pop(): E = {
    if (isEmpty) throw new NoSuchElementException("Stack underflow")
    val item = a(n - 1)
    a(n - 1) = null
    n = n - 1
    if (n > 0 && n == a.length / 4) resize(a.length / 2)
    item
  }

  def peek(): E = {
    if (isEmpty) throw new NoSuchElementException("Stack underflow")
    a(n - 1)
  }

  override def iterator: Iterator[E] = new ReverseArrayIterator(n - 1)

  private class ReverseArrayIterator(var i: Int) extends Iterator[E] {

    override def hasNext: Boolean = i >= 0

    override def next(): E = {
      if (!hasNext) throw new NoSuchElementException
      val item = a(i)
      i = i - 1
      item
    }
  }
}

object ResizingArrayStack {
  def apply[E>:Null :ClassTag](elements: E*): ResizingArrayStack[E] = {
    val s = new ResizingArrayStack[E]
    elements.foreach(s.push)
    s
  }

  def main(args: Array[String]): Unit = {
    val s = ResizingArrayStack(1,2,3,4,5,6)
    while (!s.isEmpty) {
      print(s.pop() + " ")
    }
  }
}

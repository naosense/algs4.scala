package collection

import scala.reflect.ClassTag

class ResizingArrayBag[E >: Null : ClassTag] extends Iterable[E] {
  private var a = new Array[E](2)
  private var n = 0

  override def isEmpty: Boolean = n == 0

  override def knownSize: Int = n

  override def className: String = "ResizingArrayBag"

  private def resize(capacity: Int): Unit = {
    assert(capacity >= n)
    a = Array.copyOf(a, capacity)
  }

  def add(e: E): Unit = {
    if (n == a.length) resize(2 * a.length)
    a(n) = e
    n = n + 1
  }

  override def iterator: Iterator[E] = new ArrayIterator(n)

  private class ArrayIterator(n: Int) extends Iterator[E] {
    private var i = 0

    override def hasNext: Boolean = i < n

    override def next(): E = {
      if (!hasNext) throw new NoSuchElementException
      val item = a(i)
      i = i + 1
      item
    }
  }
}

object ResizingArrayBag {
  def apply[E >: Null : ClassTag](elements: E*): ResizingArrayBag[E] = {
    val bag = new ResizingArrayBag[E]()
    elements.foreach(bag.add)
    bag
  }

  def main(args: Array[String]): Unit = {
    val bag = ResizingArrayBag(1, 2, 3, 4, 5)
    println(bag)
  }
}

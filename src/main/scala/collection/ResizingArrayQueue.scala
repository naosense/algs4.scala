package collection

import scala.reflect.ClassTag

class ResizingArrayQueue[E >: Null : ClassTag] extends Iterable[E] {
  private var q = new Array[E](2)
  private var n = 0
  private var fst = 0
  private var lst = 0

  override def isEmpty: Boolean = n == 0

  override def knownSize: Int = n

  override def className: String = "ResizingArrayQueue"

  private def resize(capacity: Int): Unit = {
    assert(capacity >= n)
    val copy = new Array[E](capacity)
    for (i <- 0 until n) copy(i) = q((fst + i) % q.length)
    q = copy
    fst = 0
    lst = n
  }

  def enqueue(e: E): Unit = {
    if (n == q.length) resize(2 * q.length)
    q(lst) = e
    lst = lst + 1
    if (lst == q.length) lst = 0 // ?
    n = n + 1
  }

  def dequeue(): E = {
    if (isEmpty) throw new NoSuchElementException("Queue underflow")
    val item = q(fst)
    q(fst) = null
    n = n - 1
    fst = fst + 1
    if (fst == q.length) fst = 0
    if (n > 0 && n == q.length / 4) resize(q.length / 2)
    item
  }

  def peek(): E = {
    if (isEmpty) throw new NoSuchElementException("Queue underflow")
    q(fst)
  }

  override def iterator: Iterator[E] = new ArrayIterator(n)

  private class ArrayIterator(n: Int) extends Iterator[E] {
    private var i = 0

    override def hasNext: Boolean = i < n

    override def next(): E = {
      if (!hasNext) throw new NoSuchElementException
      val item = q((i + fst) % q.length)
      i = i + 1
      item
    }
  }
}

object ResizingArrayQueue {
  def apply[E >: Null : ClassTag](elements: E*): ResizingArrayQueue[E] = {
    val q = new ResizingArrayQueue[E]()
    elements.foreach(q.enqueue)
    q
  }

  def main(args: Array[String]): Unit = {
    val q = ResizingArrayQueue(1, 2, 3, 4, 5)
    println(q)
    while (!q.isEmpty) {
      print(q.dequeue() + " ")
    }
  }
}

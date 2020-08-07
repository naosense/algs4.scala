package collection

import util.StdRandom

import scala.reflect.ClassTag

class IndexMaxPQ[K >: Null : ClassTag : Ordering](maxN: Int) extends Iterable[Int] {
  if (maxN < 0) throw new IllegalArgumentException
  private var n = 0
  private val pq: Array[Int] = new Array[Int](maxN + 1)
  private val qp: Array[Int] = new Array[Int](maxN + 1)
  private val keys: Array[K] = new Array[K](maxN + 1)
  for (i <- 0 to maxN) {
    qp(i) = -1
  }

  override def isEmpty: Boolean = n == 0

  def contains(i: Int): Boolean = {
    validateIndex(i)
    qp(i) != -1
  }

  override def size: Int = n

  def insert(i: Int, key: K): Unit = {
    validateIndex(i)
    if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue")
    n += 1
    qp(i) = n
    pq(n) = i
    keys(i) = key
    swim(n)
  }

  def maxIndex(): Int = {
    if (n == 0) throw new NoSuchElementException("Priority queue underflow")
    pq(1)
  }

  def maxKey(): K = {
    if (n == 0) throw new NoSuchElementException("Priority queue underflow")
    keys(pq(1))
  }

  def delMax(): Int = {
    if (n == 0) throw new NoSuchElementException("Priority queue underflow")
    val max = pq(1)
    exch(1, n)
    n -= 1
    sink(1)

    assert(pq(n + 1) == max)
    qp(max) = -1 // delete
    keys(max) = null
    pq(n + 1) = -1 // not needed 什么意思
    max
  }

  def keyOf(i: Int): K = {
    validateIndex(i)
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue")
    else keys(i)
  }

  def changeKey(i: Int, key: K): Unit = {
    validateIndex(i)
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue")
    keys(i) = key
    swim(qp(i))
    sink(qp(i))
  }

  def increaseKey(i: Int, key: K): Unit = {
    validateIndex(i)
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue")
    implicitly(Ordering[K]).compare(keys(i), key) match {
      case x if x == 0 =>
        throw new IllegalArgumentException("Calling increaseKey() with a key equal to the key in the priority queue")
      case x if x > 0 =>
        throw new IllegalArgumentException("Calling increaseKey() with a key that is strictly less than the key in the priority queue")
      case x if x < 0 =>
        keys(i) = key
        swim(qp(i))
    }
  }

  def decreaseKey(i: Int, key: K): Unit = {
    validateIndex(i)
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue")
    implicitly(Ordering[K]).compare(keys(i), key) match {
      case x if x == 0 =>
        throw new IllegalArgumentException("Calling decreaseKey() with a key equal to the key in the priority queue")
      case x if x < 0 =>
        throw new IllegalArgumentException("Calling decreaseKey() with a key that is strictly greater than the key in the priority queue")
      case x if x > 0 =>
        keys(i) = key
        sink(qp(i))
    }
  }

  def delete(i: Int): Unit = {
    validateIndex(i)
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue")
    val index = qp(i)
    exch(index, n)
    n -= 1
    swim(index)
    sink(index)
    keys(i) = null
    qp(i) = -1
  }

  private def validateIndex(i: Int): Unit = {
    if (i < 0) throw new IllegalArgumentException(s"index is negative: $i")
    if (i >= maxN) throw new IllegalArgumentException(s"index >= capacity: $i")
  }

  private def less(i: Int, j: Int): Boolean = implicitly(Ordering[K]).lt(keys(pq(i)), keys(pq(j)))

  private def exch(i: Int, j: Int): Unit = {
    val swap = pq(i)
    pq(i) = pq(j)
    pq(j) = swap
    qp(pq(i)) = i // 这是在干嘛
    qp(pq(j)) = j
  }

  private def swim(k: Int): Unit = {
    var kk = k
    while (kk > 1 && less(kk / 2, kk)) {
      exch(kk, kk / 2)
      kk /= 2
    }
  }

  private def sink(k: Int): Unit = {
    var goon = true
    var kk = k
    while (2 * kk <= n && goon) {
      var j = 2 * kk
      if (j < n && less(j, j + 1)) j += 1
      goon = less(kk, j)
      if (goon) {
        exch(kk, j)
        kk = j
      }
    }
  }

  override protected[this] def className: String = "IndexMaxPQ"

  override def iterator: Iterator[Int] = new HeapIterator

  private class HeapIterator extends Iterator[Int] {
    private val copy: IndexMaxPQ[K] = new IndexMaxPQ[K](pq.length - 1)
    for (i <- 1 to n) {
      copy.insert(pq(i), keys(pq(i)))
    }

    override def hasNext: Boolean = !copy.isEmpty

    override def next(): Int = {
      if (!hasNext) throw new NoSuchElementException
      copy.delMax()
    }
  }
}

object IndexMaxPQ {
  def apply[K >: Null : ClassTag : Ordering](elements: K*): IndexMaxPQ[K] = {
    val pq = new IndexMaxPQ[K](elements.length)
    elements.zipWithIndex.foreach { case (e, i) => pq.insert(i, e) }
    pq
  }

  def main(args: Array[String]): Unit = {
    val strings = Array("it", "was", "the", "best", "of", "times", "it", "was", "the", "worst")
    val pq = IndexMaxPQ(strings: _*)
    pq.foreach(i => println(i + " " + strings(i)))
    println()

    for (i <- strings.indices) {
      if (StdRandom.uniform() < 0.5) {
        pq.increaseKey(i, strings(i) + strings(i))
      } else {
        pq.decreaseKey(i, strings(i).substring(0, 1))
      }
    }

    while (!pq.isEmpty) {
      val key = pq.maxKey()
      val i = pq.delMax()
      println(i + " " + key)
    }
    println()

    for (i <- strings.indices) {
      pq.insert(i, strings(i))
    }

    val perm = new Array[Int](strings.length)
    strings.indices.foreach(i => perm(i) = i)
    StdRandom.shuffle(perm)
    for (i <- perm.indices) {
      val key = pq.keyOf(perm(i))
      pq.delete(perm(i))
      println(perm(i) + " " + key)
    }
  }
}

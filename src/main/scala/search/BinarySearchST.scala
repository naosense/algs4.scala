package search

import scala.reflect.ClassTag

class BinarySearchST[K: Ordering : ClassTag, V: ClassTag](capacity: Int) extends ST[K, V] {
  private var n = 0
  private var ks = new Array[K](capacity)
  private var vs = new Array[V](capacity)

  override def size(): Int = n

  override def get(key: K): Option[V] = {
    if (isEmpty) return None
    val i = rank(key)
    if (i < n && implicitly[Ordering[K]].equiv(ks(i), key)) Some(vs(i))
    else None
  }

  private def rank(key: K): Int = {
    0
  }

  override def put(key: K, value: V): Unit = {
    val i = rank(key)
    // key已存在
    if (i < n && implicitly[Ordering[K]].equiv(ks(i), key)) {
      vs(i) = value
      return
    }

    if (n == ks.length) resize(2 * ks.length)

    // 将大于i的位置往后移动一位，空出一个位置插入新值
    for (i <- n until i by -1) {
      ks(i) = ks(i - 1)
    }
  }

  private def resize(capacity: Int): Unit = {
    assert(capacity >= n)
    ks = Array.copyOf(ks, capacity)
    vs = Array.copyOf(vs, capacity)
  }

  override def delete(key: K): Unit = ???

  override def contains(key: K): Boolean = ???

  override def keys(): Iterable[K] = ???
}

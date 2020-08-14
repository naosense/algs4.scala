package search

import collection.Queue

import scala.reflect.ClassTag

class BinarySearchST[K: Ordering : ClassTag, V: ClassTag](capacity: Int) extends ST[K, V] {
  private var n = 0
  private var ks = new Array[K](capacity)
  private var vs = new Array[V](capacity)

  def this() = {
    this(2)
  }

  override def size(): Int = n

  override def get(key: K): Option[V] = {
    if (isEmpty) return None
    val i = rank(key)
    if (i < n && implicitly[Ordering[K]].equiv(ks(i), key)) Some(vs(i))
    else None
  }

  /**
   * 返回严格小于`key`的键值个数。
   * 它是怎么保证返回值是严格小于`key`的个数的？分为两种情况：
   *
   * 1. `key`已经存在，返回的是`key`值的下标`i`，而`key`是第`i+1`个数。
   *
   * 2. `key`不存在，假设最终返回的`a(lo) > key`，因为a(lo) >= a(hi) = a(lo-1) > key
   *
   * @param key 键值
   * @return 个数
   */
  private def rank(key: K): Int = {
    var lo = 0
    var hi = n - 1
    while (lo <= hi) {
      val mid = lo + (hi - lo) / 2
      // 1 3 5 7    4
      implicitly[Ordering[K]].compare(key, ks(mid)) match {
        case x if x < 0 => hi = mid - 1
        case x if x > 0 => lo = mid + 1
        case _ => return mid
      }
    }
    lo
  }

  /**
   * 获取第`k`小的键值。
   *
   * @param k 排名
   * @return 返回第k小的键值
   */
  def select(k: Int): Option[K] = {
    if (k < 0 || k >= size()) throw new IllegalArgumentException(s"called select() with invalid argument: $k")
    Some(ks(k))
  }

  /**
   * 返回小于等于`key`的最大键值。
   *
   * @param key 键值
   * @return 返回小于等于`key`的最大键值
   */
  def floor(key: K): Option[K] = {
    val i = rank(key)
    // key已经存在了
    if (i < n && implicitly[Ordering[K]].equiv(ks(i), key)) return Some(ks(i))
    if (i == 0) None
    else Some(ks(i - 1))
  }

  /**
   * 返回大于等于`key`的最小键值。
   *
   * @param key 键值
   * @return 返回大于等于`key`的最小键值
   */
  def ceiling(key: K): Option[K] = {
    val i = rank(key)
    if (i < n && implicitly[Ordering[K]].equiv(ks(i), key)) return Some(ks(i))
    if (i == n) None
    else Some(ks(i)) // ks(i)有可能小于key吗？
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
    for (j <- n until i by -1) {
      ks(j) = ks(j - 1)
      vs(j) = vs(j - 1)
    }
    ks(i) = key
    vs(i) = value
    n += 1
  }

  private def resize(capacity: Int): Unit = {
    assert(capacity >= n)
    ks = Array.copyOf(ks, capacity)
    vs = Array.copyOf(vs, capacity)
  }

  override def delete(key: K): Unit = {
    if (isEmpty) return

    val i = rank(key)
    if (i == n || !implicitly[Ordering[K]].equiv(ks(i), key)) return

    for (j <- i until n - 1) {
      ks(j) = ks(j + 1)
      vs(j) = vs(j + 1)
    }
    n -= 1
    ks(n) = null.asInstanceOf[K]
    vs(n) = null.asInstanceOf[V]
    if (n > 0 && n == ks.length / 4) resize(ks.length / 2)
  }

  override def contains(key: K): Boolean = get(key) ne None

  override def keys(): Iterable[K] = keys(min(), max())

  def min(): K = {
    if (isEmpty) throw new NoSuchElementException("Called min() with empty symbol table")
    ks(0)
  }

  def max(): K = {
    if (isEmpty) throw new NoSuchElementException("Called max() with empty symbol table")
    ks(n - 1)
  }

  def deleteMin(): Unit = {
    if (isEmpty) throw new NoSuchElementException("Symbol table underflow error")
    delete(min())
  }

  def deleteMax(): Unit = {
    if (isEmpty) throw new NoSuchElementException("Symbol table underflow error")
    delete(max())
  }

  def size(lo: K, hi: K): Int = {
    if (implicitly[Ordering[K]].gt(lo, hi)) return 0
    if (contains(hi)) rank(hi) - rank(lo) + 1
    else rank(hi) - rank(lo)
  }

  def keys(lo: K, hi: K): Iterable[K] = {
    val q = new Queue[K]
    if (implicitly[Ordering[K]].gt(lo, hi)) return q
    for (i <- rank(lo) until rank(hi)) {
      q.enqueue(ks(i))
    }
    if (contains(hi)) q.enqueue(ks(rank(hi)))
    q
  }
}

object BinarySearchST {
  def apply[K: Ordering : ClassTag, V: ClassTag](ts: (K, V)*): BinarySearchST[K, V] = {
    val st = new BinarySearchST[K, V]
    ts.foreach(t => st.put(t._1, t._2))
    st
  }

  def main(args: Array[String]): Unit = {
    val st = new BinarySearchST[Int, Int]
    st.put(1, 2)
    st.put(3, 4)
    st.put(5, 6)
    println(st.contains(3))
    println(st.contains(2))
    println(st.keys())

    val st2 = BinarySearchST('a' -> 1, 'b' -> 2, 'c' -> 3)
    println(st2.min())
    println(st2.max())
    println(st2.keys())
  }
}

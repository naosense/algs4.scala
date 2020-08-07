package search

import collection.Queue

import scala.annotation.tailrec

/**
 * 链表构造的[[ST]]
 *
 * @tparam K key的类型
 * @tparam V value的类型
 */
class SequentialSearchST[K, V] extends ST[K, V] {
  private var first: Node = End
  private var n = 0

  override def size(): Int = n

  override def get(key: K): Option[V] = {
    if (key == null) throw new IllegalArgumentException("argument to get() is null")
    get(first, key)
  }

  @tailrec
  private def get(node: Node, key: K): Option[V] = {
    node match {
      case Entry(k, v, _) if k == key => Some(v)
      case Entry(k, _, next) if k != key => get(next, key)
      case End => None
    }
  }

  override def put(key: K, value: V): Unit = {
    put(first, key, value)
  }

  @tailrec
  private def put(node: Node, key: K, value: V): Unit = {
    node match {
      case e@Entry(k, _, _) if k == key => e.value = value
      case Entry(k, _, next) if k != key => put(next, key, value)
      case End =>
        first = Entry(key, value, first)
        n += 1
    }
  }

  override def delete(key: K): Unit = {
    if (key == null) throw new IllegalArgumentException("argument to delete() is null")
    first = delete(first, key)
  }

  @tailrec
  private def delete(x: Node, key: K): Node = {
    x match {
      case Entry(k, _, next) if k == key => n -= 1; next
      case Entry(k, _, next) if k != key => delete(next, key)
      case End => End
    }
  }

  override def keys(): Iterable[K] = {
    val q = new Queue[K]
    keys(first, q)
  }

  @tailrec
  private def keys(node: Node, q: Queue[K]): Queue[K] = {
    node match {
      case Entry(k, _, next) => q.enqueue(k); keys(next, q)
      case End => q
    }
  }

  override def contains(key: K): Boolean = {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null")
    get(key) != null
  }

  sealed trait Node
  case class Entry(key: K, var value: V, next: Node) extends Node
  case object End extends Node
}

object SequentialSearchST {
  def apply[K, V](ts: (K, V)*): SequentialSearchST[K, V] = {
    val st = new SequentialSearchST[K, V]
    ts.foreach(t => st.put(t._1, t._2))
    st
  }

  def main(args: Array[String]): Unit = {
    val st = new SequentialSearchST[Int, Int]
    st.put(1, 2)
    st.put(3, 4)
    st.put(5, 6)
    println(st.keys())

    val st2 = SequentialSearchST('a' -> 1, 'b' -> 2)
    println(st2.keys())
  }
}

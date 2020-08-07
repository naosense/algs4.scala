package search

trait ST[K, V] {
  def size(): Int

  def isEmpty: Boolean = size() == 0

  def get(key: K): Option[V]

  def put(key: K, value: V): Unit

  def delete(key: K): Unit

  def contains(key: K): Boolean

  def keys(): Iterable[K]
}

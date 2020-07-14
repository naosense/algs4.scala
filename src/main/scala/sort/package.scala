package object sort {
  def exch[A](a: Array[A], i: Int, j: Int): Unit = {
    val swap = a(i)
    a(i) = a(j)
    a(j) = swap
  }

  def show[A](a: Array[A]): Unit = {
    a.foreach(println)
  }

  def isSorted[A](a: Array[A], lo: Int, hi: Int)(implicit ordering: Ordering[A]): Boolean = {
    for (i <- lo + 1 until hi) {
      if (ordering.lt(a(i), a(i - 1))) return false
    }
    true
  }
}

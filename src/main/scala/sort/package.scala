package object sort {

  def less[A: Ordering](a: A, b: A): Boolean = {
    implicitly[Ordering[A]].lt(a, b)
  }

  def exch[A](a: Array[A], i: Int, j: Int): Unit = {
    val swap = a(i)
    a(i) = a(j)
    a(j) = swap
  }

  def show[A](a: Array[A], sorted: Boolean = true): Unit = {
    if (sorted) print("sorted: ") else print("origin: ")
    a.foreach(e => print(e + " "))
    println()
  }

  def isSorted[A: Ordering](a: Array[A]): Boolean = {
    isSorted(a, 0, a.length)
  }

  def isSorted[A: Ordering](a: Array[A], lo: Int, hi: Int): Boolean = {
    for (i <- lo + 1 until hi) {
      if (implicitly[Ordering[A]].lt(a(i), a(i - 1))) return false
    }
    true
  }
}

package sort

object Insertion {
  def sort[A](a: Array[A])(implicit ord: A => Ordered[A]): Unit = {
    val n = a.length
    for (i <- 1 until n; j <- i until 0 by -1) {
      if (a(j) < a(j - 1)) exch(a, j, j - 1)
    }
  }

  def main(args: Array[String]): Unit = {
    val a = Array(1, 4, 1, 3, 8)
    sort(a)
    show(a)
  }
}

package sort

import scala.util.Random

object Insertion {

  def sort[A: Ordering](a: Array[A]): Unit = {
    val n = a.length
    for (i <- 1 until n; j <- i until 0 by -1) {
      if (less(a(j), a(j - 1))) exch(a, j, j - 1)
    }
  }

  def main(args: Array[String]): Unit = {
    val a = Array.fill(10)(Random.nextInt(10))
    show(a, false)
    sort(a)
    show(a)
    sort(a)((x, y) => y - x)
    show(a)
  }
}

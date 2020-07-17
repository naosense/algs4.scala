package sort

import scala.util.Random

object InsertionX {
  def sort[A: Ordering](a: Array[A]): Unit = {
    val n = a.length
    var exchanges = 0
    for (i <- n - 1 until 0 by -1) {
      if (less(a(i), a(i - 1))) {
        exch(a, i, i - 1)
        exchanges = exchanges + 1
      }
    }
    if (exchanges == 0) return
    for (i <- 2 until n) {
      val v = a(i)
      var j = i
      while (less(v, a(j - 1))) {
        a(j) = a(j - 1)
        j = j - 1
      }
      a(j) = v
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

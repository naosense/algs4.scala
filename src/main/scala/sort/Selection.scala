package sort

import scala.util.Random

object Selection {
  def sort[A: Ordering](a: Array[A]): Unit = {
    val n = a.length
    for (i <- 0 until n) {
      var min = i
      for (j <- i + 1 until n) {
        if (less(a(j), a(min))) min = j
      }
      exch(a, i, min)
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

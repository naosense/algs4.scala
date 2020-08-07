package sort

import scala.util.Random

object Heap {
  def sort[A: Ordering](a: Array[A]): Unit = {
    val n = a.length

    // 改成 1 to n/2可以吗？
    for (k <- n / 2 to 1 by -1) sink(a, k, n)

    var i = n
    while (i > 1) {
      exch(a, 0, { i -= 1; i })
      sink(a, 1, i)
    }
  }

  private def sink[A: Ordering](a: Array[A], k: Int, n: Int): Unit = {
    var kk = k
    var goon = true
    while (2 * kk <= n && goon) {
      var j = 2 * kk
      if (j < n && less(a(j - 1), a(j))) j += 1
      goon = less(a(kk - 1), a(j - 1))
      if (goon) {
        exch(a, kk - 1, j - 1)
        kk = j
      }
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

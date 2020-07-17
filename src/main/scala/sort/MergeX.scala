package sort

import scala.util.Random

object MergeX {
  val CUT_OFF = 7

  def sort[A: Ordering](a: Array[A]): Unit = {
    val aux = a.clone()
    sort(aux, a, 0, a.length - 1)
  }

  private def sort[A: Ordering](src: Array[A], dst: Array[A], lo: Int, hi: Int): Unit = {
    if (hi <= lo + CUT_OFF) {
      insertionSort(dst, lo, hi)
      return
    }
    val mid = lo + (hi - lo) / 2
    sort(dst, src, lo, mid)
    sort(dst, src, mid + 1, hi)

    if (!less(src(mid + 1), src(mid))) {
      Array.copy(src, lo, dst, lo, hi - lo + 1)
      return
    }

    merge(src, dst, lo, mid, hi)
  }

  private def merge[A: Ordering](src: Array[A], dst: Array[A], lo: Int, mid: Int, hi: Int): Unit = {
    var i = lo
    var j = mid + 1
    for (k <- lo to hi) {
      if (i > mid) { dst(k) = src(j); j += 1 }
      else if (j > hi) { dst(k) = src(i); i += 1 }
      else if (less(src(j), src(i))) { dst(k) = src(j); j += 1 }
      else { dst(k) = src(i); i += 1 }
    }
  }

  private def insertionSort[A: Ordering](a: Array[A], lo: Int, hi: Int): Unit = {
    for (i <- lo to hi; j <- i until lo by -1) {
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

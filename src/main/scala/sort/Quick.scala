package sort

import util.StdRandom

import scala.util.Random

object Quick {

  def sort[A: Ordering](a: Array[A]): Unit = {
    StdRandom.shuffle(a)
    sort(a, 0, a.length - 1)
  }

  private def sort[A: Ordering](a: Array[A], lo: Int, hi: Int): Unit = {
    if (hi <= lo) return
    val j = partition(a, lo, hi)
    sort(a, lo, j - 1)
    sort(a, j + 1, hi)
  }

  private def partition[A: Ordering](a: Array[A], lo: Int, hi: Int): Int = {
    import scala.util.control.Breaks._

    var i = lo
    var j = hi + 1
    val v = a(lo)
    breakable {
      while (true) {
        i += 1
        breakable {
          while (less(a(i), v)) {
            if (i == hi) break()
            i += 1
          }
        }
        j -= 1
        breakable {
          while (less(v, a(j))) {
            if (j == lo) break()
            j -= 1
          }
        }
        if (i >= j) break()
        exch(a, i, j)
      }
    }
    exch(a, lo, j)
    j
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

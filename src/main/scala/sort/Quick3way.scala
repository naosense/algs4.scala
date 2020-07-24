package sort

import util.StdRandom

object Quick3way {
  def sort[A: Ordering](a: Array[A]): Unit = {
    StdRandom.shuffle(a)
    sort(a, 0, a.length - 1)
  }

  def sort[A: Ordering](a: Array[A], lo: Int, hi: Int): Unit = {
    if (hi <= lo) return
    var lt = lo
    var gt = hi
    val v = a(lo)
    var i = lo + 1
    while (i <= gt) {
      implicitly[Ordering[A]].compare(a(i), v) match {
        case x if x < 0 => exch(a, { lt += 1; lt - 1 }, { i += 1; i - 1 }) // 模拟it++,i++，下同
        case x if x > 0 => exch(a, i, { gt -= 1; gt + 1 })
        case _ => i += 1
      }
    }

    // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
    sort(a, lo, lt - 1)
    sort(a, gt + 1, hi)
  }
}

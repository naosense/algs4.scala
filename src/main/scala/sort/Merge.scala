package sort

import scala.reflect.ClassTag
import scala.util.Random

object Merge {
  /**
   * 这里没有使用
   * {{{
   *   def sort[A: Ordering : ClassTag](a: Array[A]): Unit
   * }}}
   * 这种方式是因为调用`sort`方法时需显式设置`ClassTag`。
   *
   * @param a 需要排序的数组
   * @tparam A 数组元素的类型
   */
  def sort[A: Ordering](a: Array[A]): Unit = {
    val et: ClassTag[A] = ClassTag(a.getClass.getComponentType)
    val aux = et.newArray(a.length)
    sort(a, aux, 0, a.length - 1)
    assert(isSorted(a))
  }

  private def sort[A: Ordering](a: Array[A], aux: Array[A], lo: Int, hi: Int): Unit = {
    if (hi <= lo) return
    val mid = lo + (hi - lo) / 2
    sort(a, aux, lo, mid)
    sort(a, aux, mid + 1, hi)
    merge(a, aux, lo, mid, hi)
  }

  private def merge[A: Ordering](a: Array[A], aux: Array[A], lo: Int, mid: Int, hi: Int): Unit = {
    // copy to aux[]
    for (k <- lo to hi) {
      aux(k) = a(k)
    }

    var i = lo
    var j = mid + 1
    for (k <- lo to hi) {
      if (i > mid) { a(k) = aux(j); j += 1 }
      else if (j > hi) { a(k) = aux(i); i += 1 }
      else if (less(aux(j), aux(i))) { a(k) = aux(j); j += 1 }
      else { a(k) = aux(i); i += 1 }
    }
  }

  def main(args: Array[String]): Unit = {
    val a = Array.fill(10)(Random.nextInt(10))
    show(a, false)
    sort(a)
    show(a)
    a.sorted
    sort(a)((x, y) => y - x)
    show(a)
  }
}

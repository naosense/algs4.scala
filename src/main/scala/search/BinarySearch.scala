package search

import java.util

import sort.Quick

object BinarySearch {

  def search[A: Ordering](a: Array[A], key: A): Int = {
    var lo = 0
    var hi = a.length - 1
    while (lo <= hi) {
      val mid = lo + (hi - lo) / 2
      implicitly[Ordering[A]].compare(key, a(mid)) match {
        case x if x < 0 => hi = mid - 1
        case x if x > 0 => lo = mid + 1
        case _ => return mid
      }
    }
    -1
  }

  def main(args: Array[String]): Unit = {
    val a = Array(1, 2, 3, 4, 7, 9, 1)
    Quick.sort(a)
    println(util.Arrays.toString(a))
    println(search(a, 1))
    println(search(a, 7))
    println(search(a, 0))
  }
}

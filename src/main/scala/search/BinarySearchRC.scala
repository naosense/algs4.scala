package search

import java.util

import sort.Quick

import scala.annotation.tailrec

/**
 * 二分查找递归版本
 */
object BinarySearchRC {
  def search[A: Ordering](a: Array[A], key: A): Int = {
    search(a, key, 0, a.length - 1)
  }

  @tailrec
  private def search[A: Ordering](a: Array[A], key: A, lo: Int, hi: Int): Int = {
    if (lo > hi) -1
    else {
      val mid = lo + (hi - lo) / 2
      implicitly[Ordering[A]].compare(key, a(mid)) match {
        case x if x < 0 => search(a, key, lo, mid - 1)
        case x if x > 0 => search(a, key, mid + 1, hi)
        case _ => mid
      }
    }
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

package util

import sort._

import scala.util.Random

object Timer {
  /**
   * 返回`p`的运行时间（毫秒）
   *
   * @param p      需要测量运行时间的程序
   * @param warmup 预热次数
   * @param run    运行次数
   * @return 运行时间
   */
  def time(p: => Unit, warmup: Int = 10, run: Int = 10): Double = {
    for (_ <- 0 until warmup) {
      p
    }
    val start = System.nanoTime()
    for (_ <- 0 until run) {
      p
    }
    val end = System.nanoTime()
    ((end - start) * 1.0 / run / 10000).round / 100.0
  }

  /**
   * 比较`p1`和`p2`的运行时间。
   *
   * @param p1     p1
   * @param p2     p2
   * @param warmup 预热次数
   * @param run    运行次数
   * @return `time(p1)/time(p2)`
   */
  def compare(p1: => Unit, p2: => Unit, warmup: Int = 10, run: Int = 10): Double =
    (time(p1, warmup, run) / time(p2, warmup, run) * 10).round / 10.0

  def main(args: Array[String]): Unit = {
    val a1 = Array.fill(10000)(Random.nextInt(10000000))
    val a2 = a1.clone()
    val a3 = a1.clone()
    val a4 = a1.clone()
    val a5 = a1.clone()
    val a6 = a1.clone()
    val a7 = a1.clone()
    val a8 = a1.clone()
    val a9 = a1.clone()

    val t1 = Timer.time(Quick.sort(a1), 0, 1)
    val t2 = Timer.time(Merge.sort(a2), 0, 1)
    val t3 = Timer.time(Selection.sort(a3), 0, 1)
    val t4 = Timer.time(Insertion.sort(a4), 0, 1)
    val t5 = Timer.time(InsertionX.sort(a5), 0, 1)
    val t6 = Timer.time(java.util.Arrays.sort(a6), 0, 1)
    val t7 = Timer.time(MergeX.sort(a7), 0, 1)
    val t8 = Timer.time(Quick3way.sort(a8), 0, 1)
    val t9 = Timer.time(Heap.sort(a9), 0, 1)
    println(
      s"""
         |     quick   $t1
         | quick3way   $t8
         |     merge   $t2
         |    mergex   $t7
         |      heap   $t9
         | selection   $t3
         | insertion   $t4
         |insertionx   $t5
         |      java   $t6"""
        .stripMargin)
  }
}

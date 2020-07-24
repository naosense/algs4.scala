package util

class QuickUnionUF(n: Int) {
  private val parent = new Array[Int](n)
  private var cnt = n

  /**
   * Initializes an empty union-find data structure with
   * `n` elements `0` through `n-1`.
   * Initially, each elements is in its own set.
   *
   * @throws IllegalArgumentException if { @code n < 0}
   */
  if (n < 0) throw new IllegalArgumentException
  (0 until n).foreach(i => parent(i) = i)

  def count(): Int = cnt

  def find(p: Int): Int = {
    validate(p)
    var pp = p
    while (pp != parent(pp)) {
      pp = parent(pp)
    }
    p
  }

  private def validate(p: Int): Unit = {
    val n = parent.length
    if (p < 0 || p >= n) {
      throw new IllegalArgumentException(s"index $p is not between and 0 and ${ n - 1 }")
    }
  }

  @deprecated
  def connected(p: Int, q: Int): Boolean = find(p) == find(q)

  def union(p: Int, q: Int): Unit = {
    val rootP = find(p)
    val rootQ = find(q)
    if (rootP == rootQ) return
    parent(rootP) = rootQ
    cnt -= 1
  }
}

object QuickUnionUF {
  def main(args: Array[String]): Unit = {
    import scala.util.control.Breaks.{break, breakable}
    val n = StdIn.readInt()
    val uf = new QuickUnionUF(n)
    while (!StdIn.isEmpty) {
      println(StdIn.readString())
      val p = StdIn.readInt()
      val q = StdIn.readInt()
      breakable {
        if (uf.find(p) == uf.find(q)) break()
      }
      uf.union(p, q)
      println(p + " " + q)
    }
    println(uf.count + " components")
  }
}

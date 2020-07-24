package util

class WeightedQuickUnionUF(n: Int) {
  if (n < 0) throw new IllegalArgumentException
  private val parent = new Array[Int](n)
  private val size = new Array[Int](n)
  private var cnt = n
  for (i <- 0 until n) {
    parent(i) = i
    size(i) = 1
  }

  def count(): Int = cnt

  def find(p: Int): Int = {
    validate(p)
    var pp = p
    while (pp != parent(pp)) {
      pp = parent(pp)
    }
    pp
  }

  def connected(p: Int, q: Int): Boolean = find(p) == find(q)

  def union(p: Int, q: Int): Unit = {
    val rootP = find(p)
    val rootQ = find(q)
    if (rootP == rootQ) return

    if (size(rootP) < size(rootQ)) {
      parent(rootP) = rootQ
      size(rootQ) += size(rootP)
    } else {
      parent(rootQ) = rootP
      size(rootP) += size(rootQ)
    }
    cnt -= 1
  }

  private def validate(p: Int): Unit = {
    val n = parent.length
    if (p < 0 || p >= n) throw new IllegalArgumentException(s"index $p is not between 0 and ${ n - 1 }")
  }
}

object WeightedQuickUnionUF {
  def main(args: Array[String]): Unit = {
    import scala.util.control.Breaks.{break, breakable}
    val n = StdIn.readInt()
    val uf = new WeightedQuickUnionUF(n)
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

package other

class UF(n: Int) {
  private val parents = new Array[Int](n)
  private val rank = new Array[Byte](n)
  private var cnt = n

  for (i <- 0 until n) {
    parents(i) = i
    rank(i) = 0
  }

  def find(p: Int): Int = {
    var p2 = p
    validate(p2)
    while (p2 != parents(p2)) {
      parents(p2) = parents(parents(p2))
      p2 = parents(p2)
    }
    p2
  }

  def count(): Int = cnt

  @deprecated("Replace with two calls to {@link #find(int)}.")
  def connected(p: Int, q: Int): Boolean = find(p) == find(q)

  def union(p: Int, q: Int): Unit = {
    val rootP = find(p)
    val rootQ = find(q)
    if (rootP == rootQ) return

    if (rank(rootP) < rank(rootQ)) parents(rootP) = rootQ
    else if (rank(rootP) > rank(rootQ)) parents(rootQ) = rootP
    else {
      parents(rootQ) = rootP
      rank(rootP) = (rank(rootP) + 1).toByte
    }
    cnt = cnt - 1
  }

  def validate(p: Int): Unit = {
    val n = parents.length
    if (p < 0 || p >= n) {
      throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1))
    }
  }
}

object UF {
  def main(args: Array[String]): Unit = {
    import util.StdIn
    import scala.util.control.Breaks.{break, breakable}
    val n = StdIn.readInt()
    val uf = new UF(n)
    breakable {
      while (!StdIn.isEmpty) {
        println(StdIn.readString())
        val p = StdIn.readInt()
        val q = StdIn.readInt()
        if (uf.find(p) == uf.find(q)) break()
        uf.union(p, q)
        println(p + " " + q)
      }
    }
    println(uf.count + " components")
  }
}

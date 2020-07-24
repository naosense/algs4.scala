package util

class QuickFindUF(n: Int) {
  private val id = new Array[Int](n)
  private var cnt = n

  if (n < 0) throw new IllegalArgumentException
  (0 until n).foreach(i => id(i) = i)

  def count(): Int = cnt

  def find(p: Int): Int = {
    validate(p)
    id(p)
  }

  @deprecated
  def connected(p: Int, q: Int): Boolean = {
    validate(p)
    validate(p)
    id(p) == id(q)
  }

  def union(p: Int, q: Int): Unit = {
    validate(p)
    validate(q)
    val pid = id(p)
    val qid = id(q)

    if (pid == qid) return

    for (i <- id.indices if id(i) == pid) {
      id(i) = qid
    }
    cnt -= 1
  }

  private def validate(p: Int): Unit = {
    val n = id.length
    if (p < 0 || p >= n) {
      throw new IllegalArgumentException(s"index $p is not between and 0 and ${ n - 1 }")
    }
  }
}

object QuickFindUF {
  def main(args: Array[String]): Unit = {
    import scala.util.control.Breaks.{break, breakable}
    val n = StdIn.readInt()
    val uf = new QuickFindUF(n)
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

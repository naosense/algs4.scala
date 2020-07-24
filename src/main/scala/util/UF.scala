package util

/**
 * The `UF` class represents a <em>union–find data type</em>
 * (also known as the <em>disjoint-sets data type</em>).
 * It supports the classic <em>union</em> and <em>find</em> operations,
 * along with a <em>count</em> operation that returns the total number
 * of sets.
 * <p>
 * The union–find data type models a collection of sets containing
 * <em>n</em> elements, with each element in exactly one set.
 * The elements are named 0 through <em>n</em>–1.
 * Initially, there are <em>n</em> sets, with each element in its
 * own set. The <em>canonical element</em> of a set
 * (also known as the <em>root</em>, <em>identifier</em>,
 * <em>leader</em>, or <em>set representative</em>)
 * is one distinguished element in the set. Here is a summary of
 * the operations:
 * <ul>
 * <li><em>find</em>(<em>p</em>) returns the canonical element
 * of the set containing <em>p</em>. The <em>find</em> operation
 * returns the same value for two elements if and only if
 * they are in the same set.
 * <li><em>union</em>(<em>p</em>, <em>q</em>) merges the set
 * containing element <em>p</em> with the set containing
 * element <em>q</em>. That is, if <em>p</em> and <em>q</em>
 * are in different sets, replace these two sets
 * with a new set that is the union of the two.
 * <li><em>count</em>() returns the number of sets.
 * </ul>
 * <p>
 * The canonical element of a set can change only when the set
 * itself changes during a call to <em>union</em>&mdash;it cannot
 * change during a call to either <em>find</em> or <em>count</em>.
 * <p>
 * This implementation uses <em>weighted quick union by rank</em>
 * with <em>path compression by halving</em>.
 * The constructor takes &Theta;(<em>n</em>) time, where
 * <em>n</em> is the number of elements.
 * The <em>union</em> and <em>find</em> operations take
 * &Theta;(log <em>n</em>) time in the worst case.
 * The <em>count</em> operation takes &Theta;(1) time.
 * Moreover, starting from an empty data structure with <em>n</em> sites,
 * any intermixed sequence of <em>m</em> <em>union</em> and <em>find</em>
 * operations takes <em>O</em>(<em>m</em> &alpha;(<em>n</em>)) time,
 * where &alpha;(<em>n</em>) is the inverse of
 * <a href = "https://en.wikipedia.org/wiki/Ackermann_function#Inverse">Ackermann's function</a>.
 * <p>
 * For alternative implementations of the same API, see
 * [[QuickUnionUF]], [[QuickFindUF]], and [[WeightedQuickUnionUF]].
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/15uf">Section 1.5</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
class UF(n: Int) {
  /**
   * Initializes an empty union-find data structure with
   * `n` elements `0` through `n-1`.
   * Initially, each elements is in its own set.
   *
   * @throws IllegalArgumentException if { @code n < 0}
   */
  if (n < 0) throw new IllegalArgumentException
  private val parents = new Array[Int](n) // parent[i] = parent of i
  private val rank = new Array[Byte](n) // rank[i] = rank of subtree rooted at i (never more than 31)
  private var cnt = n // number of components
  for (i <- 0 until n) {
    parents(i) = i
    rank(i) = 0
  }

  /**
   * Returns the canonical element of the set containing element `p`.
   *
   * @param p an element
   * @return the canonical element of the set containing `p`
   * @throws IllegalArgumentException unless `0 <= p < n`
   */
  def find(p: Int): Int = {
    var p2 = p
    validate(p2)
    while (p2 != parents(p2)) {
      parents(p2) = parents(parents(p2))
      p2 = parents(p2)
    }
    p2
  }

  /**
   * Returns the number of sets.
   *
   * @return the number of sets (between `1` and `n`)
   */
  def count(): Int = cnt

  /**
   * Returns true if the two elements are in the same set.
   *
   * @param p one element
   * @param q the other element
   * @return `true` if `p` and `q` are in the same set;
   *         `false` otherwise
   * @throws IllegalArgumentException unless both `0 <= p < n` and `0 <= q < n`
   * @deprecated Replace with two calls to [[UF#find(int)]].
   */
  @deprecated("Replace with two calls to [[UF#find(int)]].")
  def connected(p: Int, q: Int): Boolean = find(p) == find(q)

  /**
   * Merges the set containing element `p` with the
   * the set containing element `q`.
   *
   * @param p one element
   * @param q the other element
   * @throws IllegalArgumentException unless both `0 <= p < n` and `0 <= q < n`
   */

  def union(p: Int, q: Int): Unit = {
    val rootP = find(p)
    val rootQ = find(q)
    if (rootP == rootQ) return

    rank(rootP) - rank(rootQ) match {
      case x if x > 0 => parents(rootP) = rootQ
      case x if x < 0 => parents(rootQ) = rootP
      case _ =>
        parents(rootQ) = rootP
        rank(rootP) = (rank(rootP) + 1).toByte
    }
    cnt -= 1
  }

  def validate(p: Int): Unit = {
    val n = parents.length
    if (p < 0 || p >= n) throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1))
  }
}

object UF {
  def main(args: Array[String]): Unit = {
    import scala.util.control.Breaks.{break, breakable}
    val n = StdIn.readInt()
    val uf = new UF(n)
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

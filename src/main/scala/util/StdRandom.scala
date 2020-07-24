package util

import java.util.Random

object StdRandom {
  private var seed: Long = System.currentTimeMillis()
  private var rnd: Random = new Random(seed)

  def setSeed(s: Long): Unit = {
    seed = s
    rnd = new Random(seed)
  }

  def getSeed: Long = seed

  def uniform(): Double = rnd.nextDouble()

  def uniform(n: Int): Int = {
    if (n <= 0) throw new IllegalArgumentException(s"argument must be positive: $n")
    rnd.nextInt(n)
  }

  def uniform(n: Long): Long = {
    if (n <= 0L) throw new IllegalArgumentException(s"argument must be positive: $n")

    var r = rnd.nextLong()
    val m = n - 1
    if ((n & m) == 0L) return r & m

    var u = r >>> 1
    r = u % n
    while (u + m - r < 0L) {
      u = rnd.nextLong() >>> 1
      r = u % n
    }
    r
  }

  def random(): Double = uniform()

  def uniform(a: Int, b: Int): Int = {
    if ((b <= a) || (b - a).asInstanceOf[Long] >= Int.MaxValue)
      throw new IllegalArgumentException(s"invalid range: [$a, $b")

    a + uniform(b - a)
  }

  def uniform(a: Double, b: Double): Double = {
    if (a >= b) {
      throw new IllegalArgumentException(s"invalid range: [$a, $b")
    }

    a + uniform() * (b - a)
  }

  def bernoulli(p: Double): Boolean = {
    if (!(p >= 0.0 && p <= 1.0)) {
      throw new IllegalArgumentException(s"probability p must be between 0.0 and 1.0: $p")
    }
    uniform() < p
  }

  def bernoulli(): Boolean = bernoulli(0.5)

  def gaussian(): Double = {
    var r = 0.0
    var x = 0.0
    var y = 0.0
    do {
      x = uniform(-1.0, 1.0)
      y = uniform(-1.0, 1.0)
      r = x * x + y * y
    } while (r >= 1 || r == 0)
    x * Math.sqrt(-2 * Math.log(r) / r)
  }

  def gaussian(mu: Double, sigma: Double): Double = mu + sigma * gaussian()

  def geometric(p: Double): Int = {
    if (p < 0) {
      throw new IllegalArgumentException(s"probability p must be greater than 0: $p")
    }
    if (p > 1.0) {
      throw new IllegalArgumentException(s"probability p must not be larger than 0: $p")
    }
    Math.ceil(Math.log(uniform()) / Math.log(1.0 - p)).asInstanceOf[Int]
  }

  def poisson(lambda: Double): Int = {
    if (lambda <= 0.0) {
      throw new IllegalArgumentException(s"lambda must be positive: $lambda")
    }
    if (lambda.isInfinite) {
      throw new IllegalArgumentException(s"lambda must not be infinite: $lambda")
    }
    var k = 0
    var p = 1.0
    val expLambda = Math.exp(-lambda)
    do {
      k += 1
      p *= uniform()
    } while (p >= expLambda)
    k - 1
  }

  def pareto(alpha: Double): Double = {
    if (alpha <= 0) {
      throw new IllegalArgumentException(s"alpha must be positive: $alpha")
    }
    Math.pow(1 - uniform(), -1.0 / alpha) - 1.0
  }

  def pareto(): Double = pareto(1.0)

  def cauchy(): Double = Math.tan(Math.PI * (uniform() - 0.5))

  def discrete(probabilities: Array[Double]): Int = {
    if (probabilities == null) {
      throw new IllegalArgumentException("argument array is null")
    }
    val EPSILON = 1.0E-14
    val sum = probabilities.sum
    if (sum > 1.0 + EPSILON || sum < 1.0 - EPSILON) {
      throw new IllegalArgumentException(s"sum of array entries does not approximately equal 1.0: $sum")
    }

    // the for loop may not return a value when both r is (nearly) 1.0 and when the
    // cumulative sum is less than 1.0 (as a result of floating-point roundoff error)
    while (true) {
      val r = uniform()
      var sum = 0.0
      for (i <- probabilities.indices) {
        sum += probabilities(i)
        if (sum > r) return i
      }
    }
    // can't reach here
    -1
  }

  def discrete(frequencies: Array[Int]): Int = {
    if (frequencies == null) throw new IllegalArgumentException("")
    var sum: Long = frequencies.sum
    if (sum == 0)
      throw new IllegalArgumentException("at least one array must be positive")
    if (sum > Int.MaxValue)
      throw new IllegalArgumentException("sum of frequencies overflows an int")

    val r = uniform(sum.asInstanceOf[Int])
    sum = 0
    for (i <- frequencies.indices) {
      sum += frequencies(i)
      if (sum > r) return i
    }
    // can not reach here
    -1
  }

  def exp(lambda: Double): Double = {
    if (lambda <= 0) throw new IllegalArgumentException(s"lambda must be positive: $lambda")
    -Math.log(1 - uniform()) / lambda
  }

  def shuffle[A](a: Array[A]): Unit = {
    val n = a.length
    for (i <- 0 until n) {
      val r = i + uniform(n - i)
      exch(a, i, r)
    }
  }

  def shuffle[A](a: Array[A], lo: Int, hi: Int): Unit = {
    validateNotNull(a)
    for (i <- lo until hi) {
      val r = i + uniform(hi - i)
      exch(a, i, r)
    }
  }

  def permutation(n: Int): Array[Int] = {
    if (n < 0) throw new IllegalArgumentException("argument is negative")
    val perm = (0 until n).toArray
    shuffle(perm)
    perm
  }

  def permutation(n: Int, k: Int): Array[Int] = {
    if (n < 0) throw new IllegalArgumentException("k must be between 0 and n")
    if (n < 0 || k > n) throw new IllegalArgumentException("k must be between 0 and n")
    val perm = new Array[Int](k)
    for (i <- 0 until k) {
      val r = uniform(i + 1)
      perm(i) = perm(r)
      perm(r) = i
    }
    for (i <- k until n) {
      val r = uniform(i + 1)
      if (r < k) perm(r) = i
    }
    perm
  }

  private def exch[A](a: Array[A], i: Int, j: Int): Unit = {
    val swap = a(i)
    a(i) = a(j)
    a(j) = swap
  }

  private def validateNotNull(x: Any): Unit = {
    if (x == null) throw new IllegalArgumentException("argument is null")
  }

  private def show[A](a: Array[A]): Unit = {
    a.foreach(e => print(e + " "))
    println()
  }

  def main(args: Array[String]): Unit = {
    printf("%2d\n", uniform(100))
    printf("%8.5f\n", uniform(10.0, 99.0))
    printf("%5b\n", bernoulli(0.5))
    printf("%7.5f\n", gaussian(9.0, 0.2))
    printf("%1d\n", discrete(Array(0.5, 0.3, 0.1, 0.1)))
    printf("%1d\n", discrete(Array(5, 3, 1, 1)))
    printf("%11d\n", uniform(100000000000L))
    val a = Array("A", "B", "C", "D", "E", "F", "G")
    shuffle(a)
    show(a)
  }
}

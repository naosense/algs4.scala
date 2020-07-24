package util

import java.io.{BufferedInputStream, IOException}

object BinaryStdIn {
  private val EOF = -1
  private var in: BufferedInputStream = _
  private var buffer = 0
  private var n = 0
  private var isInitialized = false

  def initialize(): Unit = {
    in = new BufferedInputStream(System.in)
    buffer = 0
    n = 0
    fillBuffer()
    isInitialized = true
  }

  private def fillBuffer(): Unit = {
    try {
      buffer = in.read()
      n = 8
    } catch {
      case e: IOException =>
        println("EOF")
        buffer = EOF
        n = -1
    }
  }

  def close(): Unit = {
    if (!isInitialized) initialize()
    try {
      in.close()
      isInitialized = false
    } catch {
      case e: IOException => throw new IllegalArgumentException(s"Can not close BinaryStdIn $e")
    }
  }

  def isEmpty: Boolean = {
    if (!isInitialized) initialize()
    buffer == EOF
  }

  def readBoolean(): Boolean = {
    if (isEmpty) throw new NoSuchElementException("Reading from empty input stream")
    n -= 1
    val bit = ((buffer >> n) & 1) == 1
    if (n == 0) fillBuffer()
    bit
  }

  def readChar(): Char = {
    if (isEmpty) throw new NoSuchElementException("Reading from empty input stream")

    if (n == 8) {
      val x = buffer
      fillBuffer()
      return (x & 0xff).toChar
    }

    var x = buffer
    x <<= (8 - n)
    val oldN = n
    fillBuffer()
    if (isEmpty) throw new NoSuchElementException("Reading from empty input stream")
    n = oldN
    x |= (buffer >>> n)
    (x & 0xff).toChar
  }

  def readChar(r: Int): Char = {
    if (r < 1 || r > 16) throw new IllegalArgumentException(s"Illegal value of r = $r")

    if (r == 8) return readChar()

    var x = 0
    for (i <- 0 until r) {
      x <<= 1
      val bit = readBoolean()
      if (bit) x |= 1
    }
    x.toChar
  }
}

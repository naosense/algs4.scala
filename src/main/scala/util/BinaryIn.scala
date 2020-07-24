package util

import java.io.{BufferedInputStream, File, FileInputStream, IOException, InputStream}
import java.net.{Socket, URL}

class BinaryIn(is: InputStream) {
  private var in: BufferedInputStream = new BufferedInputStream(is)
  private var buffer = 0
  private var n = 0

  def this() {
    this(System.in)
    fillBuffer()
  }

  def this(socket: Socket) {
    this(socket.getInputStream)
    fillBuffer()
  }

  def this(url: URL) {
    this(url.openConnection().getInputStream)
    fillBuffer()
  }

  def this(name: String) {
    this(System.in)
    val file = new File(name)
    if (file.exists()) {
      val fis = new FileInputStream(file)
      in = new BufferedInputStream(fis)
    } else {
      // try for files included in jar
      var url = getClass.getResource(name)

      // or url from web
      if (url == null) {
        url = new URL(name)
      }

      val site = url.openConnection()
      val is = site.getInputStream
      in = new BufferedInputStream(is)
    }

    fillBuffer()
  }

  private def fillBuffer(): Unit = {
    try {
      buffer = in.read()
      n = 8
    } catch {
      case _: IOException =>
        System.err.println("EOF")
        buffer = BinaryIn.EOF
        n = -1
    }
  }

  def exists(): Boolean = in != null

  def isEmpty: Boolean = buffer == BinaryIn.EOF

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
    // the above code doesn't quite work for the last character if N = 8
    // because buffer will be -1
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

  def readString(): String = {
    if (isEmpty) throw new NoSuchElementException("Reading from empty input stream")

    val sb = new StringBuilder
    while (!isEmpty) {
      sb.append(readChar())
    }
    sb.toString()
  }

  def readShort(): Short = {
    var x = 0
    for (_ <- 0 until 2) {
      val c = readChar()
      x <<= 8
      x |= c
    }
    x.toShort
  }

  def readInt(): Int = {
    var x = 0
    for (_ <- 0 until 4) {
      val c = readChar()
      x <<= 8
      x |= c
    }
    x
  }

  def readInt(r: Int): Int = {
    if (r < 1 || r > 32) throw new IllegalArgumentException(s"Illegal value of r = $r")

    if (r == 32) return readInt()

    var x = 0
    for (_ <- 0 until r) {
      x <<= 1
      val bit = readBoolean()
      if (bit) x |= 1
    }
    x
  }

  def readLong(): Long = {
    var x: Long = 0
    for (_ <- 0 until 8) {
      val c = readChar()
      x <<= 8
      x |= c
    }
    x
  }

  def readDouble(): Double = java.lang.Double.longBitsToDouble(readLong())

  def readFloat(): Float = java.lang.Float.intBitsToFloat(readInt())

  def readByte(): Byte = (readChar() & 0xff).toByte
}

object BinaryIn {
  private val EOF = -1

  def main(args: Array[String]): Unit = {
    val in = new BinaryIn(args(0))
    val out = new BinaryOut()

    while (!in.isEmpty) {
      val c = in.readChar()
      out.write(c)
    }
    out.flush()
  }
}

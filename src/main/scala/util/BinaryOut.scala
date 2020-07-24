package util

import java.io.{BufferedOutputStream, FileOutputStream, IOException, OutputStream}
import java.net.Socket

class BinaryOut(os: OutputStream) {
  private var out = new BufferedOutputStream(os)
  private var buffer = 0
  private var n = 0

  def this() {
    this(System.out)
  }

  def this(filename: String) {
    this(System.out)
    try {
      val os = new FileOutputStream(filename)
      out = new BufferedOutputStream(os)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def this(socket: Socket) {
    this(System.out)
    try {
      val os = socket.getOutputStream
      out = new BufferedOutputStream(os)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  private def writeBit(x: Boolean): Unit = {
    buffer <<= 1
    if (x) buffer |= 1

    n += 1
    if (n == 8) clearBuffer()
  }

  private def writeByte(x: Int): Unit = {
    if (n == 0) {
      try {
        out.write(x)
      } catch {
        case e: IOException => e.printStackTrace()
      }
      return
    }

    for (i <- 0 until 8) {
      val bit = ((x >>> (8 - i - 1)) & 1) == 1
      writeBit(bit)
    }
  }

  def flush(): Unit = {
    clearBuffer()
    try {
      out.flush()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def close(): Unit = {
    flush()
    try {
      out.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def write(x: Boolean): Unit = writeBit(x)

  def write(x: Byte): Unit = writeByte(x & 0xff)

  def write(x: Int): Unit = {
    writeByte((x >>> 24) & 0xff)
    writeByte((x >>> 16) & 0xff)
    writeByte((x >>> 8) & 0xff)
    writeByte((x >>> 0) & 0xff)
  }

  def write(x: Int, r: Int): Unit = {
    if (r == 32) {
      write(x)
      return
    }

    if (r < 1 || r > 32) throw new IllegalArgumentException(s"Illegal value for r = $r")
    if (x >= (1 << r)) throw new IllegalArgumentException(s"Illegal $r-bit char = $x")
    for (i <- 0 until r) {
      val bit = ((x >>> (r - i - 1)) & 1) == 1
      writeBit(bit)
    }
  }

  def write(x: Double): Unit = {
    write(java.lang.Double.doubleToRawLongBits(x))
  }

  def write(x: Long): Unit = {
    writeByte(((x >>> 56) & 0xff).toInt)
    writeByte(((x >>> 48) & 0xff).toInt)
    writeByte(((x >>> 40) & 0xff).toInt)
    writeByte(((x >>> 32) & 0xff).toInt)
    writeByte(((x >>> 24) & 0xff).toInt)
    writeByte(((x >>> 16) & 0xff).toInt)
    writeByte(((x >>> 8) & 0xff).toInt)
    writeByte(((x >>> 0) & 0xff).toInt)
  }

  def write(x: Float): Unit = write(java.lang.Float.floatToRawIntBits(x))

  def write(x: Short): Unit = {
    write((x >>> 8) & 0xff)
    write((x >>> 0) & 0xff)
  }

  def write(x: Char): Unit = {
    if (x < 0 || x >= 256) throw new IllegalArgumentException(s"Illegal 8-bit char = $x")
    writeByte(x)
  }

  def write(x: Char, r: Int): Unit = {
    if (r == 8) {
      write(x)
      return
    }
    if (r < 1 || r > 16) throw new IllegalArgumentException(s"Illegal value for r = $r")
    if (x >= (1 << r)) throw new IllegalArgumentException(s"Illegal $r-bit char = $x")
    for (i <- 0 until r) {
      val bit = ((x >>> (r - i - 1)) & 1) == 1
      writeBit(bit)
    }
  }

  def write(s: String): Unit = s.foreach(write)

  def write(s: String, r: Int): Unit = s.foreach(c => write(c, r))

  private def clearBuffer(): Unit = {
    if (n == 0) return
    if (n > 0) buffer <<= (8 - n)
    try {
      out.write(buffer)
    } catch {
      case e: IOException => e.printStackTrace()
    }
    n = 0
    buffer = 0
  }
}

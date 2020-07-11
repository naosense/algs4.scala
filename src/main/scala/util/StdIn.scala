package util

import java.util.Scanner
import java.util.InputMismatchException
import java.util.NoSuchElementException

import scala.collection.mutable.ListBuffer

object StdIn {
  private val CHARSET_NAME = "UTF-8"
  private val WHITESPACE_PATTERN = "\\p{javaWhitespace}+".r.pattern
  private val EMPTY_PATTERN = "".r.pattern
  private val EVERYTHING_PATTERN = "\\A".r.pattern
  private var scanner: Scanner = _

  resync()

  def resync(): Unit = setScanner(new Scanner(new java.io.BufferedInputStream(System.in), CHARSET_NAME))

  def setScanner(scanner: Scanner): Unit = {
    StdIn.scanner = scanner
  }

  def isEmpty: Boolean = !scanner.hasNext

  def hasNextLine: Boolean = scanner.hasNextLine

  def hasNextChar: Boolean = {
    scanner.useDelimiter(EMPTY_PATTERN)
    val result = scanner.hasNext
    scanner.useDelimiter(WHITESPACE_PATTERN)
    result
  }

  def readLine(): String = {
    var line: String = null
    try {
      line = scanner.nextLine()
    } catch {
      case _: Exception =>
        line = null
    }
    line
  }

  def readChar(): Char = {
    try {
      scanner.useDelimiter(EMPTY_PATTERN)
      val ch = scanner.next()
      assert(ch.length == 1, "Internal (Std)In.readChar() error!"
        + " Please contact the authors.")
      scanner.useDelimiter(WHITESPACE_PATTERN)
      ch.charAt(0)
    } catch {
      case _: Exception =>
        throw new NoSuchElementException("attempts to read a 'char' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readAll(): String = {
    if (hasNextLine) return ""

    val result = scanner.useDelimiter(EVERYTHING_PATTERN).next()
    scanner.useDelimiter(WHITESPACE_PATTERN)
    result
  }

  def readString(): String = {
    try scanner.next
    catch {
      case _: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'String' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readInt(): Int = {
    try scanner.nextInt
    catch {
      case _: InputMismatchException =>
        val token = scanner.next
        throw new InputMismatchException("attempts to read an 'int' value from standard input, "
          + "but the next token is \"" + token + "\"")
      case _: NoSuchElementException =>
        throw new NoSuchElementException("attemps to read an 'int' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readDouble(): Double = {
    try scanner.nextDouble
    catch {
      case e: InputMismatchException =>
        val token = scanner.next
        throw new InputMismatchException("attempts to read a 'double' value from standard input, "
          + "but the next token is \"" + token + "\"")
      case e: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'double' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readFloat(): Float = {
    try scanner.nextFloat
    catch {
      case e: InputMismatchException =>
        val token = scanner.next
        throw new InputMismatchException("attempts to read a 'float' value from standard input, "
          + "but the next token is \"" + token + "\"")
      case e: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'float' value from standard input, "
          + "but there no more tokens are available")
    }
  }

  def readLong(): Long = {
    try scanner.nextLong
    catch {
      case e: InputMismatchException =>
        val token = scanner.next
        throw new InputMismatchException("attempts to read a 'long' value from standard input, "
          + "but the next token is \"" + token + "\"")
      case e: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'long' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readShort(): Short = {
    try scanner.nextShort
    catch {
      case e: InputMismatchException =>
        val token = scanner.next
        throw new InputMismatchException("attempts to read a 'short' value from standard input, "
          + "but the next token is \"" + token + "\"")
      case e: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'short' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readByte(): Byte = {
    try scanner.nextByte
    catch {
      case e: InputMismatchException =>
        val token = scanner.next
        throw new InputMismatchException("attempts to read a 'byte' value from standard input, "
          + "but the next token is \"" + token + "\"")
      case e: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'byte' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readBoolean(): Boolean = {
    try {
      val token = readString()
      if ("true".equalsIgnoreCase(token)) return true
      if ("false".equalsIgnoreCase(token)) return false
      if ("1" == token) return true
      if ("0" == token) return false
      throw new InputMismatchException("attempts to read a 'boolean' value from standard input, "
        + "but the next token is \"" + token + "\"")
    } catch {
      case e: NoSuchElementException =>
        throw new NoSuchElementException("attempts to read a 'boolean' value from standard input, "
          + "but no more tokens are available")
    }
  }

  def readAllStrings(): Array[String] = {
    // we could use readAll.trim().split(), but that's not consistent
    // because trim() uses characters 0x00..0x20 as whitespace
    val tokens = WHITESPACE_PATTERN.split(readAll())
    if (tokens.isEmpty || tokens(0).length > 0) return tokens

    // don't include first token if it is leading whitespace
    val decapitokens = new Array[String](tokens.length - 1)
    for (i <- 0 until tokens.length - 1) { decapitokens(i) = tokens(i + 1) }
    decapitokens
  }

  def readAllLines(): Array[String] = {
    val lines = new ListBuffer()
    while (hasNextLine) lines + readLine
    lines.toArray
  }

  def readAllInts(): Array[Int] = {
    val fields = readAllStrings()
    val vals = new Array[Int](fields.length)
    for (i <- fields.indices) { vals(i) = fields(i).toInt }
    vals
  }

  def readAllLongs(): Array[Long] = {
    val fields = readAllStrings()
    val vals = new Array[Long](fields.length)
    for (i <- fields.indices) { vals(i) = fields(i).toLong }
    vals
  }

  def readAllDoubles(): Array[Double] = {
    val fields = readAllStrings()
    val vals = new Array[Double](fields.length)
    for (i <- fields.indices) { vals(i) = fields(i).toDouble }
    vals
  }
}

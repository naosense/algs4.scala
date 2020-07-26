package util

import java.awt.event.{ActionEvent, ActionListener, KeyEvent, KeyListener, MouseEvent, MouseListener, MouseMotionListener}
import java.awt.geom.{Arc2D, Ellipse2D, Line2D, Point2D, Rectangle2D}
import java.awt.image.{BufferedImage, DirectColorModel}
import java.awt.{BasicStroke, Color, Component, FileDialog, Font, Graphics, Graphics2D, Image, MediaTracker, RenderingHints, Toolkit}
import java.io.{File, IOException}
import java.net.{MalformedURLException, URL}
import java.util

import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel, JMenu, JMenuBar, JMenuItem, KeyStroke}

object StdDraw extends ActionListener with MouseListener with MouseMotionListener with KeyListener {

  /**
   * The color black.
   */
  val BLACK: Color = Color.BLACK

  /**
   * The color blue.
   */
  val BLUE: Color = Color.BLUE

  /**
   * The color cyan.
   */
  val CYAN: Color = Color.CYAN

  /**
   * The color dark gray.
   */
  val DARK_GRAY: Color = Color.DARK_GRAY

  /**
   * The color gray.
   */
  val GRAY: Color = Color.GRAY

  /**
   * The color green.
   */
  val GREEN: Color = Color.GREEN

  /**
   * The color light gray.
   */
  val LIGHT_GRAY: Color = Color.LIGHT_GRAY

  /**
   * The color magenta.
   */
  val MAGENTA: Color = Color.MAGENTA

  /**
   * The color orange.
   */
  val ORANGE: Color = Color.ORANGE

  /**
   * The color pink.
   */
  val PINK: Color = Color.PINK

  /**
   * The color red.
   */
  val RED: Color = Color.RED

  /**
   * The color white.
   */
  val WHITE: Color = Color.WHITE

  /**
   * The color yellow.
   */
  val YELLOW: Color = Color.YELLOW

  /**
   * Shade of blue used in <em>Introduction to Programming in Java</em>.
   * It is Pantone 300U. The RGB values are approximately (9, 90, 166).
   */
  val BOOK_BLUE: Color = new Color(9, 90, 166)

  /**
   * Shade of light blue used in <em>Introduction to Programming in Java</em>.
   * The RGB values are approximately (103, 198, 243).
   */
  val BOOK_LIGHT_BLUE: Color = new Color(103, 198, 243)

  /**
   * Shade of red used in <em>Algorithms, 4th edition</em>.
   * It is Pantone 1805U. The RGB values are approximately (150, 35, 31).
   */
  val BOOK_RED: Color = new Color(150, 35, 31)

  /**
   * Shade of orange used in Princeton University's identity.
   * It is PMS 158. The RGB values are approximately (245, 128, 37).
   */
  val PRINCETON_ORANGE: Color = new Color(245, 128, 37)

  // default colors
  private val DEFAULT_PEN_COLOR = BLACK
  private val DEFAULT_CLEAR_COLOR = WHITE

  // current pen color
  private var penColor: Color = _

  // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
  private val DEFAULT_SIZE = 512
  private var width = DEFAULT_SIZE
  private var height = DEFAULT_SIZE

  // default pen radius
  private val DEFAULT_PEN_RADIUS = 0.002

  // current pen radius
  private var penRadius = .0

  // show we draw immediately or wait until next show?
  private var defer = false

  // boundary of drawing canvas, 0% border
  // private static final double BORDER = 0.05;
  private val BORDER = 0.00
  private val DEFAULT_XMIN = 0.0
  private val DEFAULT_XMAX = 1.0
  private val DEFAULT_YMIN = 0.0
  private val DEFAULT_YMAX = 1.0
  private var xmin = .0
  private var ymin = .0
  private var xmax = .0
  private var ymax = .0

  // for synchronization
  private val mouseLock = new Object
  private val keyLock = new Object

  // default font
  private val DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16)

  // current font
  private var font: Font = _

  // double buffered graphics
  private var offscreenImage: BufferedImage = _
  private var onscreenImage: BufferedImage = _
  private var offscreen: Graphics2D = _
  private var onscreen: Graphics2D = _

  // singleton for callbacks: avoids generation of extra .class files
  private val std = StdDraw

  // the frame for drawing to the screen
  private var frame: JFrame = _

  // mouse state
  private var isMousePress = false
  private var mX = 0.0
  private var mY = 0.0

  // queue of typed key characters
  private val keysTyped = new util.LinkedList[Character]

  // set of key codes currently pressed down
  private val keysDown = new util.TreeSet[Integer]

  init()

  def setCanvasSize(): Unit = {
    setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE)
  }

  def setCanvasSize(canvasWidth: Int, canvasHeight: Int): Unit = {
    if (canvasWidth < 0) throw new IllegalArgumentException("width must be positive")
    if (canvasHeight < 0) throw new IllegalArgumentException("height must be positive")
    width = canvasWidth
    height = canvasHeight
    init()
  }

  private def init(): Unit = {
    if (frame != null) frame.setVisible(false)

    frame = new JFrame()
    offscreenImage = new BufferedImage(2 * width, 2 * height, BufferedImage.TYPE_INT_ARGB)
    onscreenImage = new BufferedImage(2 * width, 2 * height, BufferedImage.TYPE_INT_ARGB)
    offscreen = offscreenImage.createGraphics()
    onscreen = onscreenImage.createGraphics()
    offscreen.scale(2.0, 2.0)

    setXscale()
    setYscale()
    offscreen.setColor(DEFAULT_CLEAR_COLOR)
    offscreen.fillRect(0, 0, width, height)
    setPenColor()
    setPenRadius()
    setFont()
    clear()

    val hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    offscreen.addRenderingHints(hints)

    val icon = new StdDraw.RetinaImageIcon(onscreenImage)
    val draw = new JLabel(icon)

    draw.addMouseListener(std)
    draw.addMouseMotionListener(std)

    frame.setContentPane(draw)
    frame.addKeyListener(std) // JLabel cannot get keyboard focus

    frame.setFocusTraversalKeysEnabled(false) // allow VK_TAB with isKeyPressed()

    frame.setResizable(false)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) // closes all windows

    // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
    frame.setTitle("Standard Draw")
    frame.setJMenuBar(createMenuBar())
    frame.pack()
    frame.requestFocusInWindow
    frame.setVisible(true)
  }

  private def createMenuBar(): JMenuBar = {
    val menuBar = new JMenuBar
    val menu = new JMenu("file")
    menuBar.add(menu)
    val item = new JMenuItem(" Save...")
    item.addActionListener(std)
    // Java 10+: replace getMenuShortcutKeyMask() with getMenuShortcutKeyMaskEx()
    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit.getMenuShortcutKeyMask))
    menu.add(item)
    menuBar
  }

  private def validate(x: Double, name: String): Unit = {
    if (x.isNaN) throw new IllegalArgumentException(s"$name is NaN")
    if (x.isInfinite) throw new IllegalArgumentException("$name is infinite")
  }

  private def validateNonnegative(x: Double, name: String): Unit = {
    if (x < 0) throw new IllegalArgumentException(s"$name is negative")
  }

  private def validateNotNull(x: Any, name: String): Unit = {
    if (x == null) throw new IllegalArgumentException(s"$name is null")
  }

  def setScale(): Unit = {
    setXscale()
    setYscale()
  }

  def setScale(min: Double, max: Double): Unit = {
    setXscale(min, max)
    setYscale(min, max)
  }

  def setXscale(): Unit = {
    setXscale(DEFAULT_XMIN, DEFAULT_XMAX)
  }

  def setYscale(): Unit = {
    setYscale(DEFAULT_YMIN, DEFAULT_YMAX)
  }

  private def setXscale(min: Double, max: Double): Unit = {
    validate(min, "min")
    validate(max, "max")
    val size = max - min
    if (size == 0) throw new IllegalArgumentException("min and max are same")
    mouseLock.synchronized {
      xmin = min - BORDER * size
      xmax = max + BORDER * size
    }
  }

  private def setYscale(min: Double, max: Double): Unit = {
    validate(min, "min")
    validate(max, "max")
    val size = max - min
    if (size == 0) throw new IllegalArgumentException("min and max are same")
    mouseLock.synchronized {
      ymin = min - BORDER * size
      ymax = max + BORDER * size
    }
  }

  // helper functions that scale from user coordinates to screen coordinates and back
  private def scaleX(x: Double) = width * (x - xmin) / (xmax - xmin)

  private def scaleY(y: Double) = height * (ymax - y) / (ymax - ymin)

  private def factorX(w: Double) = w * width / Math.abs(xmax - xmin)

  private def factorY(h: Double) = h * height / Math.abs(ymax - ymin)

  private def userX(x: Double) = xmin + x * (xmax - xmin) / width

  private def userY(y: Double) = ymax - y * (ymax - ymin) / height

  def clear(): Unit = {
    clear(DEFAULT_CLEAR_COLOR)
  }

  def clear(color: Color): Unit = {
    validateNotNull(color, "color")
    offscreen.setColor(color)
    offscreen.fillRect(0, 0, width, height)
    offscreen.setColor(penColor)
    draw()
  }

  def getPenRadius: Double = penRadius

  def setPenRadius(): Unit = {
    setPenRadius(DEFAULT_PEN_RADIUS)
  }

  def setPenRadius(radius: Double): Unit = {
    validate(radius, "pen radius")
    validateNonnegative(radius, "pen radius")

    penRadius = radius
    val scaledPenRadius = (radius * DEFAULT_SIZE).toFloat
    val stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    offscreen.setStroke(stroke)
  }

  def getPenColor: Color = penColor

  def setPenColor(): Unit = {
    setPenColor(DEFAULT_PEN_COLOR)
  }

  def setPenColor(color: Color): Unit = {
    validateNotNull(color, "color")
    penColor = color
    offscreen.setColor(color)
  }

  def setPenColor(red: Int, green: Int, blue: Int): Unit = {
    if (red < 0 || red >= 256) throw new IllegalArgumentException("red must be between 0 - 255")
    if (green < 0 || green >= 256) throw new IllegalArgumentException("green must be between 0 - 255")
    if (blue < 0 || blue >= 256) throw new IllegalArgumentException("blue must be between 0 - 255")
    setPenColor(new Color(red, green, blue))
  }

  def getFont: Font = font

  def setFont(): Unit = {
    setFont(DEFAULT_FONT)
  }

  def setFont(font: Font): Unit = {
    validateNotNull(font, "font")
    StdDraw.font = font
  }

  def line(x0: Double, y0: Double, x1: Double, y1: Double): Unit = {
    validate(x0, "x0")
    validate(y0, "y0")
    validate(x1, "x1")
    validate(y1, "y1")
    offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)))
    draw()
  }

  private def pixel(x: Double, y: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    offscreen.fillRect(Math.round(scaleX(x)).toInt, Math.round(scaleY(y)).toInt, 1, 1)
  }

  def point(x: Int, y: Int): Unit = {
    validate(x, "x")
    validate(y, "y")

    val xs = scaleX(x)
    val ys = scaleX(y)
    val r = penRadius
    val scaledPenRadius = (r * DEFAULT_SIZE).toFloat

    if (scaledPenRadius < 1) pixel(x, y)
    else offscreen.fill(new Ellipse2D.Double(
      xs - scaledPenRadius / 2,
      ys - scaledPenRadius / 2,
      scaledPenRadius,
      scaledPenRadius
    ))
    draw()
  }

  def circle(x: Double, y: Double, radius: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(radius, "radius")
    validateNonnegative(radius, "radius")

    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * radius)
    val hs = factorY(2 * radius)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.draw(new Ellipse2D.Double(
      xs - ws / 2,
      ys - hs / 2,
      ws,
      hs
    ))
    draw()
  }

  def fillCircle(x: Double, y: Double, radius: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(radius, "radius")
    validateNonnegative(radius, "radius")

    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * radius)
    val hs = factorY(2 * radius)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.fill(new Ellipse2D.Double(
      xs - ws / 2,
      ys - hs / 2,
      ws,
      hs
    ))
    draw()
  }

  def ellipse(x: Double, y: Double, semiMajorAxis: Double, semiMinorAxis: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(semiMajorAxis, "semimajor axis")
    validate(semiMinorAxis, "semiminor axis")
    validateNonnegative(semiMajorAxis, "semimajor axis")
    validateNonnegative(semiMinorAxis, "semiminor axis")

    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * semiMajorAxis)
    val hs = factorY(2 * semiMinorAxis)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.draw(new Ellipse2D.Double(xs - ws / 2, ys - hs / 2, ws, hs))
    draw()
  }

  def filledEllipse(x: Double, y: Double, semiMajorAxis: Double, semiMinorAxis: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(semiMajorAxis, "semimajor axis")
    validate(semiMinorAxis, "semiminor axis")
    validateNonnegative(semiMajorAxis, "semimajor axis")
    validateNonnegative(semiMinorAxis, "semiminor axis")
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * semiMajorAxis)
    val hs = factorY(2 * semiMinorAxis)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.fill(new Ellipse2D.Double(xs - ws / 2, ys - hs / 2, ws, hs))
    draw()
  }

  def arc(x: Double, y: Double, radius: Double, angle1: Double, angle2: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(radius, "arc radius")
    validate(angle1, "angle1")
    validate(angle2, "angle2")
    validateNonnegative(radius, "arc radius")
    var a2 = angle2
    while (a2 < angle1) a2 += 360
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * radius)
    val hs = factorY(2 * radius)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.draw(new Arc2D.Double(xs - ws / 2, ys - hs / 2, ws, hs, angle1, a2 - angle1, Arc2D.OPEN))
    draw()
  }

  def square(x: Double, y: Double, halfLength: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(halfLength, "halfLength")
    validateNonnegative(halfLength, "half length")
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * halfLength)
    val hs = factorY(2 * halfLength)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.draw(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs))
    draw()
  }

  def filledSquare(x: Double, y: Double, halfLength: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(halfLength, "halfLength")
    validateNonnegative(halfLength, "half length")
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * halfLength)
    val hs = factorY(2 * halfLength)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs))
    draw()
  }

  def rectangle(x: Double, y: Double, halfWidth: Double, halfHeight: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(halfWidth, "halfWidth")
    validate(halfHeight, "halfHeight")
    validateNonnegative(halfWidth, "half width")
    validateNonnegative(halfHeight, "half height")
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * halfWidth)
    val hs = factorY(2 * halfHeight)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.draw(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs))
    draw()
  }

  import java.awt.geom.Rectangle2D

  def filledRectangle(x: Double, y: Double, halfWidth: Double, halfHeight: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(halfWidth, "halfWidth")
    validate(halfHeight, "halfHeight")
    validateNonnegative(halfWidth, "half width")
    validateNonnegative(halfHeight, "half height")
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(2 * halfWidth)
    val hs = factorY(2 * halfHeight)
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs))
    draw()
  }

  import java.awt.geom.GeneralPath

  def polygon(x: Array[Double], y: Array[Double]): Unit = {
    validateNotNull(x, "x-coordinate array")
    validateNotNull(y, "y-coordinate array")
    for (i <- x.indices) { validate(x(i), "x[" + i + "]") }
    for (i <- y.indices) { validate(y(i), "y[" + i + "]") }
    val n1 = x.length
    val n2 = y.length
    if (n1 != n2) throw new IllegalArgumentException("arrays must be of the same length")
    val n = n1
    if (n == 0) return
    val path = new GeneralPath
    path.moveTo(scaleX(x(0)).toFloat, scaleY(y(0)).toFloat)
    for (i <- 0 until n) { path.lineTo(scaleX(x(i)).toFloat, scaleY(y(i)).toFloat) }
    path.closePath()
    offscreen.draw(path)
    draw()
  }

  def filledPolygon(x: Array[Double], y: Array[Double]): Unit = {
    validateNotNull(x, "x-coordinate array")
    validateNotNull(y, "y-coordinate array")
    for (i <- x.indices) { validate(x(i), "x[" + i + "]") }
    for (i <- y.indices) { validate(y(i), "y[" + i + "]") }
    val n1 = x.length
    val n2 = y.length
    if (n1 != n2) throw new IllegalArgumentException("arrays must be of the same length")
    val n = n1
    if (n == 0) return
    val path = new GeneralPath
    path.moveTo(scaleX(x(0)).toFloat, scaleY(y(0)).toFloat)
    for (i <- 0 until n) { path.lineTo(scaleX(x(i)).toFloat, scaleY(y(i)).toFloat) }
    path.closePath()
    offscreen.fill(path)
    draw()
  }

  // get an image from the given filename// get an image from the given filename
  private def getImage(filename: String) = {
    if (filename == null) throw new IllegalArgumentException
    // to read from file
    var icon = new ImageIcon(filename)
    // try to read from URL
    if ((icon == null) || (icon.getImageLoadStatus != MediaTracker.COMPLETE)) try {
      val url = new URL(filename)
      icon = new ImageIcon(url)
    } catch {
      case e: MalformedURLException =>

      /* not a url */
    }
    // in case file is inside a .jar (classpath relative to StdDraw)
    if ((icon == null) || (icon.getImageLoadStatus != MediaTracker.COMPLETE)) {
      val url = this.getClass.getResource(filename)
      if (url != null) icon = new ImageIcon(url)
    }
    // in case file is inside a .jar (classpath relative to root of jar)
    if ((icon == null) || (icon.getImageLoadStatus != MediaTracker.COMPLETE)) {
      val url = this.getClass.getResource("/" + filename)
      if (url == null) throw new IllegalArgumentException("image " + filename + " not found")
      icon = new ImageIcon(url)
    }
    icon.getImage
  }

  def picture(x: Double, y: Double, filename: String): Unit = {
    validate(x, "x")
    validate(y, "y")
    validateNotNull(filename, "filename")
    // BufferedImage image = getImage(filename);
    val image = getImage(filename)
    val xs = scaleX(x)
    val ys = scaleY(y)
    // int ws = image.getWidth();    // can call only if image is a BufferedImage
    // int hs = image.getHeight();
    val ws = image.getWidth(null)
    val hs = image.getHeight(null)
    if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt")
    offscreen.drawImage(image, (xs - ws / 2.0).round.toInt, (ys - hs / 2.0).round.toInt, null)
    draw()
  }

  def picture(x: Double, y: Double, filename: String, degrees: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(degrees, "degrees")
    validateNotNull(filename, "filename")
    // BufferedImage image = getImage(filename);
    val image = getImage(filename)
    val xs = scaleX(x)
    val ys = scaleY(y)
    // int ws = image.getWidth();    // can call only if image is a BufferedImage
    // int hs = image.getHeight();
    val ws = image.getWidth(null)
    val hs = image.getHeight(null)
    if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt")
    offscreen.rotate(Math.toRadians(-degrees), xs, ys)
    offscreen.drawImage(image, (xs - ws / 2.0).round.toInt, (ys - hs / 2.0).round.toInt, null)
    offscreen.rotate(Math.toRadians(+degrees), xs, ys)
    draw()
  }

  def picture(x: Double, y: Double, filename: String, scaledWidth: Double, scaledHeight: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(scaledWidth, "scaled width")
    validate(scaledHeight, "scaled height")
    validateNotNull(filename, "filename")
    validateNonnegative(scaledWidth, "scaled width")
    validateNonnegative(scaledHeight, "scaled height")
    val image = getImage(filename)
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(scaledWidth)
    val hs = factorY(scaledHeight)
    if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt")
    if (ws <= 1 && hs <= 1) pixel(x, y)
    else offscreen.drawImage(image, (xs - ws / 2.0).round.toInt, (ys - hs / 2.0).round.toInt, ws.round.toInt, hs.round.toInt, null)
    draw()
  }

  def picture(x: Double, y: Double, filename: String, scaledWidth: Double, scaledHeight: Double, degrees: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(scaledWidth, "scaled width")
    validate(scaledHeight, "scaled height")
    validate(degrees, "degrees")
    validateNotNull(filename, "filename")
    validateNonnegative(scaledWidth, "scaled width")
    validateNonnegative(scaledHeight, "scaled height")
    val image = getImage(filename)
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = factorX(scaledWidth)
    val hs = factorY(scaledHeight)
    if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt")
    if (ws <= 1 && hs <= 1) pixel(x, y)
    offscreen.rotate(Math.toRadians(-degrees), xs, ys)
    offscreen.drawImage(image, (xs - ws / 2.0).round.toInt, (ys - hs / 2.0).round.toInt, ws.round.toInt, hs.round.toInt, null)
    offscreen.rotate(Math.toRadians(+degrees), xs, ys)
    draw()
  }

  def text(x: Double, y: Double, text: String): Unit = {
    validate(x, "x")
    validate(y, "y")
    validateNotNull(text, "text")
    offscreen.setFont(font)
    val metrics = offscreen.getFontMetrics
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = metrics.stringWidth(text)
    val hs = metrics.getDescent
    offscreen.drawString(text, (xs - ws / 2.0).toFloat, (ys + hs).toFloat)
    draw()
  }

  def text(x: Double, y: Double, t: String, degrees: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    validate(degrees, "degrees")
    validateNotNull(t, "text")
    val xs = scaleX(x)
    val ys = scaleY(y)
    offscreen.rotate(Math.toRadians(-degrees), xs, ys)
    text(x, y, t)
    offscreen.rotate(Math.toRadians(+degrees), xs, ys)
  }

  def textLeft(x: Double, y: Double, text: String): Unit = {
    validate(x, "x")
    validate(y, "y")
    validateNotNull(text, "text")
    offscreen.setFont(font)
    val metrics = offscreen.getFontMetrics
    val xs = scaleX(x)
    val ys = scaleY(y)
    val hs = metrics.getDescent
    offscreen.drawString(text, xs.toFloat, (ys + hs).toFloat)
    draw()
  }

  def textRight(x: Double, y: Double, text: String): Unit = {
    validate(x, "x")
    validate(y, "y")
    validateNotNull(text, "text")
    offscreen.setFont(font)
    val metrics = offscreen.getFontMetrics
    val xs = scaleX(x)
    val ys = scaleY(y)
    val ws = metrics.stringWidth(text)
    val hs = metrics.getDescent
    offscreen.drawString(text, (xs - ws).toFloat, (ys + hs).toFloat)
    draw()
  }

  /**
   * Pauses for t milliseconds. This method is intended to support computer animations.
   *
   * @param t number of milliseconds
   */
  def pause(t: Int): Unit = {
    validateNonnegative(t, "t")
    try Thread.sleep(t)
    catch {
      case e: InterruptedException =>
        println("Error sleeping")
    }
  }

  def show(): Unit = {
    onscreen.drawImage(offscreenImage, 0, 0, null)
    frame.repaint()
  }

  private def draw(): Unit = {
    if (!defer) show()
  }

  /**
   * Enables double buffering. All subsequent calls to
   * drawing methods such as `line()`, `circle()`,
   * and `square()` will be deferred until the next call
   * to show(). Useful for animations.
   */
  def enableDoubleBuffering(): Unit = {
    defer = true
  }

  /**
   * Disables double buffering. All subsequent calls to
   * drawing methods such as {@code line()}, {@code circle()},
   * and {@code square()} will be displayed on screen when called.
   * This is the default.
   */
  def disableDoubleBuffering(): Unit = {
    defer = false
  }

  /**
   * Saves the drawing to using the specified filename.
   * The supported image formats are JPEG and PNG;
   * the filename suffix must be {@code .jpg} or {@code .png}.
   *
   * @param  filename the name of the file with one of the required suffixes
   * @throws IllegalArgumentException if { @code filename} is { @code null}
   */
  def save(filename: String): Unit = {
    validateNotNull(filename, "filename")
    val file = new File(filename)
    val suffix = filename.substring(filename.lastIndexOf('.') + 1)
    // png files
    if ("png".equalsIgnoreCase(suffix)) {
      try ImageIO.write(onscreenImage, suffix, file)
      catch {
        case e: IOException =>
          e.printStackTrace()
      }
    } else {
      // need to change from ARGB to RGB for JPEG
      // reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
      if ("jpg".equalsIgnoreCase(suffix)) {
        val raster = onscreenImage.getRaster
        val newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, Array[Int](0, 1, 2))
        val cm = onscreenImage.getColorModel.asInstanceOf[DirectColorModel]
        val newCM = new DirectColorModel(cm.getPixelSize, cm.getRedMask, cm.getGreenMask, cm.getBlueMask)
        val rgbBuffer = new BufferedImage(newCM, newRaster, false, null)
        try ImageIO.write(rgbBuffer, suffix, file)
        catch {
          case e: IOException =>
            e.printStackTrace()
        }
      }
      else System.out.println("Invalid image file type: " + suffix)
    }
  }

  /**
   * This method cannot be called directly.
   */
  override def actionPerformed(e: ActionEvent): Unit = {
    val chooser = new FileDialog(StdDraw.frame, "Use a .png or .jpg extension", FileDialog.SAVE)
    chooser.setVisible(true)
    val filename = chooser.getFile
    if (filename != null) StdDraw.save(chooser.getDirectory + File.separator + chooser.getFile)
  }

  /**
   * Returns true if the mouse is being pressed.
   *
   * @return { @code true} if the mouse is being pressed; { @code false} otherwise
   */
  def isMousePressed: Boolean = {
    mouseLock.synchronized {
      isMousePress
    }
  }

  /**
   * Returns the <em>x</em>-coordinate of the mouse.
   *
   * @return the <em>x</em>-coordinate of the mouse
   */
  def mouseX(): Double = {
    mouseLock synchronized {
      mX
    }
  }

  /**
   * Returns the <em>y</em>-coordinate of the mouse.
   *
   * @return <em>y</em>-coordinate of the mouse
   */
  def mouseY(): Double = {
    mouseLock synchronized {
      mY
    }
  }

  override def mouseClicked(e: MouseEvent): Unit = {
    // this body is intentionally left empty
  }

  override def mousePressed(e: MouseEvent): Unit = {
    mouseLock synchronized {
      mX = StdDraw.userX(e.getX)
      mY = StdDraw.userY(e.getY)
      isMousePress = true
    }
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    mouseLock synchronized {
      isMousePress = false
    }
  }

  override def mouseEntered(e: MouseEvent): Unit = {
    // this body is intentionally left empty
  }

  override def mouseExited(e: MouseEvent): Unit = {
    // this body is intentionally left empty
  }

  override def mouseDragged(e: MouseEvent): Unit = {
    mouseLock synchronized {
      mX = StdDraw.userX(e.getX)
      mY = StdDraw.userY(e.getY)
    }
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    mouseLock synchronized {
      mX = StdDraw.userX(e.getX)
      mY = StdDraw.userY(e.getY)
    }
  }

  /**
   * Returns true if the user has typed a key (that has not yet been processed).
   *
   * @return { @code true} if the user has typed a key (that has not yet been processed
   *         by { @link #nextKeyTyped()}; { @code false} otherwise
   */
  def hasNextKeyTyped: Boolean = {
    keyLock synchronized
      !keysTyped.isEmpty
  }

  def nextKeyTyped: Char = {
    keyLock synchronized {
      if (keysTyped.isEmpty) throw new NoSuchElementException("your program has already processed all keystrokes")
      keysTyped.remove(keysTyped.size - 1)
      // return keysTyped.removeLast();
    }
  }

  def isKeyPressed(keycode: Int): Boolean = {
    keyLock.synchronized {
      keysDown.contains(keycode)
    }
  }

  override def keyTyped(e: KeyEvent): Unit = {
    keyLock.synchronized {
      keysTyped.addFirst(e.getKeyChar)
    }
  }

  override def keyPressed(e: KeyEvent): Unit = {
    keyLock.synchronized {
      keysDown.add(e.getKeyCode)
    }
  }

  override def keyReleased(e: KeyEvent): Unit = {
    keyLock synchronized {
      keysDown.remove(e.getKeyCode)
    }
  }

  private class RetinaImageIcon(image: Image) extends ImageIcon(image) {
    override def getIconWidth: Int = super.getIconWidth / 2

    /**
     * Gets the height of the icon.
     *
     * @return the height in pixels of this icon
     */
    override def getIconHeight: Int = super.getIconHeight / 2

    override def paintIcon(c: Component, g: Graphics, x: Int, y: Int): Unit = {
      val g2 = g.create.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.scale(0.5, 0.5)
      super.paintIcon(c, g2, x * 2, y * 2)
      g2.dispose()
    }
  }

  def main(args: Array[String]): Unit = {
    StdDraw.square(0.2, 0.8, 0.1)
    StdDraw.filledSquare(0.8, 0.8, 0.2)
    StdDraw.circle(0.8, 0.2, 0.2)

    StdDraw.setPenColor(StdDraw.BOOK_RED)
    StdDraw.setPenRadius(0.02)
    StdDraw.arc(0.8, 0.2, 0.1, 200, 45)

    // draw a blue diamond
    StdDraw.setPenRadius()
    StdDraw.setPenColor(StdDraw.BOOK_BLUE)
    val x = Array(0.1, 0.2, 0.3, 0.2)
    val y = Array(0.2, 0.3, 0.2, 0.1)
    StdDraw.filledPolygon(x, y)

    // text
    StdDraw.setPenColor(StdDraw.BLACK)
    StdDraw.text(0.2, 0.5, "black text")
    StdDraw.setPenColor(StdDraw.WHITE)
    StdDraw.text(0.8, 0.8, "white text")
  }
}

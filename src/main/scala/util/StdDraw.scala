package util

import java.awt.event.{ActionEvent, ActionListener, KeyEvent, KeyListener, MouseEvent, MouseListener, MouseMotionListener}
import java.awt.geom.{Arc2D, Ellipse2D, GeneralPath, Line2D, Point2D, Rectangle2D}
import java.awt.image.{BufferedImage, DirectColorModel}
import java.awt.{BasicStroke, Color, Component, FileDialog, Font, Graphics, Graphics2D, Image, MediaTracker, RenderingHints, Toolkit}
import java.io.{File, IOException}
import java.net.{MalformedURLException, URL}
import java.util

import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel, JMenu, JMenuBar, JMenuItem, KeyStroke}

/**
 * The `StdDraw` class provides a basic capability for
 * creating drawings with your programs. It uses a simple graphics model that
 * allows you to create drawings consisting of points, lines, squares,
 * circles, and other geometric shapes in a window on your computer and
 * to save the drawings to a file. Standard drawing also includes
 * facilities for text, color, pictures, and animation, along with
 * user interaction via the keyboard and mouse.
 * <p>
 * <b>Getting started.</b>
 * To use this class, you must have `StdDraw.class` in your
 * Java classpath. If you used our autoinstaller, you should be all set.
 * Otherwise, either download
 * <a href = "https://introcs.cs.princeton.edu/java/code/stdlib.jar">stdlib.jar</a>
 * and add to your Java classpath or download
 * <a href = "https://introcs.cs.princeton.edu/java/stdlib/StdDraw.java">StdDraw.java</a>
 * and put a copy in your working directory.
 * <p>
 * Now, type the following short program into your editor:
 * {{{
 * public class TestStdDraw {
 *  public static void main(String[] args) {
 *             StdDraw.setPenRadius(0.05);
 *             StdDraw.setPenColor(StdDraw.BLUE);
 *             StdDraw.point(0.5, 0.5);
 *             StdDraw.setPenColor(StdDraw.MAGENTA);
 *             StdDraw.line(0.2, 0.2, 0.8, 0.2);
 *   }
 * }
 * }}}
 *
 * If you compile and execute the program, you should see a window
 * appear with a thick magenta line and a blue point.
 * This program illustrates the two main types of methods in standard
 * drawing—methods that draw geometric shapes and methods that
 * control drawing parameters.
 * The methods `StdDraw.line()` and `StdDraw.point()`
 * draw lines and points; the methods `StdDraw.setPenRadius()`
 * and `StdDraw.setPenColor()` control the line thickness and color.
 * <p>
 * <b>Points and lines.</b>
 * You can draw points and line segments with the following methods:
 * <ul>
 * <li> [[StdDraw#point(double x, double y)]]
 * <li> [[StdDraw#line(double x1, double y1, double x2, double y2)]]
 * </ul>
 * <p>
 * The <em>x</em>- and <em>y</em>-coordinates must be in the drawing area
 * (between 0 and 1 and by default) or the points and lines will not be visible.
 * <p>
 * <b>Squares, circles, rectangles, and ellipses.</b>
 * You can draw squares, circles, rectangles, and ellipses using
 * the following methods:
 * <ul>
 * <li> [[StdDraw#circle(double x, double y, double radius)]]
 * <li> [[StdDraw#ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis)]]
 * <li> [[StdDraw#square(double x, double y, double halfLength)]]
 * <li> [[StdDraw#rectangle(double x, double y, double halfWidth, double halfHeight)]]
 * </ul>
 * <p>
 * All of these methods take as arguments the location and size of the shape.
 * The location is always specified by the <em>x</em>- and <em>y</em>-coordinates
 * of its <em>center</em>.
 * The size of a circle is specified by its radius and the size of an ellipse is
 * specified by the lengths of its semi-major and semi-minor axes.
 * The size of a square or rectangle is specified by its half-width or half-height.
 * The convention for drawing squares and rectangles is parallel to those for
 * drawing circles and ellipses, but may be unexpected to the uninitiated.
 * <p>
 * The methods above trace outlines of the given shapes. The following methods
 * draw filled versions:
 * <ul>
 * <li> [[StdDraw#filledCircle(double x, double y, double radius)]]
 * <li> [[StdDraw#filledEllipse(double x, double y, double semiMajorAxis, double semiMinorAxis)]]
 * <li> [[StdDraw#filledSquare(double x, double y, double radius)]]
 * <li> [[StdDraw#filledRectangle(double x, double y, double halfWidth, double halfHeight)]]
 * </ul>
 * <p>
 * <b>Circular arcs.</b>
 * You can draw circular arcs with the following method:
 * <ul>
 * <li> [[StdDraw#arc(double x, double y, double radius, double angle1, double angle2)]]
 * </ul>
 * <p>
 * The arc is from the circle centered at (<em>x</em>, <em>y</em>) of the specified radius.
 * The arc extends from angle1 to angle2. By convention, the angles are
 * <em>polar</em> (counterclockwise angle from the <em>x</em>-axis)
 * and represented in degrees. For example, `StdDraw.arc(0.0, 0.0, 1.0, 0, 90)`
 * draws the arc of the unit circle from 3 o'clock (0 degrees) to 12 o'clock (90 degrees).
 * <p>
 * <b>Polygons.</b>
 * You can draw polygons with the following methods:
 * <ul>
 * <li> [[StdDraw#polygon(double[] x, double[] y)]]
 * <li> [[StdDraw#filledPolygon(double[] x, double[] y)]]
 * </ul>
 * <p>
 * The points in the polygon are `x[i]`, `y[i]`).
 * For example, the following code fragment draws a filled diamond
 * with vertices (0.1, 0.2), (0.2, 0.3), (0.3, 0.2), and (0.2, 0.1):
 * {{{
 * double[] x = { 0.1, 0.2, 0.3, 0.2 };
 * double[] y = { 0.2, 0.3, 0.2, 0.1 };
 * StdDraw.filledPolygon(x, y);
 * }}}
 * <b>Pen size.</b>
 * The pen is circular, so that when you set the pen radius to <em>r</em>
 * and draw a point, you get a circle of radius <em>r</em>. Also, lines are
 * of thickness 2<em>r</em> and have rounded ends. The default pen radius
 * is 0.005 and is not affected by coordinate scaling. This default pen
 * radius is about 1/200 the width of the default canvas, so that if
 * you draw 100 points equally spaced along a horizontal or vertical line,
 * you will be able to see individual circles, but if you draw 200 such
 * points, the result will look like a line.
 * <ul>
 * <li> [[StdDraw#setPenRadius(double radius)]]
 * </ul>
 * <p>
 * For example, `StdDraw.setPenRadius(0.025)` makes
 * the thickness of the lines and the size of the points to be five times
 * the 0.005 default.
 * To draw points with the minimum possible radius (one pixel on typical
 * displays), set the pen radius to 0.0.
 * <p>
 * <b>Pen color.</b>
 * All geometric shapes (such as points, lines, and circles) are drawn using
 * the current pen color. By default, it is black.
 * You can change the pen color with the following methods:
 * <ul>
 * <li> [[StdDraw#setPenColor(int red, int green, int blue)]]
 * <li> [[StdDraw#setPenColor(Color color)]]
 * </ul>
 * <p>
 * The first method allows you to specify colors using the RGB color system.
 * This <a href = "http://johndyer.name/lab/colorpicker/">color picker</a>
 * is a convenient way to find a desired color.
 * The second method allows you to specify colors using the
 * `Color` data type that is discussed in Chapter 3. Until then,
 * you can use this method with one of these predefined colors in standard drawing:
 * [[StdDraw#BLACK]], [[StdDraw#BLUE]], [[StdDraw#CYAN]], [[StdDraw#DARK_GRAY]], [[StdDraw#GRAY]],
 * [[StdDraw#GREEN]], [[StdDraw#LIGHT_GRAY]], [[StdDraw#MAGENTA]], [[StdDraw#ORANGE]],
 * [[StdDraw#PINK]], [[StdDraw#RED]], [[StdDraw#WHITE]], [[StdDraw#YELLOW]],
 * [[StdDraw#BOOK_BLUE]], [[StdDraw#BOOK_LIGHT_BLUE]], [[StdDraw#BOOK_RED]], and
 * [[StdDraw#PRINCETON_ORANGE]].
 * For example, [[StdDraw.setPenColor(StdDraw.MAGENTA)]] sets the
 * pen color to magenta.
 * <p>
 * <b>Canvas size.</b>
 * By default, all drawing takes places in a 512-by-512 canvas.
 * The canvas does not include the window title or window border.
 * You can change the size of the canvas with the following method:
 * <ul>
 * <li> [[StdDraw#setCanvasSize(int width, int height)]]
 * </ul>
 * <p>
 * This sets the canvas size to be <em>width</em>-by-<em>height</em> pixels.
 * It also erases the current drawing and resets the coordinate system,
 * pen radius, pen color, and font back to their default values.
 * Ordinarly, this method is called once, at the very beginning of a program.
 * For example, `StdDraw.setCanvasSize(800, 800)`
 * sets the canvas size to be 800-by-800 pixels.
 * <p>
 * <b>Canvas scale and coordinate system.</b>
 * By default, all drawing takes places in the unit square, with (0, 0) at
 * lower left and (1, 1) at upper right. You can change the default
 * coordinate system with the following methods:
 * <ul>
 * <li> [[StdDraw#setXscale(double xmin, double xmax)]]
 * <li> [[StdDraw#setYscale(double ymin, double ymax)]]
 * <li> [[StdDraw#setScale(double min, double max)]]
 * </ul>
 * <p>
 * The arguments are the coordinates of the minimum and maximum
 * <em>x</em>- or <em>y</em>-coordinates that will appear in the canvas.
 * For example, if you  wish to use the default coordinate system but
 * leave a small margin, you can call `StdDraw.setScale(-.05, 1.05)`.
 * <p>
 * These methods change the coordinate system for subsequent drawing
 * commands; they do not affect previous drawings.
 * These methods do not change the canvas size; so, if the <em>x</em>-
 * and <em>y</em>-scales are different, squares will become rectangles
 * and circles will become ellipses.
 * <p>
 * <b>Text.</b>
 * You can use the following methods to annotate your drawings with text:
 * <ul>
 * <li> [[StdDraw#text(double x, double y, String text)]]
 * <li> [[StdDraw#text(double x, double y, String text, double degrees)]]
 * <li> [[StdDraw#textLeft(double x, double y, String text)]]
 * <li> [[StdDraw#textRight(double x, double y, String text)]]
 * </ul>
 * <p>
 * The first two methods write the specified text in the current font,
 * centered at (<em>x</em>, <em>y</em>).
 * The second method allows you to rotate the text.
 * The last two methods either left- or right-align the text at (<em>x</em>, <em>y</em>).
 * <p>
 * The default font is a Sans Serif font with point size 16.
 * You can use the following method to change the font:
 * <ul>
 * <li> [[StdDraw#setFont(Font font)]]
 * </ul>
 * <p>
 * You use the [[Font]] data type to specify the font. This allows you to
 * choose the face, size, and style of the font. For example, the following
 * code fragment sets the font to Arial Bold, 60 point.
 * <pre>
 * Font font = new Font("Arial", Font.BOLD, 60);
 *   StdDraw.setFont(font);
 *   StdDraw.text(0.5, 0.5, "Hello, World");
 * </pre>
 * <p>
 * <b>Images.</b>
 * You can use the following methods to add images to your drawings:
 * <ul>
 * <li> [[StdDraw#picture(double x, double y, String filename)]]
 * <li> [[StdDraw#picture(double x, double y, String filename, double degrees)]]
 * <li> [[StdDraw#picture(double x, double y, String filename, double scaledWidth, double scaledHeight)]]
 * <li> [[StdDraw#picture(double x, double y, String filename, double scaledWidth, double scaledHeight, double degrees)]]
 * </ul>
 * <p>
 * These methods draw the specified image, centered at (<em>x</em>, <em>y</em>).
 * The supported image formats are JPEG, PNG, and GIF.
 * The image will display at its native size, independent of the coordinate system.
 * Optionally, you can rotate the image a specified number of degrees counterclockwise
 * or rescale it to fit snugly inside a width-by-height bounding box.
 * <p>
 * <b>Saving to a file.</b>
 * You save your image to a file using the <em>File → Save</em> menu option.
 * You can also save a file programatically using the following method:
 * <ul>
 * <li> [[StdDraw#save(String filename)]]
 * </ul>
 * <p>
 * The supported image formats are JPEG and PNG. The filename must have either the
 * extension .jpg or .png.
 * We recommend using PNG for drawing that consist solely of geometric shapes and JPEG
 * for drawings that contains pictures.
 * <p>
 * <b>Clearing the canvas.</b>
 * To clear the entire drawing canvas, you can use the following methods:
 * <ul>
 * <li> [[StdDraw#clear()]]
 * <li> [[StdDraw#clear(Color color)]]
 * </ul>
 * <p>
 * The first method clears the canvas to white; the second method
 * allows you to specify a color of your choice. For example,
 * `StdDraw.clear(StdDraw.LIGHT_GRAY)` clears the canvas to a shade
 * of gray.
 * <p>
 * <b>Computer animations and double buffering.</b>
 * Double buffering is one of the most powerful features of standard drawing,
 * enabling computer animations.
 * The following methods control the way in which objects are drawn:
 * <ul>
 * <li> [[StdDraw#enableDoubleBuffering()]]
 * <li> [[StdDraw#disableDoubleBuffering()]]
 * <li> [[StdDraw#show()]]
 * <li> [[StdDraw#pause(int t)]]
 * </ul>
 * <p>
 * By default, double buffering is disabled, which means that as soon as you
 * call a drawing
 * method—such as `point()` or `line()`—the
 * results appear on the screen.
 * <p>
 * When double buffering is enabled by calling [[StdDraw#enableDoubleBuffering()]],
 * all drawing takes place on the <em>offscreen canvas</em>. The offscreen canvas
 * is not displayed. Only when you call
 * [[StdDraw#show()]] does your drawing get copied from the offscreen canvas to
 * the onscreen canvas, where it is displayed in the standard drawing window. You
 * can think of double buffering as collecting all of the lines, points, shapes,
 * and text that you tell it to draw, and then drawing them all
 * <em>simultaneously</em>, upon request.
 * <p>
 * The most important use of double buffering is to produce computer
 * animations, creating the illusion of motion by rapidly
 * displaying static drawings. To produce an animation, repeat
 * the following four steps:
 * <ul>
 * <li> Clear the offscreen canvas.
 * <li> Draw objects on the offscreen canvas.
 * <li> Copy the offscreen canvas to the onscreen canvas.
 * <li> Wait for a short while.
 * </ul>
 * <p>
 * The [[StdDraw#clear()]], [[StdDraw#show()]], and [[StdDraw#pause(int t)]] methods
 * support the first, third, and fourth of these steps, respectively.
 * <p>
 * For example, this code fragment animates two balls moving in a circle.
 * {{{
 *   StdDraw.setScale(-2, +2);
 *   StdDraw.enableDoubleBuffering();
 *
 *   for (double t = 0.0; true; t += 0.02) {
 *   double x = Math.sin(t);
 *   double y = Math.cos(t);
 *         StdDraw.clear();
 *         StdDraw.filledCircle(x, y, 0.05);
 *         StdDraw.filledCircle(-x, -y, 0.05);
 *         StdDraw.show();
 *         StdDraw.pause(20);
 *   }
 * }}}
 * <p>
 * <b>Keyboard and mouse inputs.</b>
 * Standard drawing has very basic support for keyboard and mouse input.
 * It is much less powerful than most user interface libraries provide, but also much simpler.
 * You can use the following methods to intercept mouse events:
 * <ul>
 * <li> [[StdDraw#isMousePressed()]]
 * <li> [[StdDraw#mouseX()]]
 * <li> [[StdDraw#mouseY()]]
 * </ul>
 * <p>
 * The first method tells you whether a mouse button is currently being pressed.
 * The last two methods tells you the <em>x</em>- and <em>y</em>-coordinates of the mouse's
 * current position, using the same coordinate system as the canvas (the unit square, by default).
 * You should use these methods in an animation loop that waits a short while before trying
 * to poll the mouse for its current state.
 * You can use the following methods to intercept keyboard events:
 * <ul>
 * <li> [[StdDraw#hasNextKeyTyped()]]
 * <li> [[StdDraw#nextKeyTyped()]]
 * <li> [[StdDraw#isKeyPressed(int keycode)]]
 * </ul>
 * <p>
 * If the user types lots of keys, they will be saved in a list until you process them.
 * The first method tells you whether the user has typed a key (that your program has
 * not yet processed).
 * The second method returns the next key that the user typed (that your program has
 * not yet processed) and removes it from the list of saved keystrokes.
 * The third method tells you whether a key is currently being pressed.
 * <p>
 * <b>Accessing control parameters.</b>
 * You can use the following methods to access the current pen color, pen radius,
 * and font:
 * <ul>
 * <li> [[StdDraw#getPenColor()]]
 * <li> [[StdDraw#getPenRadius()]]
 * <li> [[StdDraw#getFont()]]
 * </ul>
 * <p>
 * These methods are useful when you want to temporarily change a
 * control parameter and reset it back to its original value.
 * <p>
 * <b>Corner cases.</b>
 * Here are some corner cases.
 * <ul>
 * <li> Drawing an object outside (or partly outside) the canvas is permitted.
 * However, only the part of the object that appears inside the canvas
 * will be visible.
 * <li> Any method that is passed a `null` argument will throw an
 * [[IllegalArgumentException]].
 * <li> Any method that is passed a [[Double#NaN]],
 * [[Double#POSITIVE_INFINITY]], or [[Double#NEGATIVE_INFINITY]]
 * argument will throw an [[IllegalArgumentException]].
 * <li> Due to floating-point issues, an object drawn with an <em>x</em>- or
 * <em>y</em>-coordinate that is way outside the canvas (such as the line segment
 * from (0.5, –10^308) to (0.5, 10^308) may not be visible even in the
 * part of the canvas where it should be.
 * </ul>
 * <p>
 * <b>Performance tricks.</b>
 * Standard drawing is capable of drawing large amounts of data.
 * Here are a few tricks and tips:
 * <ul>
 * <li> Use <em>double buffering</em> for static drawing with a large
 * number of objects.
 * That is, call [[StdDraw#enableDoubleBuffering()]] before
 * the sequence of drawing commands and call [[StdDraw#show()]] afterwards.
 * Incrementally displaying a complex drawing while it is being
 * created can be intolerably inefficient on many computer systems.
 * <li> When drawing computer animations, call `show()`
 * only once per frame, not after drawing each individual object.
 * <li> If you call `picture()` multiple times with the same filename,
 * Java will cache the image, so you do not incur the cost of reading
 * from a file each time.
 * </ul>
 * <p>
 * <b>Known bugs and issues.</b>
 * <ul>
 * <li> The `picture()` methods may not draw the portion of the image that is
 * inside the canvas if the center point (<em>x</em>, <em>y</em>) is outside the
 * canvas.
 * This bug appears only on some systems.
 * </ul>
 * <p>
 * <b>Reference.</b>
 * For additional documentation,
 * see <a href="https://introcs.cs.princeton.edu/15inout">Section 1.5</a> of
 * <em>Computer Science: An Interdisciplinary Approach</em>
 * by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
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
  val BOOK_BLUE = new Color(9, 90, 166)

  /**
   * Shade of light blue used in <em>Introduction to Programming in Java</em>.
   * The RGB values are approximately (103, 198, 243).
   */
  val BOOK_LIGHT_BLUE = new Color(103, 198, 243)

  /**
   * Shade of red used in <em>Algorithms, 4th edition</em>.
   * It is Pantone 1805U. The RGB values are approximately (150, 35, 31).
   */
  val BOOK_RED = new Color(150, 35, 31)

  /**
   * Shade of orange used in Princeton University's identity.
   * It is PMS 158. The RGB values are approximately (245, 128, 37).
   */
  val PRINCETON_ORANGE = new Color(245, 128, 37)

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

  /**
   * Sets the canvas (drawing area) to be 512-by-512 pixels.
   * This also erases the current drawing and resets the coordinate system,
   * pen radius, pen color, and font back to their default values.
   * Ordinarily, this method is called once, at the very beginning
   * of a program.
   */
  def setCanvasSize(): Unit = {
    setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE)
  }

  /**
   * Sets the canvas (drawing area) to be <em>width</em>-by-<em>height</em> pixels.
   * This also erases the current drawing and resets the coordinate system,
   * pen radius, pen color, and font back to their default values.
   * Ordinarly, this method is called once, at the very beginning
   * of a program.
   *
   * @param  canvasWidth  the width as a number of pixels
   * @param  canvasHeight the height as a number of pixels
   * @throws IllegalArgumentException unless both `canvasWidth` and
   *                                  `canvasHeight` are positive
   */
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

  private def createMenuBar() = {
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

  /**
   * Sets the <em>x</em>-scale to be the default (between 0.0 and 1.0).
   */
  def setXscale(): Unit = {
    setXscale(DEFAULT_XMIN, DEFAULT_XMAX)
  }

  /**
   * Sets the <em>y</em>-scale to be the default (between 0.0 and 1.0).
   */
  def setYscale(): Unit = {
    setYscale(DEFAULT_YMIN, DEFAULT_YMAX)
  }

  /**
   * Sets the <em>x</em>-scale to the specified range.
   *
   * @param  min the minimum value of the <em>x</em>-scale
   * @param  max the maximum value of the <em>x</em>-scale
   * @throws IllegalArgumentException if `max==min`
   * @throws IllegalArgumentException if either `min` or `max` is either NaN or infinite
   */
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

  /**
   * Sets the <em>y</em>-scale to the specified range.
   *
   * @param  min the minimum value of the <em>y</em>-scale
   * @param  max the maximum value of the <em>y</em>-scale
   * @throws IllegalArgumentException if `max==min`
   * @throws IllegalArgumentException if either `min` or `max` is either NaN or infinite
   */
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

  /**
   * Clears the screen to the default color (white).
   */
  def clear(): Unit = {
    clear(DEFAULT_CLEAR_COLOR)
  }

  /**
   * Clears the screen to the specified color.
   *
   * @param color the color to make the background
   * @throws IllegalArgumentException if `color` is `null`
   */
  def clear(color: Color): Unit = {
    validateNotNull(color, "color")
    offscreen.setColor(color)
    offscreen.fillRect(0, 0, width, height)
    offscreen.setColor(penColor)
    draw()
  }

  /**
   * Returns the current pen radius.
   *
   * @return the current value of the pen radius
   */
  def getPenRadius: Double = penRadius

  /**
   * Sets the pen size to the default size (0.002).
   * The pen is circular, so that lines have rounded ends, and when you set the
   * pen radius and draw a point, you get a circle of the specified radius.
   * The pen radius is not affected by coordinate scaling.
   */
  def setPenRadius(): Unit = {
    setPenRadius(DEFAULT_PEN_RADIUS)
  }

  /**
   * Sets the radius of the pen to the specified size.
   * The pen is circular, so that lines have rounded ends, and when you set the
   * pen radius and draw a point, you get a circle of the specified radius.
   * The pen radius is not affected by coordinate scaling.
   *
   * @param  radius the radius of the pen
   * @throws IllegalArgumentException if `radius` is negative, NaN, or infinite
   */
  def setPenRadius(radius: Double): Unit = {
    validate(radius, "pen radius")
    validateNonnegative(radius, "pen radius")

    penRadius = radius
    val scaledPenRadius = (radius * DEFAULT_SIZE).toFloat
    val stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    offscreen.setStroke(stroke)
  }

  /**
   * Returns the current pen color.
   *
   * @return the current pen color
   */
  def getPenColor: Color = penColor

  /**
   * Sets the pen color to the default color (black).
   */
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

  /**
   * Draws a line segment between (<em>x</em><sub>0</sub>, <em>y</em><sub>0</sub>) and
   * (<em>x</em><sub>1</sub>, <em>y</em><sub>1</sub>).
   *
   * @param  x0 the <em>x</em>-coordinate of one endpoint
   * @param  y0 the <em>y</em>-coordinate of one endpoint
   * @param  x1 the <em>x</em>-coordinate of the other endpoint
   * @param  y1 the <em>y</em>-coordinate of the other endpoint
   * @throws IllegalArgumentException if any coordinate is either NaN or infinite
   */
  def line(x0: Double, y0: Double, x1: Double, y1: Double): Unit = {
    validate(x0, "x0")
    validate(y0, "y0")
    validate(x1, "x1")
    validate(y1, "y1")
    offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)))
    draw()
  }

  /**
   * Draws one pixel at (<em>x</em>, <em>y</em>).
   * This method is private because pixels depend on the display.
   * To achieve the same effect, set the pen radius to 0 and call `point()`.
   *
   * @param  x the <em>x</em>-coordinate of the pixel
   * @param  y the <em>y</em>-coordinate of the pixel
   * @throws IllegalArgumentException if `x` or `y` is either NaN or infinite
   */
  private def pixel(x: Double, y: Double): Unit = {
    validate(x, "x")
    validate(y, "y")
    offscreen.fillRect(Math.round(scaleX(x)).toInt, Math.round(scaleY(y)).toInt, 1, 1)
  }

  /**
   * Draws a point centered at (<em>x</em>, <em>y</em>).
   * The point is a filled circle whose radius is equal to the pen radius.
   * To draw a single-pixel point, first set the pen radius to 0.
   *
   * @param x the <em>x</em>-coordinate of the point
   * @param y the <em>y</em>-coordinate of the point
   * @throws IllegalArgumentException if either `x` or `y` is either NaN or infinite
   */
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

  /**
   * Draws a circle of the specified radius, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x      the <em>x</em>-coordinate of the center of the circle
   * @param  y      the <em>y</em>-coordinate of the center of the circle
   * @param  radius the radius of the circle
   * @throws IllegalArgumentException if `radius` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a filled circle of the specified radius, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x      the <em>x</em>-coordinate of the center of the circle
   * @param  y      the <em>y</em>-coordinate of the center of the circle
   * @param  radius the radius of the circle
   * @throws IllegalArgumentException if `radius` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws an ellipse with the specified semimajor and semiminor axes,
   * centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x             the <em>x</em>-coordinate of the center of the ellipse
   * @param  y             the <em>y</em>-coordinate of the center of the ellipse
   * @param  semiMajorAxis is the semimajor axis of the ellipse
   * @param  semiMinorAxis is the semiminor axis of the ellipse
   * @throws IllegalArgumentException if either `semiMajorAxis`
   *                                  or `semiMinorAxis` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a filled ellipse with the specified semimajor and semiminor axes,
   * centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x             the <em>x</em>-coordinate of the center of the ellipse
   * @param  y             the <em>y</em>-coordinate of the center of the ellipse
   * @param  semiMajorAxis is the semimajor axis of the ellipse
   * @param  semiMinorAxis is the semiminor axis of the ellipse
   * @throws IllegalArgumentException if either `semiMajorAxis`
   *                                  or `semiMinorAxis` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a circular arc of the specified radius,
   * centered at (<em>x</em>, <em>y</em>), from angle1 to angle2 (in degrees).
   *
   * @param  x      the <em>x</em>-coordinate of the center of the circle
   * @param  y      the <em>y</em>-coordinate of the center of the circle
   * @param  radius the radius of the circle
   * @param  angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
   * @param  angle2 the angle at the end of the arc. For example, if
   *                you want a 90 degree arc, then angle2 should be angle1 + 90.
   * @throws IllegalArgumentException if `radius` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a square of the specified size, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x          the <em>x</em>-coordinate of the center of the square
   * @param  y          the <em>y</em>-coordinate of the center of the square
   * @param  halfLength one half the length of any side of the square
   * @throws IllegalArgumentException if `halfLength` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a filled square of the specified size, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x          the <em>x</em>-coordinate of the center of the square
   * @param  y          the <em>y</em>-coordinate of the center of the square
   * @param  halfLength one half the length of any side of the square
   * @throws IllegalArgumentException if `halfLength` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a rectangle of the specified size, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x          the <em>x</em>-coordinate of the center of the rectangle
   * @param  y          the <em>y</em>-coordinate of the center of the rectangle
   * @param  halfWidth  one half the width of the rectangle
   * @param  halfHeight one half the height of the rectangle
   * @throws IllegalArgumentException if either `halfWidth` or `halfHeight` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a filled rectangle of the specified size, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x          the <em>x</em>-coordinate of the center of the rectangle
   * @param  y          the <em>y</em>-coordinate of the center of the rectangle
   * @param  halfWidth  one half the width of the rectangle
   * @param  halfHeight one half the height of the rectangle
   * @throws IllegalArgumentException if either `halfWidth` or `halfHeight` is negative
   * @throws IllegalArgumentException if any argument is either NaN or infinite
   */
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

  /**
   * Draws a polygon with the vertices
   * (<em>x</em><sub>0</sub>, <em>y</em><sub>0</sub>),
   * (<em>x</em><sub>1</sub>, <em>y</em><sub>1</sub>), ...,
   * (<em>x</em><sub><em>n</em>–1</sub>, <em>y</em><sub><em>n</em>–1</sub>).
   *
   * @param  x an array of all the <em>x</em>-coordinates of the polygon
   * @param  y an array of all the <em>y</em>-coordinates of the polygon
   * @throws IllegalArgumentException unless `x[]` and `y[]`
   *                                  are of the same length
   * @throws IllegalArgumentException if any coordinate is either NaN or infinite
   * @throws IllegalArgumentException if either `x[]` or `y[]` is `null`
   */
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

  /**
   * Draws a filled polygon with the vertices
   * (<em>x</em><sub>0</sub>, <em>y</em><sub>0</sub>),
   * (<em>x</em><sub>1</sub>, <em>y</em><sub>1</sub>), ...,
   * (<em>x</em><sub><em>n</em>–1</sub>, <em>y</em><sub><em>n</em>–1</sub>).
   *
   * @param  x an array of all the <em>x</em>-coordinates of the polygon
   * @param  y an array of all the <em>y</em>-coordinates of the polygon
   * @throws IllegalArgumentException unless `x[]` and `y[]`
   *                                  are of the same length
   * @throws IllegalArgumentException if any coordinate is either NaN or infinite
   * @throws IllegalArgumentException if either `x[]` or `y[]` is `null`
   */
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

  /**
   * Draws the specified image centered at (<em>x</em>, <em>y</em>).
   * The supported image formats are JPEG, PNG, and GIF.
   * As an optimization, the picture is cached, so there is no performance
   * penalty for redrawing the same image multiple times (e.g., in an animation).
   * However, if you change the picture file after drawing it, subsequent
   * calls will draw the original picture.
   *
   * @param  x        the center <em>x</em>-coordinate of the image
   * @param  y        the center <em>y</em>-coordinate of the image
   * @param  filename the name of the image/picture, e.g., "ball.gif"
   * @throws IllegalArgumentException if the image filename is invalid
   * @throws IllegalArgumentException if either `x` or `y` is either NaN or infinite
   */
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

  /**
   * Draws the specified image centered at (<em>x</em>, <em>y</em>),
   * rotated given number of degrees.
   * The supported image formats are JPEG, PNG, and GIF.
   *
   * @param  x        the center <em>x</em>-coordinate of the image
   * @param  y        the center <em>y</em>-coordinate of the image
   * @param  filename the name of the image/picture, e.g., "ball.gif"
   * @param  degrees  is the number of degrees to rotate counterclockwise
   * @throws IllegalArgumentException if the image filename is invalid
   * @throws IllegalArgumentException if `x`, `y`, `degrees` is NaN or infinite
   * @throws IllegalArgumentException if `filename` is `null`
   */
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

  /**
   * Draws the specified image centered at (<em>x</em>, <em>y</em>),
   * rescaled to the specified bounding box.
   * The supported image formats are JPEG, PNG, and GIF.
   *
   * @param  x            the center <em>x</em>-coordinate of the image
   * @param  y            the center <em>y</em>-coordinate of the image
   * @param  filename     the name of the image/picture, e.g., "ball.gif"
   * @param  scaledWidth  the width of the scaled image (in screen coordinates)
   * @param  scaledHeight the height of the scaled image (in screen coordinates)
   * @throws IllegalArgumentException if either `scaledWidth`
   *                                  or `scaledHeight` is negative
   * @throws IllegalArgumentException if the image filename is invalid
   * @throws IllegalArgumentException if `x` or `y` is either NaN or infinite
   * @throws IllegalArgumentException if `filename` is `null`
   */
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

  /**
   * Draws the specified image centered at (<em>x</em>, <em>y</em>), rotated
   * given number of degrees, and rescaled to the specified bounding box.
   * The supported image formats are JPEG, PNG, and GIF.
   *
   * @param  x            the center <em>x</em>-coordinate of the image
   * @param  y            the center <em>y</em>-coordinate of the image
   * @param  filename     the name of the image/picture, e.g., "ball.gif"
   * @param  scaledWidth  the width of the scaled image (in screen coordinates)
   * @param  scaledHeight the height of the scaled image (in screen coordinates)
   * @param  degrees      is the number of degrees to rotate counterclockwise
   * @throws IllegalArgumentException if either `scaledWidth`
   *                                  or `scaledHeight` is negative
   * @throws IllegalArgumentException if the image filename is invalid
   */
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

  /**
   * Writes the given text string in the current font, centered at (<em>x</em>, <em>y</em>).
   *
   * @param  x    the center <em>x</em>-coordinate of the text
   * @param  y    the center <em>y</em>-coordinate of the text
   * @param  text the text to write
   * @throws IllegalArgumentException if `text` is `null`
   * @throws IllegalArgumentException if `x` or `y` is either NaN or infinite
   */
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

  /**
   * Writes the given text string in the current font, centered at (<em>x</em>, <em>y</em>) and
   * rotated by the specified number of degrees.
   *
   * @param  x       the center <em>x</em>-coordinate of the text
   * @param  y       the center <em>y</em>-coordinate of the text
   * @param  t       the text to write
   * @param  degrees is the number of degrees to rotate counterclockwise
   * @throws IllegalArgumentException if `t` is `null`
   * @throws IllegalArgumentException if `x`, `y`, or `degrees` is either NaN or infinite
   */
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

  /**
   * Writes the given text string in the current font, left-aligned at (<em>x</em>, <em>y</em>).
   *
   * @param  x    the <em>x</em>-coordinate of the text
   * @param  y    the <em>y</em>-coordinate of the text
   * @param  text the text
   * @throws IllegalArgumentException if `text` is `null`
   * @throws IllegalArgumentException if `x` or `y` is either NaN or infinite
   */
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

  /**
   * Writes the given text string in the current font, right-aligned at (<em>x</em>, <em>y</em>).
   *
   * @param  x    the <em>x</em>-coordinate of the text
   * @param  y    the <em>y</em>-coordinate of the text
   * @param  text the text to write
   * @throws IllegalArgumentException if `text` is `null`
   * @throws IllegalArgumentException if `x` or `y` is either NaN or infinite
   */
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

  /**
   * Copies offscreen buffer to onscreen buffer. There is no reason to call
   * this method unless double buffering is enabled.
   */
  def show(): Unit = {
    onscreen.drawImage(offscreenImage, 0, 0, null)
    frame.repaint()
  }

  // draw onscreen if defer is false
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
   * drawing methods such as `line()`, `circle()`,
   * and `square()` will be displayed on screen when called.
   * This is the default.
   */
  def disableDoubleBuffering(): Unit = {
    defer = false
  }

  /**
   * Saves the drawing to using the specified filename.
   * The supported image formats are JPEG and PNG;
   * the filename suffix must be `.jpg` or `.png`.
   *
   * @param  filename the name of the file with one of the required suffixes
   * @throws IllegalArgumentException if `filename` is `null`
   */
  def save(filename: String): Unit = {
    validateNotNull(filename, "filename")
    val file = new File(filename)
    val suffix = filename.substring(filename.lastIndexOf('.') + 1)
    // png files
    if ("png".equalsIgnoreCase(suffix)) try ImageIO.write(onscreenImage, suffix, file)
    catch {
      case e: IOException =>
        e.printStackTrace()
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
   * @return `true` if the mouse is being pressed; `false` otherwise
   */
  def isMousePressed: Boolean = mouseLock synchronized isMousePress

  /**
   * Returns the <em>x</em>-coordinate of the mouse.
   *
   * @return the <em>x</em>-coordinate of the mouse
   */
  def mouseX(): Double = mouseLock synchronized mX

  /**
   * Returns the <em>y</em>-coordinate of the mouse.
   *
   * @return <em>y</em>-coordinate of the mouse
   */
  def mouseY(): Double = mouseLock synchronized mY

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
   *         by [[StdDraw#nextKeyTyped]]; `false` otherwise
   */
  def hasNextKeyTyped: Boolean = keyLock synchronized !keysTyped.isEmpty

  def nextKeyTyped: Char = keyLock synchronized {
    if (keysTyped.isEmpty) throw new NoSuchElementException("your program has already processed all keystrokes")
    keysTyped.remove(keysTyped.size - 1)
    // return keysTyped.removeLast();
  }

  def isKeyPressed(keycode: Int): Boolean = keyLock synchronized keysDown.contains(keycode)

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

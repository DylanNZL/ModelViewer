package Assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class can load model data from files and manage it.
 */
class Model {

  private int m_numVertices;
  private int m_numTriangles;
  private ArrayList<Vector3f> Vectors = new ArrayList<>();
  private ArrayList<Triangle> Triangles = new ArrayList<>(); // Originals
  private ArrayList<Triangle> Transformed = new ArrayList<>(); // Transformed

  // Offsets
  private int offsetX = 0;
  private int offsetY = 0;
  private int offsetZ = 0;

  // the largest absolute coordinate value of the untransformed model data
  private float m_maxSize;

  private Model() {
  }

  /**
   * Creates a {@link Model} instance for the data in the specified file.
   *
   * @param file The file to load.
   * @return The {@link Model}, or null if an error occurred.
   */
  public static Model loadModel(final File file) {
    final Model model = new Model();

    // read the data from the file
    if (!model.loadModelFromFile(file)) {
      return null;
    }

    return model;
  }

  /**
   * Reads model data from the specified file.
   *
   * @param file The file to load.
   * @return True on success, false otherwise.
   */
  protected boolean loadModelFromFile(final File file) {
    m_maxSize = 0.f;

    try (final Scanner scanner = new Scanner(file)) {
      // the first line specifies the vertex count
      m_numVertices = scanner.nextInt();

      // read all vertex coordinates
      float x, y, z;
      for (int i = 0; i < m_numVertices; ++i) {
        // advance the position to the beginning of the next line
        scanner.nextLine();

        // read the vertex coordinates
        x = scanner.nextFloat();
        y = scanner.nextFloat();
        z = scanner.nextFloat();

        // store the vertex data
        Vectors.add(new Vector3f(x, y, z));

        // determine the max value in any dimension in the model file
        m_maxSize = Math.max(m_maxSize, Math.abs(x));
        m_maxSize = Math.max(m_maxSize, Math.abs(y));
        m_maxSize = Math.max(m_maxSize, Math.abs(z));
      }

      // the next line specifies the number of triangles
      scanner.nextLine();
      m_numTriangles = scanner.nextInt();

      // read all polygon data (assume triangles); these are indices into
      // the vertex array
      int v1, v2, v3;
      for (int i = 0; i < m_numTriangles; ++i) {
        scanner.nextLine();

        // the model files start with index 1, we start with 0
        v1 = scanner.nextInt() - 1;
        v2 = scanner.nextInt() - 1;
        v3 = scanner.nextInt() - 1;

        // store the triangle data in a suitable data structure
        Triangles.add(new Triangle (
                new Vector4f(Vectors.get(v1)),
                new Vector4f(Vectors.get(v2)),
                new Vector4f(Vectors.get(v3))
        ));
      }
    } catch (FileNotFoundException e) {
      System.err.println("No such file " + file.toString() + ": "
          + e.getMessage());
      return false;
    } catch (NoSuchElementException e) {
      System.err.println("Invalid file format: " + e.getMessage());
      return false;
    } catch (Exception e) {
      System.err.println("Something went wrong while reading the"
          + " model file: " + e.getMessage());
      e.printStackTrace();
      return false;
    }

    System.out.println("Number of vertices in model: " + m_numVertices);
    System.out.println("Number of triangles in model: " + m_numTriangles);

    for (Triangle Triangle : Triangles) {
      Triangle.calculateNormal();
    }

    return true;
  }

  /**
   * Returns the largest absolute coordinate value of the original,
   * untransformed model data.
   */
  public float getMaxSize() {
    return m_maxSize;
  }

  public ArrayList<Triangle> getTriangles() {
    return Triangles;
    // TODO: Change to transformed once that is implemented
    //return Transformed;
  }

  // Increase the X offset of the vertices in the triangle(s) by 10
  public void increaseX() {
    offsetX += 10;
  }

  // Increase the Y offset of the vertices in the triangle(s) by 10
  public void increaseY() {
    offsetY += 10;
  }

  // Increase the Z offset of the vertices in the triangle(s) by 10
  public void increaseZ() {
    offsetZ += 10;
  }

  // Decrease the X offset of the vertices in the triangle(s) by 10
  public void decreaseX () {
    offsetX -= 10;
  }

  // Decrease the Y offset of the vertices in the triangle(s) by 10
  public void decreaseY () {
    offsetY -= 10;
  }

  // Decrease the Z offset of the vertices in the triangle(s) by 10
  public void decreaseZ() {
    offsetZ -= 10;
  }
}

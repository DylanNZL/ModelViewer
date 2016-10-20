package Assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class can load model data from files and manage it.
 */
class Model {

  // ArrayList of Vectors, used to load triangles in
  private ArrayList<Vector4f> Vectors = new ArrayList<>();

  // ArrayLists of Triangles. The original ones are not altered, but the transformed are.
  private ArrayList<Triangle> Triangles = new ArrayList<>(); // Originals
  private ArrayList<Triangle> Transformed = new ArrayList<>(); // Transformed

  // Offsets
  private float offsetX = 0;
  private float offsetY = 0;
  private float offsetZ = 0;

  // Rotates
  private int rotateX = 0;
  private int rotateY = 0;
  private int rotateZ = 0;

  // Scale
  private float scale = 50.f;

  // Matrix variable. Enables the matrix to be combined with the multiple rotate functions.
  private Matrix4f matrix;

  // The largest absolute coordinate value of the untransformed model data
  private float m_maxSize;

  private Model() {
  }

  /**
   * Creates a {@link Model} instance for the data in the specified file.
   *
   * @param file The file to load.
   * @return The {@link Model}, or null if an error occurred.
   */
  static Model loadModel(final File file) {
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
  private boolean loadModelFromFile(final File file) {
    m_maxSize = 0.f;

    int m_numVertices;
    int m_numTriangles;
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
        Vectors.add(new Vector4f(x, y, z));

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
                Vectors.get(v1),
                Vectors.get(v2),
                Vectors.get(v3)
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

    // Create New transforms based on the current values (if the values are default it won't do anything)
    newTransforms();

    return true;
  }

  /**
   * New transforms is responsible for cloning the original model each time it is called.
   * It creates a translation Matrix4f instance and then applies it.
   * Followed by a new matrix that is used to do the rotation along the three axis, which is applied before
   *  creating a new matrix that is used to scale up/down the model to the current scale value.
   * It is called in multiple classes, so is package-private.
   */
  void newTransforms() {
    Transformed.clear();
    for (Triangle triangle : Triangles) {
      Triangle add = new Triangle(
              new Vector4f(triangle.v[0]),
              new Vector4f(triangle.v[1]),
              new Vector4f(triangle.v[2])
      );
      Transformed.add(add);
    }

    // Create new matrix for offsets
    matrix = Matrix4f.createTranslateInstance(offsetX, offsetY, offsetZ);
    applyTransform(matrix);

    // Create new matrix for Rotations
    matrix = new Matrix4f();
    if (rotateX != 0) { rotateX(rotateX); }
    if (rotateY != 0) { rotateY(rotateY); }
    if (rotateZ != 0) { rotateZ(rotateZ); }
    applyTransform(matrix);

    // Create a scale transformation
    Matrix4f scaleMat = Matrix4f.createScaleInstance(scale, scale, scale);
    applyTransform(scaleMat);

    Transformed.forEach(Triangle::calculateNormal);
    Collections.sort(Transformed);
  }

  /**
   * Rotate Transformers
   * These instantate new instances of the createRotate* of Matrix4F and then multiply them with the current matrix
   * This means applyTransforms can be called once on all three rotations
   */

   private void rotateX (float rotate) {
    // Rotate around y axis.
    final Matrix4f xMat = Matrix4f.createRotateXInstance((float) Math.toRadians(rotate));
    matrix = matrix.multiply(xMat);
  }

  private void rotateY (float rotate) {
    // Rotate around y axis.
    final Matrix4f yMat = Matrix4f.createRotateYInstance((float) Math.toRadians(rotate));
    matrix = matrix.multiply(yMat);
  }

  private void rotateZ (float rotate) {
    // Rotate around z axis
    Matrix4f zMat = Matrix4f.createRotateZInstance((float) Math.toRadians(rotate));
    matrix = matrix.multiply(zMat);
  }

  /**
   * Sets the scale variable to the best fit for the window.
   * It is sent height & width from the canvas class.
   */

  void setScale(float height, float width) {
    if (height < width) {
      scale = (height / 2) / m_maxSize;
    } else {
      scale = (width / 2) / m_maxSize;
    }
  }

  /**
   * Rotate updaters
   * Set the new value of that rotate axis if it is not equal to the current value (may cause too much redraws if it was)
   *  afterwards call new transforms to apply the new rotation tranformation
   */

  void setRotateX(int mRotateX) {
    if (rotateX != mRotateX) {
      rotateX = mRotateX;
      newTransforms();
    }
  }

  void setRotateY(int mRotateY) {
    if (rotateY != mRotateY) {
      rotateY = mRotateY;
      newTransforms();
    }
  }

  void setRotateZ(int mRotateZ) {
    if (rotateZ != mRotateZ) {
      rotateZ = mRotateZ;
      newTransforms();
    }
  }

  /**
   * Scale Increment/Decrement
   * Increases by a scale of 1.1 and decreases by 0.9
   * Calls newTransforms to update the model afterwards
   */

  void incrementScale() {
    scale = scale * 1.1f;
    newTransforms();
  }

  void decrementScale() {
    scale = scale * 0.9f;
    newTransforms();
  }

  /**
   * Offset Value Increase/Decrease functions
   * Increment & Decrement in values of .5 as the scale is usually quite big so will show a large amount of movement
   *  even though it is a small amount
   * Calls newTransforms to update the model afterwards
   */

  void increaseX() {
    offsetX += 0.5;
    newTransforms();
  }

  void increaseY() {
    offsetY += 0.5;
    newTransforms();
  }

  void increaseZ() {
    offsetZ += 0.5;
    newTransforms();
  }

  void decreaseX () {
    offsetX -= 0.5;
    newTransforms();
  }

  void decreaseY () {
    offsetY -= 0.5;
    newTransforms();
  }

  void decreaseZ() {
    offsetZ -= 0.5;
    newTransforms();
  }

  /**
   * applyTransform applies the transformations that are sent to it
   * It utilises the multiply vector4f to transform the vector of each triangle in the Transformed ArrayList
   * It sends as input the same vector to both sides of the multiply function, to ensure previous changes are not altered
   */
  private void applyTransform(final Matrix4f transform) {
    for (final Triangle t : Transformed) {
      for (int i = 0; i < t.v.length; ++i) {
        transform.multiply(t.v[i], t.v[i]);
      }
    }
  }

  /**
   * Return the transformed Arraylist of the triangles that have been read in.
   */
  ArrayList<Triangle> getTriangles() {
    return Transformed;
  }
}

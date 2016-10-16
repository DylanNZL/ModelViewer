package render3d;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A model of a 3D object that is represented by a polygon mesh.
 */
class Model {
  private final ArrayList<Triangle> original; // Before transforms.
  private final ArrayList<Triangle> transformed; // After transforms.

  private Matrix4f modelMat = new Matrix4f();

  Model(final ArrayList<Triangle> triangles) {
    this.original = triangles;

    // Create a copy of the polygon mesh.
    this.transformed = new ArrayList<>(triangles.size());
    for (final Triangle triangle : triangles) {
      transformed.add(new Triangle(
          new Vector4f(triangle.v[0]),
          new Vector4f(triangle.v[1]),
          new Vector4f(triangle.v[2])
      ));
    }
  }

  /**
   * Replaces the current model matrix with transform.
   */
  public void setModelMatrix(final Matrix4f transform) {
    modelMat = transform;
  }

  /**
   * Returns the model matrix.
   */
  public Matrix4f getModelMatrix() {
    return modelMat;
  }

  /**
   * Applies the given transform to the model's vertex positions.
   */
  public void applyTransform(final Matrix4f transform) {
    for (int i = 0; i < original.size(); ++i) {
      final Triangle tIn = original.get(i);
      final Triangle tOut = transformed.get(i);
      for (int vertex = 0; vertex < tIn.v.length; ++vertex) {
        transform.multiply(tIn.v[vertex], tOut.v[vertex]);
      }
    }
  }

  /**
   * Recalculates all surface normals from their vertices.
   */
  public void updateNormals() {
    transformed.forEach(Triangle::calculateNormal);
  }

  /**
   * Sorts the triangles of the model in back-to-front order.
   */
  public void sortTriangles() {
    Collections.sort(transformed);
  }

  /**
   * Performs perspective division, homogenising all position vectors.
   */
  public void doPerspectiveDivision() {
    for (final Triangle triangle : transformed) {
      for (int vertex = 0; vertex < triangle.v.length; ++vertex) {
        triangle.v[vertex].homogenise();
      }
    }
  }

  public ArrayList<Triangle> getTriangles() {
    return transformed;
  }
}

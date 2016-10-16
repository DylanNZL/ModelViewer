package render3d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

public class Render3D {
  private final Canvas canvas;
  private final Model model;

  private Timer timer;

  private final float cameraMaxDist = 1000;
  private final float cameraMinDist = 350;
  private final float cameraSpeed = 150;
  private float cameraDirection = 1.f; // Animation direction.
  private boolean animateCamera = false; // Toggle camera animation.
  private Vector3f cameraPos = new Vector3f(0, 0, cameraMinDist);

  private boolean enablePerspective = true; // Toggle perspective projection.

  public Render3D(final Canvas canvas, final Model model) {
    this.canvas = canvas;
    this.model = model;

    canvas.model = model;
  }

  /**
   * Starts the render loop.
   *
   * @param frameRate The frame rate for canvas updates in frames-per-second.
   */
  public void startRenderLoop(final int frameRate) {
    if (timer != null) timer.stop();

    final float delay = 1000.f / frameRate;
    timer = new Timer((int) delay, e -> renderFrame(delay / 1000.f));
    timer.setInitialDelay(0);
    timer.start();
  }

  public void pause() {
    if (timer != null) timer.stop();
  }

  public void resume() {
    if (timer != null) timer.restart();
  }

  public boolean isRunning() {
    return timer != null && timer.isRunning();
  }

  /**
   * Updates the scene and renders the next frame.
   */
  private void renderFrame(final float dt) {
    update(dt);
    canvas.repaint();
  }

  private void update(final float dt) {
    if (animateCamera) { // Animate the camera position along the z-axis.
      float dist = cameraPos.z + cameraSpeed * cameraDirection * dt;
      if (dist >= cameraMaxDist) {
        cameraDirection = -1.f;
        dist = cameraMaxDist;
      } else if (dist <= cameraMinDist) {
        cameraDirection = 1.f;
        dist = cameraMinDist;
      }
      cameraPos.z = dist;
    }

    // Create the projection and view matrices.
    final Matrix4f proj = enablePerspective
        ? Matrix4f.createPerspective(200)
        : new Matrix4f();
    final Matrix4f view = Matrix4f.lookAt(
        cameraPos, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

    // Rotate the model around its local y-axis.
    final Matrix4f r = Matrix4f.createRotateYInstance(dt * (float) Math.toRadians(30.));
    model.setModelMatrix(model.getModelMatrix().multiply(r));

    // Update the model.
    Matrix4f transform = view.multiply(model.getModelMatrix());
    model.applyTransform(transform);
    model.updateNormals();
    if (enablePerspective) {
      // Apply the projection after normals have been updated, but before
      // triangles are sorted, as the transformation overwrites the transformed
      // triangles array in the original triangle order.
      transform = proj.multiply(transform);
      model.applyTransform(transform);
    }
    model.sortTriangles();
    // Homogenise vectors after normals are updated and triangles sorted!
    if (enablePerspective) model.doPerspectiveDivision();
  }

  /**
   * Generates the polygons for a pyramid model.
   */
  private static Model generatePyramid() {
    // Define the polygon mesh.
    final ArrayList<Triangle> polys = new ArrayList<>();
    polys.add(new Triangle( // Front
        new Vector4f(-1.f, -1.f, 1.f, 1.f),
        new Vector4f(1.f, -1.f, 1.f, 1.f),
        new Vector4f(0.f, 1.f, 0.f, 1.f)
    ));
    polys.add(new Triangle( // Right
        new Vector4f(1.f, -1.f, 1.f, 1.f),
        new Vector4f(1.f, -1.f, -1.f, 1.f),
        new Vector4f(0.f, 1.f, 0.f, 1.f)
    ));
    polys.add(new Triangle( // Back
        new Vector4f(1.f, -1.f, -1.f, 1.f),
        new Vector4f(-1.f, -1.f, -1.f, 1.f),
        new Vector4f(0.f, 1.f, 0.f, 1.f)
    ));
    polys.add(new Triangle( // Left
        new Vector4f(-1.f, -1.f, -1.f, 1.f),
        new Vector4f(-1.f, -1.f, 1.f, 1.f),
        new Vector4f(0.f, 1.f, 0.f, 1.f)
    ));
    polys.add(new Triangle( // Bottom 1
        new Vector4f(-1.f, -1.f, 1.f, 1.f),
        new Vector4f(-1.f, -1.f, -1.f, 1.f),
        new Vector4f(1.f, -1.f, -1.f, 1.f)
    ));
    polys.add(new Triangle( // Bottom 2
        new Vector4f(-1.f, -1.f, 1.f, 1.f),
        new Vector4f(1.f, -1.f, -1.f, 1.f),
        new Vector4f(1.f, -1.f, 1.f, 1.f)
    ));

    return new Model(polys);
  }

  public static void main(String[] args) {
    final int frameRate = 30;

    // Create the model and set its initial scale.
    final Model model = generatePyramid();
    model.setModelMatrix(Matrix4f.createScaleInstance(150, 150, 150));

    // Create the GUI and start rendering.
    SwingUtilities.invokeLater(() -> {
      final JFrame frame = new JFrame("3D Rendering Example");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      final Canvas canvas = new Canvas();
      frame.setContentPane(canvas);

      frame.setSize(512, 512);
      frame.setVisible(true);

      final Render3D renderer = new Render3D(canvas, model);
      renderer.startRenderLoop(frameRate);

      frame.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
              System.exit(0);
              break;
            case KeyEvent.VK_SPACE:
              if (renderer.isRunning()) renderer.pause();
              else renderer.resume();
              break;
            case KeyEvent.VK_C:
              renderer.animateCamera = !renderer.animateCamera;
              break;
            case KeyEvent.VK_L:
              canvas.enableLighting = !canvas.enableLighting;
              renderer.renderFrame(0.f);
              break;
            case KeyEvent.VK_P:
              renderer.enablePerspective = !renderer.enablePerspective;
              renderer.renderFrame(0.f);
              break;
          }
        }
      });
    });
  }
}

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

class Canvas extends JPanel {

  Model model;
  Stroke wireframeStroke = new BasicStroke(1);

  boolean renderWireframe = true;
  boolean cullBackFaces = true;
  boolean enableLighting = true;

  // A directional light source.
  Vector3f dirToLight = new Vector3f(1.f, 1.f, 1.f).normalize();
  // The colour and intensity of the light source, for all reflection terms.
  Vector3f lightIntensity = new Vector3f(1.f, 1.f, 1.f);

  Color modelColour = Color.BLUE;
  // The material reflection terms should add up to 1.
  float ambientReflection = 0.3f;
  float diffuseReflection = 0.7f;

  Canvas() {
    setBackground(Color.WHITE);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (model == null) return;

    final ArrayList<Triangle> triangles = model.getTriangles();
    if (triangles == null || triangles.size() == 0) return;

    final Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(wireframeStroke);

    // Move the origin to the center of the canvas and flip the y-axis.
    g2.translate(getWidth() / 2., getHeight() / 2.);
    g2.scale(1., -1.);

    final Polygon poly = new Polygon(new int[3], new int[3], 3);
    final Vector3f intensity = new Vector3f(1.f, 1.f, 1.f);
    for (final Triangle triangle : triangles) {
      if (cullBackFaces && triangle.normal.z <= 0.f) continue;

      poly.xpoints[0] = (int) triangle.v[0].x;
      poly.xpoints[1] = (int) triangle.v[1].x;
      poly.xpoints[2] = (int) triangle.v[2].x;
      poly.ypoints[0] = (int) triangle.v[0].y;
      poly.ypoints[1] = (int) triangle.v[1].y;
      poly.ypoints[2] = (int) triangle.v[2].y;

      if (enableLighting) {
        calculateLightIntensity(triangle.normal, dirToLight, intensity);
        final Color colour = new Color(
            (int) (modelColour.getRed() * intensity.x),
            (int) (modelColour.getGreen() * intensity.y),
            (int) (modelColour.getBlue() * intensity.z));
        g2.setPaint(colour);
      } else { // No lighting.
        g2.setPaint(modelColour);
      }

      g2.fill(poly);

      if (renderWireframe) {
        g2.setPaint(Color.BLACK);
        g2.draw(poly);
      }
    }

    g2.dispose();
  }

  private void calculateLightIntensity(final Vector3f normal,
                                       final Vector3f toLight,
                                       final Vector3f out) {
    final float diffuseIntensity =
        Math.max(Vector3f.dotProduct(toLight, normal), 0.f)
            * diffuseReflection;

    final float intensity = ambientReflection + diffuseIntensity;

    out.x = lightIntensity.x * intensity;
    out.y = lightIntensity.y * intensity;
    out.z = lightIntensity.z * intensity;
  }
}

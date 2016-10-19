package render3d;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
    final Matrix4f proj;
    if (enablePerspective) {
      proj = Matrix4f.createPerspective(200);
    }
    else {
      proj = new Matrix4f();
    }
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


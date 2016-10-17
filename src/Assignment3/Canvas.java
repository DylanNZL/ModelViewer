package Assignment3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The draw area.
 */
class Canvas extends JPanel {

  private static final long serialVersionUID = 1L;

  private Model m_model;

  // Controller variables, changed by the GUI
  private double scale = 10;
  private boolean wireFrame = true;
  private boolean solid = true;
  private boolean backFace = true;


  public Canvas() {
    setOpaque(true);
  }

  public void setModel(final Model model) {
    m_model = model;
  }

  public void incrementScale() {
    scale = scale * 1.1;
  }

  public void decrementScale() {
    scale = scale * 0.9;
  }

  public void updateWireframe(boolean update) {
    wireFrame = update;
  }

  public void updateSolid(boolean update) {
    solid = update;
  }

  public void updateBackFace(boolean update) {
    backFace = update;
  }

  public void update() {
    this.repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (m_model == null) return;

    // TODO render the model

    final ArrayList<Triangle> triangles = m_model.getTriangles();
    if (triangles == null || triangles.size() == 0) return;

    Graphics2D g2 = (Graphics2D) g.create();
    // Set AA on
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Move the origin to the center of the canvas and flip the y-axis.
    g2.translate(getWidth() / 2., getHeight() / 2.);
    g2.scale(scale, 0-scale);

    final Polygon poly = new Polygon(new int[3], new int[3], 3);
    for (final Triangle triangle : triangles) {
      //if (backFace && triangle.normal.z <= 0.f) continue;

      poly.xpoints[0] = (int) triangle.v[0].x * (int) scale;
      poly.xpoints[1] = (int) triangle.v[1].x * (int) scale;
      poly.xpoints[2] = (int) triangle.v[2].x * (int) scale;
      poly.ypoints[0] = (int) triangle.v[0].y * (int) scale;
      poly.ypoints[1] = (int) triangle.v[1].y * (int) scale;
      poly.ypoints[2] = (int) triangle.v[2].y * (int) scale;

      // if render solids is true, fill the polygon
      if (solid) {
        g2.setPaint(Color.BLUE);
        g2.fill(poly);
      }

      // if render wireframe is true, draw a black
      if (wireFrame) {
        g2.setPaint(Color.BLACK);
        g2.draw(poly);
      }
    }
    g2.dispose();
  }
}

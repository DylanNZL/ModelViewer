package Assignment3;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ModelViewer {

  private static final int INIT_WIDTH = 1024;
  private static final int INIT_HEIGHT = 768;

  private JFrame m_frame;

  private Canvas m_canvas;

  private JSlider m_sliderRotateX;
  private JSlider m_sliderRotateY;
  private JSlider m_sliderRotateZ;

  private JButton m_btnScaleUp;
  private JButton m_btnScaleDown;
  private JButton m_btnIncrX;
  private JButton m_btnDecrX;
  private JButton m_btnIncrY;
  private JButton m_btnDecrY;
  private JButton m_btnIncrZ;
  private JButton m_btnDecrZ;

  private JCheckBox m_chkRenderWireframe;
  private JCheckBox m_chkRenderSolid;
  private JCheckBox m_chkCullBackFaces;

  private JMenuItem m_menuOpenModelFile;

  private Model m_currentModel;

  //////////////////////////////////////////////////////////////////////////////
  private ChangeListener m_sliderChangeListener = new ChangeListener() {

    @Override
    public void stateChanged(ChangeEvent e) {
      final JSlider source = (JSlider) e.getSource();
      if (m_currentModel != null) {
        if (source == m_sliderRotateX) {
          // TODO
          System.out.println("RotateX");
        } else if (source == m_sliderRotateY) {
          // TODO
          System.out.println("RotateY");
        } else if (source == m_sliderRotateZ) {
          // TODO
          System.out.println("RotateZ");
        }
      }
    }
  };

  private ActionListener m_btnActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      final Object source = e.getSource();
      if (m_currentModel != null) {
        // scale changes
        if (source == m_btnScaleUp) {
          // TODO
          System.out.println("Scale Up");
        } else if (source == m_btnScaleDown) {
          // TODO
          System.out.println("Scale Down");
        }
        // translation changes
        else if (source == m_btnIncrX) {
          // TODO
          System.out.println("IncrX");
        } else if (source == m_btnDecrX) {
          // TODO
          System.out.println("DecrX");
        } else if (source == m_btnIncrY) {
          // TODO
          System.out.println("IncrY");
        } else if (source == m_btnDecrY) {
          // TODO
          System.out.println("DecrY");
        } else if (source == m_btnIncrZ) {
          // TODO
          System.out.println("IncrZ");
        } else if (source == m_btnDecrZ) {
          // TODO
          System.out.println("DecrZ");
        }
      }
    }
  };

  private ActionListener m_chkActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      final Object source = e.getSource();
      if (source == m_chkRenderWireframe) {
        // TODO
        System.out.println("RenderWireframe");
      } else if (source == m_chkRenderSolid) {
        // TODO
        System.out.println("RenderSolid");
      } else if (source == m_chkCullBackFaces) {
        // TODO
        System.out.println("BackFaces");
      }
    }
  };

  private ActionListener m_menuActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == m_menuOpenModelFile) {
        final Model model = loadModelFile();
        if (model != null) {
          m_currentModel = model;
          m_canvas.setModel(model);
          m_canvas.update();
        }
      }
    }
  };

  //////////////////////////////////////////////////////////////////////////////
  private ModelViewer() {
  }

  /**
   * Factory method for {@link ModelViewer}.
   */
  public static ModelViewer create() {
    final ModelViewer viewer = new ModelViewer();
    SwingUtilities.invokeLater(viewer::createGui);
    return viewer;
  }

  /**
   * Creates the GUI. Must be called from the EDT.
   */
  private void createGui() {
    m_frame = new JFrame("Assignment3.Model Viewer");
    m_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // setup the content pane
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    m_frame.setContentPane(contentPane);

    final Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);

    // setup the canvas and control panel
    m_canvas = new Canvas();
    m_canvas.setBorder(border);
    contentPane.add(m_canvas, BorderLayout.CENTER);
    final JComponent controlPanel = createControlPanel();
    controlPanel.setBorder(border);
    contentPane.add(controlPanel, BorderLayout.LINE_START);

    // add the menu
    final JMenuBar menuBar = new JMenuBar();
    m_frame.setJMenuBar(menuBar);
    final JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);
    m_menuOpenModelFile = new JMenuItem("Open");
    m_menuOpenModelFile.addActionListener(m_menuActionListener);
    fileMenu.add(m_menuOpenModelFile);

    // register a key event dispatcher to get a turn in handling all
    // key events, independent of which component currently has the focus
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(e -> {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
              System.exit(0);
              return true; // consume the event
            default:
              return false; // do not consume the event
          }
        });

    m_frame.setSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
    m_frame.setVisible(true);
  }

  /**
   * Creates and returns the control panel. Must be called from the EDT.
   */
  private JComponent createControlPanel() {
    final JPanel toolbar = new JPanel(new GridBagLayout());

    final GridBagConstraints gbcDefault = new GridBagConstraints();
    gbcDefault.gridx = 0;
    gbcDefault.gridy = GridBagConstraints.RELATIVE;
    gbcDefault.gridwidth = 2;
    gbcDefault.fill = GridBagConstraints.HORIZONTAL;
    gbcDefault.insets = new Insets(5, 10, 5, 10);
    gbcDefault.anchor = GridBagConstraints.FIRST_LINE_START;
    gbcDefault.weightx = 0.5;

    final GridBagConstraints gbcLabels =
        (GridBagConstraints) gbcDefault.clone();
    gbcLabels.insets = new Insets(5, 10, 0, 10);

    final GridBagConstraints gbcTwoCol =
        (GridBagConstraints) gbcDefault.clone();
    gbcTwoCol.gridwidth = 1;
    gbcTwoCol.gridx = 0;
    gbcTwoCol.insets.right = 5;

    GridBagConstraints gbc;

    // setup the rotation sliders
    m_sliderRotateX = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    m_sliderRotateY = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    m_sliderRotateZ = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    m_sliderRotateX.setPaintLabels(true);
    m_sliderRotateY.setPaintLabels(true);
    m_sliderRotateZ.setPaintLabels(true);
    m_sliderRotateX.setPaintTicks(true);
    m_sliderRotateY.setPaintTicks(true);
    m_sliderRotateZ.setPaintTicks(true);
    m_sliderRotateX.setMajorTickSpacing(90);
    m_sliderRotateY.setMajorTickSpacing(90);
    m_sliderRotateZ.setMajorTickSpacing(90);
    m_sliderRotateX.addChangeListener(m_sliderChangeListener);
    m_sliderRotateY.addChangeListener(m_sliderChangeListener);
    m_sliderRotateZ.addChangeListener(m_sliderChangeListener);
    gbc = gbcDefault;
    toolbar.add(new JLabel("Rotation X:"), gbcLabels);
    toolbar.add(m_sliderRotateX, gbc);
    toolbar.add(new JLabel("Rotation Y:"), gbcLabels);
    toolbar.add(m_sliderRotateY, gbc);
    toolbar.add(new JLabel("Rotation Z:"), gbcLabels);
    toolbar.add(m_sliderRotateZ, gbc);

    m_btnScaleDown = new JButton("- size");
    m_btnScaleUp = new JButton("+ size");
    m_btnScaleDown.addActionListener(m_btnActionListener);
    m_btnScaleUp.addActionListener(m_btnActionListener);
    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnScaleDown, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnScaleUp, gbc);

    m_btnIncrX = new JButton("+ x");
    m_btnDecrX = new JButton("- x");
    m_btnIncrY = new JButton("+ y");
    m_btnDecrY = new JButton("- y");
    m_btnIncrZ = new JButton("+ z");
    m_btnDecrZ = new JButton("- z");
    m_btnIncrX.addActionListener(m_btnActionListener);
    m_btnDecrX.addActionListener(m_btnActionListener);
    m_btnIncrY.addActionListener(m_btnActionListener);
    m_btnDecrY.addActionListener(m_btnActionListener);
    m_btnIncrZ.addActionListener(m_btnActionListener);
    m_btnDecrZ.addActionListener(m_btnActionListener);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecrX, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncrX, gbc);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecrY, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncrY, gbc);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecrZ, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncrZ, gbc);

    // add check boxes
    gbc = gbcDefault;

    m_chkRenderWireframe = new JCheckBox("Render Wireframe");
    m_chkRenderWireframe.setSelected(true);
    m_chkRenderWireframe.addActionListener(m_chkActionListener);
    toolbar.add(m_chkRenderWireframe, gbc);

    m_chkRenderSolid = new JCheckBox("Render Solid");
    m_chkRenderSolid.setSelected(true);
    m_chkRenderSolid.addActionListener(m_chkActionListener);
    toolbar.add(m_chkRenderSolid, gbc);

    m_chkCullBackFaces = new JCheckBox("Cull Back Faces");
    m_chkCullBackFaces.setSelected(true);
    m_chkCullBackFaces.addActionListener(m_chkActionListener);
    gbc = (GridBagConstraints) gbcDefault.clone();
    gbc.weighty = 1.;
    gbc.gridheight = GridBagConstraints.REMAINDER;
    toolbar.add(m_chkCullBackFaces, gbc);

    return toolbar;
  }

  /**
   * Displays a chooser dialog and loads the selected model.
   *
   * @return The model, or null if the user cancels the action or something
   * goes wrong.
   */
  private Model loadModelFile() {
    // show a file chooser for model files
    JFileChooser chooser = new JFileChooser("./");
    chooser.setFileFilter(new FileNameExtensionFilter(
        ".dat model files", "dat"));
    int retVal = chooser.showOpenDialog(m_frame);
    if (retVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();

      // try to load the model from the selected file
      final Model model = Model.loadModel(file);

      if (model != null) {
        // initialise the scale value so that the model fits into the
        // render window
        // TODO
      }

      return model;
    }

    return null;
  }

  public static void main(String[] args) {
    System.out.println("*****************************************");
    System.out.println("* 159.235 Assignment 3, Semester 2 2016 *");
    System.out.println("* Submitted by:  Cross, Dylan, 15219491 *");
    System.out.println("*****************************************");

    ModelViewer.create();
  }
}

/**
 * This class can load model data from files and manage it.
 */
class Model {

  private int m_numVertices;
  private int m_numTriangles;
  private ArrayList<Vector3f> Vectors = new ArrayList<>();
  private ArrayList<Triangle> Triangles = new ArrayList<>(); // Originals
  private ArrayList<Triangle> Transformed = new ArrayList<>(); // Transformed


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
}

/**
 * The draw area.
 */
class Canvas extends JPanel {

  private static final long serialVersionUID = 1L;

  private Model m_model;

  public Canvas() {
    setOpaque(true);
  }

  public void setModel(final Model model) {
    m_model = model;
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
    g2.scale(1., -1.);

    final Polygon poly = new Polygon(new int[3], new int[3], 3);
    for (final Triangle triangle : triangles) {
      //if (/*cullBackFaces && */triangle.normal.z <= 0.f) continue;

      poly.xpoints[0] = (int) triangle.v[0].x;
      poly.xpoints[1] = (int) triangle.v[1].x;
      poly.xpoints[2] = (int) triangle.v[2].x;
      poly.ypoints[0] = (int) triangle.v[0].y;
      poly.ypoints[1] = (int) triangle.v[1].y;
      poly.ypoints[2] = (int) triangle.v[2].y;

      g2.setPaint(Color.BLUE);
      g2.fill(poly);

      g2.setPaint(Color.BLACK);
      g2.draw(poly);

      // wireframe
      /*if (renderWireframe) {
        g2.setPaint(Color.BLACK);
        g2.draw(poly);
      }*/
    }
    g2.dispose();
  }
}
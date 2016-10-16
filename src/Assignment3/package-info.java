/**
 *  Created by Dylan on 15-Oct-16.
 *  Written in Java 1.8.040 using IntelliJ 2016.4
 *  Assignment 3 for 159.235 Graphical Programming
 * ---------------------------------------------------------------------------------------------------------------------
 *  Write a 3D model viewer with the following features in Java:
 *    Can load model data from the model files with .dat extension provided on the Stream course page (this
 *      functionality is provided in the skeleton code). You will need to decide on a data structure to store the data.
 *    The origin of the global coordinate system must be located at the centre of the screen.
 *    Six buttons to increment and decrement the x, y, and z positions of the model in world space, using a
 *      translation transform, by a sensible value based on the canvas size (e.g. 10%).
 *    Three sliders with a range of 0-360 to adjust the model's rotation in the xy, xz, and yz planes in model space.
 *    Two buttons to scale the model up or down by a factor of 1.1 and 0.9, respectively. The initial scale must be
 *      chosen based on the model dimensions such that the model fits nicely into the viewport.
 *    All transformations must be implemented using matrices.
 *    Cull faces that are facing away from the camera when rendering solid objects (i.e. not only the wireframe) and
 *      the user has enabled culling. The camera should be located at the origin and facing down the negative z-axis.
 *    Render the model using the Painter's algorithm. The rendered wireframe model should appear “solid” when the
 *      respective check box is selected. The wireframe mesh should also be clearly visible, overlaid on the surface,
 *      when this option is enabled.
 *    Three check boxes: one to toggle rendering the wireframe, one to toggle filling in the polygons' surface area,
 *      and one to toggle back-face culling
 */
package Assignment3;
/*
 * Austin Morris
 * CS445: Program 2
 * Last modified: 10/28/2017
 */
package austin.cs445_program2;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author morri
 */
public class Main {
    public static ArrayList polygons = new ArrayList<Polygon>();
    public static final float[][] IDENTITY_MATRIX = {{1, 0, 0},{0, 1, 0},{0, 0, 1}};

    public static void main(String[] args) throws FileNotFoundException {
        
        File file = new File("coordinates.txt");
        Scanner sc = new Scanner(file);
        String line;
        Polygon lastPolygon = null;
        float[][] tempMatrix = new float[3][3];
        
        //parse coordinates.txt into polygon objects
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] chars = line.split(" ");
            tempMatrix[0][0] = IDENTITY_MATRIX[0][0];
            tempMatrix[0][1] = IDENTITY_MATRIX[0][1];
            tempMatrix[0][2] = IDENTITY_MATRIX[0][2];
            tempMatrix[1][0] = IDENTITY_MATRIX[1][0];
            tempMatrix[1][1] = IDENTITY_MATRIX[1][1];
            tempMatrix[1][2] = IDENTITY_MATRIX[1][2];
            tempMatrix[2][0] = IDENTITY_MATRIX[2][0];
            tempMatrix[2][1] = IDENTITY_MATRIX[2][1];
            tempMatrix[2][2] = IDENTITY_MATRIX[2][2];
            
            switch(chars[0]) {
                case "P":
                    Polygon newPolygon = new Polygon();
                    newPolygon.setColor(Float.parseFloat(chars[1]), Float.parseFloat(chars[2]), Float.parseFloat(chars[3]));
                    polygons.add(newPolygon);
                    break;
                case "T":
                    break;
                case "t":
                    tempMatrix[0][2] = Float.parseFloat(chars[1]);
                    tempMatrix[1][2] = Float.parseFloat(chars[2]);
                    lastPolygon.transform = multiply(tempMatrix, lastPolygon.transform);
                    break;
                case "r":
                    tempMatrix[0][0] = (float)  Math.cos(Math.toRadians(Double.parseDouble(chars[1])));
                    tempMatrix[0][1] = (float) -Math.sin(Math.toRadians(Double.parseDouble(chars[1])));
                    tempMatrix[1][0] = (float)  Math.sin(Math.toRadians(Double.parseDouble(chars[1])));
                    tempMatrix[1][1] = (float)  Math.cos(Math.toRadians(Double.parseDouble(chars[1])));
                    lastPolygon.transform = multiply(tempMatrix, lastPolygon.transform);
                    break;
                case "s":
                    tempMatrix[0][0] = Float.parseFloat(chars[1]);
                    tempMatrix[1][1] = Float.parseFloat(chars[2]);
                    lastPolygon.transform = multiply(tempMatrix, lastPolygon.transform);
                    break;
                case "p":
                    break;
                default:
                    lastPolygon.addVertex(Float.parseFloat(chars[0]), Float.parseFloat(chars[1]));
                    break;
            }
            lastPolygon = (Polygon) polygons.get(polygons.size() - 1);
        }
        
        //perform transformation on each vertex in each polygon
        for (int i = 0; i < polygons.size(); i++) {
            Polygon tempPolygon = (Polygon) polygons.get(i);
            for (int j = 0; j < tempPolygon.vertices.size(); j++) {
                float[] vertex = (float[]) tempPolygon.vertices.get(j);
                float[][] vertexMatrix = {{vertex[0]},{vertex[1]},{1}};
                vertexMatrix = multiply(tempPolygon.transform, vertexMatrix);
                vertex[0] = vertexMatrix[0][0];
                vertex[1] = vertexMatrix[1][0];
            }
        }
        
        Main main = new Main();
        main.start();
    }

    public Main() {
        //this.polygons = new ArrayList<Polygon>();
    }
    
    public void start() {
        try {
            createWindow();
            initGL();
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Oh Yeah!");
        Display.create();
    }
    
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-320, 320, -240, 240, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    private void render() {
        while (!Display.isCloseRequested()) {
            try {
                if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    System.exit(0);
                }
                
                //randomize colors and distort polygons if user holds spacebar
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    for (int i = 0; i < polygons.size(); i++) {
                        Polygon polygon = (Polygon) polygons.get(i);
                        polygon.color[0] += (Math.random() - .5) * .2;
                        polygon.color[1] += (Math.random() - .5) * .2;
                        polygon.color[2] += (Math.random() - .5) * .2;
                        
                        ArrayList vertices = polygon.vertices;
                        for (int j = 0; j < vertices.size(); j++) {
                            float[] vertex = (float[]) vertices.get(j);
                            vertex[0] += (Math.random() - .5) * 5;
                            vertex[1] += (Math.random() - .5) * 5;
                        }
                    }
                }
                
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                glColor3f(1.0f, 1.0f, 0.0f);
                glPointSize(1);
                
                for (int i = 0; i < polygons.size(); i++) {
                    drawPolygon((Polygon) polygons.get(i));
                }
                
                
                
                
                Display.update();
                Display.sync(60);
            } catch (Exception e) {}
        }
        Display.destroy();
    }
    
    public static float[][] multiply(float[][] arg1, float[][] arg2) {
        int arg1Rows = arg1.length;
        int arg1Cols = arg1[0].length;
        int arg2Cols = arg2[0].length;
        float[][] product = new float[arg1Rows][arg2Cols];
        for (int i = 0; i < arg1Rows; i++) {
            for (int j = 0; j < arg2Cols; j++) {
                for (int k = 0; k < arg1Cols; k++) {
                    product[i][j] = product[i][j] + arg1[i][k] * arg2[k][j];
                }
            }
        }
        return product;
    }
    
    
    public static void drawPolygon(Polygon polygon) {
        ArrayList<float[]> vertices = polygon.vertices;
        ArrayList<float[]> all_edges = new ArrayList();
        ArrayList<float[]> global_edges = new ArrayList();
        ArrayList<float[]> active_edges = new ArrayList();
        boolean parity;
        int scanline = 0;
        
        glColor3f(polygon.color[0], polygon.color[1], polygon.color[2]);
        
        //populate all_edges list
        for (int i = 0; i < vertices.size(); i++) {
            float[] currVertex = vertices.get(i);
            float[] nextVertex = vertices.get((i + 1) % vertices.size());
            float x0 = currVertex[0];
            float y0 = currVertex[1];
            float x1 = nextVertex[0];
            float y1 = nextVertex[1];
            
            float[] edge = new float[4];
            edge[0] = (y0 <= y1) ? y0 : y1; //y-min
            edge[1] = (y0 >= y1) ? y0 : y1; //y-max
            edge[2] = (y0 <= y1) ? x0 : x1; //x-val
            edge[3] = (x1 - x0) / (y1 - y0); //1/m
            all_edges.add(edge);
        }
        
        //move edges from all_edges into global_edges
        for (int i = 0; i < all_edges.size(); i++) {
            float[] edge = all_edges.get(i);
            if (edge[3] == Float.NaN || edge[3] == Float.POSITIVE_INFINITY || edge[3] == Float.NEGATIVE_INFINITY) {
                continue;
            }
            global_edges.add(edge);
        }
        
        //sorts by increasing y-min and x-val
        Collections.sort(global_edges, new Comparator<float[]>() {
            @Override
            public int compare(float[] edge1, float[] edge2) {
                if (edge1[0] < edge2[0]) return -1;
                if (edge1[0] > edge2[0]) return 1;

                //y-min is equal
                if (edge1[2] < edge2[2]) return -1;
                if (edge1[2] > edge2[2]) return 1;

                //x-val is equal
                if (edge1[1] < edge2[1]) return -1;
                if (edge1[1] > edge2[1]) return 1;

                //y-max is equal
                return 0;
            }
        });
        
        //set scanline equal to lowest y-min
        scanline = (int) Math.floor(global_edges.get(0)[0]);
        
        //draw loop
        do {
            parity = false;
            
            //draw pixels
            int nextEdge = 0;
            for (int x = -320; x < 320; x++) {
                if ((nextEdge <= active_edges.size() - 1) && (x >= active_edges.get(nextEdge)[2])) {
                    nextEdge += 1;
                    parity = !parity;
                }
                if (parity) {
                    glBegin(GL_POINTS);
                        glVertex2f(x, scanline);
                    glEnd();
                }
            }
            
            scanline += 1;
            
            //remove edges from active edge table if necessary
            for (int i = 0; i < active_edges.size(); i++) {
                float[] edge = active_edges.get(i);
                if (edge[1] <= scanline) {
                    active_edges.remove(i);
                    i -= 1;
                }
            }

            //update x value by 1/m
            for (int i = 0; i < active_edges.size(); i++) {
                float[] edge = active_edges.get(i);
                edge[2] += edge[3];
            }
            
            //move edges with y-min <= scanline to active_edges
            int size = global_edges.size();
            for (int i = 0; i < size; i++) {
                if (global_edges.get(i)[0] <= scanline) {
                    active_edges.add(global_edges.get(i));
                    global_edges.remove(i);
                    size -= 1;
                    i -= 1;
                }
            }
            
            //re-sort active edge table
            Collections.sort(active_edges, new Comparator<float[]>() {
            @Override
            public int compare(float[] edge1, float[] edge2) {
                //if (edge1[0] < edge2[0]) return -1;
                //if (edge1[0] > edge2[0]) return 1;

                //y-min is equal
                if (edge1[2] < edge2[2]) return -1;
                if (edge1[2] > edge2[2]) return 1;

                //x-val is equal
                if (edge1[1] < edge2[1]) return -1;
                if (edge1[1] > edge2[1]) return 1;

                //y-max is equal
                return 0;
            }
        });
            
        } while (!active_edges.isEmpty());        
    }
}



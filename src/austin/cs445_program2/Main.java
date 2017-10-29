/*
 * Austin Morris
 * CS445: Program 2
 * Last modified: 10/28/2017
 */
package austin.cs445_program2;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
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
                    tempMatrix[0][0] = (float) Math.cos(Math.toRadians(Double.parseDouble(chars[1])));
                    tempMatrix[0][1] = (float) -Math.sin(Math.toRadians(Double.parseDouble(chars[1])));
                    tempMatrix[1][0] = (float) Math.sin(Math.toRadians(Double.parseDouble(chars[1])));
                    tempMatrix[1][1] = (float) Math.cos(Math.toRadians(Double.parseDouble(chars[1])));
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
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                glColor3f(1.0f, 1.0f, 0.0f);
                glPointSize(1);
                
                for (int i = 0; i < polygons.size(); i++) {
                    Polygon temp = (Polygon) polygons.get(i);
                    glColor3f(temp.color[0], temp.color[1], temp.color[2]);
                    glBegin(GL_POLYGON);
                        for (int j = 0; j < temp.vertices.size(); j++) {
                            float[] vertex = (float[]) temp.vertices.get(j);
                            glVertex2f(vertex[0], vertex[1]);
                        }
                    glEnd();
                }
                
                glBegin(GL_POLYGON);
                    
                glEnd();
                
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
}

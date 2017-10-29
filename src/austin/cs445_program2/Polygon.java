/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package austin.cs445_program2;

import java.util.ArrayList;

/**
 *
 * @author morri
 */
public class Polygon {
    public ArrayList vertices = new ArrayList();
    public float[] color = new float[3];
    public float[][] transform = new float[3][3];
    
    public Polygon() {
        transform[0][0] = 1f;
        transform[1][1] = 1f;
        transform[2][2] = 1f;
    }
    
    public void addVertex(float x, float y) {
        float[] vertex = {x, y};
        vertices.add(vertex);
    }
    
    public void setColor(float r, float g, float b) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    public static final double degToRad = Math.PI / 180;
    public Camera camera;
    public final BufferedImage image;
    public JFrame frame;
    private double a, b, c, d;

    public Window(Camera camera) {
        this.camera = camera;
        this.image = new BufferedImage(camera.width, camera.height, BufferedImage.TYPE_INT_RGB);
    }

    public void EraseWindow() {
        Graphics2D frame = image.createGraphics();

        frame.setColor(Color.BLACK);
        for (int x = 0; x < camera.width; x++)
            for (int y = 0; y < camera.height; y++)
                image.setRGB(x, y, Color.BLACK.getRGB());
    }

    private double Distance(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2) + Math.pow(v2.z - v1.z, 2));
    }

    private double ProdScal(Vertex v1, Vertex v2, Vertex v3) {
        return (v2.x - v1.x) * (v3.x - v1.x) + (v2.y - v1.y) * (v3.y - v1.y) + (v2.z - v1.z) * (v3.z - v1.z);
    }

   public void DrawFrame() {
       EraseWindow();
       int a = 0;
       for (Vertex vertex : vertices) {
           a++;
           double angleYPoint = Math.atan((vertex.y - camera.position.y) / ((vertex.x - camera.position.x) * Math.cos(camera.rotation.x) + (vertex.z - camera.position.z) * Math.sin(camera.rotation.x))) - camera.rotation.y;
           double posY = camera.near * Math.tan(angleYPoint);
           int y = (int) (camera.height / 2 - posY * camera.height / camera.heightInSpace);

           double nearYOnXZPlan = Math.sqrt(Math.pow(camera.near, 2) + Math.pow(posY, 2)) * Math.cos(angleYPoint + camera.rotation.y);
           double angleXPoint = Math.atan((vertex.z - camera.position.z) / (vertex.x - camera.position.x)) - camera.rotation.x;
           double posX = Math.tan(angleXPoint) * nearYOnXZPlan;
           int x = (int) (camera.width / 2 - posX * camera.width / camera.widthInSpace);

           vertex.coords = new Coords(x, y);

           Vertex p1 = new Vertex(camera.position.x, 0, camera.position.z);
           Vertex p2 = new Vertex(camera.position.x + Math.cos(camera.rotation.x), 0, camera.position.z + Math.sin(camera.rotation.x));
           double angleBehind = Math.acos(ProdScal(p1, p2, vertex) / (Distance(p1, p2) * Distance(p1, vertex)));

           vertex.behindScreen = angleBehind > Math.PI/2;
       }

       for (Edge edge : edges) {
           Graphics2D frame = image.createGraphics();
           frame.setColor(edge.color);
           if (!edge.start.behindScreen && !edge.end.behindScreen)
                frame.drawLine(edge.start.coords.x, edge.start.coords.y, edge.end.coords.x, edge.end.coords.y);
       }
   }
}

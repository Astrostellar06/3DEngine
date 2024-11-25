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
       double cosRotationX = Math.cos(camera.rotation.x);
       double sinRotationX = Math.sin(camera.rotation.x);
       double cosRotationY = Math.cos(camera.rotation.y);
       double sinRotationY = Math.sin(camera.rotation.y);
       double cameraBarycenterX = cosRotationX * cosRotationY * camera.near;
       double cameraBarycenterY = sinRotationY * camera.near;
       double cameraBarycenterZ = sinRotationX * cosRotationY * camera.near;
       Vertex cameraToBarycenter = new Vertex(camera.position.x + cameraBarycenterX,camera.position.y +  cameraBarycenterY, camera.position.z + cameraBarycenterZ);
       for (Vertex vertex : vertices) {
           double angleYPoint = Math.atan2((vertex.y - camera.position.y), ((vertex.x - camera.position.x + ((vertex.x - camera.position.x) == 0 ? 1e-6 : 0)) * cosRotationX + (vertex.z - camera.position.z) * sinRotationX)) - camera.rotation.y;
           double posY = camera.near * Math.tan(angleYPoint);
           int y = (int) (camera.height / 2 - posY * camera.height / camera.heightInSpace);

           double nearYOnXZPlan = Math.sqrt(Math.pow(camera.near, 2) + Math.pow(posY, 2)) * Math.cos(angleYPoint + camera.rotation.y);
           double angleXPoint = Math.atan((vertex.z - camera.position.z) / (vertex.x - camera.position.x + ((vertex.x - camera.position.x) == 0 ? 1e-6 : 0))) - camera.rotation.x;
           double posX = Math.tan(angleXPoint) * nearYOnXZPlan;
           int x = (int) (camera.width / 2 - posX * camera.width / camera.widthInSpace);

           vertex.behindScreen = ProdScal(camera.position, cameraToBarycenter, vertex) < 0;
           vertex.coords = new Coords(x, y);
       }

       for (Edge edge : edges) {
           Graphics2D frame = image.createGraphics();
           frame.setColor(edge.color);
           if (!edge.start.behindScreen && !edge.end.behindScreen)
                frame.drawLine(edge.start.coords.x, edge.start.coords.y, edge.end.coords.x, edge.end.coords.y);
       }
   }
}

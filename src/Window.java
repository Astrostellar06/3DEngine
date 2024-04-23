import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    public static final double degToRad = Math.PI / 180;
    public Camera camera;
    public int largeur;
    public int hauteur;
    private Vertex[] corners;
    private Vertex cameraBarycenter;
    public final BufferedImage image;
    public JFrame frame;

    public Window(int largeur, int hauteur, Camera camera) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.camera = camera;
        this.image = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

    }

    public void EraseWindow() {
        Graphics2D frame = image.createGraphics();

        frame.setColor(Color.BLACK);
        for (int x = 0; x < largeur; x++)
            for (int y = 0; y < hauteur; y++)
                image.setRGB(x, y, Color.BLACK.getRGB());
    }

    private double Distance(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2) + Math.pow(v2.z - v1.z, 2));
    }

    private Vertex FindCameraBarycenter(Vertex position, Vertex rotation, double normal) {
        double cameraBarycenterX = Math.cos(rotation.x) * Math.cos(rotation.y) * normal;
        double cameraBarycenterY = Math.sin(rotation.y) * normal;
        double cameraBarycenterZ = Math.sin(rotation.x) * Math.cos(rotation.y) * normal;
        return new Vertex(position.x + cameraBarycenterX, position.y + cameraBarycenterY, position.z + cameraBarycenterZ);
    }

    private void FindIntersection(Vertex p1, Vertex p2, Vertex p3, Vertex cameraPosition, Vertex vertex) {
        FindIntersection(p1, p2, p3, cameraPosition, vertex, false);
    }

    private void FindIntersection(Vertex p1, Vertex p2, Vertex p3, Vertex startVertex, Vertex endVertex, boolean recalculate) {
        // P1 doit être le barycentre de la caméra
        /*double a = (p3.z - p1.z - ((p2.z - p1.z) * (p3.y - p1.y)) / (p2.y - p1.y)) / ((p3.x - p1.x) + ((p2.x - p1.x) * (p1.y - p3.y)) / (p2.y - p1.y));
        double b = (p2.z - p1.z - a * (p2.x - p1.x)) / (p2.y - p1.y);
        double c = p1.z - b * p1.y - a * p1.x;*/

        // Plan : ax + by + cz + d = 0
        // Droite : x = startVertex.x + q2.x * t // y = startVertex.y + q2.y * t // z = startVertex.z + q2.z * t

        // On a deux vecteurs directeurs du plan : p2 - p1 et p3 - p1
        // On fait le produit vectoriel de ces deux vecteurs pour trouver le vecteur normal au plan
        double a = (p2.y - p1.y) * (p3.z - p1.z) - (p2.z - p1.z) * (p3.y - p1.y);
        double b = (p2.z - p1.z) * (p3.x - p1.x) - (p2.x - p1.x) * (p3.z - p1.z);
        double c = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);

        double d = -a * p1.x - b * p1.y - c * p1.z;

        // t est le paramètre de l'équation de la droite
        // startVertex sert de point de départ et q2 de vecteur directeur (q2 = endVertex - startVertex)
        // On remplace x, y et z dans l'équation du plan et on trouve t
        Vertex q2 = new Vertex(endVertex.x - startVertex.x, endVertex.y - startVertex.y, endVertex.z - startVertex.z);
        double tParam = (a * q2.x + b * q2.y + c * q2.z) != 0 ? -(a * startVertex.x + b * startVertex.y + c * startVertex.z + d) / (a * q2.x + b * q2.y + c * q2.z) : 0;
        // On remplace t dans l'équation de la droite pour trouver le point d'intersection
        Vertex intersection = new Vertex(startVertex.x + q2.x * tParam, startVertex.y + q2.y * tParam, startVertex.z + q2.z * tParam);
        if (!recalculate) {
            endVertex.behindFocalPoint = (startVertex.x <= Math.max(intersection.x, endVertex.x) && startVertex.x >= Math.min(intersection.x, endVertex.x) &&
                    startVertex.y <= Math.max(intersection.y, endVertex.y) && startVertex.y >= Math.min(intersection.y, endVertex.y) &&
                    startVertex.z <= Math.max(intersection.z, endVertex.z) && startVertex.z >= Math.min(intersection.z, endVertex.z));
            endVertex.behindScreen = (endVertex.x <= Math.max(intersection.x, startVertex.x) && endVertex.x >= Math.min(intersection.x, startVertex.x) &&
                    endVertex.y <= Math.max(intersection.y, startVertex.y) && endVertex.y >= Math.min(intersection.y, startVertex.y) &&
                    endVertex.z <= Math.max(intersection.z, startVertex.z) && endVertex.z >= Math.min(intersection.z, startVertex.z)) || endVertex.behindFocalPoint;
        }

        // Maintenant on veut trouver les coordonnées de l'intersection dans le plan de la caméra
        double angleX = Math.atan((intersection.z - p1.z) / (intersection.x - p1.x));
        if (intersection.x < p1.x)
            angleX += Math.PI;
        angleX -= camera.rotation.x;
        angleX = angleX % (2 * Math.PI);
        if (angleX < 0)
            angleX += 2 * Math.PI;
        double distanceY, distanceX;
        if (Math.abs(Math.cos(camera.rotation.y)) <= Math.pow(10, -4)) {
            Vertex endVertex2 = !recalculate ? endVertex : intersection;
            distanceY = ((endVertex2.x - camera.position.x) * (Math.cos(camera.rotation.x)) + (endVertex2.z - camera.position.z) * (Math.sin(camera.rotation.x))) / ((endVertex2.y - camera.position.y) != 0 ? (endVertex2.y - camera.position.y) : 1) * camera.near;
            distanceX = ((endVertex2.z - camera.position.z) * (Math.cos(camera.rotation.x)) - (endVertex2.x - camera.position.x) * (Math.sin(camera.rotation.x))) / ((endVertex2.y - camera.position.y) != 0 ? (endVertex2.y - camera.position.y) : 1) * camera.near;
            if (endVertex2.y - camera.position.y > 0)
                distanceX = -distanceX;
        } else {
            distanceY = (p1.y - intersection.y) / Math.cos(camera.rotation.y);
            distanceX = Math.sqrt(Math.abs(Math.pow(Distance(p1, intersection), 2) - Math.pow(distanceY, 2))) * (angleX / degToRad > 180 ? 1 : -1);
        }

        double Y = distanceY * Math.cos(camera.rotation.z) + distanceX * Math.sin(camera.rotation.z);
        distanceX = distanceX * Math.cos(camera.rotation.z) - distanceY * Math.sin(camera.rotation.z);
        distanceY = Y;

        //double verticalFov = (double) hauteur/largeur * camera.fov;
        double largeurEcran = 2 * camera.near * Math.tan(camera.fov / 2 * degToRad);
        double hauteurEcran = 2 * Math.tan(camera.fov * degToRad / 2) * camera.near * ((double) hauteur/ (double) largeur);

        int coordX = (int) (largeur / 2 + Math.round((double) largeur / largeurEcran * distanceX) * (endVertex.behindFocalPoint ? -1 : 1));
        int coordY = (int) (hauteur / 2 + Math.round((double) hauteur / hauteurEcran * distanceY) * (endVertex.behindFocalPoint ? -1 : 1));

        if (!recalculate)
            endVertex.coords = new Coords(coordX, coordY);
        else
            startVertex.coords = new Coords(coordX, coordY);

        endVertex.visible = !(endVertex.behindScreen || coordX < 0 || coordX >= largeur || coordY < 0 || coordY >= hauteur);
    }

    private Vertex[] FindCameraCorner(Vertex position, Vertex rotation, double near, double verticalFov, double horizontalFov) {
        // Calcul de la largeur et de la hauteur de l'écran
        double largeurEcran = 2 * near * Math.tan(horizontalFov / 2);
        double hauteurEcran = 2 * near * Math.tan(verticalFov / 2);

        // Calcul du delta x, delta y et delta z qui correspondent à la distance entre le barycentre de la caméra et un coin de l'écran

        // Delta Y : à rajouter au barycentre de la caméra pour obtenir le haut de l'écran
        double deltaY = Math.cos(rotation.y) * hauteurEcran / 2;

        // Delta XZ : longueur entre le barycentre de la caméra et le haut de la camera, projeté sur le plan XZ
        double deltaXZ = Math.sin(rotation.y) * hauteurEcran / 2;

        // Delta X1 : longueur entre le barycentre de la caméra et le haut de la camera, projeté sur le plan X1
        double deltaX1 = Math.cos(rotation.x) * deltaXZ;
        // Delta Z1 : longueur entre le barycentre de la caméra et le haut de la camera, projeté sur le plan Z1
        double deltaZ1 = Math.sin(rotation.x) * deltaXZ;

        // Delta Z et delta X : à rajouter au barycentre de la caméra pour obtenir le bord de l'écran
        double deltaZ = Math.cos(rotation.x) * largeurEcran / 2;
        double deltaX = Math.sin(rotation.x) * largeurEcran / 2;

        return new Vertex[]{
                new Vertex(position.x - deltaX1 - deltaX, position.y + deltaY, position.z - deltaZ1 + deltaZ),
                new Vertex(position.x - deltaX1 + deltaX, position.y + deltaY, position.z - deltaZ1 - deltaZ),
                new Vertex(position.x + deltaX1 - deltaX, position.y - deltaY, position.z + deltaZ1 + deltaZ),
                new Vertex(position.x + deltaX1 + deltaX, position.y - deltaY, position.z + deltaZ1 - deltaZ),
        };
    }

    private void EraseWindowFast() {
        Graphics2D frame = image.createGraphics();
        frame.setColor(Color.BLACK);
        frame.fillRect(0, 0, largeur, hauteur);
    }

    private void DrawEdge(BufferedImage image, Edge edge) {
        Graphics2D frame = image.createGraphics();
        frame.setColor(edge.color);
        if (edge.start.behindScreen ^ edge.end.behindScreen)
            FindIntersection(cameraBarycenter, corners[0], corners[1], edge.start.behindScreen ? edge.start : edge.end, edge.start.behindScreen ? edge.end : edge.start, true);
        if (!edge.start.behindScreen || !edge.end.behindScreen)
            frame.drawLine(edge.start.coords.x, edge.start.coords.y, edge.end.coords.x, edge.end.coords.y);
    }

    public void DrawFrame() {

        cameraBarycenter = FindCameraBarycenter(camera.position, camera.rotation, camera.near);
        double verticalFov = 2 * Math.atan(Math.tan(camera.fov * degToRad / 2) * ((double) hauteur/largeur));
        corners = FindCameraCorner(cameraBarycenter, camera.rotation, camera.near, verticalFov * degToRad, camera.fov * degToRad);

        for (Vertex vertex : vertices)
            FindIntersection(cameraBarycenter, corners[0], corners[1], camera.position, vertex);

        EraseWindowFast();
        for (Edge edge : edges)
            DrawEdge(image, edge);
    }
}

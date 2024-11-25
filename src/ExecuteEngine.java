import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ExecuteEngine {
    private static final int fps = 60;
    private static final int tickSpeed = 1000;

    public static void main(String[] args) {
        Window window = new Window( new Camera(new Vertex(0, 0, 0), new Vertex(0 * Window.degToRad, 0 * Window.degToRad, 0), 1000, 700, 90, 0.1f));
        StartWindow(window);

        long time = 0;
        long lastFrame = System.currentTimeMillis();
        long lastTick = System.nanoTime();

        float pas = 0.005f;
        float anglePas = 0.1f;

        do {
            if (System.nanoTime() - lastTick > Math.pow(10, 9)/tickSpeed ) {
                lastTick = System.nanoTime();
                if (Keyboard.isKeyPressed(KeyEvent.VK_Q)) {
                    if (window.camera.local) {
                        window.camera.position.z += pas * Math.cos(window.camera.rotation.x);
                        window.camera.position.x -= pas * Math.sin(window.camera.rotation.x);
                    } else {
                        window.camera.position.z += pas;
                    }
                }
                if (Keyboard.isKeyPressed(KeyEvent.VK_D)) {
                    if (window.camera.local) {
                        window.camera.position.z -= pas * Math.cos(window.camera.rotation.x);
                        window.camera.position.x += pas * Math.sin(window.camera.rotation.x);
                    } else {
                        window.camera.position.z -= pas;
                    }
                }
                if (Keyboard.isKeyPressed(KeyEvent.VK_Z)) {
                    if (window.camera.local) {
                        window.camera.position.y += pas * Math.sin(window.camera.rotation.y);
                        window.camera.position.z += pas * Math.cos(window.camera.rotation.y) * Math.sin(window.camera.rotation.x);
                        window.camera.position.x += pas * Math.cos(window.camera.rotation.y) * Math.cos(window.camera.rotation.x);
                    } else {
                        window.camera.position.x += pas;
                    }
                }
                if (Keyboard.isKeyPressed(KeyEvent.VK_S)) {
                    if (window.camera.local) {
                        window.camera.position.y -= pas * Math.sin(window.camera.rotation.y);
                        window.camera.position.z -= pas * Math.cos(window.camera.rotation.y) * Math.sin(window.camera.rotation.x);
                        window.camera.position.x -= pas * Math.cos(window.camera.rotation.y) * Math.cos(window.camera.rotation.x);
                    } else {
                        window.camera.position.x -= pas;
                    }
                }
                if (Keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
                    window.camera.position.y += pas;
                }
                if (Keyboard.isKeyPressed(KeyEvent.VK_SHIFT)) {
                    window.camera.position.y -= pas;
                }
                if (Keyboard.isKeyPressed(KeyEvent.VK_UP))
                    window.camera.rotation.y += anglePas * Window.degToRad;
                if (Keyboard.isKeyPressed(KeyEvent.VK_DOWN))
                    window.camera.rotation.y -= anglePas * Window.degToRad;
                if (Keyboard.isKeyPressed(KeyEvent.VK_LEFT))
                    window.camera.rotation.x += anglePas * Window.degToRad;
                if (Keyboard.isKeyPressed(KeyEvent.VK_RIGHT))
                    window.camera.rotation.x -= anglePas * Window.degToRad;
                if (Keyboard.isKeyPressed(KeyEvent.VK_J))
                    window.camera.rotation.z += anglePas * Window.degToRad;
                if (Keyboard.isKeyPressed(KeyEvent.VK_L))
                    window.camera.rotation.z -= anglePas * Window.degToRad;
                if (Keyboard.isKeyPressed(KeyEvent.VK_UP) || Keyboard.isKeyPressed(KeyEvent.VK_DOWN))
                    window.camera.rotation.y = window.camera.rotation.y % (2 * Math.PI);
                if (Keyboard.isKeyPressed(KeyEvent.VK_LEFT) || Keyboard.isKeyPressed(KeyEvent.VK_RIGHT))
                    window.camera.rotation.x = window.camera.rotation.x % (2 * Math.PI);
                if (Keyboard.isKeyPressed(KeyEvent.VK_J) || Keyboard.isKeyPressed(KeyEvent.VK_L))
                    window.camera.rotation.z = window.camera.rotation.z % (2 * Math.PI);

                if (Keyboard.isKeyPressed(KeyEvent.VK_ENTER)) {
                    if (System.currentTimeMillis() - time >= 100) {
                        window.camera.local = !window.camera.local;
                        time = System.currentTimeMillis();
                    }
                }

                if (window.camera.rotation.x > Math.PI)
                    window.camera.rotation.x -= 2 * Math.PI;
                if (window.camera.rotation.y > Math.PI)
                    window.camera.rotation.y -= 2 * Math.PI;
                if (window.camera.rotation.z > Math.PI)
                    window.camera.rotation.z -= 2 * Math.PI;
                if (window.camera.rotation.x < -Math.PI)
                    window.camera.rotation.x += 2 * Math.PI;
                if (window.camera.rotation.y < -Math.PI)
                    window.camera.rotation.y += 2 * Math.PI;
                if (window.camera.rotation.z < -Math.PI)
                    window.camera.rotation.z += 2 * Math.PI;
            }

            if (System.currentTimeMillis() - lastFrame >= 1000/fps) {
                window.DrawFrame();
                window.frame.repaint();
                lastFrame = System.currentTimeMillis();
            }
        } while (!Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE));
    }

    public static void StartWindow(Window window) {
        // Créer une étiquette pour afficher l'image
        JLabel label = new JLabel(new ImageIcon(window.image));

        // Créer une fenêtre JFrame pour afficher l'étiquette
        JFrame fenetre = new JFrame("Render 3D");
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.add(label); // Ajouter l'étiquette à la fenêtre
        fenetre.pack(); // Ajuster la taille de la fenêtre pour s'adapter à l'image
        fenetre.setVisible(true);
        window.frame = fenetre;

        InitializeScene(window);
        window.EraseWindow();
        window.DrawFrame();
        window.frame.repaint();
    }

    public static void InitializeScene(Window window) {
        Vertex v1 = new Vertex(1, .5f, -.5f);
        Vertex v2 = new Vertex(1, .5f, .5f);
        Vertex v3 = new Vertex(2, .5f, -.5f);
        Vertex v4 = new Vertex(2, .5f, .5f);
        Vertex v5 = new Vertex(1, 1.5f, -.5f);
        Vertex v6 = new Vertex(1, 1.5f, .5f);
        Vertex v7 = new Vertex(2, 1.5f, -.5f);
        Vertex v8 = new Vertex(2, 1.5f, .5f);

        Edge e1 = new Edge(v1, v2);
        Edge e2 = new Edge(v1, v3);
        Edge e3 = new Edge(v2, v4);
        Edge e4 = new Edge(v3, v4);
        Edge e5 = new Edge(v1, v5);
        Edge e6 = new Edge(v2, v6);
        Edge e7 = new Edge(v3, v7);
        Edge e8 = new Edge(v4, v8);
        Edge e9 = new Edge(v5, v6);
        Edge e10 = new Edge(v5, v7);
        Edge e11 = new Edge(v6, v8);
        Edge e12 = new Edge(v7, v8);

        Vertex v9 = new Vertex(1, 1.5f, -.5f);
        Vertex v10 = new Vertex(1, 1.5f, .5f);
        Vertex v11 = new Vertex(2, 1.5f, -.5f);
        Vertex v12 = new Vertex(2, 1.5f, .5f);
        Vertex v13 = new Vertex(1.5f, 2, 0);

        Edge e13 = new Edge(v9, v10);
        Edge e14 = new Edge(v9, v11);
        Edge e15 = new Edge(v10, v12);
        Edge e16 = new Edge(v11, v12);
        Edge e17 = new Edge(v9, v13);
        Edge e18 = new Edge(v10, v13);
        Edge e19 = new Edge(v11, v13);
        Edge e20 = new Edge(v12, v13);

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();

        for (int i = -10; i <= 10; i++) {
            for (int j = -10; j <= 10; j++) {
                Vertex vertex = new Vertex(i, 0, j);
                vertices.add(vertex);
            }
        }

        edges = new ArrayList<Edge>();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                edges.add(new Edge(vertices.get(i*21 + j), vertices.get(i*21 + j + 1), Color.GREEN));
                edges.add(new Edge(vertices.get((i+1)*21 + j), vertices.get(i*21 + j), Color.GREEN));
            }
            edges.add(new Edge(vertices.get((i+1)*21 - 1), vertices.get((i+2)*21 - 1), Color.GREEN));
            edges.add(new Edge(vertices.get(20 * 21 + i), vertices.get(20*21 + i + 1), Color.GREEN));
        }

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);
        vertices.add(v5);
        vertices.add(v6);
        vertices.add(v7);
        vertices.add(v8);

        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);
        edges.add(e5);
        edges.add(e6);
        edges.add(e7);
        edges.add(e8);
        edges.add(e9);
        edges.add(e10);
        edges.add(e11);
        edges.add(e12);

        window.vertices = vertices;
        window.edges = edges;
        /*vertices = new ArrayList<Vertex>() {
            {
                add(v1);
                add(v2);
                add(v3);
                add(v4);
                add(v5);
                /*add(v6);
                add(v7);
                add(v8);*/
            /*}
        };

        edges = new ArrayList<Edge>() {
            {
                add(e1);
                add(e2);
                add(e3);
                add(e4);
                add(e5);
                add(e6);
                add(e7);
                add(e8);
                /*add(e9);
                add(e10);
                add(e11);
                add(e12);*/
        //}
        //};
    }
}

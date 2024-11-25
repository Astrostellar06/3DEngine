import java.awt.*;

public class Edge {
    public Vertex start;
    public Vertex end;
    public Color color;

    public Edge(Vertex start, Vertex end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Edge(Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
        this.color = Color.RED;
    }
}

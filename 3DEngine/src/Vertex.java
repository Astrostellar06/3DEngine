public class Vertex {
    public double x;
    public double y;
    public double z;
    public Coords coords;
    public boolean visible;
    public boolean behindScreen;
    public boolean behindFocalPoint;

    public Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

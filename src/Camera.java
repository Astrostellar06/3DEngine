public class Camera {
    public Vertex position;
    public Vertex rotation;
    public double fov;
    public double near;
    public boolean local = true;

    public Camera(Vertex position, Vertex rotation, double fov, double near) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
        this.near = near;
    }
}

public class Camera {
    public Vertex position;
    public Vertex rotation;
    public int width;
    public double widthInSpace;
    public int height;
    public double heightInSpace;
    public double fov;
    public double verticalFov;
    public double near;
    public boolean local = true;

    public Camera(Vertex position, Vertex rotation, int width, int height, double fov, double near) {
        this.position = position;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
        this.fov = fov;
        this.widthInSpace = near * Math.tan(fov / 2);
        this.heightInSpace = this.widthInSpace * height / width;
        this.verticalFov = Math.atan(heightInSpace / near) * 2;
        this.near = near;
    }
}

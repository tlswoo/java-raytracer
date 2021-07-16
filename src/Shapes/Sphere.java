package Shapes;

import org.opencv.core.Point3;

public class Sphere {
    Point3 center;
    double radius;
    Color color;
    double specularity;
    double reflectivity;

    public Sphere(Point3 center, double radius, Color color, double specularity, double reflectivity) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specularity = specularity;
        this.reflectivity = reflectivity;
    }

    public Point3 getCenter() {
        return this.center;
    }

    public double getRadius() {
        return this.radius;
    }

    public Color getColor() {
        return this.color;
    }

    public double getSpecularity() { return this.specularity; }

    public double getReflectivity() { return this.reflectivity; }

    public void makeMatte() {
        this.specularity = -1;
    }

    // Modifier methods for circle origin/color
//    void setCenter(Point3 center) {
//        this.center = center;
//    }
//
//    void setColor(Shapes.MyColor color) {
//        this.color = color;
//    }
}

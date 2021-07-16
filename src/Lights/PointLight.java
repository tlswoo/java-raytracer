package Lights;

import org.opencv.core.Point3;

public class PointLight extends Light {
    public PointLight(double intensity, Point3 position) {
        this.type = LightType.POINT;
        this.intensity = intensity;
        this.position = position;
    }

    public Point3 getPosition() {
        return this.position;
    }

    public Point3 getDirection() {
        throw new UnsupportedOperationException("Point lights do not have a direction");
    }
}

package Lights;

import org.opencv.core.Point3;

public class DirectionalLight extends Light {
    public DirectionalLight(double intensity, Point3 direction) {
        this.type = LightType.DIRECTIONAL;
        this.intensity = intensity;
        this.direction = direction;
    }

    public Point3 getPosition() {
        throw new UnsupportedOperationException("Directional lights do not have a position");
    }

    public Point3 getDirection() {
        return this.direction;
    }
}

package Lights;

import org.opencv.core.Point3;

public class AmbientLight extends Light {
    public AmbientLight(double intensity) {
        this.type = LightType.AMBIENT;
        this.intensity = intensity;
    }

    public Point3 getPosition() {
        throw new UnsupportedOperationException("Ambient lights do not have a position");
    }

    public Point3 getDirection() {
        throw new UnsupportedOperationException("Ambient lights do not have a direction");
    }
}

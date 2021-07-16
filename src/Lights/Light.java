package Lights;

import org.opencv.core.Point3;

public abstract class Light {
    LightType type;
    double intensity;
    Point3 position, direction;

    public void turnOff() {
        this.intensity = 0;
    }

    public LightType getType() {
        return this.type;
    }

    public double getIntensity() {
        return this.intensity;
    }

    public abstract Point3 getPosition();
    public abstract Point3 getDirection();
}

package Shapes;

import org.opencv.core.Scalar;

public class Color {
    // Note that colorValue is stored in BGR order
    double[] colorValue = new double[]{0, 0, 0};

    public Color(double r, double g, double b) {
        colorValue[0] = b;
        colorValue[1] = g;
        colorValue[2] = r;
    }

    public Color scaleColor(double factor) {
        return new Color(this.colorValue[2] * factor, this.colorValue[1] * factor, this.colorValue[0] * factor);
    }

    public Color addColor(Color other) {
        double[] otherColorValue = other.getBGR();
        return new Color(this.colorValue[2] + otherColorValue[2], this.colorValue[1] + otherColorValue[1], this.colorValue[0] + otherColorValue[0]);
    }

    public Scalar asScalar() {
        return new Scalar(this.colorValue[0], this.colorValue[1], this.colorValue[2]);
    }

    public double[] getBGR() {
        return this.colorValue;
    }
}

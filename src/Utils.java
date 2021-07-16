import org.opencv.core.Point3;

public class Utils {
    static Point3 getVector(Point3 to, Point3 from) {
        return new Point3(to.x - from.x, to.y - from.y, to.z - from.z);
    }

    static double getMagnitude(Point3 vec) {
        return Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.y, 2) + Math.pow(vec.z, 2));
    }

    static Point3 getSum(Point3 vec1, Point3 vec2) {
        return new Point3(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
    }

    static Point3 scaleVector(Point3 vec, double factor) {
        return new Point3(vec.x * factor, vec.y * factor, vec.z * factor);
    }

    // Pair class for intersectRaySphere method
    static class Pair<S, T> {
        S first;
        T second;

        Pair(S first, T second) {
            this.first = first;
            this.second = second;
        }
    }

}

import Shapes.Color;
import Shapes.Sphere;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;

import java.util.Arrays;
import java.util.List;
import java.lang.Math;

import Lights.*;

public class Raytracer {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    // Define all colors here
    static Color BLACK = new Color(0, 0, 0);
    static Color WHITE = new Color(255, 255, 255);
    static Color RED = new Color(255, 0, 0);
    static Color GREEN = new Color(0, 255, 0);
    static Color BLUE = new Color(0, 0, 255);
    static Color YELLOW = new Color(253, 253, 150);
    static Color PURPLE = new Color(128, 0, 128);
    static Color PERIWINKLE = new Color(204, 204, 255);
    static Color SAGE = new Color(178, 172, 136);

    // Canvas and viewport properties
    private static final Color BACKGROUND_COLOR = BLACK;

    private static final double Cw = 1920;
    private static final double Ch = 1080;

    // Viewport dimensions
    private static final double d = 1;
    private static final double Vw = 1.78;
    private static final double Vh = 1;

    static void putPixel(Mat canvas, Point p, Color color) {
        int row, col;
        row = (int)(Ch / 2 - p.y);
        col = (int)(Cw / 2 + p.x);
        canvas.put(row, col, color.getBGR());
    }

    // Conversion between a pixel's coordinate on the canvas, to each "square" on the viewport
    static Point3 canvasToViewport(Point p) {
        return new Point3(p.x * Vw / Cw, p.y * Vh / Ch, d);
    }

    static Utils.Pair<Double, Double> intersectRaySphere(Point3 O, Point3 D, Sphere sphere) {
        double radius = sphere.getRadius();
        Point3 CO = Utils.getVector(O, sphere.getCenter());
        Utils.Pair<Double, Double> solutions = new Utils.Pair<Double, Double>(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        double a, b, c;
        a = D.dot(D);
        b = 2 * CO.dot(D);
        c = CO.dot(CO) - (radius * radius);

        double discriminant = (b * b) - 4 * a * c;
        if (discriminant >= 0) {
            solutions.first = (-b + Math.sqrt(discriminant)) / (2 * a);
            solutions.second = (-b - Math.sqrt(discriminant)) / (2 * a);
        }

        return solutions;
    }

    // t is the distance on the ray from the current point/camera origin
    static Color traceRay(Point3 O, Point3 D, double t_min, double t_max, List<Sphere> spheres, List<Light> lights) {
        Utils.Pair<Sphere, Double> sphereInfo = closestIntersection(O, D, t_min, t_max, spheres);
        Sphere closest_sphere = sphereInfo.first;
        double closest_t = sphereInfo.second;

        if (closest_sphere == null) {
            return BACKGROUND_COLOR;
        }

        // Handle lighting calculations here
        Point3 P = Utils.getSum(O, Utils.scaleVector(D, closest_t));
        Point3 N = Utils.getVector(P, closest_sphere.getCenter());
        N = Utils.scaleVector(N, 1 / Utils.getMagnitude(N));

        return closest_sphere.getColor().scaleColor(computeLighting(P, N, Utils.scaleVector(D, -1), closest_sphere.getSpecularity(), spheres, lights));
    }

    // LIGHTING //////////////////////////////////////////////////
    static Utils.Pair<Sphere, Double> closestIntersection(Point3 O, Point3 D, double t_min, double t_max, List<Sphere> spheres) {
        double closest_t = Double.POSITIVE_INFINITY;
        Sphere closest_sphere = null;
        for (Sphere sph : spheres) {
            Utils.Pair<Double, Double> solutions = intersectRaySphere(O, D, sph);
            double t1, t2;
            t1 = solutions.first;
            t2 = solutions.second;
            if (t1 >= t_min && t1 <= t_max && t1 < closest_t) {
                closest_t = t1;
                closest_sphere = sph;
            }
            if (t2 >= t_min && t2 <= t_max && t2 < closest_t) {
                closest_t = t2;
                closest_sphere = sph;
            }
        }
        return new Utils.Pair<Sphere, Double>(closest_sphere, closest_t);
    }

    // Compute the lighting of point P given its normal N
    static double computeLighting(Point3 P, Point3 N, Point3 V, double specularity, List<Sphere> spheres, List<Light> lights) {
        double intensity = 0;
        double t_max = 0;
        for (Light light : lights) {
            LightType ltype = light.getType();
            if (ltype == LightType.AMBIENT) {
                intensity += light.getIntensity();
            } else {
                Point3 L;
                if (ltype == LightType.POINT) {
                    L = Utils.getVector(light.getPosition(), P);
                    t_max = 1;
                } else { // Assumed directional light
                    L = light.getDirection();
                    t_max = Double.POSITIVE_INFINITY;
                }

                // Shadow calculations
                Utils.Pair<Sphere, Double> shadowInfo = closestIntersection(P, L, 0.001, t_max, spheres);
                Sphere shadowCaster = shadowInfo.first;
                if (shadowCaster != null) {
                    continue; // Move on to next light; shadow is cast, so intensity is set at 0
                }

                // Diffuse reflection
                double ndl = N.dot(L); // N dot L
                if (ndl > 0) {
                    intensity += light.getIntensity() * (ndl / (Utils.getMagnitude(N) * Utils.getMagnitude(L)));
                }

                // Specular reflection (specularity of -1 => matte object)
                if (specularity != -1) {
                    // R is the vector representing the reflection from the light L
                    Point3 R = Utils.getVector(Utils.scaleVector(N, 2 * N.dot(L)), L);
                    double rdv = R.dot(V); // R dot V
                    if (rdv > 0) {
                        intensity += light.getIntensity() * Math.pow(rdv / (Utils.getMagnitude(R) * Utils.getMagnitude(V)), specularity);
                    }
                }
            }
        }
        return intensity;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);

        // Initialize canvas
        Mat canvas = new Mat((int)Ch, (int)Cw, CvType.CV_8UC3, BACKGROUND_COLOR.asScalar());

        // Initialize camera origin
        Point3 O = new Point3(0, 0, 0);

        // Initialize spheres
        Sphere s1 = new Sphere(new Point3(0, 0, 3), 0.5, RED, 500, 0.2);
        Sphere s2 = new Sphere(new Point3(1, 0.1, 3.5), 0.5, PERIWINKLE, 500, 0.3);
        Sphere s3 = new Sphere(new Point3(-1, 0.1, 3.5), 0.5, SAGE, 10, 0.4);
        Sphere s4 = new Sphere(new Point3(0, -25, 6), 25, PURPLE, 1000, 0.5);
        List<Sphere> spheres = Arrays.asList(s1, s2, s3, s4);

//        for (Sphere s : spheres) {
//            s.makeMatte();
//        }

        // Initialize lights
        AmbientLight l1 = new AmbientLight(0.2);
        PointLight l2 = new PointLight(0.6, new Point3(2, 1, 0));
        DirectionalLight l3 = new DirectionalLight(0.2, new Point3(1, 4, 4));
        List<Light> lights = Arrays.asList(l1, l2, l3);

        // Main raytracing procedure
        for (int x = (int)(-Cw / 2); x < (int)(Cw / 2); x++) {
            for (int y = (int)(-Ch / 2); y < (int)(Ch / 2); y++) {
                // nextPoint refers to a canvas coordinate (centered at 0)
                Point nextPoint = new Point(x, y);
                Point3 D = canvasToViewport(nextPoint);
                Color traceColor = traceRay(O, D, 1, Double.POSITIVE_INFINITY, spheres, lights);
                putPixel(canvas, nextPoint, traceColor);
            }
        }

        // Show window and handle exit
        HighGui.imshow("Raytracer", canvas);
        HighGui.waitKey();
        System.exit(0);
    }
}

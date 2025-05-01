package Handlers;

public class Vector2 {

    public double x,y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double length, double theta, boolean isRadians) {
        if(!isRadians)
            theta = Math.toRadians(theta);

        theta -= Math.PI/2;

        this.x = length * Math.cos(theta);
        this.y = length * Math.sin(theta);
    }

    public void add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void subtract(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void multiplyScalar(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public void normalize() {
        double length = Math.sqrt(x * x + y * y);
        if (length != 0) {
            this.x /= length;
            this.y /= length;
        }
    }

    public double dot(Vector2 v) {
        return this.x * v.x + this.y * v.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2 returnSetLength(double length) {
        Vector2 returnSet = this;

        returnSet.normalize();
        returnSet.multiplyScalar(length);

        return returnSet;
    }

    public void setLength(double length) {
        normalize();
        multiplyScalar(length);
    }

    public String toString() {
        return"X: " + x + " Y: " + y;
    }
}

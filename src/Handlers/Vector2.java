/*
 * Vector2.java
 * Leo Bogaert
 * May 7, 2025,
 * Simple vector class to handle vector operations
 */

package Handlers;

public class Vector2 {

    public double x,y;

    /**
     * Constructor for Vector2, component form
     * @param x double x component
     * @param y double y component
     */
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for Vector2, length and angle form
     * @param length double length of the vector
     * @param theta double angle of the vector
     * @param isRadians boolean if the angle is in radians or degrees
     */
    public Vector2(double length, double theta, boolean isRadians) {
        if(!isRadians)
            theta = Math.toRadians(theta);

        theta -= Math.PI/2;

        this.x = length * Math.cos(theta);
        this.y = length * Math.sin(theta);
    }

    /**
     * Adds a vector to this vector
     * @param v Vector2 vector to be added
     * @return self
     */
    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    /**
     * Adds to this vector
     * @param x double to be added
     * @param y double to be added
     * @return self
     */
    public Vector2 add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Subtracts a vector to this vector
     * @param v Vector2 vector to be subtracted
     * @return self
     */
    public Vector2 subtract(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    /**
     * Multiplies this vector by a scalar
     * @param scalar double scalar to multiply by
     * @return self
     */
    public Vector2 multiplyScalar(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    /**
     * Normalizes this vector
     */
    public void normalize() {
        double length = Math.sqrt(x * x + y * y);
        if (length != 0) {
            this.x /= length;
            this.y /= length;
        }
    }

    /**
     * Calculates the dot product of this vector and another vector
     * @param v Vector2 vector to be dotted
     * @return double dot product
     */
    public double dot(Vector2 v) {
        return this.x * v.x + this.y * v.y;
    }

    /**
     * Calculates the length of this vector
     * @return double length of the vector
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Returns a vector of set length, does not modify this vector
     * @param length double length to set
     * @return self
     */
    public Vector2 returnSetLength(double length) {
        Vector2 returnSet = this;

        returnSet.normalize();
        returnSet.multiplyScalar(length);

        return returnSet;
    }

    /**
     * Sets this vector to another vector
     * @param v Vector2 vector to set to
     * @return self
     */
    public Vector2 set(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    /**
     * Sets this vector to a new x and y
     * @param x double x component
     * @param y double y component
     * @return self
     */
    public Vector2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }



    /**
     * Sets the length of this vector
     * @param length double length to set
     */
    public void setLength(double length) {
        normalize();
        multiplyScalar(length);
    }

    /**
     * Converts this vector to a string
     * @return String representation of the vector
     */
    public String toString() {
        return"X: " + x + " Y: " + y;
    }
}

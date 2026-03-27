/*******************************************************************************************************************************************
 * Copyright 2006-2026 TyTech B.V., Lange Vijverberg 4, 2513 AC, The Hague, The Netherlands. All rights reserved under the copyright laws of
 * The Netherlands and applicable international laws, treaties, and conventions. TyTech B.V. is a subsidiary company of Tygron Group B.V..
 *
 * This software is proprietary information of TyTech B.V.. You may freely redistribute and use this SDK code, with or without modification,
 * provided you include the original copyright notice and use it in compliance with your Tygron Platform License Agreement.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************************************************************************/
package nl.tytech.data.engine.serializable;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.logger.TLogger;

/**
 * Vector3d: Stores the vector as 3 double values
 *
 * @author Maxim Knepfle
 */
public class Vector3d implements Serializable {

    private static final long serialVersionUID = -1205148417639450780L;

    /**
     * the x value of the vector.
     */
    @XMLValue
    public double x = 0;

    /**
     * the y value of the vector.
     */
    @XMLValue
    public double y = 0;

    /**
     * the z value of the vector.
     */
    @XMLValue
    public double z = 0;

    public Vector3d() {

    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Vector3d base) {
        this(base.x, base.y, base.z);
    }

    public Vector3d addLocal(Vector3d vec) {

        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    public double angle(Vector3d other) {
        double normDot = this.dot(other) / (this.length() * other.length());
        if (normDot < -1.0) {
            normDot = -1.0;
        }

        if (normDot > 1.0) {
            normDot = 1.0;
        }

        return (float) Math.acos(normDot);
    }

    public Vector3d cross(Vector3d v2) {
        return new Vector3d(this).crossLocal(v2);
    }

    public Vector3d crossLocal(double otherX, double otherY, double otherZ) {

        double tempx = y * otherZ - z * otherY;
        double tempy = z * otherX - x * otherZ;
        z = x * otherY - y * otherX;
        x = tempx;
        y = tempy;
        return this;
    }

    public Vector3d crossLocal(Vector3d v2) {
        return crossLocal(v2.x, v2.y, v2.z);
    }

    public double distance(Vector3d end) {
        double dx = x - end.x;
        double dy = y - end.y;
        double dz = z - end.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double dot(Vector3d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3d mult(double scalar) {

        Vector3d result = new Vector3d(this);
        result.x *= scalar;
        result.y *= scalar;
        result.z *= scalar;
        return result;
    }

    public Vector3d multLocal(double scalar) {

        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public Vector3d normalize() {
        return new Vector3d(x, y, z).normalizeLocal();
    }

    public Vector3d normalizeLocal() {
        double length = x * x + y * y + z * z;
        if (length != 1d && length != 0d) {
            length = 1.0d / Math.sqrt(length);
        }
        this.x = x * length;
        this.y = y * length;
        this.z = z * length;
        return this;
    }

    public void set(Vector3d other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3d subtractLocal(Vector3d vec) {

        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    public Double[] toArray(Double[] array) {
        if (array == null) {
            array = new Double[3];
        }

        if (array.length != 3) {
            TLogger.severe(
                    "Cannot convert " + this.getClass().getSimpleName() + " to an array of size " + array.length + ". Only 3 is allowed.");
            return array;
        }

        array[0] = x;
        array[1] = y;
        array[2] = z;

        return array;
    }

    public Float[] toArray(Float[] array) {
        if (array == null) {
            array = new Float[3];
        }

        if (array.length != 3) {
            TLogger.severe(
                    "Cannot convert " + this.getClass().getSimpleName() + " to an array of size " + array.length + ". Only 3 is allowed.");
            return array;
        }

        array[0] = (float) x;
        array[1] = (float) y;
        array[2] = (float) z;

        return array;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}

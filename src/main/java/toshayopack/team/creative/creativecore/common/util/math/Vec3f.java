package toshayopack.team.creative.creativecore.common.util.math;

public class Vec3f extends VecNf<Vec3f> {
    public float x;
    public float y;
    public float z;

    public Vec3f() {
    }

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f(Vec3f vec) {
        this(vec.x, vec.y, vec.z);
    }

    public void set(Vec3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public float get(Axis axis) {
        float var10000;
        switch (axis) {
            case X:
                var10000 = this.x;
                break;
            case Y:
                var10000 = this.y;
                break;
            case Z:
                var10000 = this.z;
                break;
            default:
                var10000 = 0.0F;
        }

        return var10000;
    }

    public float get(int dim) {
        float var10000;
        switch (dim) {
            case 0:
                var10000 = this.x;
                break;
            case 1:
                var10000 = this.y;
                break;
            case 2:
                var10000 = this.z;
                break;
            default:
                var10000 = 0.0F;
        }

        return var10000;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(int dim, float value) {
        switch (dim) {
            case 0:
                this.x = value;
                break;
            case 1:
                this.y = value;
                break;
            case 2:
                this.z = value;
        }

    }

    public void set(Axis axis, float value) {
        switch (axis) {
            case X:
                this.x = value;
                break;
            case Y:
                this.y = value;
                break;
            case Z:
                this.z = value;
        }

    }

    public int dimensions() {
        return 3;
    }

    public void add(Vec3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void sub(Vec3f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public void scale(double dScale) {
        float scale = (float)dScale;
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3f)) {
            return false;
        } else {
            return ((Vec3f)obj).x == this.x && ((Vec3f)obj).y == this.y && ((Vec3f)obj).z == this.z;
        }
    }

    public double distance(Vec3f vec) {
        float x = this.x - vec.x;
        float y = this.y - vec.y;
        float z = this.z - vec.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double angle(Vec3f vec) {
        double vDot = (double)this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }

        if (vDot > 1.0) {
            vDot = 1.0;
        }

        return Math.acos(vDot);
    }

    public void cross(Vec3f vec1, Vec3f vec2) {
        this.x = vec1.y * vec2.z - vec1.z * vec2.y;
        this.y = vec2.x * vec1.z - vec2.z * vec1.x;
        this.z = vec1.x * vec2.y - vec1.y * vec2.x;
    }

    public float dot(Vec3f vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }
}


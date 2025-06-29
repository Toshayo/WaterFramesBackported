package toshayopack.team.creative.creativecore.common.util.math;

public class Vec3d extends VecNd<Vec3d> {
    public double x;
    public double y;
    public double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(Vec3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public void set(Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void set(net.minecraft.util.math.Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public double get(int dim) {
        double var10000;
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
                var10000 = 0.0;
        }

        return var10000;
    }

    public double get(Axis axis) {
        double var10000;
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
                var10000 = 0.0;
        }

        return var10000;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Axis axis, double value) {
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

    public void set(int dim, double value) {
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

    public int dimensions() {
        return 3;
    }

    public void add(Vec3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public void sub(Vec3d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
    }

    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3d)) {
            return false;
        } else {
            return ((Vec3d)obj).x == this.x && ((Vec3d)obj).y == this.y && ((Vec3d)obj).z == this.z;
        }
    }

    public double distance(net.minecraft.util.math.Vec3d vec) {
        return this.distance(vec.x, vec.y, vec.z);
    }

    public double distance(Vec3d vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        double z = this.z - vec.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(double x, double y, double z) {
        double posX = this.x - x;
        double posY = this.y - y;
        double posZ = this.z - z;
        return Math.sqrt(posX * posX + posY * posY + posZ * posZ);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double angle(Vec3d vec) {
        double vDot = this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }

        if (vDot > 1.0) {
            vDot = 1.0;
        }

        return Math.acos(vDot);
    }

    public double dot(Vec3d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }
}


package toshayopack.team.creative.creativecore.common.util.math;

public class Vec2f extends VecNf<Vec2f> {
    public float x;
    public float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vec2f vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public float get(int dim) {
        if (dim == 0) {
            return this.x;
        } else {
            return dim == 1 ? this.y : 0.0F;
        }
    }

    public void set(int dim, float value) {
        if (dim == 0) {
            this.x = value;
        } else if (dim == 1) {
            this.y = value;
        }

    }

    public int dimensions() {
        return 2;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void add(Vec2f vec) {
        this.x += this.x;
        this.y += this.y;
    }

    public void sub(Vec2f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
    }

    public void scale(double scale) {
        this.x *= (float)scale;
        this.y *= (float)scale;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2f)) {
            return false;
        } else {
            return ((Vec2f)obj).x == this.x && ((Vec2f)obj).y == this.y;
        }
    }

    public double distance(Vec2f vec) {
        double x = this.x - vec.x;
        double y = this.y - vec.y;
        return Math.sqrt(x * x + y * y);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double angle(Vec2f vec) {
        double vDot = (double)this.dot(vec) / (this.length() * vec.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }

        if (vDot > 1.0) {
            vDot = 1.0;
        }

        return Math.acos(vDot);
    }

    public float dot(Vec2f vec) {
        return this.x * vec.x + this.y * vec.y;
    }
}


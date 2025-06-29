package toshayopack.team.creative.creativecore.common.util.math;


import net.minecraft.util.math.Vec3d;

public enum Axis {
    X {
        public double get(double x, double y, double z) {
            return x;
        }

        public float get(float x, float y, float z) {
            return x;
        }

        public int get(int x, int y, int z) {
            return x;
        }

        public <T> T get(T x, T y, T z) {
            return x;
        }

        public double get(Vec3d vec) {
            return vec.x;
        }

        public Vec3d set(Vec3d vec, double value) {
            return new Vec3d(value, vec.y, vec.z);
        }

        public Axis one() {
            return Axis.Y;
        }

        public Axis two() {
            return Axis.Z;
        }

        public Facing facing(boolean positive) {
            return positive ? Facing.EAST : Facing.WEST;
        }
    },
    Y {
        public double get(double x, double y, double z) {
            return y;
        }

        public float get(float x, float y, float z) {
            return y;
        }

        public int get(int x, int y, int z) {
            return y;
        }

        public <T> T get(T x, T y, T z) {
            return y;
        }

        public double get(Vec3d vec) {
            return vec.y;
        }

        public Vec3d set(Vec3d vec, double value) {
            return new Vec3d(vec.x, value, vec.z);
        }

        public Axis one() {
            return Axis.Z;
        }

        public Axis two() {
            return Axis.X;
        }

        public Facing facing(boolean positive) {
            return positive ? Facing.UP : Facing.DOWN;
        }
    },
    Z {
        public double get(double x, double y, double z) {
            return z;
        }

        public float get(float x, float y, float z) {
            return z;
        }

        public int get(int x, int y, int z) {
            return z;
        }

        public <T> T get(T x, T y, T z) {
            return z;
        }

        public double get(Vec3d vec) {
            return vec.z;
        }

        public Vec3d set(Vec3d vec, double value) {
            return new Vec3d(vec.x, vec.y, value);
        }

        public Axis one() {
            return Axis.X;
        }

        public Axis two() {
            return Axis.Y;
        }

        public Facing facing(boolean positive) {
            return positive ? Facing.SOUTH : Facing.NORTH;
        }
    };

    Axis() {
    }

    public abstract Axis one();

    public abstract Axis two();

    public abstract Facing facing(boolean var1);

    public abstract double get(double var1, double var3, double var5);

    public abstract float get(float var1, float var2, float var3);

    public abstract int get(int var1, int var2, int var3);

    public abstract double get(Vec3d var1);

    public abstract Vec3d set(Vec3d var1, double var2);

    public abstract <T> T get(T var1, T var2, T var3);
}


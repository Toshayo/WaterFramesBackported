package toshayopack.team.creative.creativecore.common.util.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public enum Facing {
    DOWN(Axis.Y, false, new Vec3d(0, -1, 0), -1) {
        public Facing opposite() {
            return Facing.UP;
        }

        public EnumFacing toVanilla() {
            return EnumFacing.DOWN;
        }

        public double get(AxisAlignedBB bb) {
            return bb.minY;
        }

        public AxisAlignedBB set(AxisAlignedBB bb, double value) {
            return new AxisAlignedBB(bb.minX, value, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }

        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return minY;
        }

        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return minY;
        }

        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return minY;
        }
    },
    UP(Axis.Y, true, new Vec3d(0, 1, 0), -1) {
        public Facing opposite() {
            return Facing.DOWN;
        }

        public EnumFacing toVanilla() {
            return EnumFacing.UP;
        }

        public double get(AxisAlignedBB bb) {
            return bb.maxY;
        }

        public AxisAlignedBB set(AxisAlignedBB bb, double value) {
            return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, value, bb.maxZ);
        }

        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return maxY;
        }

        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return maxY;
        }

        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return maxY;
        }
    },
    NORTH(Axis.Z, false, new Vec3d(0, 0, -1), 2) {
        public Facing opposite() {
            return SOUTH;
        }

        public EnumFacing toVanilla() {
            return EnumFacing.NORTH;
        }

        public double get(AxisAlignedBB bb) {
            return bb.minZ;
        }

        public AxisAlignedBB set(AxisAlignedBB bb, double value) {
            return new AxisAlignedBB(bb.minX, bb.minY, value, bb.maxX, bb.maxY, bb.maxZ);
        }

        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return minZ;
        }

        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return minZ;
        }

        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return minZ;
        }
    },
    SOUTH(Axis.Z, true, new Vec3d(0, 0, 1), 0) {
        public Facing opposite() {
            return Facing.NORTH;
        }

        public EnumFacing toVanilla() {
            return EnumFacing.SOUTH;
        }

        public double get(AxisAlignedBB bb) {
            return bb.maxZ;
        }

        public AxisAlignedBB set(AxisAlignedBB bb, double value) {
            return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, value);
        }

        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return maxZ;
        }

        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return maxZ;
        }

        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return maxZ;
        }
    },
    WEST(Axis.X, false, new Vec3d(-1, 0, 0), 1) {
        public Facing opposite() {
            return Facing.EAST;
        }

        public EnumFacing toVanilla() {
            return EnumFacing.WEST;
        }

        public double get(AxisAlignedBB bb) {
            return bb.minX;
        }

        public AxisAlignedBB set(AxisAlignedBB bb, double value) {
            return new AxisAlignedBB(value, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }

        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return minX;
        }

        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return minX;
        }

        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return minX;
        }
    },
    EAST(Axis.X, true, new Vec3d(1, 0, 0), 3) {
        public Facing opposite() {
            return Facing.WEST;
        }

        public EnumFacing toVanilla() {
            return EnumFacing.EAST;
        }

        public double get(AxisAlignedBB bb) {
            return bb.maxX;
        }

        public AxisAlignedBB set(AxisAlignedBB bb, double value) {
            return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, value, bb.maxY, bb.maxZ);
        }

        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return maxX;
        }

        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return maxX;
        }

        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return maxX;
        }
    };

    public static final Facing[] VALUES = new Facing[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    public final String name = this.name().toLowerCase();
    public final Axis axis;
    public final boolean positive;
    public final Vec3d normal;
    public final int horizontalIndex;

    public static Facing get(int index) {
        return VALUES[index];
    }

    public static Facing get(EnumFacing direction) {
        if (direction == null) {
            return null;
        } else {
            Facing var10000;
            switch (direction) {
                case DOWN:
                    var10000 = DOWN;
                    break;
                case UP:
                    var10000 = UP;
                    break;
                case NORTH:
                    var10000 = NORTH;
                    break;
                case SOUTH:
                    var10000 = SOUTH;
                    break;
                case WEST:
                    var10000 = WEST;
                    break;
                case EAST:
                    var10000 = EAST;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }
    }

    public static Facing get(Axis axis, boolean positive) {
        Facing var10000;
        switch (axis) {
            case X:
                var10000 = positive ? EAST : WEST;
                break;
            case Y:
                var10000 = positive ? UP : DOWN;
                break;
            case Z:
                var10000 = positive ? SOUTH : NORTH;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return var10000;
    }

    Facing(Axis axis, boolean positive, Vec3d normal, int horizontalIndex) {
        this.axis = axis;
        this.positive = positive;
        this.normal = normal;
        this.horizontalIndex = horizontalIndex;
    }

    public abstract Facing opposite();

    public abstract EnumFacing toVanilla();

    public Axis one() {
        return this.axis.one();
    }

    public Axis two() {
        return this.axis.two();
    }

    public abstract double get(AxisAlignedBB var1);

    public abstract AxisAlignedBB set(AxisAlignedBB var1, double var2);

    public abstract float get(float var1, float var2, float var3, float var4, float var5, float var6);

    public abstract double get(double var1, double var3, double var5, double var7, double var9, double var11);

    public abstract int get(int var1, int var2, int var3, int var4, int var5, int var6);
}

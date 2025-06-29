package toshayopack.team.creative.creativecore.common.util.math;

public class AlignedBox {
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;

    public AlignedBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public AlignedBox() {
        this(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public AlignedBox(AlignedBox cube) {
        this(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ);
    }

    public void add(float x, float y, float z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
    }

    public void scale(float scale) {
        this.minX *= scale;
        this.minY *= scale;
        this.minZ *= scale;
        this.maxX *= scale;
        this.maxY *= scale;
        this.maxZ *= scale;
    }

    public float getSize(Axis axis) {
        float var10000;
        switch (axis) {
            case X:
                var10000 = this.maxX - this.minX;
                break;
            case Y:
                var10000 = this.maxY - this.minY;
                break;
            case Z:
                var10000 = this.maxZ - this.minZ;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public String toString() {
        return "cube[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public float get(Facing facing) {
        float var10000;
        switch (facing) {
            case EAST:
                var10000 = this.maxX;
                break;
            case WEST:
                var10000 = this.minX;
                break;
            case UP:
                var10000 = this.maxY;
                break;
            case DOWN:
                var10000 = this.minY;
                break;
            case SOUTH:
                var10000 = this.maxZ;
                break;
            case NORTH:
                var10000 = this.minZ;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public void set(Facing facing, float value) {
        switch (facing) {
            case EAST:
                this.maxX = value;
                break;
            case WEST:
                this.minX = value;
                break;
            case UP:
                this.maxY = value;
                break;
            case DOWN:
                this.minY = value;
                break;
            case SOUTH:
                this.maxZ = value;
                break;
            case NORTH:
                this.minZ = value;
        }

    }

    public void setMin(Axis axis, float value) {
        switch (axis) {
            case X:
                this.minX = value;
                break;
            case Y:
                this.minY = value;
                break;
            case Z:
                this.minZ = value;
        }

    }

    public float getMin(Axis axis) {
        float var10000;
        switch (axis) {
            case X:
                var10000 = this.minX;
                break;
            case Y:
                var10000 = this.minY;
                break;
            case Z:
                var10000 = this.minZ;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public void setMax(Axis axis, float value) {
        switch (axis) {
            case X:
                this.maxX = value;
                break;
            case Y:
                this.maxY = value;
                break;
            case Z:
                this.maxZ = value;
        }

    }

    public float getMax(Axis axis) {
        float var10000;
        switch (axis) {
            case X:
                var10000 = this.maxX;
                break;
            case Y:
                var10000 = this.maxY;
                break;
            case Z:
                var10000 = this.maxZ;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }
}

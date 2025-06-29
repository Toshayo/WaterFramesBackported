package toshayopack.team.creative.creativecore.common.util.math;

public enum BoxCorner {
    EUN(Facing.EAST, Facing.UP, Facing.NORTH),
    EUS(Facing.EAST, Facing.UP, Facing.SOUTH),
    EDN(Facing.EAST, Facing.DOWN, Facing.NORTH),
    EDS(Facing.EAST, Facing.DOWN, Facing.SOUTH),
    WUN(Facing.WEST, Facing.UP, Facing.NORTH),
    WUS(Facing.WEST, Facing.UP, Facing.SOUTH),
    WDN(Facing.WEST, Facing.DOWN, Facing.NORTH),
    WDS(Facing.WEST, Facing.DOWN, Facing.SOUTH);

    public final Facing x;
    public final Facing y;
    public final Facing z;
    public BoxCorner neighborOne;
    public BoxCorner neighborTwo;
    public BoxCorner neighborThree;
    public static final BoxCorner[][] FACING_CORNERS = new BoxCorner[][]{{EDN, EDS, WDN, WDS}, {EUN, EUS, WUN, WUS}, {EUN, EDN, WUN, WDN}, {EUS, EDS, WUS, WDS}, {WUN, WUS, WDN, WDS}, {EUN, EUS, EDN, EDS}};

    BoxCorner(Facing x, Facing y, Facing z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void init() {
        this.neighborOne = getCorner(this.x.opposite(), this.y, this.z);
        this.neighborTwo = getCorner(this.x, this.y.opposite(), this.z);
        this.neighborThree = getCorner(this.x, this.y, this.z.opposite());
    }

    public boolean isFacing(Facing facing) {
        return this.getFacing(facing.axis) == facing;
    }

    public Facing getFacing(Axis axis) {
        Facing var10000;
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
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static BoxCorner getCorner(Facing x, Facing y, Facing z) {
        BoxCorner[] var3 = values();

        for (BoxCorner corner : var3) {
            if (corner.x == x && corner.y == y && corner.z == z) {
                return corner;
            }
        }

        return null;
    }

    static {
        BoxCorner[] var0 = values();

        for (BoxCorner corner : var0) {
            corner.init();
        }

    }
}

package net.toshayo.waterframes;

import net.minecraft.util.EnumFacing;
import net.toshayo.waterframes.blocks.BigTVBlock;
import net.toshayo.waterframes.blocks.TVBlock;
import net.toshayo.waterframes.tileentities.DisplayTileEntity;
import toshayopack.team.creative.creativecore.common.util.math.AlignedBox;

import java.util.Objects;

public final class DisplayCaps {

    public static final BooleanCap FALSE = tile -> false;

    public DisplayCaps(boolean renderBothSides, boolean projects, boolean resizes, float growSize, BoxCap box) {
        this(renderBothSides, projects, resizes, growSize, FALSE, box);
    }

    public static final DisplayCaps
            FRAME = new DisplayCaps(true, false, true, 0.001F,/* tile -> true,*/ (t, d, a, r) -> DisplayTileEntity.getBasicBox(t)),
            PROJECTOR = new DisplayCaps(false, true, true, 0.999F, (t, d, a, r) -> DisplayTileEntity.getBasicBox(t)),
            TV = new DisplayCaps(false, false, false, 0.001F, tile -> true/*EnumFacing.getFront(tile.getBlockMetadata()) != tile.blockFacing*/, (t, d, a, r) -> TVBlock.box(d, a, r)),
            BIG_TV = new DisplayCaps(false, false, false, 0.001F, tile -> true, (t, d, a, r) -> BigTVBlock.box(d, r));

    private final boolean renderBehind;
    private final boolean projects;
    private final boolean resizes;
    private final float growSize;
    private final BooleanCap invertedFace;
    private final BoxCap box;

    public DisplayCaps(boolean renderBehind, boolean projects, boolean resizes, float growSize, BooleanCap invertedFace, BoxCap box) {
        this.renderBehind = renderBehind;
        this.projects = projects;
        this.resizes = resizes;
        this.growSize = growSize;
        this.invertedFace = invertedFace;
        this.box = box;
    }


    public boolean invertedFace(DisplayTileEntity tile) {
        return invertedFace.get(tile);
    }

    public AlignedBox getBox(DisplayTileEntity tile, EnumFacing facing, EnumFacing attachedFace, boolean renderMode) {
        return box.get(tile, facing, attachedFace, renderMode);
    }

    public boolean renderBehind() {
        return renderBehind;
    }

    public boolean projects() {
        return projects;
    }

    public boolean resizes() {
        return resizes;
    }

    public float growSize() {
        return growSize;
    }

    public BooleanCap invertedFace() {
        return invertedFace;
    }

    public BoxCap box() {
        return box;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        DisplayCaps that = (DisplayCaps) obj;
        return this.renderBehind == that.renderBehind &&
                this.projects == that.projects &&
                this.resizes == that.resizes &&
                Float.floatToIntBits(this.growSize) == Float.floatToIntBits(that.growSize) &&
                Objects.equals(this.invertedFace, that.invertedFace) &&
                Objects.equals(this.box, that.box);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderBehind, projects, resizes, growSize, invertedFace, box);
    }

    @Override
    public String toString() {
        return "DisplayCaps[" +
                "renderBehind=" + renderBehind + ", " +
                "projects=" + projects + ", " +
                "resizes=" + resizes + ", " +
                "growSize=" + growSize + ", " +
                "invertedFace=" + invertedFace + ", " +
                "box=" + box + ']';
    }


    @FunctionalInterface
    public interface BooleanCap {
        boolean get(DisplayTileEntity tile);
    }

    @FunctionalInterface
    public interface BoxCap {
        AlignedBox get(DisplayTileEntity tile, EnumFacing facing, EnumFacing attachedFace, boolean renderMode);
    }
}

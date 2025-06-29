package net.toshayo.waterframes.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IUnlistedProperty;

public class ExtendedBlockStateProvider {
    public static final IUnlistedProperty<TileEntity> TILE = new IUnlistedProperty<TileEntity>() {

        @Override
        public String getName() {
            return "tile";
        }

        @Override
        public boolean isValid(TileEntity tile) {
            return true;
        }

        @Override
        public Class<TileEntity> getType() {
            return TileEntity.class;
        }

        @Override
        public String valueToString(TileEntity tile) {
            return tile.toString();
        }
    };
}

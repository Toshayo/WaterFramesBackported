package net.toshayo.waterframes.client;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.toshayo.waterframes.WaterFramesMod;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WaterFramesMod.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void onModelEvent(ModelRegistryEvent event) {
        for(Item item : WaterFramesMod.ITEMS) {
            ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
            ModelBakery.registerItemVariants(item, model);
            ModelLoader.setCustomModelResourceLocation(item, 0, model);
        }
    }
}

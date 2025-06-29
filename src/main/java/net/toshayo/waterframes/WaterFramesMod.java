package net.toshayo.waterframes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.toshayo.waterframes.proxy.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Mod(
        modid = WaterFramesMod.MOD_ID,
        name = WaterFramesMod.NAME,
        version = WaterFramesMod.VERSION,
        certificateFingerprint = "ee4beef430d574ba7d8c096a4f7f9c6c755bd30f"
)
public class WaterFramesMod {
    public static final String MOD_ID = "waterframes";
    public static final String NAME = "WaterFramesBackported";
    public static final String VERSION = "1.2.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static Block FRAME;
    public static Block TV;
    public static Block BIG_TV;
    public static Block PROJECTOR;
    public static Block TV_BOX;
    public static Item REMOTE;

    public static final List<Item> ITEMS = new ArrayList<>();


    @Mod.Instance(MOD_ID)
    public static WaterFramesMod INSTANCE;

    @SidedProxy(
            clientSide = "net.toshayo.waterframes.proxy.ClientProxy",
            serverSide = "net.toshayo.waterframes.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) throws Exception {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public static void onInit(FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @Mod.EventHandler
    public static void onPostInit(FMLPostInitializationEvent event) throws Exception {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public static void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new WaterFramesCommand());
    }

    public static URI createURI(String s) {
        File f = new File(s);
        // accept local paths as file uris
        if (!f.isDirectory() && f.exists()) {
            return new File(s).toURI();
        }

        try {
            return new URI(s);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public static void onFingerPrintViolation(FMLFingerprintViolationEvent event) {
        if (!event.isDirectory()) {
            System.err.println("A file failed to match with the signing key.");
            System.err.println("If you *know* this is a homebrew/custom build then this is expected, carry on.");
        }
    }

    public static boolean isObfEnv() {
        return !FMLForgePlugin.RUNTIME_DEOBF;
    }
}

package net.toshayo.waterframes.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DisplayControl {
    public static final Integer DEFAULT_SIZE = 32;
    public static final int SYNC_TIME = 1500;

    private static volatile TextureDisplay[] displays = new TextureDisplay[DEFAULT_SIZE];
    private static int position = 0;
    private static boolean checkSize = false;
    private static int ticks = 0;
    private static boolean isPaused = false;


    public static void add(TextureDisplay display) {
        if (checkSize) {
            if (((float) position / displays.length) <= 0.25f) {
                TextureDisplay[] freshMeal = new TextureDisplay[displays.length / 2]; // free unused memory
                System.arraycopy(displays, 0, freshMeal, 0, position);
                displays = freshMeal;
            }
            checkSize = false;
        }

        if (position >= displays.length) { // position never should be major than length
            TextureDisplay[] freshMeal = new TextureDisplay[displays.length * 2];
            position = copyData$resetPosition(displays, freshMeal);
            displays = freshMeal;
            checkSize = true;
        }

        displays[position++] = display;
    }

    public static void pause() {
        if(isPaused) {
            return;
        }
        for (int i = 0; i < position; i++) {
            if (displays[i] != null) {
                displays[i].setPauseMode(true);
            }
        }
        isPaused = true;
    }

    public static void resume() {
        if(!isPaused) {
            return;
        }
        for (int i = 0; i < position; i++) {
            if (displays[i] != null) {
                displays[i].setPauseMode(false);
            }
        }
        isPaused = false;
    }

    public static void remove(int i) {
        if (i > displays.length) {
            return; // 'i' cannot be over position
        }
        displays[i] = null;
    }

    public static void remove(TextureDisplay obj) {
        if (obj == null) return; // null cannot be removed, duh
        for (int i = 0; i < position; i++) {
            if (obj == displays[i]) {
                displays[i] = null;
                break;
            }
        }
    }

    public static void release() {
        for (int i = 0; i < position; i++) {
            if (displays[i] != null) {
                displays[i].release();
                displays[i] = null;
            }
        }

        displays = new TextureDisplay[DEFAULT_SIZE];
        position = 0;
    }

    private static int copyData$resetPosition(TextureDisplay[] current, TextureDisplay[] target) {
        int freshPosition = 0;
        for (int i = 0; i < current.length; i++) { // tries to ignore all null pos to stack all instanced objects, spend less memory
            if (current[i] != null) {
                target[freshPosition++] = current[i];
                current[i] = null;
            }
        }
        return freshPosition;
    }

    public static void tick() {
        if (++ticks == Integer.MAX_VALUE) {
            ticks = 0;
        }
    }

    public static int getTicks() {
        return ticks;
    }

    @SubscribeEvent
    public void onUnloadingLevel(WorldEvent.Unload event) {
        if (event.getWorld() != null && event.getWorld().isRemote) {
            DisplayControl.release();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if(Minecraft.getMinecraft().isGamePaused()) {
                pause();
            } else {
                resume();
            }
            DisplayControl.tick();
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if(Minecraft.getMinecraft().player == event.getEntity()) {
            if (event.getWorld() != null && event.getWorld().isRemote) {
                DisplayControl.release();
            }
        }
    }
}
package net.toshayo.waterframes.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ChatUtils {
    public static void fatalMessage(EntityPlayer player, String key, Object ...args) {
        TextComponentTranslation msg = new TextComponentTranslation(key, args);
        msg.getStyle().setColor(TextFormatting.DARK_RED);
        player.sendMessage(msg);
    }

    public static void errorMessage(EntityPlayer player, String key, Object ...args) {
        TextComponentTranslation msg = new TextComponentTranslation(key, args);
        msg.getStyle().setColor(TextFormatting.RED);
        player.sendMessage(msg);
    }

    public static void successMessage(EntityPlayer player, String key, Object ...args) {
        TextComponentTranslation msg = new TextComponentTranslation(key, args);
        msg.getStyle().setColor(TextFormatting.AQUA);
        player.sendMessage(msg);
    }
}

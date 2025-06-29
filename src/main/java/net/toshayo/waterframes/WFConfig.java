package net.toshayo.waterframes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class WFConfig {
    private static final Pattern HOSTS_PATTERN = Pattern.compile("^(?!-)([a-zA-Z0-9-]{1,63}\\.)+[a-zA-Z]{2,63}$");

    private static Configuration config;

    private static final String[] WHITELIST = new String[]{
            "imgur.com",
            "gyazo.com",
            "prntscr.com",
            "tinypic.com",
            "puu.sh",
            "pinimg.com",
            "photobucket.com",
            "staticflickr.com",
            "flic.kr",
            "tenor.co",
            "tenor.com",
            "gfycat.com",
            "giphy.com",
            "gph.is",
            "gifbin.com",
            "i.redd.it",
            "media.tumblr.com",
            "twimg.com",
            "githubusercontent.com",
            "googleusercontent.com",
            "googleapis.com",
            "wikimedia.org",
            "ytimg.com",
            "youtube.com",
            "youtu.be",
            "twitch.tv",
            "twitter.com",
            "x.com",
            "soundcloud.com",
            "kick.com",
            "streamable.com",
            "srrapero720.me",
            "fbcdn.net",
            "drive.google.com",
            "discordapp.com",
            "images.discordapp.net",
            "discord.com",
    };

    // RENDERING
    private static double maxWidth = 40;
    private static double maxHeight = 40;
    private static int maxRenderDistance = 64;
    private static double maxProjectionDistance = 64;
    // MULTIMEDIA
    private static int maxVolumeDistance = 64;
    private static int maxVolume = 100;
    private static boolean useMultimedia = true;
    private static boolean useSlavismMode;
    private static boolean keepRendering = true;
    // BEHAVIOR
    private static boolean useLightsOnPlay = true;
    private static boolean useRedstone = true;
    private static boolean useMasterModeOnRedsone = false;
    // REMOTE CONTROL
    private static int remoteDistance = 32;

    // PERMISSIONS
    private static boolean useInAdventure = false;
    private static boolean useInSurvival = true;
    private static boolean useForAnyone = true;
    private static boolean useWhitelist = true;
    private static final List<String> whitelist = new ArrayList<>(Arrays.asList(WHITELIST));

    // OVERRIDES (client)
    private static boolean overrideServerConfig = false;
    private static boolean clientUseMultimedia = false;
    private static boolean clientSlavistMode;
    private static boolean clientKeepsRendering = false;


    public static void init(File file) {
        if(config == null) {
            config = new Configuration(file);
            config.load();
            syncConfig();
        }
    }

    public static void syncConfig() {
        try {
            // rendering
            config.addCustomCategoryComment("rendering", "All configurations about rendering");
            maxWidth = config.getInt("maxWidth", "rendering", 40, 1, 256, "Width limit of displays in blocks");
            maxHeight = config.getInt("maxHeight", "rendering", 40, 1, 256, "Height limit of displays in blocks");
            maxRenderDistance = config.getInt("maxRenderDistance", "rendering", 64, 4, 512, "Max Radius of rendering distance in blocks");
            maxProjectionDistance = config.getInt("maxProjectionDistance", "rendering", 64, 4, 512, "Max distance of projections in blocks");
            keepRendering = config.getBoolean("keepRendering", "rendering", true, "Enables media processing and rendering, disabling it will not render nothing, you can still hear videos");

            // multimedia
            config.addCustomCategoryComment("multimedia", "Configuration related to multimedia sources like Videos or Music");
            maxVolumeDistance = config.getInt("maxVolumeDistance", "multimedia", 64, 8, 512, "Max volume distance radius");
            maxVolume = config.getInt("maxVolume", "multimedia", 100, 10, 120, "Max volume value. Values over 100 uses VLC überVolume");

            // multimedia/watermedia
            useMultimedia = config.getBoolean("useMultimedia", "multimedia", true, "Enables VLC/FFMPEG usage for multimedia processing like videos and music (support added by WATERMeDIA)");
            useSlavismMode = config.getBoolean("useSlavismMode", "multimedia", true, "Enables VLC/FFMPEG usage for multimedia processing like videos and music (support added by WATERMeDIA)");

            // block_behavior
            config.addCustomCategoryComment("block_behavior", "Configuration related to interactions with vanilla and modded features");
            useLightsOnPlay = config.getBoolean("useLightsOnPlay", "block_behavior", true, "Enable light feature on frames while is playing");

            // redstone
            config.addCustomCategoryComment("redstone", "Redstone interaction options");
            useRedstone = config.getBoolean("useRedstone", "redstone", true, "Enable the feature");
            useMasterModeOnRedsone = config.getBoolean("masterMode", "redstone", false, "Redstone inputs forces paused playback and ignores any other control sources");

            // remote_control
            config.addCustomCategoryComment("remote_control", "Configuration related to remote control");
            remoteDistance = config.getInt("remoteDistance", "remote_control", 32, 4, 256, "Distance in blocks of RC range");

            // permissions
            config.addCustomCategoryComment("permissions", "Configurations related to permissions");
            useInAdventure = config.getBoolean("usableInAdventureMode", "permissions", false, "Changes if players in Adventure mode can use displays");
            useInSurvival = config.getBoolean("usableInSurvivalMode", "permissions", true, "Changes if players in Survival mode can use displays");
            useForAnyone = config.getBoolean("usableForAnyone", "permissions", true, "Changes if any player can use displays, otherwise only admins can use it");

            // whitelist
            config.addCustomCategoryComment("whitelist", "Whitelist configuration");
            useWhitelist = config.getBoolean("enable", "whitelist", true, "Enables whitelist feature\n[WARNING]: THE AUTHOR OF THE MOD IS NOT RESPONSIBLE IF IN YOUR SERVER SOMEONE PUTS NSFW MEDIA\nWATERMEDIA HAVE SUPPORT FOR ADULT PAGES AND WHITELIST WAS DESIGNED TO PREVENT THAT");
            whitelist.clear();
            whitelist.addAll(Arrays.asList(config.getStringList("urls", "whitelist", WHITELIST, "")));

            // clientside
            config.addCustomCategoryComment("client", "Configurations to override server config");
            overrideServerConfig = config.getBoolean("enable", "client", false, "Enables the option");
            clientUseMultimedia = config.getBoolean("useMultimedia", "client", false, "Overrides 'waterframes.watermedia.enable' option\nEnables VLC/FFMPEG usage for multimedia processing (support added by WATERMeDIA)");
            clientSlavistMode = config.getBoolean("clientSlavistMode", "client", false, "Overrides 'waterframes.multimedia.slavistMode' option\nForce-enable WATERMeDIA's slavist mode, slavism enhances youtube quality at the cost of have unstable playback");
            clientKeepsRendering = config.getBoolean("keepRendering", "client", false, "Overrides 'waterframes.rendering.keepRendering'\nEnables media processing and rendering, disabling it will not render nothing, you can still hear videos");

        } catch (Exception e) {
            WaterFramesMod.LOGGER.error("Failed to load config: {}", e.getMessage());
        } finally {
            if(config.hasChanged())
                config.save();
        }
    }

    public static float maxWidth() {
        return (float) maxWidth;
    }

    public static float maxHeight() {
        return (float) maxHeight;
    }

    public static float maxWidth(float width) {
        return Math.min(width, maxWidth());
    }

    public static float maxHeight(float height) {
        return Math.min(height, maxHeight());
    }

    public static int maxRenDis() {
        return maxRenderDistance;
    }

    public static int maxRenDis(int value) {
        return Math.min(value, maxRenDis());
    }

    public static float maxProjDis() {
        return (float) maxProjectionDistance;
    }

    public static float maxProjDis(float value) {
        return Math.min(value, maxProjDis());
    }

    public static boolean keepsRendering() {
        return overrideServerConfig ? clientKeepsRendering : keepRendering;
    }

    public static boolean useLightOnPlay() {
        return useLightsOnPlay;
    }

    // MULTIMEDIA
    public static int maxVolDis() {
        return maxVolumeDistance;
    }

    public static int maxVolDis(int value) {
        return Math.min(value, maxVolDis());
    }

    public static int maxVol() {
        return maxVolume;
    }

    public static int maxVol(int value) {
        return Math.max(Math.min(value, maxVol()), 0);
    }

    public static boolean useMultimedia() {
        return overrideServerConfig ? clientUseMultimedia : useMultimedia;
    }

    public static boolean useSlavismMode() {
        return overrideServerConfig ? clientSlavistMode : useSlavismMode;
    }

    // BEHAVIOR
    public static boolean useRedstone() {
        return useRedstone;
    }

    public static boolean useMasterModeRedstone() {
        return useRedstone() && useMasterModeOnRedsone;
    }

    public static int maxRcDis() {
        return remoteDistance;
    }

    // PERMISSIONS
    public static boolean useInAdv() {
        return useInAdventure;
    }

    public static boolean useInSurv() {
        return useInSurvival;
    }

    public static boolean useForAnyone() {
        return useForAnyone;
    }

    public static boolean useWhitelist() {
        return useWhitelist;
    }

    public static void useWhitelist(boolean state) {
        useWhitelist = state;
        config.save();
    }

    public static boolean toggleWhitelist() {
        useWhitelist(!useWhitelist());
        return useWhitelist();
    }

    public static void addOnWhitelist(String url) {
        whitelist.add(url);
        config.save();
    }

    public static boolean removeOnWhitelist(String url) {
        boolean removed = false;
        try {
            return removed = whitelist.remove(url);
        } finally {
            if (removed) {
                config.save();
            }
        }
    }

    public static boolean isWhiteListed(String url) {
        if (!useWhitelist()) {
            return true;
        }

        if (url.startsWith("local://")
                || url.startsWith("game://")
                || url.startsWith("user://")
                || url.startsWith("users://")) {
            return true; // local files = anything you have
        }

        try {
            String host = new URL(url).getHost();

            for (String s : whitelist) {
                if (host.endsWith("." + s) || host.equals(s)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean canSave(EntityPlayer player, String url) {
        if (isAdmin(player)) {
            return true;
        }

        try {
            return url.isEmpty() || isWhiteListed(url);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canInteractBlock(EntityPlayer player) {
        /*WorldSettings.GameType gameType = (player instanceof EntityPlayerMP)
                ? ((EntityPlayerMP) player).gameMode.getGameModeForPlayer()
                : Minecraft.getMinecraft().gameMode.getPlayerMode();

        if (isAdmin(player)) return true;
        if (!useInSurv() && gameType.equals(GameType.SURVIVAL)) return false;
        if (!useInAdv() && gameType.equals(GameType.ADVENTURE)) return false;

        return useForAnyone();*/
        return true;
    }

    public static boolean canInteractItem(EntityPlayer player) {
        if (isAdmin(player)) {
            return true;
        }
        return useForAnyone();
    }

    public static boolean isAdmin(EntityPlayer player) {
        /*World level = player.worldObj;

        // OWNER
        String name = player.getGameProfile().getName();
        if (name.equals("SrRaapero720") || name.equals("SrRapero720")) {
            return true;
        }

        if (level.isRemote) { // validate if was singleplayer and if was the admin
            IntegratedServer integrated = Minecraft.getMinecraft().getIntegratedServer();
            if (integrated != null) {
                return integrated.isSingleplayerOwner(player.getGameProfile()) || player.hasPermissions(integrated.getOperatorUserPermissionLevel());
            } else { // is a guest, check perms
                return player.hasPermissions(WaterFramesMod.getServerOpPermissionLevel(level));
            }
        } else {
            return player.hasPermissions(player.getServer().getOperatorUserPermissionLevel());
        }*/
        return true;
    }
}

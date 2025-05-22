## WaterFramesBackported

A rewrite for forge 1.7.10 of a nice mod WATERFrAMES (source at: [https://github.com/SrRapero720/waterframes](https://github.com/SrRapero720/waterframes))

### Dependencies

- [WATERMeDIA](https://www.curseforge.com/minecraft/mc-mods/watermedia): Multimedia Support

    Take latest version (recommended 2.1.24), and it does not matter that the mod is for 1.21.+
- JNA

    You will need jna and jna-platform libraries (5.10.0 for example). You can download these libraries here:
    - [https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar](https://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.10.0/jna-platform-5.10.0.jar)
    - [https://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.10.0/jna-platform-5.10.0.jar](https://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.10.0/jna-platform-5.10.0.jar)
    
    They should be installed in ``mods`` folder like any other mod.

### Credits

- [SrRapero720](https://github.com/SrRapero720): Original author of the mod
- [FabiAcr](https://www.twitch.tv/fabi_acr): Author of the WATERFrAMES block models
- **Kotyarendj**: Author of the current WATERFrAMES block textures


### More info about the 1.7.10 port

- The mod uses WATERMeDIA that was developed for 1.21.1. That means some features might be broken.
- Redstone control is not supported yet.
- Displays can show camera view from Security Craft if LookingGlass is installed. To use this feature
  set ``camera://x,y,z`` where x, y and z are camera coordinates.
- OpenComputers integration is present. You can set URL and other parameters with computers.
- Other features might not be ported from WATERFrAMES.

If you are experiencing crashes, feel free to open issues [here](https://github.com/Toshayo/WaterFrames/issues) I'll fix when I have time.

**Please don't report issues directly to WATERMeDIA or original WATERFrAMES mod!**


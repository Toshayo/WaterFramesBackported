package net.toshayo.waterframes;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({
        "net.toshayo.waterframes.transformers"
})
public class WaterFramesPlugin implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("WaterFramesBackportedPlugin");

    public WaterFramesPlugin() {
        /*final Path jnaLibPath = Paths.get("mods", "waterframes-jna-5.10.0.jar");
        if(!jnaLibPath.toFile().exists()) {
            LOGGER.info("Extracting JNA library to {}", jnaLibPath.toAbsolutePath());
            try(InputStream is = getClass().getResourceAsStream("/jars/jna-5.10.0.jar")) {
                if(is != null) {
                    try(OutputStream os = Files.newOutputStream(jnaLibPath)) {
                        IOUtils.copy(is, os);
                    }
                    LOGGER.info("JNA extraction completed");
                } else {
                    LOGGER.fatal("Failed to find embedded JNA library!");
                    throw new RuntimeException("JNA library is not included");
                }
            } catch (IOException e) {
                LOGGER.fatal("Failed to extract JNA library!");
                throw new RuntimeException(e);
            }
        }*/
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                "net.toshayo.waterframes.transformers.LibrariesDowngradingTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

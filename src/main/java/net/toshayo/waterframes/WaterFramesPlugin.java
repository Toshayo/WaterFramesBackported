package net.toshayo.waterframes;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({
        "net.toshayo.waterframes.transformers"
})
public class WaterFramesPlugin implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("WaterFramesBackportedPlugin");

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

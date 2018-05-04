package ooo.sansk.shinylogger;

import com.pixelmonmod.pixelmon.Pixelmon;
import ooo.sansk.shinylogger.event.PixelmonListener;
import ooo.sansk.shinylogger.service.CaptureLoggerRepository;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(id = PluginInfo.PLUGIN_ID, name = PluginInfo.PLUGIN_NAME, version = "1.0", description = PluginInfo.PLUGIN_DESCRIPTION)
public class ShinyLoggerPlugin {

    private static final String CAPTURES_STORAGE_FILE_NAME = "shinyCaptures.csv";

    private PixelmonListener pixelmonListener;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        startPlugin();
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        stopPlugin();
    }

    @Listener
    public void onGameReload(GameReloadEvent gre) {
        stopPlugin();
        startPlugin();
    }

    private void startPlugin() {
        CaptureLoggerRepository captureLoggerRepository = createCaptureLoggerService(configPath.resolve(CAPTURES_STORAGE_FILE_NAME));
        pixelmonListener = new PixelmonListener(new CaptureLogger(captureLoggerRepository));
        Pixelmon.EVENT_BUS.register(pixelmonListener);
    }

    public void stopPlugin() {
        logger.warn("Disabling {}", PluginInfo.PLUGIN_NAME);
        Pixelmon.EVENT_BUS.unregister(pixelmonListener);
        Sponge.getEventManager().unregisterPluginListeners(this);
    }

    private CaptureLoggerRepository createCaptureLoggerService(Path storageFilePath) {
        CaptureLoggerRepository captureLoggerRepository = new CaptureLoggerRepository(storageFilePath);
        captureLoggerRepository.loadAll();
        return captureLoggerRepository;
    }
}

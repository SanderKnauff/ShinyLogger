package ooo.sansk.shinylogger;

import com.pixelmonmod.pixelmon.Pixelmon;
import ooo.sansk.shinylogger.command.CaptureLogDisplayCommand;
import ooo.sansk.shinylogger.command.PurgeCaptureDataCommand;
import ooo.sansk.shinylogger.event.PixelmonListener;
import ooo.sansk.shinylogger.service.CaptureLoggerRepository;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Plugin(id = PluginInfo.PLUGIN_ID, name = PluginInfo.PLUGIN_NAME, version = "1.0", description = PluginInfo.PLUGIN_DESCRIPTION)
public class ShinyLoggerPlugin {

    private static final String CAPTURES_STORAGE_FILE_NAME = "shinyCaptures.csv";

    private PixelmonListener pixelmonListener;
    private Collection<CommandMapping> registeredCommands;

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
        registeredCommands = registerCommands(captureLoggerRepository);
    }

    public void stopPlugin() {
        logger.warn("Disabling {}", PluginInfo.PLUGIN_NAME);
        Pixelmon.EVENT_BUS.unregister(pixelmonListener);
        Sponge.getEventManager().unregisterPluginListeners(this);
        unregisterCommands();
    }

    private CaptureLoggerRepository createCaptureLoggerService(Path storageFilePath) {
        CaptureLoggerRepository captureLoggerRepository = new CaptureLoggerRepository(storageFilePath);
        captureLoggerRepository.loadAll();
        return captureLoggerRepository;
    }

    private Collection<CommandMapping> registerCommands(CaptureLoggerRepository captureLoggerRepository) {
        List<CommandMapping> commandMappingList = new ArrayList<>();
        Sponge.getCommandManager().register(this, CaptureLogDisplayCommand.createCommandSpec(this, captureLoggerRepository), "listshiny", "shinycaptures", "showshinycaptures").ifPresent(commandMappingList::add);
        Sponge.getCommandManager().register(this, PurgeCaptureDataCommand.createCommandSpec(captureLoggerRepository), "purgeshinylogs").ifPresent(commandMappingList::add);
        return commandMappingList;
    }

    private void unregisterCommands() {
        registeredCommands.forEach(Sponge.getCommandManager()::removeMapping);
    }


}

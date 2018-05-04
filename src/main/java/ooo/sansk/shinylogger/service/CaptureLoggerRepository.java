package ooo.sansk.shinylogger.service;

import ooo.sansk.shinylogger.PluginInfo;
import ooo.sansk.shinylogger.ShinyLoggerPlugin;
import ooo.sansk.shinylogger.model.PlayerCaptures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CaptureLoggerRepository implements Repository<UUID, PlayerCaptures> {

    private static final Logger logger = LoggerFactory.getLogger(CaptureLoggerRepository.class);

    private final Path playerCaptureCacheStorageFilePath;
    private Map<UUID, PlayerCaptures> playerCapturesCache;

    public CaptureLoggerRepository(Path playerCaptureCacheStorageFilePath) {
        this.playerCaptureCacheStorageFilePath = playerCaptureCacheStorageFilePath;
    }

    public void loadAll() {
        if (!captureCacheFileExists())
            createEmptyPlayerCaptureCacheFile();
        playerCapturesCache = readPlayerCapturesFromFile();
    }

    public Collection<PlayerCaptures> getAll() {
        if (isPlayerCaptureCacheLoaded())
            loadAll();
        return playerCapturesCache.values();
    }

    public void addOne(PlayerCaptures item) {
        if (isPlayerCaptureCacheLoaded())
            loadAll();
        replaceInOrAddToCache(item);
        save();
    }

    @Override
    public Optional<PlayerCaptures> findOne(UUID id) {
        return Optional.ofNullable(playerCapturesCache.get(id));
    }

    public void save() {
        try {
            Files.write(playerCaptureCacheStorageFilePath, getSerializedCache());
        } catch (IOException ioe) {
            logger.error("ShinyLogger was unable to do an IO operation due to an exception. The cache is still in memory and may be saved by resolving the cause of the error and forcing a save (By catching a shiny pokemon yourself).  Reason: ({}: {})",
                    ioe.getClass().getSimpleName(),
                    ioe.getMessage());
        }
    }

    private List<String> getSerializedCache() {
        return playerCapturesCache.values().stream()
                .map(this::searializeEntry)
                .collect(Collectors.toList());
    }

    private String searializeEntry(PlayerCaptures playerCaptures) {
        return String.format("%s,%s", playerCaptures.getPlayerId(), playerCaptures.getCount());
    }

    private void replaceInOrAddToCache(PlayerCaptures playerCaptures) {
        playerCapturesCache.put(playerCaptures.getPlayerId(), playerCaptures);
    }

    private boolean isPlayerCaptureCacheLoaded() {
        return playerCapturesCache == null;
    }

    private void createEmptyPlayerCaptureCacheFile() {
        try {
            createStorageDirectoryIfNeccecary();
            Files.createFile(playerCaptureCacheStorageFilePath);
        } catch (IOException e) {
            disablePluginDueToExceptionOnSetup(e);
        }
    }

    private void createStorageDirectoryIfNeccecary() {
        try {
            Files.createDirectories(playerCaptureCacheStorageFilePath.getParent());
        } catch (IOException e) {
            disablePluginDueToExceptionOnSetup(e);
        }
    }

    private boolean captureCacheFileExists() {
        return playerCaptureCacheStorageFilePath.toFile().exists();
    }

    private Map<UUID, PlayerCaptures> readPlayerCapturesFromFile() {
        try {
            return Files.readAllLines(playerCaptureCacheStorageFilePath).stream()
                    .map(this::createPlayerCaptureFromstring)
                    .collect(Collectors.toMap(PlayerCaptures::getPlayerId,
                            Function.identity(),
                            (existingElement, colidingElement) -> existingElement,
                            HashMap::new));
        } catch (IOException ioe) {
            disablePluginDueToExceptionOnSetup(ioe);
            return Collections.emptyMap();
        }
    }

    private void disablePluginDueToExceptionOnSetup(IOException ioe) {
        logger.error("ShinyLogger was unable to do an IO operation required for setting up due to an exception. The plugin will now disable. Reason: ({}: {})",
                ioe.getClass().getSimpleName(),
                ioe.getMessage());
        Sponge.getPluginManager().getPlugin(PluginInfo.PLUGIN_ID)
                .map(PluginContainer::getInstance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ShinyLoggerPlugin.class::cast)
                .ifPresent(ShinyLoggerPlugin::stopPlugin);
    }

    private PlayerCaptures createPlayerCaptureFromstring(String playerCaptureEntry) {
        String[] variables = playerCaptureEntry.split(",");
        UUID playerId = UUID.fromString(variables[0]);
        int captures = Integer.parseInt(variables[1]);
        return new PlayerCaptures(playerId, captures);
    }
}

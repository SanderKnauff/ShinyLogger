package ooo.sansk.shinylogger.display;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.UUID;

public class PlayerCapturesOutput {

    private static final String UNKNOWN_PLAYER_NAME = "Unknown";

    protected String getPlayerNameFromUUID(UUID playerId) {
        return Sponge.getServiceManager().provide(UserStorageService.class)
                .map(userStorageService -> getPlayerNameFromStorage(playerId, userStorageService))
                .orElse(UNKNOWN_PLAYER_NAME);
    }

    private String getPlayerNameFromStorage(UUID playerId, UserStorageService userStorageService) {
        return userStorageService.get(playerId)
                .map(User::getName)
                .orElse(UNKNOWN_PLAYER_NAME);
    }
}

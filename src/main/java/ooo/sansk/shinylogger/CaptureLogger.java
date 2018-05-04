package ooo.sansk.shinylogger;

import net.minecraft.entity.player.EntityPlayerMP;
import ooo.sansk.shinylogger.model.PlayerCaptures;
import ooo.sansk.shinylogger.service.CaptureLoggerRepository;

import java.util.Optional;
import java.util.UUID;

public class CaptureLogger {

    private final CaptureLoggerRepository captureLoggerRepository;

    public CaptureLogger(CaptureLoggerRepository captureLoggerRepository) {
        this.captureLoggerRepository = captureLoggerRepository;
    }

    public void logCaptureForPlayer(EntityPlayerMP player) {
        PlayerCaptures amountOfCapturesByPlayer = getAmountOfCapturesByPlayer(player);
        amountOfCapturesByPlayer.setCount(amountOfCapturesByPlayer.getCount() + 1);
        captureLoggerRepository.addOne(amountOfCapturesByPlayer);
    }

    public PlayerCaptures getAmountOfCapturesByPlayer(EntityPlayerMP entityPlayerMP) {
        return findPlayerCaptureLogByUUID(entityPlayerMP.getUniqueID()).orElseGet(() -> {
            PlayerCaptures playerCaptures = new PlayerCaptures(entityPlayerMP.getUniqueID(), 0);
            captureLoggerRepository.addOne(playerCaptures);
            return playerCaptures;
        });
    }

    public Optional<PlayerCaptures> findPlayerCaptureLogByUUID(UUID uuid) {
        return captureLoggerRepository.getAll().stream()
                .filter(playerCaptures -> playerCaptures.getPlayerId().equals(uuid))
                .findFirst();
    }
}

package ooo.sansk.shinylogger.event;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ooo.sansk.shinylogger.CaptureLogger;
import org.slf4j.LoggerFactory;

public class PixelmonListener {

    private final CaptureLogger captureLogger;

    public PixelmonListener(CaptureLogger captureLogger) {
        this.captureLogger = captureLogger;
    }

    @SubscribeEvent
    public void onPlayerPokemonCatch(CaptureEvent.SuccessfulCapture successfulCaptureEvent) {
        if(successfulCaptureEvent.getPokemon().getIsShiny()) {
            captureLogger.logCaptureForPlayer(successfulCaptureEvent.player);
            LoggerFactory.getLogger(PixelmonListener.class).info("Player {} captured a shiny",
                    successfulCaptureEvent.player.getName());
        }
    }
}

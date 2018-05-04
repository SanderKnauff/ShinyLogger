package ooo.sansk.shinylogger.event;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ooo.sansk.shinylogger.CaptureLogger;

public class PixelmonListener {

    private final CaptureLogger captureLogger;

    public PixelmonListener(CaptureLogger captureLogger) {
        this.captureLogger = captureLogger;
    }

    @SubscribeEvent
    public void onPlayerPokemonCatch(CaptureEvent.SuccessfulCapture successfulCaptureEvent) {
        if(successfulCaptureEvent.getPokemon().getIsShiny()) {
            captureLogger.logCaptureForPlayer(successfulCaptureEvent.player);
        }
    }
}

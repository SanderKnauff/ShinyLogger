package ooo.sansk.shinylogger.display.printer;

import ooo.sansk.shinylogger.display.PlayerCapturesOutput;
import ooo.sansk.shinylogger.model.PlayerCaptures;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Comparator;

public class CaptureLogPrinter extends PlayerCapturesOutput {

    private final CommandSource logReceiver;
    private final Collection<PlayerCaptures> logData;

    public CaptureLogPrinter(CommandSource logReceiver, Collection<PlayerCaptures> logData) {
        this.logReceiver = logReceiver;
        this.logData = logData;
    }

    public void print() {
        printHeader();
        printData();
        printFooter();
    }

    private void printHeader() {
        logReceiver.sendMessage(Text.of(TextColors.GOLD, "###### ", TextColors.GREEN, "Logged Shiny Captures", TextColors.GOLD, " ######"));
    }

    private void printData() {
        logData.stream()
                .sorted(Comparator.comparingInt(PlayerCaptures::getCount).reversed())
                .forEach(this::printPlayerCaptureEntry);
    }

    private void printFooter() {
        logReceiver.sendMessage(Text.of(TextColors.GOLD, "###### ", TextColors.GREEN, "Total Captures: ", TextColors.BLUE, calculateTotalCaptureCount(), TextColors.GOLD, " ######"));
    }

    private int calculateTotalCaptureCount() {
        return logData.stream().mapToInt(PlayerCaptures::getCount).sum();
    }

    private void printPlayerCaptureEntry(PlayerCaptures playerCaptures) {
        logReceiver.sendMessage(Text.of(TextColors.RED, playerCaptures.getPlayerId(), TextColors.DARK_GRAY, " (", TextColors.YELLOW, getPlayerNameFromUUID(playerCaptures.getPlayerId()), TextColors.DARK_GRAY, "): ", TextColors.BLUE, playerCaptures.getCount()));
    }


}

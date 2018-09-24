package ooo.sansk.shinylogger.display.printer;

import ooo.sansk.shinylogger.display.PlayerCapturesOutput;
import ooo.sansk.shinylogger.model.PlayerCaptures;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Comparator;

public class CaptureLogPrinter extends PlayerCapturesOutput {

    private static final int DEFAULT_PRINT_AMOUNT = 10;

    private final CommandSource logReceiver;
    private final Collection<PlayerCaptures> logData;
    private final int amountToPrint;

    public CaptureLogPrinter(CommandSource logReceiver, Collection<PlayerCaptures> logData) {
        this(logReceiver, logData, DEFAULT_PRINT_AMOUNT);
    }

    public CaptureLogPrinter(CommandSource logReceiver, Collection<PlayerCaptures> logData, int amountToPrint) {
        this.logReceiver = logReceiver;
        this.logData = logData;
        this.amountToPrint = amountToPrint;
    }

    public void print() {
        printHeader();
        printData();
        printFooter();
    }

    private void printHeader() {
        logReceiver.sendMessage(Text.of(TextColors.GOLD, "###### ", TextColors.GREEN, "Top ", Math.min(logData.size(), amountToPrint), " Logged Shiny Captures", TextColors.GOLD, " ######"));
    }

    private void printData() {
        logData.stream()
                .sorted(Comparator.comparingInt(PlayerCaptures::getCount).reversed())
                .limit(amountToPrint)
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

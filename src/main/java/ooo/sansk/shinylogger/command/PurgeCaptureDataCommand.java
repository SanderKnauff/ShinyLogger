package ooo.sansk.shinylogger.command;

import ooo.sansk.shinylogger.model.PlayerCaptures;
import ooo.sansk.shinylogger.service.CaptureLoggerRepository;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class PurgeCaptureDataCommand implements CommandExecutor {

    private static final String DESCRIPTION_SHORT = "Purge the logged shiny capture data.";
    private static final String DESCRIPTION_EXTENDED = "Optionally clear the logged data of a single player.";
    private static final String PERMISSION_NODE = "shinylogger.purge";

    private static final String COMMAND_ARGUMENT_PLAYER = "player";
    private static final Text COMMAND_ARGUMENT_KEY_PLAYER = Text.of(COMMAND_ARGUMENT_PLAYER);

    private final CaptureLoggerRepository captureLoggerRepository;

    public PurgeCaptureDataCommand(CaptureLoggerRepository captureLoggerRepository) {
        this.captureLoggerRepository = captureLoggerRepository;
    }

    public static CommandSpec createCommandSpec(CaptureLoggerRepository captureLoggerRepository) {
        return CommandSpec.builder()
                .executor(new PurgeCaptureDataCommand(captureLoggerRepository))
                .description(Text.of(DESCRIPTION_SHORT))
                .extendedDescription(Text.of(DESCRIPTION_EXTENDED))
                .permission(PERMISSION_NODE)
                .arguments(GenericArguments.optionalWeak(GenericArguments.player(Text.of(COMMAND_ARGUMENT_KEY_PLAYER))))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext context) {
        Optional<Player> oPlayerToClear = context.getOne(COMMAND_ARGUMENT_KEY_PLAYER);
        if (oPlayerToClear.isPresent())
            clearLoggedDataForPlayer(commandSource, oPlayerToClear.get());
        else
            clearAllLoggedData(commandSource);
        return CommandResult.success();
    }

    private void clearLoggedDataForPlayer(CommandSource commandSource, Player player) {
        Optional<PlayerCaptures> oPlayerCaptures = captureLoggerRepository.removeOne(player.getUniqueId());
        if (oPlayerCaptures.isPresent())
            commandSource.sendMessage(Text.of(
                    TextColors.RED, "Removed all logged captures of player ",
                    TextColors.GOLD, player.getName(),
                    TextColors.RED, ". Player had captured ",
                    TextColors.BLUE, oPlayerCaptures.get().getCount(),
                    TextColors.RED, " shiny pokemon since last data purge.")
            );
        else
            commandSource.sendMessage(Text.of(
                    TextColors.RED, "Could not find any logged captures for ",
                    TextColors.GOLD, player.getName(),
                    TextColors.RED, "."));
    }

    private void clearAllLoggedData(CommandSource commandSource) {
        commandSource.sendMessage(Text.of(TextColors.RED, "Removed all currently logged shiny captures."));
        captureLoggerRepository.removeAll();

    }
}

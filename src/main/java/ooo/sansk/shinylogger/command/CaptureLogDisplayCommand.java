package ooo.sansk.shinylogger.command;

import ooo.sansk.shinylogger.display.gui.CaptureLogGui;
import ooo.sansk.shinylogger.display.printer.CaptureLogPrinter;
import ooo.sansk.shinylogger.service.CaptureLoggerRepository;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CaptureLogDisplayCommand implements CommandExecutor {

    private static final String DESCRIPTION_SHORT = "Show captured shiny statistics";
    private static final String DESCRIPTION_EXTENDED = "Display the list of Players that have captured shiny Pokeémon and the amounts the have caught";
    private static final String PERMISSION_NODE = "shinylogger.display";

    private static final String COMMAND_ARGUMENT_LIST = "list";
    private static final Text COMMAND_ARGUMENT_KEY_LIST = Text.of(COMMAND_ARGUMENT_LIST);

    private final Object pluginInstance;
    private final CaptureLoggerRepository captureLoggerRepository;

    public CaptureLogDisplayCommand(Object pluginInstance, CaptureLoggerRepository captureLoggerRepository) {
        this.pluginInstance = pluginInstance;
        this.captureLoggerRepository = captureLoggerRepository;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext context) {
        if(!verifyCommandSourceIsPlayer(commandSource) || commandRequestsPrintedList(context))
            printListToChat(commandSource);
        else
            openGui(Player.class.cast(commandSource));
        return CommandResult.success();
    }


    private boolean verifyCommandSourceIsPlayer(CommandSource commandSource) {
        return Player.class.isInstance(commandSource);
    }

    private boolean commandRequestsPrintedList(CommandContext context) {
        return context.getOne(COMMAND_ARGUMENT_KEY_LIST).isPresent();
    }

    private void printListToChat(CommandSource commandSource) {
        new CaptureLogPrinter(commandSource, captureLoggerRepository.getAll()).print();
    }

    private void openGui(Player dataReceiver) {
        new CaptureLogGui(pluginInstance, dataReceiver, captureLoggerRepository.getAll()).displayData();
    }

    public static CommandSpec createCommandSpec(Object pluginInstance, CaptureLoggerRepository captureLoggerRepository) {
        return CommandSpec.builder()
                .executor(new CaptureLogDisplayCommand(pluginInstance, captureLoggerRepository))
                .description(Text.of(DESCRIPTION_SHORT))
                .extendedDescription(Text.of(DESCRIPTION_EXTENDED))
                .permission(PERMISSION_NODE)
                .arguments(GenericArguments.optionalWeak(GenericArguments.literal(Text.of(COMMAND_ARGUMENT_KEY_LIST), COMMAND_ARGUMENT_LIST)))
                .build();
    }
}

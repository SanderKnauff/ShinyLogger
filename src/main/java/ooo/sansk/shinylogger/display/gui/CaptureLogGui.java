package ooo.sansk.shinylogger.display.gui;

import ooo.sansk.shinylogger.display.PlayerCapturesOutput;
import ooo.sansk.shinylogger.model.PlayerCaptures;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CaptureLogGui extends PlayerCapturesOutput {

    private static final int INVENTORY_WIDTH = 9;
    private static final int INVENTORY_HEIGHT = 6;
    private static final int NAVIGATION_ROW_COUNT = 1;
    private static final Text INVENTORY_TITLE = Text.of("Shiny count per player");

    private final Object pluginInstance;
    private final Player logReceiver;
    private final List<PlayerCaptures> logData;

    private Inventory inventory;
    private int topRowIndex = 0;

    public CaptureLogGui(Object pluginInstance, Player logReceiver, Collection<PlayerCaptures> logData) {
        this.pluginInstance = pluginInstance;
        this.logReceiver = logReceiver;
        this.logData = logData.stream()
                .sorted(Comparator.comparingInt(PlayerCaptures::getCount).reversed())
                .collect(Collectors.toList());
    }

    public void displayData() {
        createDisplayInventory();
        updateInventory();
        logReceiver.openInventory(inventory);
    }

    private void createDisplayInventory() {
        inventory = Inventory.builder()
                .property(InventoryDimension.of(INVENTORY_WIDTH, INVENTORY_HEIGHT))
                .property(InventoryTitle.of(INVENTORY_TITLE))
                .listener(ClickInventoryEvent.class, this::handleInventoryClick)
                .build(pluginInstance);
    }

    private void handleInventoryClick(ClickInventoryEvent clickInventoryEvent) {
        clickInventoryEvent.setCancelled(true);
        clickInventoryEvent.getTransactions().forEach(this::handleSlotTransaction);
    }

    private void handleSlotTransaction(SlotTransaction slotTransaction) {
        int slotIndex = getSlotIndex(slotTransaction);
        if (slotIndex == 8)
            moveRow(-1);
        else if (slotIndex == 53)
            moveRow(1);
    }

    private void moveRow(int amountOfRowsToMove) {
        topRowIndex = Math.max(topRowIndex + amountOfRowsToMove, 0);
        topRowIndex = Math.min(topRowIndex, calculateMaxRow());
        updateInventory();
    }

    private int calculateMaxRow() {
        int containerWidth = INVENTORY_WIDTH - NAVIGATION_ROW_COUNT;
        return logData.size() / containerWidth;
    }

    private void updateInventory() {
        inventory.first().clear();
        addControlButtons();
        fillGui();
    }

    private void fillGui() {
        for (int listIndex = 0; listIndex < logData.size(); listIndex++)
            if (!isInInventoryNavigationRow(listIndex))
                fillSlot(listIndex);

    }

    private void fillSlot(int listIndex) {
        inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(getSlotPosFromIndex(listIndex)))
                .offer(buildItemStack(logData.get(listIndex)));
    }

    private ItemStack buildItemStack(PlayerCaptures playerCaptures) {
        return ItemStack.builder()
                .itemType(ItemTypes.SKULL)
                .add(Keys.DISPLAY_NAME, Text.of(getPlayerNameFromUUID(playerCaptures.getPlayerId())))
                .add(Keys.ITEM_LORE, Collections.singletonList(Text.of("Captures: ", playerCaptures.getCount())))
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, GameProfile.of(playerCaptures.getPlayerId()))
                .build();
    }

    private SlotPos getSlotPosFromIndex(int listIndex) {
        int containerWidth = INVENTORY_WIDTH - NAVIGATION_ROW_COUNT;
        int rowIndex = (listIndex - (topRowIndex * containerWidth)) / containerWidth;
        int columnIndex = (listIndex - (topRowIndex * containerWidth)) % containerWidth;
        return SlotPos.of(columnIndex, rowIndex);
    }

    private boolean isInInventoryNavigationRow(int i) {
        return i < topRowIndex * (INVENTORY_WIDTH - 1);
    }

    private void addControlButtons() {
        inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 0)))
                .offer(ItemStack.builder().itemType(ItemTypes.STONE_BUTTON).build());
        inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 5)))
                .offer(ItemStack.builder().itemType(ItemTypes.STONE_BUTTON).build());
    }


    private int getSlotIndex(SlotTransaction slotTransaction) {
        return slotTransaction.getSlot().getInventoryProperty(SlotIndex.class).map(slot -> slot.getValue() != null ? slot.getValue() : -1).orElse(-1);
    }
}

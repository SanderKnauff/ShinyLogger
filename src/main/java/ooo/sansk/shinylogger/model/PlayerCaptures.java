package ooo.sansk.shinylogger.model;

import java.util.UUID;

public class PlayerCaptures {

    private final UUID playerId;
    private int count;

    public PlayerCaptures(UUID playerId, int count) {
        this.playerId = playerId;
        this.count = count;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

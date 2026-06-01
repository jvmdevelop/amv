package com.jvmd.anidromvost.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomEvent {
    private Type type;
    private String username;
    private Double timestamp;
    private String message;

    public enum Type {
        PLAY, PAUSE, SEEK, CHAT, JOIN, LEAVE
    }
}

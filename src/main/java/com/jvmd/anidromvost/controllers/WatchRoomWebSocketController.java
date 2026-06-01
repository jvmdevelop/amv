package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.RoomEvent;
import com.jvmd.anidromvost.service.WatchRoomService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WatchRoomWebSocketController {
    private WatchRoomService watchRoomService;

    @MessageMapping("/room/{code}/sync")
    @SendTo("/topic/room/{code}")
    public RoomEvent sync(@DestinationVariable String code, RoomEvent event,
                          SimpMessageHeaderAccessor headerAccessor) {
        watchRoomService.findByCode(code);
        return event;
    }

    @MessageMapping("/room/{code}/chat")
    @SendTo("/topic/room/{code}")
    public RoomEvent chat(@DestinationVariable String code, RoomEvent event,
                          SimpMessageHeaderAccessor headerAccessor) {
        watchRoomService.findByCode(code);
        event.setType(RoomEvent.Type.CHAT);
        return event;
    }
}

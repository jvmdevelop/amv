package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.model.WatchRoom;
import com.jvmd.anidromvost.service.UserService;
import com.jvmd.anidromvost.service.WatchRoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
@AllArgsConstructor
public class WatchRoomController {
    private WatchRoomService watchRoomService;
    private UserService userService;

    @PostMapping
    public ResponseEntity<WatchRoom> create(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam Long episodeId) {
        User user = userService.findByUsername(principal.getUsername());
        return ResponseEntity.ok(watchRoomService.create(user, episodeId));
    }

    @GetMapping("/{code}")
    public ResponseEntity<WatchRoom> get(@PathVariable String code) {
        return ResponseEntity.ok(watchRoomService.findByCode(code));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> close(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String code) {
        User user = userService.findByUsername(principal.getUsername());
        watchRoomService.close(code, user);
        return ResponseEntity.noContent().build();
    }
}

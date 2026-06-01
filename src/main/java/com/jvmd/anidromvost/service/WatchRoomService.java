package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Episode;
import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.model.WatchRoom;
import com.jvmd.anidromvost.repository.EpisodeRepo;
import com.jvmd.anidromvost.repository.WatchRoomRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class WatchRoomService {
    private WatchRoomRepo watchRoomRepo;
    private EpisodeRepo episodeRepo;

    public WatchRoom create(User host, Long episodeId) {
        Episode episode = episodeRepo.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found: " + episodeId));

        WatchRoom room = WatchRoom.builder()
                .code(generateCode())
                .episode(episode)
                .host(host)
                .build();
        return watchRoomRepo.save(room);
    }

    public WatchRoom findByCode(String code) {
        return watchRoomRepo.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Room not found or closed: " + code));
    }

    public void close(String code, User user) {
        WatchRoom room = findByCode(code);
        if (!room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("Only the host can close the room");
        }
        room.setActive(false);
        watchRoomRepo.save(room);
    }

    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return sb.toString();
    }
}

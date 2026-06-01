package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.Episode;
import com.jvmd.anidromvost.service.EpisodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/episodes")
@AllArgsConstructor
public class EpisodeController {
    private EpisodeService episodeService;

    @GetMapping("/anime/{animeId}")
    public ResponseEntity<List<Episode>> listByAnime(@PathVariable Long animeId) {
        return ResponseEntity.ok(episodeService.findByAnimeId(animeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Episode> getById(@PathVariable Long id) {
        return ResponseEntity.ok(episodeService.findById(id));
    }
}

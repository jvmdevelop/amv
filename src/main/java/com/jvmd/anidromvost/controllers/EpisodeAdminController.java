package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.Episode;
import com.jvmd.anidromvost.service.EpisodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/episodes")
@AllArgsConstructor
public class EpisodeAdminController {
    private EpisodeService episodeService;

    @PostMapping("/anime/{animeId}")
    public ResponseEntity<Episode> create(
            @PathVariable Long animeId,
            @RequestParam Integer episodeNumber,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(episodeService.create(animeId, episodeNumber, title));
    }

    @PostMapping("/{id}/video")
    public ResponseEntity<Episode> uploadVideo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(episodeService.uploadVideo(id, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        episodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

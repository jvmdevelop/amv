package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.Episode;
import com.jvmd.anidromvost.repository.AnimeRepo;
import com.jvmd.anidromvost.repository.EpisodeRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class EpisodeService {
    private EpisodeRepo episodeRepo;
    private AnimeRepo animeRepo;
    private MinioStorageService storageService;
    private SimpMessagingTemplate messagingTemplate;

    public Episode create(Long animeId, Integer episodeNumber, String title) {
        Anime anime = animeRepo.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime not found: " + animeId));

        Episode episode = Episode.builder()
                .anime(anime)
                .episodeNumber(episodeNumber)
                .title(title)
                .build();
        return episodeRepo.save(episode);
    }

    public List<Episode> findByAnimeId(Long animeId) {
        return episodeRepo.findByAnimeIdOrderByEpisodeNumber(animeId);
    }

    public Episode findById(Long id) {
        return episodeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found: " + id));
    }

    public Episode uploadVideo(Long episodeId, MultipartFile file) throws Exception {
        Episode episode = findById(episodeId);

        if (episode.getVideoKey() != null) {
            storageService.deleteVideo(episode.getVideoKey());
        }

        String key = storageService.uploadVideo(file);
        episode.setVideoKey(key);
        episode = episodeRepo.save(episode);

        messagingTemplate.convertAndSend("/topic/new-episodes", Map.of(
                "animeId", episode.getAnime().getId(),
                "animeTitle", episode.getAnime().getTitle(),
                "episodeNumber", episode.getEpisodeNumber(),
                "episodeTitle", episode.getTitle() != null ? episode.getTitle() : ""
        ));

        return episode;
    }

    public void delete(Long id) {
        Episode episode = findById(id);
        try {
            if (episode.getVideoKey() != null) {
                storageService.deleteVideo(episode.getVideoKey());
            }
        } catch (Exception e) {
            log.warn("Failed to delete video: {}", e.getMessage());
        }
        episodeRepo.delete(episode);
    }
}

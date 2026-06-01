package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.Episode;
import com.jvmd.anidromvost.service.AnimeService;
import com.jvmd.anidromvost.service.EpisodeService;
import com.jvmd.anidromvost.service.MinioStorageService;
import io.minio.StatObjectResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/stream")
@AllArgsConstructor
public class StreamController {
    private EpisodeService episodeService;
    private AnimeService animeService;
    private MinioStorageService storageService;

    @GetMapping("/video/{episodeId}")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @PathVariable Long episodeId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws Exception {

        Episode episode = episodeService.findById(episodeId);
        if (episode.getVideoKey() == null) {
            return ResponseEntity.notFound().build();
        }

        StatObjectResponse stat = storageService.getVideoStat(episode.getVideoKey());
        long fileSize = stat.size();
        String contentType = stat.contentType() != null ? stat.contentType() : "video/mp4";

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 && !ranges[1].isEmpty()
                    ? Long.parseLong(ranges[1])
                    : fileSize - 1;
            long contentLength = end - start + 1;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            headers.set("Accept-Ranges", "bytes");
            headers.setContentLength(contentLength);
            headers.setContentType(MediaType.parseMediaType(contentType));

            StreamingResponseBody body = outputStream -> {
                try (InputStream is = storageService.getVideoStream(episode.getVideoKey(), start, contentLength)) {
                    is.transferTo(outputStream);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            return new ResponseEntity<>(body, headers, HttpStatus.PARTIAL_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Ranges", "bytes");
        headers.setContentLength(fileSize);
        headers.setContentType(MediaType.parseMediaType(contentType));

        StreamingResponseBody body = outputStream -> {
            try (InputStream is = storageService.getVideoStream(episode.getVideoKey())) {
                is.transferTo(outputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @GetMapping("/image/{animeId}/cover")
    public ResponseEntity<StreamingResponseBody> streamCover(@PathVariable Long animeId) throws Exception {
        Anime anime = animeService.findById(animeId);
        if (anime.getCoverImageKey() == null) {
            return ResponseEntity.notFound().build();
        }

        StatObjectResponse stat = storageService.getImageStat(anime.getCoverImageKey());
        String contentType = stat.contentType() != null ? stat.contentType() : "image/jpeg";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(stat.size());
        headers.setContentType(MediaType.parseMediaType(contentType));

        StreamingResponseBody body = outputStream -> {
            try (InputStream is = storageService.getImageStream(anime.getCoverImageKey())) {
                is.transferTo(outputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}

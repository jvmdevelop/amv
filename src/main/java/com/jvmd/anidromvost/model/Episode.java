package com.jvmd.anidromvost.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "episodes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"anime_id", "episode_number"}))
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    @JsonBackReference
    private Anime anime;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    private String title;

    private String videoKey;

    private Long videoDurationSeconds;

    private LocalDateTime uploadedAt;

    public Long getAnimeId() {
        return anime != null ? anime.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}

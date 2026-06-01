package com.jvmd.anidromvost.repository;

import com.jvmd.anidromvost.model.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeRepo extends JpaRepository<Anime, Long> {
    Page<Anime> findByGenresId(Long genreId, Pageable pageable);

    List<Anime> findByTitleContainingIgnoreCase(String query);
}

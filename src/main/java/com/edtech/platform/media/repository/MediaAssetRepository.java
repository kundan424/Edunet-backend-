package com.edtech.platform.media.repository;

import com.edtech.platform.media.domain.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    Optional<MediaAsset> findByLessonId(UUID lessonId);
    void deleteByLessonId(UUID lessonId);
}

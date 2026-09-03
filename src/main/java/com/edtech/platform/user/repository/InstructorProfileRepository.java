package com.edtech.platform.user.repository;

import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, UUID> {
    Optional<InstructorProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    List<InstructorProfile> findAllByVerificationStatus(VerificationStatus verificationStatus);

    @Query("SELECT p FROM InstructorProfile p JOIN FETCH p.user WHERE p.user.id IN :userIds")
    List<InstructorProfile> findByUserIdInWithUser(@Param("userIds") List<UUID> userIds);
}

package io.tinylink.core.repository;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.LinkHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkHitRepository extends JpaRepository<LinkHit, Long> {

    List<LinkHit> findTop20ByOrderByHitAtDesc();

    void deleteByLinkId(Long linkId);

    long countByLinkOwner(AppUser owner);

    List<LinkHit> findTop20ByLinkOwnerOrderByHitAtDesc(AppUser owner);
}

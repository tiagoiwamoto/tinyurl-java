package io.tinylink.core.repository;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.ShortLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    Optional<ShortLink> findByCode(String code);

    boolean existsByCode(String code);

    Page<ShortLink> findByFullUrlContainingIgnoreCase(String urlFragment, Pageable pageable);

    List<ShortLink> findTop10ByOrderByHitCountDesc();

    Page<ShortLink> findByOwner(AppUser owner, Pageable pageable);

    Page<ShortLink> findByOwnerAndFullUrlContainingIgnoreCase(AppUser owner, String urlFragment, Pageable pageable);

    Optional<ShortLink> findByIdAndOwner(Long id, AppUser owner);

    long countByOwner(AppUser owner);

    List<ShortLink> findTop10ByOwnerOrderByHitCountDesc(AppUser owner);
}

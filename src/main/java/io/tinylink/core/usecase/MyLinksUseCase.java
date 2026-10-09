package io.tinylink.core.usecase;

import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.error.LinkNotFoundException;
import io.tinylink.core.repository.LinkHitRepository;
import io.tinylink.core.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyLinksUseCase {

    private final ShortLinkRepository shortLinkRepository;
    private final LinkHitRepository linkHitRepository;

    @Transactional(readOnly = true)
    public Page<ShortLink> list(AppUser owner, String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return shortLinkRepository.findByOwner(owner, pageable);
        }
        return shortLinkRepository.findByOwnerAndFullUrlContainingIgnoreCase(owner, search.trim(), pageable);
    }

    @Transactional
    public void delete(AppUser owner, Long id) {
        ShortLink link = shortLinkRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new LinkNotFoundException(id));
        linkHitRepository.deleteByLinkId(link.getId());
        shortLinkRepository.delete(link);
    }

    @Transactional
    public ShortLink updateSplash(AppUser owner, Long id, boolean showSplash) {
        ShortLink link = shortLinkRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new LinkNotFoundException(id));
        link.setShowSplash(showSplash);
        return shortLinkRepository.save(link);
    }

    @Transactional(readOnly = true)
    public StatsUseCase.LinkStats stats(AppUser owner) {
        return new StatsUseCase.LinkStats(
                shortLinkRepository.countByOwner(owner),
                linkHitRepository.countByLinkOwner(owner),
                shortLinkRepository.findTop10ByOwnerOrderByHitCountDesc(owner),
                linkHitRepository.findTop20ByLinkOwnerOrderByHitAtDesc(owner));
    }
}

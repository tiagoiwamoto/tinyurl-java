package io.tinylink.core.usecase;

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
public class AdminLinksUseCase {

    private final ShortLinkRepository shortLinkRepository;
    private final LinkHitRepository linkHitRepository;

    @Transactional(readOnly = true)
    public Page<ShortLink> list(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return shortLinkRepository.findAll(pageable);
        }
        return shortLinkRepository.findByFullUrlContainingIgnoreCase(search.trim(), pageable);
    }

    @Transactional
    public void delete(Long id) {
        ShortLink link = shortLinkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException(id));
        linkHitRepository.deleteByLinkId(link.getId());
        shortLinkRepository.delete(link);
    }

    @Transactional
    public ShortLink updateSplash(Long id, boolean showSplash) {
        ShortLink link = shortLinkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException(id));
        link.setShowSplash(showSplash);
        return shortLinkRepository.save(link);
    }
}

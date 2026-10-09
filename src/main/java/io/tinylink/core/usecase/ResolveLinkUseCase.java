package io.tinylink.core.usecase;

import io.tinylink.core.entity.LinkHit;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.error.LinkNotFoundException;
import io.tinylink.core.repository.LinkHitRepository;
import io.tinylink.core.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ResolveLinkUseCase {

    private final ShortLinkRepository shortLinkRepository;
    private final LinkHitRepository linkHitRepository;

    @Transactional
    public ShortLink resolve(String code, String visitorIp) {
        ShortLink link = shortLinkRepository.findByCode(code)
                .orElseThrow(() -> new LinkNotFoundException(code));

        link.setHitCount(link.getHitCount() + 1);
        LinkHit hit = new LinkHit();
        hit.setLink(link);
        hit.setHitAt(Instant.now());
        hit.setVisitorIp(visitorIp);
        linkHitRepository.save(hit);

        return shortLinkRepository.save(link);
    }

    @Transactional(readOnly = true)
    public long totalLinks() {
        return shortLinkRepository.count();
    }
}

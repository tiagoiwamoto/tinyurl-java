package io.tinylink.core.usecase;

import io.tinylink.core.entity.LinkHit;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.repository.LinkHitRepository;
import io.tinylink.core.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsUseCase {

    private final ShortLinkRepository shortLinkRepository;
    private final LinkHitRepository linkHitRepository;

    @Transactional(readOnly = true)
    public LinkStats current() {
        return new LinkStats(
                shortLinkRepository.count(),
                linkHitRepository.count(),
                shortLinkRepository.findTop10ByOrderByHitCountDesc(),
                linkHitRepository.findTop20ByOrderByHitAtDesc());
    }

    public record LinkStats(long totalLinks, long totalHits, List<ShortLink> topLinks, List<LinkHit> recentHits) {
    }
}

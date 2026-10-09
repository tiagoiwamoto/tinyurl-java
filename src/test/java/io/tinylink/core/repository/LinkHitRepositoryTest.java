package io.tinylink.core.repository;

import io.tinylink.core.entity.LinkHit;
import io.tinylink.core.entity.ShortLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LinkHitRepositoryTest {

    @Autowired
    LinkHitRepository hits;

    @Autowired
    ShortLinkRepository links;

    @Test
    void deletesAllHitsOfLink() {
        ShortLink link = new ShortLink();
        link.setCode("abc123");
        link.setFullUrl("http://exemplo.com");
        link.setShowSplash(true);
        link.setHitCount(2);
        link.setCreatedAt(Instant.now());
        links.save(link);

        LinkHit hit = new LinkHit();
        hit.setLink(link);
        hit.setHitAt(Instant.now());
        hit.setVisitorIp("10.0.0.1");
        hits.save(hit);

        hits.deleteByLinkId(link.getId());
        hits.flush();

        assertThat(hits.count()).isZero();
    }
}

package io.tinylink.core.repository;

import io.tinylink.core.entity.ShortLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShortLinkRepositoryTest {

    @Autowired
    ShortLinkRepository repository;

    private ShortLink link(String code, String url, long hits) {
        ShortLink link = new ShortLink();
        link.setCode(code);
        link.setFullUrl(url);
        link.setShowSplash(true);
        link.setHitCount(hits);
        link.setCreatedAt(Instant.now());
        return repository.save(link);
    }

    @Test
    void findsByCode() {
        link("abc123", "http://exemplo.com", 0);

        assertThat(repository.findByCode("abc123")).isPresent();
        assertThat(repository.findByCode("nope")).isEmpty();
        assertThat(repository.existsByCode("abc123")).isTrue();
    }

    @Test
    void searchesIgnoringCase() {
        link("a1", "http://ExEmPlO.com/path", 0);
        link("a2", "http://outro.com", 0);

        var page = repository.findByFullUrlContainingIgnoreCase("exemplo", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getCode()).isEqualTo("a1");
    }

    @Test
    void ordersTopByHitCount() {
        link("low", "http://a.com", 1);
        link("high", "http://b.com", 99);
        link("mid", "http://c.com", 50);

        var top = repository.findTop10ByOrderByHitCountDesc();

        assertThat(top).extracting(ShortLink::getCode).containsExactly("high", "mid", "low");
    }

    @Test
    void sortsPageableByUrl() {
        link("z1", "http://zzz.com", 0);
        link("a1", "http://aaa.com", 0);

        var page = repository.findAll(PageRequest.of(0, 10, Sort.by("fullUrl").ascending()));

        assertThat(page.getContent()).extracting(ShortLink::getCode).containsExactly("a1", "z1");
    }
}

package io.tinylink.core.usecase;

import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.AppUser;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.error.InvalidUrlException;
import io.tinylink.core.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShortenLinkUseCase {

    private static final String CODE_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final ShortLinkRepository shortLinkRepository;
    private final SettingsUseCase settingsUseCase;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public ShortLink create(String rawUrl, Boolean showSplash) {
        return create(rawUrl, showSplash, null);
    }

    @Transactional
    public ShortLink create(String rawUrl, Boolean showSplash, AppUser owner) {
        String fullUrl = normalize(rawUrl);
        String host = extractHost(fullUrl);
        String resolvedIp = resolveHost(host);

        AppSettings settings = settingsUseCase.current();
        String code;
        do {
            code = randomCode(settings.getLinkLength());
        } while (shortLinkRepository.existsByCode(code));

        ShortLink link = new ShortLink();
        link.setCode(code);
        link.setFullUrl(fullUrl);
        link.setResolvedIp(resolvedIp);
        link.setShowSplash(showSplash == null || showSplash);
        link.setHitCount(0);
        link.setCreatedAt(Instant.now());
        link.setOwner(owner);
        return shortLinkRepository.save(link);
    }

    private String normalize(String rawUrl) {
        String url = rawUrl == null ? "" : rawUrl.trim();
        if (url.isEmpty()) {
            throw new InvalidUrlException("URL vazia");
        }
        if (!url.matches("(?i)^https?://.*")) {
            url = "http://" + url;
        }
        return url;
    }

    private String extractHost(String url) {
        final URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new InvalidUrlException("URL inválida: " + url);
        }
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase();
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new InvalidUrlException("Somente URLs http/https são suportadas");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new InvalidUrlException("URL inválida: " + url);
        }
        return uri.getHost();
    }

    private String resolveHost(String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException e) {
            throw new InvalidUrlException("No such host: " + host);
        }
    }

    private String randomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CODE_ALPHABET.charAt(random.nextInt(CODE_ALPHABET.length())));
        }
        return code.toString();
    }
}

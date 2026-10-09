package io.tinylink.core.usecase;

import io.tinylink.core.entity.LinkHit;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.error.LinkNotFoundException;
import io.tinylink.core.repository.LinkHitRepository;
import io.tinylink.core.repository.ShortLinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveLinkUseCaseTest {

    @Mock
    ShortLinkRepository shortLinkRepository;

    @Mock
    LinkHitRepository linkHitRepository;

    @InjectMocks
    ResolveLinkUseCase useCase;

    @Test
    void registersHitAndIncrementsCounter() {
        ShortLink link = new ShortLink();
        link.setCode("abc123");
        link.setFullUrl("http://localhost/x");
        link.setHitCount(41);
        when(shortLinkRepository.findByCode("abc123")).thenReturn(Optional.of(link));
        when(shortLinkRepository.save(any(ShortLink.class))).thenAnswer(i -> i.getArgument(0));

        ShortLink resolved = useCase.resolve("abc123", "10.0.0.1");

        assertThat(resolved.getHitCount()).isEqualTo(42);
        ArgumentCaptor<LinkHit> captor = ArgumentCaptor.forClass(LinkHit.class);
        verify(linkHitRepository).save(captor.capture());
        assertThat(captor.getValue().getVisitorIp()).isEqualTo("10.0.0.1");
        assertThat(captor.getValue().getHitAt()).isNotNull();
    }

    @Test
    void failsWhenCodeDoesNotExist() {
        when(shortLinkRepository.findByCode("zzz")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.resolve("zzz", "10.0.0.1"))
                .isInstanceOf(LinkNotFoundException.class);
    }
}

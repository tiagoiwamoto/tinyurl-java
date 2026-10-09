package io.tinylink.core.usecase;

import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.error.LinkNotFoundException;
import io.tinylink.core.repository.LinkHitRepository;
import io.tinylink.core.repository.ShortLinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminLinksUseCaseTest {

    @Mock
    ShortLinkRepository shortLinkRepository;

    @Mock
    LinkHitRepository linkHitRepository;

    @InjectMocks
    AdminLinksUseCase useCase;

    @Test
    void deletesHitsBeforeLink() {
        ShortLink link = new ShortLink();
        link.setCode("abc");
        when(shortLinkRepository.findById(7L)).thenReturn(Optional.of(link));

        useCase.delete(7L);

        InOrder order = Mockito.inOrder(linkHitRepository, shortLinkRepository);
        order.verify(linkHitRepository).deleteByLinkId(null);
        order.verify(shortLinkRepository).delete(link);
    }

    @Test
    void failsWhenIdDoesNotExist() {
        when(shortLinkRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.delete(99L))
                .isInstanceOf(LinkNotFoundException.class);
    }
}

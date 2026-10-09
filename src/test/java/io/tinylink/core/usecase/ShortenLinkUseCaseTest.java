package io.tinylink.core.usecase;

import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.core.error.InvalidUrlException;
import io.tinylink.core.repository.ShortLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortenLinkUseCaseTest {

    @Mock
    ShortLinkRepository shortLinkRepository;

    @Mock
    SettingsUseCase settingsUseCase;

    @InjectMocks
    ShortenLinkUseCase useCase;

    @BeforeEach
    void setUp() {
        AppSettings settings = new AppSettings();
        settings.setLinkLength(10);
        lenient().when(settingsUseCase.current()).thenReturn(settings);
        lenient().when(shortLinkRepository.existsByCode(anyString())).thenReturn(false);
        lenient().when(shortLinkRepository.save(any(ShortLink.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void createsLinkWithGeneratedCode() {
        ShortLink link = useCase.create("http://localhost/alguma/coisa", null);

        assertThat(link.getCode()).hasSize(10).matches("[a-zA-Z0-9]+");
        assertThat(link.getFullUrl()).isEqualTo("http://localhost/alguma/coisa");
        assertThat(link.isShowSplash()).isTrue();
        assertThat(link.getHitCount()).isZero();
    }

    @Test
    void addsHttpSchemeWhenMissing() {
        ShortLink link = useCase.create("localhost/path", false);

        assertThat(link.getFullUrl()).isEqualTo("http://localhost/path");
        assertThat(link.isShowSplash()).isFalse();
    }

    @Test
    void rejectsEmptyUrl() {
        assertThatThrownBy(() -> useCase.create("  ", null))
                .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void rejectsUnresolvableHost() {
        try (MockedStatic<InetAddress> inet = Mockito.mockStatic(InetAddress.class)) {
            inet.when(() -> InetAddress.getByName("host.inexistente.invalid"))
                    .thenThrow(new UnknownHostException("host.inexistente.invalid"));

            assertThatThrownBy(() -> useCase.create("http://host.inexistente.invalid/x", null))
                    .isInstanceOf(InvalidUrlException.class)
                    .hasMessageContaining("No such host");
        }
    }

    @Test
    void retriesWhenCodeCollides() {
        when(shortLinkRepository.existsByCode(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        ShortLink link = useCase.create("http://localhost/a", null);

        assertThat(link.getCode()).hasSize(10);
    }
}

package io.tinylink.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_settings")
@Getter
@Setter
@NoArgsConstructor
public class AppSettings {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    @Column(nullable = false)
    private String baseUrl;

    @Column(nullable = false)
    private int refreshRateSeconds;

    @Column(nullable = false)
    private int linkLength;

    @Column(nullable = false)
    private int pageSize;

    @Column(columnDefinition = "text")
    private String adHtml;
}

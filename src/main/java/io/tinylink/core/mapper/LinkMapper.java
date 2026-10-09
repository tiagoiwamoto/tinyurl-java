package io.tinylink.core.mapper;

import io.tinylink.core.entity.AppSettings;
import io.tinylink.core.entity.ShortLink;
import io.tinylink.entrypoint.rest.dto.AdminLinkResponse;
import io.tinylink.entrypoint.rest.dto.LinkResponse;
import io.tinylink.entrypoint.rest.dto.SettingsResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LinkMapper {

    @Mapping(target = "shortUrl", expression = "java(baseUrl + \"/\" + link.getCode())")
    LinkResponse toResponse(ShortLink link, @Context String baseUrl);

    @Mapping(target = "shortUrl", expression = "java(baseUrl + \"/\" + link.getCode())")
    AdminLinkResponse toAdminResponse(ShortLink link, @Context String baseUrl);

    SettingsResponse toResponse(AppSettings settings);
}

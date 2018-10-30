package io.choerodon.wiki.app.service;

import io.choerodon.wiki.api.dto.WikiLogoDTO;

public interface WikiLogoService {

    void updateLogo(WikiLogoDTO wikiLogoDTO, String username);
}

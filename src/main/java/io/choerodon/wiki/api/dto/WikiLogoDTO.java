package io.choerodon.wiki.api.dto;

public class WikiLogoDTO {
    private String favicon;
    private String systemName;
    private String systemLogo;

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemLogo() {
        return systemLogo;
    }

    public void setSystemLogo(String systemLogo) {
        this.systemLogo = systemLogo;
    }
}

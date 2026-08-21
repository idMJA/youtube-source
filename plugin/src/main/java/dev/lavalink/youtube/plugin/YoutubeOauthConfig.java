package dev.lavalink.youtube.plugin;

public class YoutubeOauthConfig {
    private boolean enabled = false;
    private boolean fallback = false;
    private String refreshToken;
    private boolean skipInitialization = false;

    public boolean getEnabled() {
        return enabled;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean getFallback() {
        return fallback;
    }

    public boolean getSkipInitialization() {
        return skipInitialization;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public void setSkipInitialization(boolean skipInitialization) {
        this.skipInitialization = skipInitialization;
    }
}

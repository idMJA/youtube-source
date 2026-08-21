package dev.lavalink.youtube.clients;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import dev.lavalink.youtube.CannotBeLoaded;
import dev.lavalink.youtube.RemotePoToken;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.skeleton.StreamingNonMusicClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tv extends StreamingNonMusicClient {
    private static final Logger log = LoggerFactory.getLogger(Tv.class);
    private static final String PS4_USER_AGENT = "Mozilla/5.0 (PlayStation; PlayStation 4/12.00) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Safari/605.1.15";
    private static final String OAUTH_USER_AGENT = "Mozilla/5.0 (ChromiumStylePlatform) Cobalt/25.lts.30.1034943-gold (unlike Gecko), Unknown_TV_Unknown_0/Unknown (Unknown, Unknown)";

    public static ClientConfig BASE_CONFIG = new ClientConfig()
        .withClientName("TVHTML5")
        .withUserAgent(PS4_USER_AGENT)
        .withClientField("clientVersion", "7.20260707.07.00");

    protected ClientOptions options;
    protected volatile String requestPoToken;
    protected volatile String requestVisitorData;
    protected volatile boolean oauthPlayback;
    protected final Boolean oauthPlaybackMode;
    protected final boolean legacyPlayback;

    public Tv(@NotNull ClientOptions options) {
        this(options, null);
    }

    public Tv(@NotNull ClientOptions options, Boolean oauthPlaybackMode) {
        this(options, oauthPlaybackMode, false);
    }

    private Tv(@NotNull ClientOptions options, Boolean oauthPlaybackMode, boolean legacyPlayback) {
        this.options = options;
        this.oauthPlaybackMode = oauthPlaybackMode;
        this.legacyPlayback = legacyPlayback;
    }

    @NotNull
    public Tv createLegacyPlaybackClient(boolean useOAuth) {
        return new Tv(options, useOAuth, true);
    }

    public boolean isLegacyPlayback() {
        return legacyPlayback;
    }

    @Override
    public boolean supportsSabrPlayback() {
        return true;
    }

    @Override
    @NotNull
    protected ClientConfig getBaseClientConfig(@NotNull HttpInterface httpInterface) {
        ClientConfig config = BASE_CONFIG.copy();
        if (legacyPlayback) {
            config.withUserAgent(PS4_USER_AGENT);
            config.withVisitorData(null);
            config.getRoot().remove("serviceIntegrityDimensions");
        } else if (oauthPlayback) {
            config.withUserAgent(OAUTH_USER_AGENT);
            config.withClientField("userAgent", OAUTH_USER_AGENT);
            config.withVisitorData(null);
            config.getRoot().remove("serviceIntegrityDimensions");
        } else if (requestVisitorData != null) {
            config.withVisitorData(requestVisitorData);
        }
        if (!oauthPlayback && !legacyPlayback && requestPoToken != null) {
            config.putOnceAndJoin(config.getRoot(), "serviceIntegrityDimensions").put("poToken", requestPoToken);
        }
        return config;
    }

    @Override
    protected boolean preferSabrPlayback() {
        return !legacyPlayback;
    }

    @Override
    public void preparePlayback(@NotNull YoutubeAudioSourceManager source,
                                @NotNull HttpInterface httpInterface,
                                @NotNull String videoId) throws IOException {
        oauthPlayback = oauthPlaybackMode != null
            ? oauthPlaybackMode
            : source.getOauth2Handler().isEnabled();

        log.debug("Preparing TVHTML5 playback with {}", legacyPlayback
            ? "PS4 without visitor data or PoToken"
            : (oauthPlayback ? "OAuth" : "visitor data and PoToken"));

        if (oauthPlayback || legacyPlayback) {
            requestPoToken = null;
            requestVisitorData = null;
            return;
        }

        requestVisitorData = source.getVisitorData();
        RemotePoToken.Result result = requestVisitorData == null
            ? null
            : source.generatePoToken(httpInterface, requestVisitorData);

        if (result != null) {
            requestPoToken = result.getPoToken();
            requestVisitorData = result.getContentBinding();
            log.debug("TVHTML5 visitor bound PoToken generated for playback");
        } else {
            requestPoToken = null;
            log.debug("TVHTML5 visitor bound PoToken unavailable continuing without PoToken");
        }
    }

    @Override
    @NotNull
    public String getPlayerParams() {
        return WEB_PLAYER_PARAMS;
    }

    @Override
    @NotNull
    public ClientOptions getOptions() {
        return this.options;
    }

    @Override
    public boolean canHandleRequest(@NotNull String identifier) {
        return false;
    }

    @Override
    public boolean supportsOAuth() {
        return oauthPlaybackMode == null || oauthPlaybackMode;
    }

    @Override
    @Nullable
    public String getPoToken() {
        return requestPoToken;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return BASE_CONFIG.getName();
    }

    @Override
    public AudioItem loadPlaylist(@NotNull YoutubeAudioSourceManager source,
                                  @NotNull HttpInterface httpInterface,
                                  @NotNull String playlistId,
                                  @Nullable String selectedVideoId) {
        throw new FriendlyException("This client cannot load playlists", Severity.COMMON,
            new RuntimeException("TVHTML5 cannot be used to load playlists"));
    }

    @Override
    public AudioItem loadVideo(@NotNull YoutubeAudioSourceManager source, @NotNull HttpInterface httpInterface, @NotNull String videoId) throws CannotBeLoaded, IOException {
        throw new FriendlyException("This client cannot load videos", Severity.COMMON,
            new RuntimeException("TVHTML5 cannot be used to load videos"));
    }

    @Override
    public AudioItem loadMix(@NotNull YoutubeAudioSourceManager source, @NotNull HttpInterface httpInterface, @NotNull String mixId, @Nullable String selectedVideoId) {
        throw new FriendlyException("This client cannot load mixes", Severity.COMMON,
            new RuntimeException("TVHTML5 cannot be used to load mixes"));
    }

    @Override
    public AudioItem loadSearch(@NotNull YoutubeAudioSourceManager source, @NotNull HttpInterface httpInterface, @NotNull String searchQuery) {
        throw new FriendlyException("This client cannot search", Severity.COMMON,
            new RuntimeException("TVHTML5 cannot be used to search"));
    }
}

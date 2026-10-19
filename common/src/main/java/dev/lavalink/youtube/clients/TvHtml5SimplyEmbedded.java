package dev.lavalink.youtube.clients;

import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.RemotePoToken;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class TvHtml5SimplyEmbedded extends TvHtml5Simply {

    public static ClientConfig BASE_CONFIG = new ClientConfig()
            .withClientName("TVHTML5_SIMPLY_EMBEDDED_PLAYER")
            .withClientField("clientVersion", "2.0")
            .withUserAgent("Mozilla/5.0 (PlayStation; PlayStation 4/12.00) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Safari/605.1.15")
            .withRootField("attestationRequest", new HashMap<String, Object>() {{ put("omitBotguardData", true); }});

    private volatile String poToken;
    private volatile String visitorData;

    public TvHtml5SimplyEmbedded() {
        this(ClientOptions.DEFAULT);
    }

    public TvHtml5SimplyEmbedded(@NotNull ClientOptions options) {
        super(options);
    }

    @Override
    @NotNull
    protected ClientConfig getBaseClientConfig(@NotNull HttpInterface httpInterface) {
        ClientConfig config = BASE_CONFIG.copy();
        if (visitorData != null) {
            config.withVisitorData(visitorData);
        }
        if (poToken != null) {
            Map<String, Object> serviceIntegrityDimensions = config.putOnceAndJoin(
                config.getRoot(), "serviceIntegrityDimensions");
            serviceIntegrityDimensions.put("poToken", poToken);
        }
        return config;
    }

    @Override
    public void preparePlayback(@NotNull YoutubeAudioSourceManager source, @NotNull HttpInterface httpInterface,
                                @NotNull String videoId) throws java.io.IOException {
        visitorData = source.getVisitorData();
        RemotePoToken.Result result = source.generatePoToken(httpInterface, visitorData != null ? visitorData : videoId);
        if (result == null) {
            poToken = null;
        } else {
            poToken = result.getPoToken();
            visitorData = result.getContentBinding();
        }
    }

    @Override
    public String getPoToken() {
        return poToken;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return BASE_CONFIG.getName();
    }

    @Override
    public boolean isEmbedded() {
        return true;
    }
}

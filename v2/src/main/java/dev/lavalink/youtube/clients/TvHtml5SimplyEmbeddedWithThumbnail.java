package dev.lavalink.youtube.clients;

import dev.lavalink.youtube.clients.skeleton.NonMusicClientWithThumbnail;
import org.jetbrains.annotations.NotNull;

public class TvHtml5SimplyEmbeddedWithThumbnail extends TvHtml5SimplyEmbedded implements NonMusicClientWithThumbnail {
    public TvHtml5SimplyEmbeddedWithThumbnail() {
        super();
    }

    public TvHtml5SimplyEmbeddedWithThumbnail(@NotNull ClientOptions options) {
        super(options);
    }
}

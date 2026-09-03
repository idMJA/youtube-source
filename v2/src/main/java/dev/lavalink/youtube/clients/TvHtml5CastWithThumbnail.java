package dev.lavalink.youtube.clients;

import dev.lavalink.youtube.clients.skeleton.NonMusicClientWithThumbnail;
import org.jetbrains.annotations.NotNull;

public class TvHtml5CastWithThumbnail extends TvHtml5Cast implements NonMusicClientWithThumbnail {
    public TvHtml5CastWithThumbnail() {
        super();
    }

    public TvHtml5CastWithThumbnail(@NotNull ClientOptions options) {
        super(options);
    }
}

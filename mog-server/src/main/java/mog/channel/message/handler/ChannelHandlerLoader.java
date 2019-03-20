package mog.channel.message.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Load all needed channel handlers.
 */
public class ChannelHandlerLoader {

    private final List<ChannelHandler> handlers = new ArrayList<>();

    /**
     * Constructor.
     */
    public ChannelHandlerLoader() {
        handlers.add(new CreateHandler());
        handlers.add(new LoadHandler());
        handlers.add(new MoveHandler());
        handlers.add(new QuitHandler());
    }

    /**
     * @return the loaded channel handlers.
     */
    public ChannelHandler[] getChannelHandlers() {
        return handlers.toArray(new ChannelHandler[handlers.size()]);
    }

}

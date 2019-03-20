package mog.channel.message.handler;

import com.artemis.ComponentMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mog.world.WorldManager;
import mog.world.component.Quit;

/**
 * Abstract channel handler that deals with exception & closed channels.
 *
 * @param <I> the message type handled.
 */
public abstract class ChannelHandler<I> extends SimpleChannelInboundHandler<I> {

    private static final WorldManager WORLD_MANAGER = WorldManager.getInstance();
    private ComponentMapper<Quit> quitMapper;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        quit(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO log exception
        cause.printStackTrace();
        quit(ctx);
    }

    public WorldManager getWorldManager() {
        return WORLD_MANAGER;
    }

    private void quit(ChannelHandlerContext ctx) {
        final int entity = WORLD_MANAGER.get(ctx.channel());
        if (entity >= 0) {
            WORLD_MANAGER.offer(world -> quitMapper.create(entity));
        }
    }
}

package mog.channel.message.handler;

import com.artemis.ComponentMapper;
import mog.event.WorldEvent;
import mog.world.WorldManager;
import mog.world.component.Load;
import mog.world.component.Socket;
import io.netty.channel.ChannelHandlerContext;
import protocol.message.lifecycle.LoadPrototype;

@io.netty.channel.ChannelHandler.Sharable
public class LoadHandler extends ChannelHandler<LoadPrototype.Load> {

    private ComponentMapper<Socket> socketMapper;
    private ComponentMapper<Load> loadingMapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoadPrototype.Load msg) {
        final WorldManager worldManager = WorldManager.getInstance();
        final WorldEvent event = (world) -> {
            final int entity = world.create();

            worldManager.link(ctx.channel(), entity);

            final Socket socket = socketMapper.create(entity);
            socket.channel = ctx.channel();
            final Load load = loadingMapper.create(entity);
            load.id = msg.getId();
        };
        worldManager.offer(event);
    }
}

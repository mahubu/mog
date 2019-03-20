package mog.channel.message.handler;

import com.artemis.ComponentMapper;
import mog.event.WorldEvent;
import mog.world.WorldManager;
import mog.world.component.Create;
import mog.world.component.Socket;
import io.netty.channel.ChannelHandlerContext;
import protocol.message.lifecycle.CreatePrototype;

@io.netty.channel.ChannelHandler.Sharable
public class CreateHandler extends ChannelHandler<CreatePrototype.Create> {

    private ComponentMapper<Socket> socketMapper;
    private ComponentMapper<Create> creatingMapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreatePrototype.Create msg) {
        final WorldManager worldManager = getWorldManager();
        final WorldEvent event = (world) -> {
            final int entity = world.create();

            worldManager.link(ctx.channel(), entity);

            final Socket socket = socketMapper.create(entity);
            socket.channel = ctx.channel();
            creatingMapper.create(entity);
        };
        worldManager.offer(event);

    }
}

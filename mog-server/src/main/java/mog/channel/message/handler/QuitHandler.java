package mog.channel.message.handler;

import com.artemis.ComponentMapper;
import mog.event.WorldEvent;
import mog.world.WorldManager;
import mog.world.component.Quit;
import io.netty.channel.ChannelHandlerContext;
import protocol.message.lifecycle.QuitPrototype;

@io.netty.channel.ChannelHandler.Sharable
public class QuitHandler extends ChannelHandler<QuitPrototype.Quit> {

    private ComponentMapper<Quit> quitMapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitPrototype.Quit msg) {
        final WorldManager worldManager = WorldManager.getInstance();
        final WorldEvent event = (world) -> {
            final int entity = worldManager.get(ctx.channel());
            quitMapper.create(entity);
        };
        worldManager.offer(event);
    }
}

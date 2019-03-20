package mog.channel.message.handler;

import com.artemis.ComponentMapper;
import mog.event.WorldEvent;
import mog.world.WorldManager;
import mog.world.component.Move;
import io.netty.channel.ChannelHandlerContext;
import protocol.message.action.MovePrototype;

@io.netty.channel.ChannelHandler.Sharable
public class MoveHandler extends ChannelHandler<MovePrototype.Move> {

    private ComponentMapper<Move> moveMapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MovePrototype.Move msg) {
        final WorldManager worldManager = WorldManager.getInstance();
        final WorldEvent event = (world) -> {
            final int entity = worldManager.get(ctx.channel());
            final Move move = moveMapper.create(entity);
            move.x = msg.getX();
            move.y = msg.getY();
            move.z = msg.getZ();
        };
        worldManager.offer(event);
    }
}

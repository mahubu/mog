package mog.channel.message.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mog.ClientBootstrap;
import protocol.message.lifecycle.CreatePrototype;

@ChannelHandler.Sharable
public class CreatedHandler extends SimpleChannelInboundHandler<CreatePrototype.Created> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreatePrototype.Created msg) {
        ClientBootstrap.offer(() -> msg.getId());
    }
}

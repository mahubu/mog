package mog.channel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import mog.Lifecyclic;
import protocol.handler.codec.protobuf.ProtobufDecoder;
import protocol.handler.codec.protobuf.ProtobufEncoder;

/**
 * Channel bootstrap : listen to incoming & out-coming messages through the channels.
 */
public class ChannelBootstrap implements Lifecyclic {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));

    private ChannelHandler[] handlers;
    private Channel channel;

    /**
     * Constructor
     *
     * @param handlers the channel handlers linked to messages.
     */
    public ChannelBootstrap(final ChannelHandler... handlers) {
        this.handlers = handlers;
    }

    @Override
    public void startup() throws InterruptedException {
        final Thread thread = new Thread(() -> {
            // Create event loop groups. One for incoming connections handling and
            // second for handling actual event by workers
            EventLoopGroup serverGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                final ServerBootstrap bootStrap = new ServerBootstrap();
                bootStrap.group(serverGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ServerChannelInitializer(handlers));

                // Bind to port
                channel = bootStrap.bind(PORT).sync().channel();
                channel.closeFuture().sync();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } finally {
                serverGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        thread.start();
    }

    @Override
    public void shutdown() {
        channel.close();
    }

    private static class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

        private ChannelHandler[] handlers;

        public ServerChannelInitializer(final ChannelHandler... handlers) {
            this.handlers = handlers;
        }

        @Override
        protected void initChannel(SocketChannel channel) {
            final ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new ProtobufVarint32FrameDecoder());
            pipeline.addLast(new ProtobufDecoder());
            if (handlers != null) {
                pipeline.addLast(handlers);
            }
            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast(new ProtobufEncoder());
        }

    }

}

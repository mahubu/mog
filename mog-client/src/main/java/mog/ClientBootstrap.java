package mog;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import mog.channel.message.handler.CreatedHandler;
import protocol.handler.codec.protobuf.ProtobufDecoder;
import protocol.handler.codec.protobuf.ProtobufEncoder;
import protocol.message.action.MovePrototype;
import protocol.message.lifecycle.CreatePrototype;
import protocol.message.lifecycle.LoadPrototype;
import protocol.message.lifecycle.QuitPrototype;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

public class ClientBootstrap {

    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));

    private static final BlockingQueue<Supplier<String>> events = new LinkedBlockingQueue<>();

    public static void offer(final Supplier<String> event) {
        events.offer(event);
    }

    public static void main(String... args) throws Exception {
        // Create event loop groups. One for incoming connections handling and
        // second for handling actual event by workers1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final Bootstrap bootStrap = new Bootstrap();
            bootStrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer());

            // Create connection
            final Channel channel = bootStrap.connect(HOST, PORT).sync().channel();

            // Get handle to handler so we can send message

            final CreatePrototype.Create create = CreatePrototype.Create.newBuilder().build();
            channel.writeAndFlush(create);

            Thread.sleep(1000);

            final MovePrototype.Move move = MovePrototype.Move.newBuilder().setX(100).setY(50).setZ(15).build();
            channel.writeAndFlush(move);

            Thread.sleep(1000);

            Supplier<String> supplier;
            while ((supplier = events.poll()) != null) {
                final LoadPrototype.Load load = LoadPrototype.Load.newBuilder().setId(supplier.get()).build();
                channel.writeAndFlush(load);
            }

            Thread.sleep(1000);

            final QuitPrototype.Quit quit = QuitPrototype.Quit.newBuilder().build();
            channel.writeAndFlush(quit);

            channel.close();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            final ChannelPipeline pipeline = channel.pipeline();

            pipeline.addLast(new ProtobufVarint32FrameDecoder());
            pipeline.addLast(new ProtobufDecoder());

            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast(new ProtobufEncoder());

            pipeline.addLast(new CreatedHandler());
        }

    }

}

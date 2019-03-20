package mog;

import mog.channel.ChannelBootstrap;
import mog.channel.message.handler.ChannelHandler;
import mog.channel.message.handler.ChannelHandlerLoader;
import mog.persistence.PersistenceManager;
import mog.world.WorldManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server bootstrap : start-up & shutdown the server main loop.
 */
public class ServerBootstrap {

    private static boolean running = true;

    /**
     * Server main loop.
     *
     * @param args the args given at start-up.
     * @throws Exception if a fatal exception arises during main loop execution.
     */
    public static void main(String... args) throws Exception {
        final List<Lifecyclic> lifecyclics = new ArrayList<>();

        final PersistenceManager persistenceManager = PersistenceManager.getInstance();
        lifecyclics.add(persistenceManager);

        final ChannelHandler[] handlers = new ChannelHandlerLoader().getChannelHandlers();

        final WorldManager worldManager = WorldManager.getInstance();
        worldManager.addHandlers(handlers);
        lifecyclics.add(worldManager);

        final ChannelBootstrap channelBootstrap = new ChannelBootstrap(handlers);
        lifecyclics.add(channelBootstrap);

        // Startup
        for (final Lifecyclic lifecyclic : lifecyclics) {
            lifecyclic.startup();
        }

        // Loop
        long time = System.nanoTime();
        while (running) {
            final long now = System.nanoTime();
            worldManager.process(now - time);
            time = now;
        }

        // Shutdown
        Collections.reverse(lifecyclics);
        for (final Lifecyclic lifecyclic : lifecyclics) {
            lifecyclic.shutdown();
        }
    }

}

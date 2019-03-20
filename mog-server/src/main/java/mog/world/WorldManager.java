package mog.world;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import mog.Lifecyclic;
import mog.event.WorldEvent;
import mog.world.system.SystemLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The world manager :
 */
public class WorldManager implements Lifecyclic {

    private static WorldManager instance = new WorldManager();

    private final BlockingQueue<WorldEvent> worldEvents;
    private ChannelEntity channelEntities;
    private ChannelHandler[] handlers;

    /**
     * Constructor.
     */
    private WorldManager() {
        super();
        worldEvents = new LinkedBlockingQueue<>();
        channelEntities = new ChannelEntity();
    }

    /**
     * @return the instance.
     */
    public static WorldManager getInstance() {
        return instance;
    }

    /**
     * Add channel handlers to be linked.
     *
     * @param handlers the channel handlers.
     */
    public void addHandlers(ChannelHandler[] handlers) {
        this.handlers = handlers;
    }

    private World world;

    @Override
    public void startup() throws Exception {
        final WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        builder.with(new SystemLoader().getSystems());
        final WorldConfiguration configuration = builder.build();
        world = new World(configuration);
        if (handlers != null) {
            for (final ChannelHandler handler : handlers) {
                world.inject(handler);
            }
        }
    }

    @Override
    public void shutdown() throws Exception {
        process(Long.MIN_VALUE);
        world.dispose();
    }

    /**
     * Process a step into the world.
     *
     * @param delta the time elapsed since last step, in nanoseconds.
     */
    public void process(final long delta) {
        world.setDelta(delta); // TODO useful for ?
        // Retrieve and process all events since last loop.
        int size = this.worldEvents.size();
        if (size > 0) {
            final List<WorldEvent> events = new ArrayList<>(size);
            this.worldEvents.drainTo(events);
            events.forEach(event -> {
                event.accept(world);
            });
        }
        // Process the world.
        world.process();
    }

    /**
     * Offer an event to the world.
     *
     * @param event the event.
     */
    public void offer(final WorldEvent event) {
        worldEvents.offer(event);
    }

    /**
     * Link a channel with an entity.
     *
     * @param channel the channel
     * @param entity  the eniity.
     */
    public void link(final Channel channel, final int entity) {
        channelEntities.put(channel, entity);
    }

    /**
     * Unlink a channel from its entity.
     *
     * @param channel the channel
     * @return the unlinked entity.
     */
    public int unlink(final Channel channel) {
        return channelEntities.remove(channel);
    }

    /**
     * Get an entity linked to a channel
     *
     * @param channel the channel
     * @return the linked entity.
     */
    public int get(final Channel channel) {
        return channelEntities.get(channel);
    }

    private static class ChannelEntity {

        private Map<Channel, Integer> entityByChannel;

        private ChannelEntity() {
            entityByChannel = new HashMap<>(256);
        }

        public void put(final Channel channel, final int entity) {
            entityByChannel.put(channel, entity);
        }

        public int remove(final Channel channel) {
            return entityByChannel.remove(channel);
        }

        public int get(final Channel channel) {
            return entityByChannel.getOrDefault(channel, -1);
        }
    }
}

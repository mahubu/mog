package mog.event;

import com.artemis.World;

import java.util.function.Consumer;

/**
 * A event send to the world.
 */
@FunctionalInterface
public interface WorldEvent extends Consumer<World> {
}

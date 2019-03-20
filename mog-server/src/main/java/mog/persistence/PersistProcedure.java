package mog.persistence;

import mog.event.WorldEvent;

import java.util.function.Supplier;

/**
 * A procedure tu be persisted.
 */
@FunctionalInterface
public interface PersistProcedure extends Supplier<WorldEvent> {
}

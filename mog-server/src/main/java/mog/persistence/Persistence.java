package mog.persistence;

import com.artemis.World;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Persistence of a document.
 */
public interface Persistence {

    /**
     * Offer a procedure to be persisted & an event to be broadcasted to the world.
     *
     * @param persistProcedure the procedure to persist.
     * @param persistEvent     the event, using procedure results, to broadcast to the world.
     * @param <T>              the type linking procedure & event.
     */
    default <T> void offer(final Supplier<T> persistProcedure, final BiConsumer<T, World> persistEvent) {
        PersistenceManager.getInstance().offer(persistProcedure, persistEvent);
    }

    /**
     * Offer a procedure to be persisted.
     *
     * @param persistProcedure the procedure to persist.
     * @param <T>              the type linked to the procedure.
     */
    default <T> void offer(final Supplier<T> persistProcedure) {
        offer(persistProcedure, null);
    }

    /**
     * Find a document into a collection.
     *
     * @param uid the document uid.
     * @return the document as a map.
     */
    Map<String, Object> findOne(final String uid);

    /**
     * Insert a document into a collection.
     *
     * @param insertion the document as a map.
     * @return the document uid.
     */
    String insertOne(final Map<String, Object> insertion);

    /**
     * Update a document from a collection.
     *
     * @param uid    the document uid.
     * @param update the update to be applied to the document, as a map.
     */
    void updateOne(final String uid, final Map<String, Object> update);

}

package mog.persistence;

import com.artemis.World;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mog.Lifecyclic;
import mog.event.WorldEvent;
import mog.world.WorldManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Persistence manager.<br/>
 * - Defines basic methods for persistence querying.<br/>
 * - Executes queries on a dedicated thread and returns results into an event broadcasted to the world.<br/>
 */
public class PersistenceManager implements Lifecyclic {

    private static final String DATABASE_NAME = "mog";

    private MongoClient client;
    private Queue<PersistProcedure> procedures;
    private boolean running;
    private Thread thread;

    private static final PersistenceManager instance = new PersistenceManager();

    /**
     * Private constructor.
     */
    private PersistenceManager() {
        super();
        procedures = new ConcurrentLinkedQueue<>();
    }

    /**
     * @return the instance.
     */
    public static PersistenceManager getInstance() {
        return instance;
    }

    @Override
    public void startup() {
        client = new MongoClient();
        running = true;
        thread = new Thread(() -> {
            while (running) {
                final PersistProcedure procedure = procedures.poll();
                if (procedure != null) {
                    final WorldEvent event = procedure.get();
                    if (event != null) {
                        WorldManager.getInstance().offer(event);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void shutdown() {
        running = false;
        while (!procedures.isEmpty()) {
            final PersistProcedure procedure = procedures.poll();
            if (procedure != null) {
                procedure.get();
            }
        }
        client.close();
    }

    /**
     * Offer a procedure to be persisted & an event to be broadcasted to the world.
     *
     * @param persistProcedure the procedure to persist.
     * @param persistEvent     the event, using procedure results, to broadcast to the world.
     * @param <T>              the type linking procedure & event.
     */
    public <T> void offer(final Supplier<T> persistProcedure, final BiConsumer<T, World> persistEvent) {
        procedures.offer(() -> {
            final T value = persistProcedure.get();
            return persistEvent != null ? (world) -> persistEvent.accept(value, world) : null;
        });
    }

    /**
     * Find a document into a collection.
     *
     * @param collection the collection.
     * @param uid        the document uid.
     * @return the document as a map.
     */
    public Map<String, Object> findOne(final String collection, final String uid) {
        final MongoDatabase database = client.getDatabase(DATABASE_NAME);
        final MongoCollection<Document> bag = database.getCollection(collection);
        final FindIterable<Document> documents = bag.find(new Document(Map.of("_id", new ObjectId(uid))));
        return documents.first();
    }

    /**
     * Insert a document into a collection.
     *
     * @param collection the collection.
     * @param insertion  the document as a map.
     * @return the document uid.
     */
    public String insertOne(final String collection, final Map<String, Object> insertion) {
        final MongoDatabase database = client.getDatabase(DATABASE_NAME);
        final MongoCollection<Document> bag = database.getCollection(collection);
        final Document document = new Document(insertion);
        bag.insertOne(document);
        return document.getObjectId("_id").toHexString();
    }

    /**
     * Update a document from a collection.
     *
     * @param collection the collection.
     * @param uid        the document uid.
     * @param update     the update to be applied to the document, as a map.
     */
    public void updateOne(final String collection, final String uid, final Map<String, Object> update) {
        final MongoDatabase database = client.getDatabase(DATABASE_NAME);
        final MongoCollection<Document> bag = database.getCollection(collection);
        final Document set = new Document().append("$set", new Document(update));
        bag.updateOne(new Document(Map.of("_id", new ObjectId(uid))), set);
    }


}

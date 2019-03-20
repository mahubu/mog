package mog.persistence;

import java.util.Map;

/**
 * Persistence for characters.
 */
public class CharacterPersistence implements Persistence {

    private static final PersistenceManager PERSISTENCE = PersistenceManager.getInstance();
    private static final String COLLECTION = "characters";

    @Override
    public Map<String, Object> findOne(String uid) {
        return PERSISTENCE.findOne(COLLECTION, uid);
    }

    @Override
    public String insertOne(Map<String, Object> insertion) {
        return PERSISTENCE.insertOne(COLLECTION, insertion);
    }

    @Override
    public void updateOne(String uid, Map<String, Object> update) {
        PERSISTENCE.updateOne(COLLECTION, uid, update);
    }
}

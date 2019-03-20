package mog;

/**
 * Add a 'lifecycle' behaviour to an object.
 */
public interface Lifecyclic {

    /**
     * Starts up the object lifecycle.
     *
     * @throws Exception when the lifecycle could not be started.
     */
    void startup() throws Exception;

    /**
     * Shutdown the object lifecycle.
     *
     * @throws Exception when the lifecycle shutdowns improperly.
     */
    void shutdown() throws Exception;

}

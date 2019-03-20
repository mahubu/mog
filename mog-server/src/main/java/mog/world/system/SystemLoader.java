package mog.world.system;

import com.artemis.BaseSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Load all needed systems.
 */
public class SystemLoader {

    private final List<BaseSystem> systems = new ArrayList<>();

    /**
     * Constructor.
     */
    public SystemLoader() {
        systems.add(new CreateSystem());
        systems.add(new LoadSystem());
        systems.add(new MoveSystem());
        systems.add(new QuitSystem());
    }

    /**
     * @return the loaded systems.
     */
    public BaseSystem[] getSystems() {
        return systems.toArray(new BaseSystem[systems.size()]);
    }

}

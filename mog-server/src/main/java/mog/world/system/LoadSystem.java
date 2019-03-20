package mog.world.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.IteratingSystem;
import mog.persistence.CharacterPersistence;
import mog.world.component.*;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LoadSystem extends IteratingSystem {

    private final CharacterPersistence persistence = new CharacterPersistence();

    private ComponentMapper<Load> loadingMapper;
    private ComponentMapper<Identifier> identifierMapper;
    private ComponentMapper<Health> healthMapper;
    private ComponentMapper<Mana> manaMapper;
    private ComponentMapper<Position> positionMapper;

    public LoadSystem() {
        super(Aspect.one(Load.class));
    }

    @Override
    protected void process(int entityId) {
        final Load load = loadingMapper.create(entityId);

        final Supplier<Map<String, Object>> find = () -> persistence.findOne(load.id);

        final BiConsumer<Map<String, Object>, World> consume = (character, World) -> {
            final Entity entity = world.getEntity(entityId);
            if (entity == null || !entity.isActive()) {
                return;
            }

            if (character != null && !character.isEmpty()) {
                final Identifier identifier = identifierMapper.create(entityId);
                identifier.id = load.id;

                final Health health = healthMapper.create(entityId);
                health.health = (int) character.get("health");

                final Mana mana = manaMapper.create(entityId);
                mana.mana = (int) character.get("mana");

                final Position position = positionMapper.create(entityId);
                final Map<String, Double> positions = (Map<String, Double>) character.get("position");
                position.x = positions.get("x").floatValue();
                position.y = positions.get("y").floatValue();
                position.z = positions.get("z").floatValue();
            }
        };

        persistence.offer(find, consume);

        loadingMapper.remove(entityId);
    }
}

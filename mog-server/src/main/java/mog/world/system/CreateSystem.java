package mog.world.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.IteratingSystem;
import mog.persistence.CharacterPersistence;
import mog.world.component.*;
import protocol.message.lifecycle.CreatePrototype;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreateSystem extends IteratingSystem {

    private final CharacterPersistence persistence = new CharacterPersistence();

    private ComponentMapper<Create> creatingMapper;
    private ComponentMapper<Identifier> identifierMapper;
    private ComponentMapper<Health> healthMapper;
    private ComponentMapper<Mana> manaMapper;
    private ComponentMapper<Position> positionMapper;
    private ComponentMapper<Socket> socketMapper;

    public CreateSystem() {
        super(Aspect.one(Create.class));
    }

    @Override
    protected void process(int entityId) {
        final int h = 100;
        final int m = 10;
        final int x = 0;
        final int y = 0;
        final int z = 0;

        final Supplier<String> insert = () -> {
            final Map<String, Object> character = new HashMap<>(3);

            character.put("health", h);

            character.put("mana", m);

            final Map<String, Object> positions = new HashMap<>(3);
            positions.put("x", x);
            positions.put("y", y);
            positions.put("z", z);
            character.put("position", positions);

            return persistence.insertOne(character);
        };

        final BiConsumer<String, World> consume = (id, world) -> {
            final Entity entity = world.getEntity(entityId);
            if (entity == null || !entity.isActive()) {
                return;
            }

            final Health health = healthMapper.create(entityId);
            health.health = h;

            final Mana mana = manaMapper.create(entityId);
            mana.mana = m;

            final Position position = positionMapper.create(entityId);
            position.x = x;
            position.y = y;
            position.z = z;

            final Identifier identifier = identifierMapper.create(entityId);
            identifier.id = id;

            final CreatePrototype.Created created = CreatePrototype.Created.newBuilder().setId(id).build();
            final Socket socket = socketMapper.create(entityId);
            socket.channel.writeAndFlush(created);
        };

        persistence.offer(insert, consume);

        creatingMapper.remove(entityId);
    }
}

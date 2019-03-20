package mog.world.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import mog.persistence.CharacterPersistence;
import mog.world.component.Identifier;
import mog.world.component.Move;
import mog.world.component.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MoveSystem extends IteratingSystem {

    private final CharacterPersistence persistence = new CharacterPersistence();

    private ComponentMapper<Identifier> identifierMapper;
    private ComponentMapper<Move> moveMapper;
    private ComponentMapper<Position> positionMapper;

    public MoveSystem() {
        super(Aspect.all(Identifier.class, Position.class, Move.class));
    }

    @Override
    protected void process(int entityId) {
        final Move move = moveMapper.create(entityId);
        final Position position = positionMapper.create(entityId);
        position.x = move.x;
        position.y = move.y;
        position.z = move.z;
        final Identifier identifier = identifierMapper.create(entityId);
        final String id = identifier.id;

        final Supplier<String> update = () -> {
            final Map<String, Object> character = new HashMap<>(1);
            final Map<String, Object> positions = new HashMap<>(3);
            positions.put("x", position.x);
            positions.put("y", position.y);
            positions.put("z", position.z);
            character.put("position", positions);
            persistence.updateOne(id, character);
            return id;
        };

        persistence.offer(update);

        moveMapper.remove(entityId);
    }
}

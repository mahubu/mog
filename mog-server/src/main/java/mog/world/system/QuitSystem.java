package mog.world.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import mog.world.WorldManager;
import mog.world.component.Quit;
import mog.world.component.Socket;

public class QuitSystem extends IteratingSystem {

    private ComponentMapper<Quit> quitMapper;
    private ComponentMapper<Socket> socketMapper;

    public QuitSystem() {
        super(Aspect.all(Quit.class, Socket.class));
    }

    @Override
    protected void process(int entityId) {
        final Socket socket = socketMapper.create(entityId);
        final int entity = WorldManager.getInstance().unlink(socket.channel);
        quitMapper.remove(entityId);
        // TODO save in db at disconnect
        world.delete(entity);
    }
}

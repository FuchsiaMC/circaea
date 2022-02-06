package net.fuchsiamc.circaea;

import net.fuchsiamc.circaea.commands.debug.PlayerNameTestCommand;
import net.fuchsiamc.circaea.framework.FuchsiaPlugin;
import net.fuchsiamc.circaea.framework.commands.IFuchsiaCommand;

import java.util.ArrayList;
import java.util.List;

public final class Circaea extends FuchsiaPlugin {
    @Override
    public List<IFuchsiaCommand> getCommands() {
        List<IFuchsiaCommand> list = new ArrayList<>();

        list.add(new PlayerNameTestCommand());

        return list;
    }
}

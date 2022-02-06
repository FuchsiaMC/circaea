package net.fuchsiamc.circaea;

import net.fuchsiamc.circaea.commands.debug.PlayerNameTestCommand;
import net.fuchsiamc.circaea.eventhandlers.PlayerEventHandler;
import net.fuchsiamc.circaea.framework.FuchsiaPlugin;
import net.fuchsiamc.circaea.framework.commands.IFuchsiaCommand;
import net.fuchsiamc.circaea.permissions.PermissionGroup;
import net.fuchsiamc.circaea.permissions.PermissionRank;
import net.fuchsiamc.circaea.util.CommandResponse;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public final class Circaea extends FuchsiaPlugin {
    /**
     * HashMap containing the permissions of all players that have joined the server since it has started.
     */
    public final HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();

    /**
     * A list containing all registered permission ranks.
     */
    public final List<PermissionRank> ranks = new ArrayList<>();

    /**
     * A list containing all registered permission groups.
     */
    public final List<PermissionGroup> groups = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        // Ensure config exists.
        saveDefaultConfig();
        readRanksAndGroups();
        refreshPermissions();

        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
    }

    @Override
    public List<IFuchsiaCommand> getCommands() {
        List<IFuchsiaCommand> list = new ArrayList<>();

        list.add(new PlayerNameTestCommand());

        return list;
    }

    public void refreshPermissions() {
        permissions.clear();
        removeAllPermissions();

        for (Player player : getServer().getOnlinePlayers()) {
            if (player != null) {
                restorePlayerPermissions(player);
            }
        }
    }

    private void removeAllPermissions() {
        for (Player player : getServer().getOnlinePlayers()) {
            player.removeAttachment(permissions.get(player.getUniqueId()));
        }
    }

    public void restorePlayerPermissions(Player player) {
        PermissionAttachment permAttachment = player.addAttachment(this);



        permissions.put(player.getUniqueId(), permAttachment);
        player.updateCommands();
    }

    public CommandResponse registerRank(PermissionRank rank) {
        if (!Objects.equals(rank.name, "circaea:default") && rank.priority == 0)
            return new CommandResponse(false, "Only \"circaea:default\" can have a priority of 0!");

        for (PermissionRank rRank : ranks) {
            if (rRank.priority == rank.priority && !Objects.equals(rRank.name, rank.name)) {
                return new CommandResponse(false, "Rank \"" + rRank.name + "\"'s priority conflicts with the rank you're trying to register!");
            }
        }

        if (ranks.stream().anyMatch(x -> Objects.equals(x.name, rank.name))) {
            ranks.replaceAll(x -> {
                if (Objects.equals(x.name rank.name)) {
                    return rank;
                }
                return x;
            });

            return new CommandResponse(true, "Successfully replaced old rank of the same name!");
        }

        ranks.add(rank);
        return new CommandResponse(true, "Successfully registered new permission rank!");
    }

    public CommandResponse registerGroup(PermissionGroup group) {
        if (groups.stream().anyMatch(x -> Objects.equals(x.name(), group.name()))) {
            groups.replaceAll(x -> {
                if (Objects.equals(x.name(), group.name())) {
                    return group;
                }
                return x;
            });

            return new CommandResponse(true, "Successfully replaced old rank of the same name!");
        }

        groups.add(group);
        return new CommandResponse(true, "Successfully registered a new group!");
    }

    /**
     * Gets the default rank (circaea:default). Creates and registers it, if necessary.
     * @return The default permission rank.
     */
    public PermissionRank getDefaultRank() {
        // Register rank initially, if necessary.
        if (ranks.stream().noneMatch(x -> Objects.equals(x.name, "circaea:default"))) {
            registerRank(new PermissionRank(new ArrayList<>(), "circaea:default", 0, null));
        }

        Optional<PermissionRank> defaultRank = ranks.stream().filter(x -> Objects.equals(x.name, "circaea:default")).findFirst();

        // This shouldn't really be necessary, but IntelliJ insists.
        return defaultRank.orElseGet(() -> new PermissionRank(new ArrayList<>(), "circaea:default", 0, null));
    }

    /**
     * Reads the permission ranks and permission groups stored in the server's configuration file.
     */
    public void readRanksAndGroups() {
        HashMap<String, Integer> rankPriorities = new HashMap<>();
        HashMap<String, List<String>> rankGroups = new HashMap<>();
        HashMap<String, String> rankInheriteds = new HashMap<>();
        HashMap<String, List<String>> groupAddedPermissions = new HashMap<>();
        HashMap<String, List<String>> groupRemovedPermissions = new HashMap<>();

        for (String rank : getConfig().getStringList("ranks.names")) {
            rankPriorities.put(rank, getConfig().getInt("ranks.priorities." + rank));
            rankGroups.put(rank, getConfig().getStringList("ranks.groups." + rank));
            rankInheriteds.put(rank, getConfig().getString("ranks.inheriteds." + rank));
        }

        for (String group : getConfig().getStringList("groups.names")) {
            groupAddedPermissions.put(group, getConfig().getStringList("groups.addedpermissions." + group));
            groupRemovedPermissions.put(group, getConfig().getStringList("groups.removedpermissions." + group));
            registerGroup(new PermissionGroup(groupAddedPermissions.get(group), groupRemovedPermissions.get(group), group));
        }

        for (String rank : getConfig().getStringList("ranks.names")) {
        }

        for (String rank : getConfig().getStringList("ranks.names")) {
            // justification: shut up
            // noinspection OptionalGetWithoutIsPresent
            PermissionRank realRank = ranks.stream().filter(x -> Objects.equals(x.name, rank)).findFirst().get();
        }
    }
}

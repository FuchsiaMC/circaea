package net.fuchsiamc.circaea.commands;

import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.managers.GroupManager;
import net.fuchsiamc.circaea.managers.PlayerManager;
import net.fuchsiamc.circaea.managers.RankManager;
import net.fuchsiamc.circaea.permissions.PermissionGroup;
import net.fuchsiamc.circaea.permissions.PermissionRank;
import net.fuchsiamc.circaea.permissions.PermittedPlayer;
import net.fuchsiamc.gaura.commands.IFuchsiaCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

// todo: completely rewrite with own command api in gaura
// super low effort, just need it to function
public class CircaeaCommand implements IFuchsiaCommand {
    private final Circaea circaea;

    public CircaeaCommand(Circaea circaea) {
        this.circaea = circaea;
    }

    @Override
    public String getCommand() {
        return "circaea";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // args[0] would be a group or rank for example
        // and that would be an arg length of 1

        // /circaea
        if (args.length < 1) {
            sender.sendMessage("run /circaea rank, group, or player");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // /circaea <subcommand>
        switch (subCommand) {
            case "group":
                return groupCommand(sender, args);
            case "rank":
                return rankCommand(sender, args);
            case "player":
                return playerCommand(sender, args);
        }

        sender.sendMessage("only group, rank, or player are valid subcommands");
        return true;
    }

    private boolean groupCommand(CommandSender sender, String[] args) {
        // /circaea group
        if (args.length < 2) {
            sender.sendMessage("valid subcommands are: register, <group name>");
            return true;
        }

        String groupCommand = args[1].toLowerCase();
        GroupManager manager = circaea.getGroupManager();

        if (groupCommand.equals("register")) {
            // /circaea group register
            if (args.length < 3) {
                sender.sendMessage("provide a group name");
                return true;
            }

            String groupName = args[2];

            if (manager.groupExists(groupName)) {
                sender.sendMessage("a group already exists with this name");
                return true;
            }

            manager.registerGroup(new PermissionGroup(groupName, new ArrayList<>(), new ArrayList<>()));
            sender.sendMessage("registered a group with the name of: " + groupName);
            return true;
        } else if (groupCommand.equals("list")) {
            // list all ranks
            sender.sendMessage(manager.getGroups().toString());
            return true;
        }

        // /circaea group <name>

        PermissionGroup foundGroup = manager.getGroup(groupCommand);

        // check if the groupCommand is a registered group name
        if (foundGroup == null) {
            sender.sendMessage("this group does not exist! please register it with /circaea group register <name>");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("please provide a subcommand of info or permission");
            return true;
        }

        String groupSubCommand = args[2].toLowerCase();
        // /circaea group <name> info
        if (groupSubCommand.equals("info")) {
            sender.sendMessage(foundGroup.toString());
            return true;
        } else if (groupSubCommand.equals("permission")) {
            if (args.length < 4) {
                sender.sendMessage("please provide a permission subcommand");
                return true;
            }

            // add, remove, clear, list
            String permissionCommand = args[3].toLowerCase();
            switch (permissionCommand) {
                case "add" -> {
                    if (args.length < 5) {
                        sender.sendMessage("please provide a permission to add");
                        return true;
                    }

                    String permission = args[4].toLowerCase();

                    if (args.length < 6) {
                        sender.sendMessage("please provide a value to set the permission to: true, false");
                        return true;
                    }

                    String value = args[5].toLowerCase();

                    if (value.equals("true")) {
                        if (foundGroup.getAddedPermissions().contains(permission)) {
                            sender.sendMessage("this permission is already added");
                            return true;
                        }

                        if (foundGroup.getRemovedPermissions().contains(permission)) {
                            sender.sendMessage("this permission is set to be removed. this will replace this");
                            foundGroup.getRemovedPermissions().remove(permission);
                        }

                        foundGroup.getAddedPermissions().add(permission);
                        sender.sendMessage("set to add the permission from this group");
                    } else if (value.equals("false")) {
                        if (foundGroup.getRemovedPermissions().contains(permission)) {
                            sender.sendMessage("this permission is already removed");
                            return true;
                        }

                        if (foundGroup.getAddedPermissions().contains(permission)) {
                            sender.sendMessage("this permission is set to be added. this will replace this");
                            foundGroup.getAddedPermissions().remove(permission);
                        }

                        foundGroup.getRemovedPermissions().add(permission);
                        sender.sendMessage("set to remove the permission from this group");
                    } else {
                        sender.sendMessage("please provide a valid value: true, false");
                        return true;
                    }

                    manager.registerGroup(foundGroup);
                    sender.sendMessage("updated the group");
                    return true;
                }
                case "remove" -> {
                    if (args.length < 5) {
                        sender.sendMessage("please provide a permission to remove");
                        return true;
                    }

                    String permission = args[4].toLowerCase();

                    foundGroup.getRemovedPermissions().remove(permission);
                    foundGroup.getAddedPermissions().remove(permission);
                    manager.registerGroup(foundGroup);
                    sender.sendMessage("removed permission from this group");
                    return true;
                }
                case "clear" -> {
                    for (String permission : foundGroup.getAddedPermissions()) {
                        foundGroup.getAddedPermissions().remove(permission);
                    }
                    for (String permission : foundGroup.getRemovedPermissions()) {
                        foundGroup.getRemovedPermissions().remove(permission);
                    }
                    manager.registerGroup(foundGroup);
                    sender.sendMessage("removed all permissions from this group");
                    return true;
                }
                case "list" -> sender.sendMessage("added permissions:\n" + foundGroup.getAddedPermissions().toString() + "\nremoved permissions:\n" + foundGroup.getRemovedPermissions().toString());
                default -> {
                    sender.sendMessage("please provide a valid permission subcommand; add, remove, clear, list");
                    return true;
                }
            }
        } else {
            sender.sendMessage("please enter a valid subcommand: info or permission");
            return true;
        }

        return false;
    }

    private boolean rankCommand(CommandSender sender, String[] args) {
        // /circaea rank
        if (args.length < 2) {
            sender.sendMessage("valid subcommands are: register, <rank-name>");
            return true;
        }

        String rankCommand = args[1].toLowerCase();
        RankManager manager = circaea.getRankManager();

        if (rankCommand.equals("register")) {
            // /circaea rank register
            if (args.length < 3) {
                sender.sendMessage("provide a rank name");
                return true;
            }

            String rankName = args[2];

            if (args.length < 4) {
                sender.sendMessage("provide a rank priority");
                return true;
            }

            int priority;

            try {
                priority = Integer.parseInt(args[3]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage("Could not parse second argument (priority) into a readable integer.");
                return false;
            }

            if (manager.rankExists(rankName)) {
                sender.sendMessage("a rank already exists with this name");
                return true;
            }

            manager.registerRank(new PermissionRank(new ArrayList<>(), rankName, priority, null));
            sender.sendMessage("registered a rank with the name of: " + rankName);
            return true;
        } else if (rankCommand.equals("list")) {
            // list all ranks
            sender.sendMessage(manager.getRanks().toString());
            return true;
        }

        // /circaea rank <name>

        PermissionRank foundRank = manager.getRank(rankCommand);

        // check if the rankCommand is a registered rank name
        if (foundRank == null) {
            sender.sendMessage("this rank does not exist! please register it with /circaea rank register <name>");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("please provide a subcommand of info or group");
            return true;
        }

        String rankSubCommand = args[2].toLowerCase();
        // /circaea rank <name> info
        switch (rankSubCommand) {
            case "info" -> {
                sender.sendMessage(foundRank.toString());
                return true;
            }
            case "group" -> {
                if (args.length < 4) {
                    sender.sendMessage("please provide a group subcommand");
                    return true;
                }

                // add, remove, clear, list
                String groupCommand = args[3].toLowerCase();
                switch (groupCommand) {
                    case "add" -> {
                        if (args.length < 5) {
                            sender.sendMessage("please provide a group to add");
                            return true;
                        }

                        String group = args[4].toLowerCase();

                        if (foundRank.getPermissionGroups().contains(group)) {
                            sender.sendMessage("this rank already contains this group");
                            return true;
                        }

                        foundRank.getPermissionGroups().add(group);
                        manager.registerRank(foundRank);
                        sender.sendMessage("updated the rank");
                        return true;
                    }
                    case "remove" -> {
                        if (args.length < 5) {
                            sender.sendMessage("please provide a group to remove");
                            return true;
                        }

                        String group = args[4].toLowerCase();

                        foundRank.getPermissionGroups().remove(group);
                        manager.registerRank(foundRank);
                        sender.sendMessage("removed group from this rank");
                        return true;
                    }
                    case "clear" -> {
                        for (String group : foundRank.getPermissionGroups()) {
                            foundRank.getPermissionGroups().remove(group);
                        }
                        manager.registerRank(foundRank);
                        sender.sendMessage("removed all groups from this rank");
                        return true;
                    }
                    case "list" -> sender.sendMessage("groups:\n" + foundRank.getPermissionGroups().toString());
                    default -> {
                        sender.sendMessage("please provide a valid group subcommand; add, remove, clear, list");
                        return true;
                    }
                }
            }
            case "child" -> {
                if (args.length < 4) {
                    sender.sendMessage("please provide a child subcommand");
                    return true;
                }
                String childSubcommand = args[3].toLowerCase();
                if (childSubcommand.equals("set")) {
                    if (args.length < 5) {
                        sender.sendMessage("please a child rank to add");
                        return true;
                    }

                    if (!manager.rankExists(args[4])) {
                        sender.sendMessage("this rank does not exist!");
                        return true;
                    }

                    foundRank.setChildRank(args[4]);
                } else if (childSubcommand.equals("clear")) {
                    foundRank.setChildRank(null);
                } else {
                    sender.sendMessage("please provide a valid child subcommand: add, remove");
                    return true;
                }
                manager.registerRank(foundRank);
                sender.sendMessage("updated the child of this rank");
                return true;
            }
            default -> {
                sender.sendMessage("please enter a valid subcommand: info, group, child");
                return true;
            }
        }

        return false;
    }

    private boolean playerCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("valid subcommands are: <player-name>");
            return true;
        }

        String playerCommand = args[1];
        PlayerManager manager = circaea.getPlayerManager();

        Player onlinePlayer = Bukkit.getPlayer(playerCommand);
        if (onlinePlayer == null) {
            // todo: api call to get uuid from name
            sender.sendMessage("this player is not online!");
            return true;
        }

        PermittedPlayer foundPlayer = manager.getPermittedPlayer(onlinePlayer.getUniqueId());

        if (foundPlayer == null) {
            sender.sendMessage("somehow the player you provided is not in the database :/");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("valid subcommands are: rank, group, info");
            return true;
        }

        String playerSubCommand = args[2].toLowerCase();

        if (playerSubCommand.equals("info")) {
            sender.sendMessage(foundPlayer.toString());
            return true;
        } else if (playerSubCommand.equals("rank")) {
            if (args.length < 4) {
                sender.sendMessage("valid subcommands are: set, clear");
                return true;
            }

            String rankCommand = args[3].toLowerCase();
            RankManager rankManager = circaea.getRankManager();

            if (rankCommand.equals("set")) {
                if (args.length < 5) {
                    sender.sendMessage("valid subcommands are: <rank name>");
                    return true;
                }

                String rankName = args[4].toLowerCase();

                PermissionRank foundRank = rankManager.getRank(rankName);

                if (foundRank == null) {
                    sender.sendMessage("this rank does not exist!");
                    return true;
                }

                foundPlayer.setRank(foundRank);
                manager.updatePlayer(foundPlayer);
            } else if (rankCommand.equals("clear")) {
                foundPlayer.setRank(rankManager.getDefaultRank());
                manager.updatePlayer(foundPlayer);
            } else {
                sender.sendMessage("valid subcommands are: set, clear");
                return true;
            }
        } else if (playerSubCommand.equals("group")) {
            if (args.length < 4) {
                sender.sendMessage("please provide a group subcommand");
                return true;
            }

            // add, remove, clear, list
            String groupCommand = args[3].toLowerCase();
            switch (groupCommand) {
                case "add" -> {
                    if (args.length < 5) {
                        sender.sendMessage("please provide a group to add");
                        return true;
                    }

                    String group = args[4].toLowerCase();

                    if (foundPlayer.getPermissionGroups().contains(group)) {
                        sender.sendMessage("this player already contains this group");
                        return true;
                    }

                    foundPlayer.getPermissionGroups().add(group);
                    manager.updatePlayer(foundPlayer);
                    sender.sendMessage("updated the player");
                    return true;
                }
                case "remove" -> {
                    if (args.length < 5) {
                        sender.sendMessage("please provide a group to remove");
                        return true;
                    }

                    String group = args[4].toLowerCase();

                    foundPlayer.getPermissionGroups().remove(group);
                    manager.updatePlayer(foundPlayer);
                    sender.sendMessage("removed group from this player");
                    return true;
                }
                case "clear" -> {
                    for (String group : foundPlayer.getPermissionGroups()) {
                        foundPlayer.getPermissionGroups().remove(group);
                    }

                    manager.updatePlayer(foundPlayer);
                    sender.sendMessage("removed all groups from this player");
                    return true;
                }
                case "list" -> sender.sendMessage("groups:\n" + foundPlayer.getPermissionGroups().toString());
                default -> {
                    sender.sendMessage("please provide a valid group subcommand; add, remove, clear, list");
                    return true;
                }
            }
        } else {
            sender.sendMessage("valid subcommands are: rank, group, info");
            return true;
        }

        return false;
    }
}

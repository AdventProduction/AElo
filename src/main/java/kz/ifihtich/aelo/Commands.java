package kz.ifihtich.aelo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements TabExecutor {

    private final Database database = AElo.getInstance().getDatabase();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0){
            return true;
        }
        String path = strings[0].toLowerCase();
        int elo = 0;
        switch (path){
            case "add": {
                if (!commandSender.hasPermission("aelo.add")) {
                    commandSender.sendMessage(getString("noPerm"));
                    return true;
                }
                if (!(strings.length == 3)){
                    if (!commandSender.hasPermission("aelo.use")){
                        commandSender.sendMessage(getString("noPerm"));
                        return true;
                    }
                    sendHelp(commandSender);
                    return true;
                }
                String target = strings[1];
                Player targetPlayer = Bukkit.getPlayerExact(target);
                if (targetPlayer == null || !targetPlayer.isOnline()) {
                    commandSender.sendMessage(getString("offline")
                            .replace("{player}", target));
                    return true;
                }

                try {
                    elo = Integer.parseInt(strings[2]);
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(getString("number"));
                    return true;
                }
                if (elo < 0) {
                    commandSender.sendMessage(getString("number"));
                    return true;
                }
                try {
                    database.addValue(target, elo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                commandSender.sendMessage(getString("add")
                        .replace("{player}", target)
                        .replace("{elo}", String.valueOf(elo)));
                return true;
            }

            case "remove": {
                if (!commandSender.hasPermission("aelo.remove")) {
                    commandSender.sendMessage(getString("noPerm"));
                    return true;
                }
                if (!(strings.length == 3)){
                    if (!commandSender.hasPermission("aelo.use")){
                        commandSender.sendMessage(getString("noPerm"));
                        return true;
                    }
                    sendHelp(commandSender);
                    return true;
                }
                String target = strings[1];
                Player targetPlayer = Bukkit.getPlayerExact(target);
                if (targetPlayer == null || !targetPlayer.isOnline()) {
                    commandSender.sendMessage(getString("offline")
                            .replace("{player}", target));
                    return true;
                }
                try {
                    elo = Integer.parseInt(strings[2]);
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(getString("number"));
                    return true;
                }
                if (elo < 0) {
                    commandSender.sendMessage(getString("number"));
                    return true;
                }
                try {
                    database.removeValue(target, elo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                commandSender.sendMessage(getString("remove")
                        .replace("{player}", target)
                        .replace("{elo}", String.valueOf(elo)));
                return true;
            }

            case "info": {
                if (!commandSender.hasPermission("aelo.info")) {
                    commandSender.sendMessage(getString("noPerm"));
                    return true;
                }
                if (!(strings.length == 2)){
                    if (!commandSender.hasPermission("aelo.use")){
                        commandSender.sendMessage(getString("noPerm"));
                        return true;
                    }
                    sendHelp(commandSender);
                    return true;
                }
                String target = strings[1];
                Player targetPlayer = Bukkit.getPlayerExact(target);
                try {
                    elo = database.getValue(target);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    commandSender.sendMessage(getString("info")
                            .replace("{player}", target)
                            .replace("{elo}", String.valueOf(elo)));
                } else {
                    commandSender.sendMessage(getString("offline")
                            .replace("{player}", target));
                }
                return true;

            }

            case "reload": {
                if (!commandSender.hasPermission("aelo.reload")) {
                    commandSender.sendMessage(getString("noPerm"));
                    return true;
                }
                commandSender.sendMessage(getString("reload"));
                AElo.getInstance().reloadEloSQL();
                AElo.getInstance().loadConfig();
                return true;
            }

            case "set": {
                if (!commandSender.hasPermission("aelo.set")){
                    commandSender.sendMessage(getString("noPerm"));
                    return true;
                }

                if (!(strings.length == 3)){
                    if (!commandSender.hasPermission("aelo.use")){
                        commandSender.sendMessage(getString("noPerm"));
                        return true;
                    }
                    sendHelp(commandSender);
                    return true;
                }

                String target = strings[1];
                Player targetPlayer = Bukkit.getPlayerExact(target);

                if (targetPlayer == null || !targetPlayer.isOnline()){
                    commandSender.sendMessage(getString("offline")
                            .replace("{player}", target));
                    return true;
                }
                try {
                    elo = Integer.parseInt(strings[2]);
                } catch (IllegalArgumentException e){
                    commandSender.sendMessage(getString("number")
                            .replace("{player}", target)
                            .replace("{elo}", String.valueOf(elo)));
                    return true;
                }
                try {
                    database.setValue(target, elo);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                commandSender.sendMessage(getString("set")
                        .replace("{player}", target)
                        .replace("{elo}", String.valueOf(elo)));
                return true;

            }
            default: {
                if (!commandSender.hasPermission("aelo.use")){
                    commandSender.sendMessage(getString("noPerm"));
                    return true;
                }
                sendHelp(commandSender);
                return true;
            }
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("aelo.tab")) {
            if (strings.length == 1) {
                return Arrays.asList("add", "remove", "info", "reload", "set");
            }
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()){
                players.add(player.getName());
            }
            if (strings[0].equalsIgnoreCase("add")){
                switch (strings.length){
                    case 2: return players;
                    case 3: return Arrays.asList("10", "20");
                }
            } else if (strings[0].equalsIgnoreCase("remove")){
                switch (strings.length){
                    case 2: return players;
                    case 3: return Arrays.asList("10", "20");
                }
            } else if (strings[0].equalsIgnoreCase("set")){
                switch (strings.length){
                    case 2: return players;
                    case 3: return Arrays.asList("10", "20");
                }
            } else if (strings[0].equalsIgnoreCase("info")){
                if (strings.length == 2){
                    return players;
                }
                else {
                    return Arrays.asList();
                }
            } else if (strings[0].equalsIgnoreCase("reload")){
                return Arrays.asList();
            }
            if (strings.length > 3){
                return Arrays.asList();
            }
        }
        return Arrays.asList();
    }

    private String getString(String path){
        return Utils.color(AElo.getInstance().getConfig().getString("messages." + path));
    }
    private void sendHelp(CommandSender sender){
        List<String> help = AElo.getInstance().getConfig().getStringList("messages.help");

        for (String str : help){
            sender.sendMessage(Utils.color(str));
        }
    }
}

package kz.ifihtich.aelo;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "aelo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ifihtich";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || !player.hasPlayedBefore()) return "";
        String name = player.getName();

        if (params.equalsIgnoreCase("elo")){
            int elo = 0;
            try {
                elo = AElo.getInstance().getDatabase().getValue(name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (elo > 0){
                return Utils.color(AElo.getInstance().getPositiveColor() + elo);
            }
            else if (elo == 0){
                return Utils.color(AElo.getInstance().getNeutralColor() + elo);
            }
            else {
                return Utils.color(AElo.getInstance().getNegativeColor() + elo);
            }
        }
        return null;
    }
}

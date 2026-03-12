package kz.ifihtich.aelo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class Event implements Listener {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player target = event.getEntity();
            Player killer = target.getKiller();

            int received = random.nextInt(AElo.getInstance().getMinKill(), AElo.getInstance().getMaxKill() + 1);
            int lost = random.nextInt(AElo.getInstance().getMinDeath(), AElo.getInstance().getMaxDeath() + 1);

            Bukkit.getScheduler().runTaskAsynchronously(AElo.getInstance(), () -> {
                try {
                    AElo.getInstance().getDatabase().addValue(killer.getName(), received);
                    AElo.getInstance().getDatabase().removeValue(target.getName(), lost);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            killer.sendMessage(Utils.color(AElo.getInstance().getConfig().getString("messages.received")
                    .replace("{player}", target.getName())
                    .replace("{elo}", String.valueOf(received)), killer));
            target.sendMessage(Utils.color(AElo.getInstance().getConfig().getString("messages.lost")
                    .replace("{player}", killer.getName())
                    .replace("{elo}", String.valueOf(lost)), target));
        }
    }
}

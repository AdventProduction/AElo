package kz.ifihtich.aelo;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class AElo extends JavaPlugin {

    private static AElo instance;

    private Database database;

    private String negativeColor;
    private String neutralColor;
    private String positiveColor;
    private int minKill;
    private int maxKill;
    private int minDeath;
    private int maxDeath;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfig();
        Utils.logo();
        this.database = new Database();
        try{
            database.connect();
            database.createTable();
        } catch (SQLException e){
            e.printStackTrace();
        }
        reloadEloSQL();
        new Placeholder().register();
        getCommand("aelo").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
    }

    public void loadConfig(){
        negativeColor = getConfig().getString("elo.negative.color", "&#FF001F");
        neutralColor = getConfig().getString("elo.neutral.color", "&#968385");
        positiveColor = getConfig().getString("elo.positive.color", "&#40EA11");
        parseRanges();

    }

    public void reloadEloSQL(){
        if (database != null){
            database.close();
        }
        try{
            database.connect();
            database.createTable();
        } catch (SQLException e){
            e.printStackTrace();
        }
        reloadConfig();
    }

    private void parseRanges(){
        String killRange = getConfig().getString("elo.main.perKill", "5-10");
        String deathRange = getConfig().getString("elo.main.perDeath", "3-7");

        String[] killSplit = killRange.split("-");
        String[] deathSplit = deathRange.split("-");

        try {
            minKill = Integer.parseInt(killSplit[0]);
            maxKill = Integer.parseInt(killSplit[1]);

            minDeath = Integer.parseInt(deathSplit[0]);
            maxDeath = Integer.parseInt(deathSplit[1]);

        } catch (Exception e){
            getLogger().warning("Ошибка диапазона ELO в config.yml");
            minKill = 5;
            maxKill = 10;
            minDeath = 3;
            maxDeath = 7;
        }

        if (minKill > maxKill){
            int tmp = minKill;
            minKill = maxKill;
            maxKill = tmp;
        }

        if (minDeath > maxDeath){
            int tmp = minDeath;
            minDeath = maxDeath;
            maxDeath = tmp;
        }
    }

    public static AElo getInstance(){
        return instance;
    }
    public Database getDatabase(){
        return database;
    }

    public String getPositiveColor() {
        return positiveColor;
    }

    public String getNeutralColor() {
        return neutralColor;
    }

    public String getNegativeColor() {
        return negativeColor;
    }
    public int getMinKill() {
        return minKill;
    }

    public int getMaxKill() {
        return maxKill;
    }

    public int getMinDeath() {
        return minDeath;
    }

    public int getMaxDeath() {
        return maxDeath;
    }
}

package code.revisor;

import code.BasicMsg;
import code.Manager;
import code.revisor.message.*;
import code.utils.module.ModuleCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RevisorManager{


    private Manager manager;

    private BasicMsg plugin;
    private ModuleCheck path;

    private CooldownData cooldownData;
    private MentionRevisor mentionRevisor;
    private FloodRevisor floodRevisor;
    private WordRevisor wordRevisor;
    private LinkRevisor linkRevisor;
    private CapsRevisor capsRevisor;
    private DotRevisor dotRevisor;

    private int revisorLevel = 2;

    public RevisorManager(Manager manager) {
        this.manager = manager;
        manager.getListManager().getModules().add("chat_revisor");
        plugin = manager.getPlugin();
        path = manager.getPathManager();
        setup();
    }

    private void setup(){
        cooldownData = new CooldownData(manager);
        mentionRevisor = new MentionRevisor(manager);
        floodRevisor = new FloodRevisor(manager);
        wordRevisor = new WordRevisor(manager);
        linkRevisor = new LinkRevisor(manager);
        capsRevisor = new CapsRevisor(manager);
        dotRevisor = new DotRevisor(manager);
    }

    public void setLevel(int level){
        revisorLevel = level;
    }

    public String revisor(UUID uuid, String message) {
        if (!(path.isOptionEnabled("chat_revisor"))){
            return message;
        }

        if (revisorLevel == 0){
            return message;
        }

        Player player = Bukkit.getPlayer(uuid);

        message = getMentionRevisor().check(player, message);
        message = getBadWordsRevisor().check(player, message);
        message = getAntiFloodRevisor().check(player, message);
        message = getCapsRevisor().check(player, message);

        if (revisorLevel > 1) {
            message = getLinkRevisor().check(player, message);
            if (message == null) return null;
        }

        revisorLevel = 2;
        return getDotRevisor().check(player, message);
    }

    public MentionRevisor getMentionRevisor() {
        return mentionRevisor;
    }

    public LinkRevisor getLinkRevisor() {
        return linkRevisor;
    }

    public FloodRevisor getAntiFloodRevisor() {
        return floodRevisor;
    }

    public CapsRevisor getCapsRevisor() {
        return capsRevisor;
    }

    public CooldownData getAntiRepeatRevisor() {
        return cooldownData;
    }

    public WordRevisor getBadWordsRevisor() {
        return wordRevisor;
    }

    public DotRevisor getDotRevisor() {
        return dotRevisor;
    }
}

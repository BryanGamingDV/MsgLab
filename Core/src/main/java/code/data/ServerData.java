package code.data;

public class ServerData {

    private boolean isMuted = false;

    private int serverTextCooldown = 0;
    private int serverCmdCooldown = 0;

    public int getServerTextCooldown() {
        return serverTextCooldown;
    }

    public int getServerCmdCooldown() {
        return serverCmdCooldown;
    }

    public String getServerTextCooldownInString() {
        return String.valueOf(serverTextCooldown);
    }

    public String getServerCmdCooldownInString() {
        return String.valueOf(serverCmdCooldown);
    }

    public void setServerTextCooldown(int serverTextCooldown) {
        this.serverTextCooldown = serverTextCooldown;
    }

    public void setServerCmdCooldown(int serverCmdCooldown) {
        this.serverCmdCooldown = serverCmdCooldown;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }
}

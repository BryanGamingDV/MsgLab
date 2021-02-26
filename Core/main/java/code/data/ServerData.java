package code.data;

public class ServerData {

    private boolean isMuted = false;

    private int serverCooldown = 0;

    public int getServerCooldown() {
        return serverCooldown;
    }


    public String getServerTextCooldown() {
        return String.valueOf(serverCooldown);
    }

    public void setServerCooldown(int serverCooldown) {
        this.serverCooldown = serverCooldown;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }
}

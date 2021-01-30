package code.utils;

import code.Manager;
import code.utils.addons.VaultSupport;

public class SupportManager {

    private Manager manager;

    private VaultSupport vaultSupport;

    public SupportManager(Manager manager){
        this.manager = manager;
        setup();
    }

    public void setup(){
        vaultSupport = new VaultSupport(manager);
    }

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }
}

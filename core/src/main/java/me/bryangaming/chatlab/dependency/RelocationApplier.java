package me.bryangaming.chatlab.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelocationApplier {
    private List<Relocation> relocationList;
    private List<?> luckoRelocations;

    public RelocationApplier() {
        relocationList = new ArrayList<>();
    }

    public void setRelocationList(List<Relocation> relocationList) {
        this.relocationList = relocationList;
    }

    public void applyRelocations(File input, File output) throws IOException {
        if (this.luckoRelocations == null) {
            List<Object> relocations = new ArrayList<>();
            luckoRelocations = relocations;

            for (Relocation relocation : relocationList) {
                relocations.add(new me.lucko.jarrelocator.Relocation(relocation.getPattern(), relocation.getRelocatedPattern()));
            }
        }

        new me.lucko.jarrelocator.JarRelocator(input, output, (List<me.lucko.jarrelocator.Relocation>) luckoRelocations)
                .run();
    }
}
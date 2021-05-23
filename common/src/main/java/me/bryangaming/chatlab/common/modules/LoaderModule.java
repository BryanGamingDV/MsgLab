package me.bryangaming.chatlab.common.modules;

import me.fixeddev.commandflow.annotated.part.Module;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;

public class LoaderModule {

    private PartInjector partInjector;

    public LoaderModule(Module... modules){
        load(modules);
    }

    public void load(Module... modules){

        partInjector = PartInjector.create();

        partInjector.install(new DefaultsModule());
        for (Module module : modules){
                partInjector.install(module);
        }
    }

    public PartInjector getInjector(){
            return partInjector;
    }
}

package me.bryangaming.chatlab.common.listeners.listener;

import me.bryangaming.chatlab.api.Event;
import me.bryangaming.chatlab.api.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListenerManager {

    private final List<Listener> listenersList = new ArrayList<>();

    public void callEvent(Event event){
        for (Listener listener : listenersList){
            Method[] methods = listener.getClass().getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(ListenerHandler.class)) {
                    continue;
                }

                try {
                    method.invoke(event);
                }catch (IllegalAccessException | InvocationTargetException exception){
                    exception.printStackTrace();
                }
            }
        }
    }

    public void registerListeners(Listener... listeners){
        listenersList.addAll(Arrays.asList(listeners));
    }
}

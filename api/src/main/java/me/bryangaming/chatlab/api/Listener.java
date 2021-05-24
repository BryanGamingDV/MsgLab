package me.bryangaming.chatlab.api;

public interface Listener<E> {

    void doAction(E event);
}

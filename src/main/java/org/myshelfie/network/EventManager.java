package org.myshelfie.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *  This class is used to manage the {@link Listener} and the events.
 *  It stores a map that links the eventType to the listeners.
 *  When an event is generated, {@link #notify(Enum, Object...)} will be called passing
 *  the event (of one of the specified eventType) and some argument.
 */
public class EventManager {
    // Map that links the Class Object representing the type of the event (enum), to the list of listeners that will be able to handle it
    protected Map<Class<? extends Enum<?>>, List<Listener<? extends Enum<?>>>> listeners = new HashMap<>();

    /**
     * Subscribe a listener to an event.
     * @param eventType The enum containing all the events this listener will listen to
     * @param listener The listener to subscribe
     */
    public <E extends Enum<E>> void subscribe(Class<E> eventType, Listener<E> listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<>());
        }
        List<Listener<?>> users = listeners.get(eventType);
        users.add(listener);
    }

    /**
     * Return the first listener of a certain class that matches a function
     * @param classToListen The class that is being listened
     * @param func A function that returns true if the listener is the required one
     * @return the first listener of a certain class that matches a function
     */
    public Listener<? extends Enum<?>> getListener(Class<? extends Enum<?>> classToListen, Function<Listener<? extends Enum<?>>, Boolean> func) {
        List<Listener<? extends Enum<?>>> listened = listeners.get(classToListen);
        if (listened == null) {
            return null;
        }
        for (Listener<? extends Enum<?>> l: listened) {
            if (func.apply(l)) {
                return l;
            }
        }
        return null;
    }

    /**
     * Unsubscribe a listener from an event.
     * @param eventType The event to unsubscribe from
     * @param listener The listener to unsubscribe
     */
    public <E extends Enum<E>> void unsubscribe(Class<E> eventType, Listener<E> listener) {
        List<Listener<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null && listener != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Notify all the listeners of the event and forward the argument.
     * @param event    The event that has been emitted
     * @param args     The arguments to forward to the listeners
     */
    public <E extends Enum<E>> void notify(E event, Object... args) {
        List<Listener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Listener<?> listener : eventListeners) {
                Listener<E> typedListener = (Listener<E>) listener;
                typedListener.update(event, args);
            }
        }
    }

}
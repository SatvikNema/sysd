package com.satvik.sysd.pubsub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Publisher {

    private final List<Subscriber> subscribers;
    private final String name;

    public Publisher(String name) {
        subscribers = Collections.synchronizedList(new ArrayList<>());
        this.name = name;
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public synchronized void publish(Object message) {
        for (Subscriber subscriber : subscribers) {
            subscriber.consume(message);
        }
    }

    public String toString(){
        return name;
    }
}

package com.satvik.sysd.pubsub;

public class Subscriber {

    private final String name;

    public Subscriber(String name) {
        this.name = name;
    }

    public void consume(Object message) {
        System.out.println(this + " consuming "+message);
    }

    public void subscribe(Publisher publisher) {
        publisher.addSubscriber(this);
    }

    public void unsubscribe(Publisher publisher) {
        publisher.removeSubscriber(this);
    }

    public String toString(){
        return name;
    }
}

package com.satvik.stockbroker.entity;

import com.satvik.stockbroker.model.Observable;
import com.satvik.stockbroker.model.Observer;
import com.satvik.stockbroker.model.OrderQueue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Stock implements Observable<Stock> {
    private String id;
    private String name;
    private AtomicLong currentPrice;
    private OrderQueue orderQueue;

    private final Set<Observer<Stock>> observers = new CopyOnWriteArraySet<>();

    public void updatePrice(long currentPrice){
        this.currentPrice.set(currentPrice);
        notifyObservers();
    }

    @Override
    public void addObserver(Observer<Stock> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<Stock> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(o -> o.update(this));
    }
}

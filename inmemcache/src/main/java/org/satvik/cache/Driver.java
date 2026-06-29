package org.satvik.cache;

import org.satvik.cache.service.LRUCache;

public class Driver {
    static void main() {
        LRUCache<Integer, Integer> cache = new LRUCache<>(3);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);


        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(1));
        System.out.println(cache.get(1));

        System.out.println("====");
        cache.put(4, 4);
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
        System.out.println(cache.get(4));

    }
}

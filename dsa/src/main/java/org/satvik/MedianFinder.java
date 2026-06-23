package org.satvik;

import java.util.Collections;
import java.util.PriorityQueue;

class MedianFinder {

    PriorityQueue<Integer> minq;
    PriorityQueue<Integer> maxq;
    boolean toggle = false;

    public MedianFinder() {
        minq = new PriorityQueue<>();
        maxq = new PriorityQueue<>(Collections.reverseOrder());
    }

    public void addNum(int num) {
        if(toggle){
            minq.add(num);
        } else {
            maxq.add(num);
        }
        toggle = !toggle;
    }

    public double findMedian() {
        int totalSize = maxq.size() + minq.size();
        double ans;
        if(maxq.size() == minq.size()){
            ans = (double)(maxq.peek() + minq.peek()) / 2;
        } else {
            if(!maxq.isEmpty() && !minq.isEmpty()){
                if(maxq.peek() > minq.peek()){
                    ans = maxq.peek();
                } else {
                    ans = minq.peek();
                }
            }
            else if(minq.isEmpty()){
                ans = maxq.peek();
            } else {
                ans = minq.peek();
            }
        }
        System.out.println(minq);
        System.out.println(maxq);
        System.out.println();
        return ans;
    }
}


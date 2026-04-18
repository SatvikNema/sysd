package com.satvik.dsa;

public class Tuple {
    public int a;
    public int b;
    public int c;

    public Tuple(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public int hashCode() {
        return a + b + c;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple) {
            Tuple t = (Tuple) obj;
            return a == t.a && b == t.b && c == t.c;
        }
        return false;
    }
}

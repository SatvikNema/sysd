package com.satvik.sysd;

import java.util.*;

public class Scratch{

    public static void main(String[] args) {
        String a = "123";
        String b = "1";
        for(char i:a.toCharArray()){
            char val = (char) (((i - '0' + 8) % 10) + '0');
            System.out.println(val);
        }

    }
}






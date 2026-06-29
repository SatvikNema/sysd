package com.satvik.sysd;

import com.satvik.sysd.service.FileSystem;

public class Driver {
    public void start(){
        FileSystem fs = new FileSystem();
        fs.mkdir("/satvik");
        fs.mkdir("/satvik/home");
        fs.touch("/satvik/home/index.html");
        fs.touch("/satvik/home/index2.html");
        fs.touch("/satvik/home/index3.html");
        fs.ls("/satvik/home");
    }
}

package com.satvik.sysd.service;


import com.satvik.sysd.entity.Directory;
import com.satvik.sysd.entity.File;
import com.satvik.sysd.entity.Node;

import java.util.Arrays;
import java.util.Collection;

public class FileSystem {
    private final Node root;
    private static final String DIR_SEPARATOR = "/";

    public FileSystem(){
        this.root = new Directory(DIR_SEPARATOR);
    }

    public void mkdir(String path){
        if(path.startsWith(DIR_SEPARATOR)){
            path = path.substring(1);
        }
        String[] dirs = path.split(DIR_SEPARATOR);
        Directory dir = returnDirectoryAtDepth(dirs, dirs.length-1);
        dir.add(new Directory(dirs[dirs.length-1]));
    }

    public void touch(String path){
        if(path.startsWith(DIR_SEPARATOR)){
            path = path.substring(1);
        }
        String[] dirs = path.split(DIR_SEPARATOR);
        Directory dir = returnDirectoryAtDepth(dirs, dirs.length-1);
        dir.add(new File(dirs[dirs.length-1]));
    }

    public void ls(String path){
        if(path.startsWith(DIR_SEPARATOR)){
            path = path.substring(1);
        }
        String[] dirs = path.split("/");
        Directory dir = returnDirectoryAtDepth(dirs, dirs.length);
        dir.list().forEach(System.out::println);
    }

    private Directory returnDirectoryAtDepth(String[] dirs, int depth){
        Node temp = root;
        for(int i=0;i<depth;i++){
            if(temp instanceof File){
                throw new IllegalArgumentException(Arrays.toString(dirs)+" cannot resolve to a file");
            }
            temp = ((Directory)temp).get(dirs[i]);
            if(temp == null){
                throw new IllegalStateException("intermediate directories should be present");
            }
        }
        return ((Directory)temp);
    }
}

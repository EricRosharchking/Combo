package com.example.liyuan.projectcombo;

import java.util.HashSet;

/**
 * Created by Liyuan on 1/6/2016.
 */
public class FileStatus {
    private boolean status;
    private HashSet<String> allFileNamesSet;

    public FileStatus(HashSet<String> set) {
        if(set.isEmpty()) {
            status = false;
        } else {
            status = true;
        }
        allFileNamesSet = set;
    }

    public boolean getStatus() {
        return status;
    }

    public HashSet<String> getAllFileNamesSet() {
        return allFileNamesSet;
    }
}

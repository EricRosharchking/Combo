package com.example.liyuan.projectcombo;

import android.content.Context;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashSet;

/**
 * Created by Liyuan on 1/4/2016.
 */
public class ScoreFile implements Serializable {
    private String title;
    private String artist;
    //private Context context;
    private HashSet<String> fileSet;////TODO all names in the set do not include the file format(".SCORE")

    private final String allFileNames = "ALL_FILE_NAMES.DATA";

    public ScoreFile() {
        title = getNewName();
        // TODO get the HashSet from a file, that's stored as byte[];
        title = getFileFormat(title);
        //if(object instanceof Context) {
        //Log.d("Log@ScoreFile33", "the Object is a Context");
        //context = (Context) object;
        //} else {
        //Log.e("Error@ScoreFile36", "the Object is not a Context");
        //}
        fileSet = openAllFileNames().getAllFileNamesSet();
        Log.d("File size Log", "" + fileSet.size());
    }

    private String getNewName() {
        //TODO find the existing names, and initiate the name{New Score, New Score(1), etc}
        //TODO put all score names in a HashSet
        String thisName = "New Score";
        String newName = addSuffix(thisName);

        return newName;
    }

    private String addSuffix(String name){
        String newName = name;
        if (name.indexOf('(') > 0) {
            newName = (name.split("\\("))[0];
        }
        int count = 0;
        if(fileSet != null){
            if (!fileSet.isEmpty()) {
                for (String s : fileSet) {
                    if (s.contains(name)) {
                        count++;
                    }
                }
            }
        }
        if (count > 0) {
            newName = newName + " (" + count + ")";
        }
        return newName;
    }

    private String getFileFormat(String name) {
        //String fileName = name + ".DATA";
        return name + ".SCORE";
    }

    protected boolean save(Score score) throws IOException {
        boolean status = false;
        String fileName = score.getTitle();
        fileName = addSuffix(fileName);
        score.setTitle(fileName);
        Context context = App.getAppContext();
        if (context != null) {
            try {

                Log.d("Log@ScoreFile85", "Context is not null");

                Log.d("", fileName);
                FileOutputStream fos = context.openFileOutput(getFileFormat(fileName), Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
                oos.writeObject(score);
                oos.close();
                fos.close();
                status = true;
                fileSet.add(fileName);
                boolean zzz = saveAllFileNames(fileSet);
                for (String s: fileSet) {
                    Log.i("Files include", s);
                }
                Log.d("Log@ScoreFile91", "Save all file names Log Status is " + zzz + fileSet.size());
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e);
            }
        } else {
            Log.e("Log@ScoreFile103", "context is null");
        }
        return status;
    }

    protected ScoreStatus open(String name) throws IOException {
        ScoreStatus scoreStatus = new ScoreStatus();

        name = getFileFormat(name);
        try{
            Context context = App.getAppContext();
            FileInputStream fis = context.openFileInput(name);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
            Object o = ois.readObject();
            scoreStatus = new ScoreStatus(o);
            Log.i("Log@ScoreFile121", "scoreStatus is " + (scoreStatus != null));
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        return scoreStatus;
    }


    public FileStatus openAllFileNames() {
        HashSet<String> thisSet = new HashSet<String>();
        //context = App.getAppContext();
        Context context = App.getAppContext();
        if (context != null) {
            Log.d("Log@ScoreFile123", "Context is not null");
            try {
                Log.d("Log@ScoreFile125", "The context is " + context.getString(R.string.login));
                FileInputStream fis = context.openFileInput(allFileNames);
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));

                Object[] objects = (Object[]) ois.readObject();

                Log.d("Log@ScoreFile131", "openAllFileNames size of objects is " + objects.length);
                for (Object o : objects) {
                    if (o instanceof byte[]) {
                        byte[] b = (byte[]) o;
                        String string = new String(b, Charset.forName("UTF-8"));
                        Log.d("Log@ScoreFile136", "openAllFileNames" + string);
                        thisSet.add(string);
                    }
                }
                ois.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("File not Found", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error@ScoreFile149", "Context is null");
        }
        return new FileStatus(thisSet);
    }

    protected boolean saveAllFileNames(HashSet<String> thisSet) throws IOException{
        boolean status = false;

        HashSet<byte[]> byteSet = new HashSet<byte[]>();
        for(String s: thisSet) {
            byte[] thisBytes = s.getBytes(Charset.forName("UTF-8"));
            byteSet.add(thisBytes);
        }

        Object[] objects = byteSet.toArray();

        for(Object o: objects) {
            if (o instanceof byte[]) {
                byte[] b = (byte[]) o;
                Log.d("ObjectLog@ScoreFile182", "The size of o is " + b.length);
            }
        }

        try{
            Context context = App.getAppContext();
            FileOutputStream fos = context.openFileOutput(allFileNames, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            oos.writeObject(objects);
            oos.close();
            fos.close();
            status = true;
        } catch(Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        return status;
    }

    public HashSet<String> getFileSet() {
        return fileSet;
    }

    /*public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }*/
}

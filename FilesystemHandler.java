/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.nio.ByteBuffer;
import java.util.List;
import org.apache.thrift.TException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class FilesystemHandler implements FilesystemService.Iface {
    @Override
    public List<String> getDir(String path) {
        
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        File dir = new File(dirPath);
        List<String> names = new ArrayList<String>(Arrays.asList(dir.list()));
        if (names.isEmpty()) {
            dirPath = System.getProperty("user.dir");
            dir = new File(dirPath);
            names = new ArrayList<String>(Arrays.asList(dir.list()));
        }
        return names;
    }
    
    @Override
    public String createDir(String path, String name) {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        File newDir = new File(dirPath + "\\" + name);
        boolean success = newDir.mkdir();
        if(success) {
            return "directory was successfully created";
        }
        else {
            return "unable to create directory";
        }
    }

    @Override
    public String getFile(String path, String filename) {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        String result = "No such file";
        File dir = new File(dirPath);
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(dir.list()));
        if (names.contains(filename)) {
            result = "Found";
        }
        return result;
    }

    @Override
    public ByteBuffer getBinary(String path, String filename) throws TException {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        ByteBuffer buff = null;
        try {
            File targetFile = new File(dirPath + "\\" + filename);
            FileInputStream payload = new FileInputStream(targetFile);
            FileChannel channel = payload.getChannel();
            buff = ByteBuffer.allocate((int) channel.size());
            channel.read(buff);
            buff.rewind();
            channel.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return buff;
    }

    @Override
    public String putFile(String path, String filename, ByteBuffer file) throws TException {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        try {
            File targetStore = new File(dirPath + "\\" + filename);
            FileOutputStream store = new FileOutputStream(targetStore);
            FileChannel channel = store.getChannel();
            channel.write(file);
            channel.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "File Processed";
    }
}

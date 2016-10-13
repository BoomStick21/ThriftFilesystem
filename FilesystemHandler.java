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
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class FilesystemHandler implements FilesystemService.Iface {
    @Override
    public List<FileStruct> getDir(String path) {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        File dir = new File(dirPath);
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
        
        List<FileStruct> fileStructs = new ArrayList<FileStruct>();
        if (files.isEmpty()) {
            dirPath = System.getProperty("user.dir");
            dir = new File(dirPath);
            files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
        }
        for (int i=0; i<files.size(); i++) {
            FileStruct payload = new FileStruct();
            payload.setName(files.get(i).getName());
            if (files.get(i).isFile()) {
                payload.setSize(files.get(i).length());
                payload.setModDate(files.get(i).lastModified());
                Path filePath = Paths.get(files.get(i).getAbsolutePath());
                try {
                    BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
                    payload.setCreatedDate(attr.creationTime().toMillis());
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            fileStructs.add(payload);
        }

        return fileStructs;
    }
    
    @Override
    public String createDir(String path, String name) {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        File newDir = new File(dirPath + "\\" + name);
        boolean success = newDir.mkdir();
        if(success) {
            return "Directory was successfully created";
        }
        else {
            return "Unable to create directory";
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
    public FileStruct getBinary(String path, String filename) throws TException {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        ByteBuffer buff = null;
        FileStruct fileStruct = new FileStruct();
        try {
            File targetFile = new File(dirPath + "\\" + filename);
            FileInputStream payload = new FileInputStream(targetFile);
            FileChannel channel = payload.getChannel();
            fileStruct.content = ByteBuffer.allocate((int) channel.size());
            channel.read(fileStruct.content);
            fileStruct.content.rewind();
            channel.close();
            
            fileStruct.setName(targetFile.getName());
            fileStruct.setModDate(targetFile.lastModified());
            fileStruct.setSize(targetFile.length());
            Path filePath = Paths.get(targetFile.getAbsolutePath());
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            fileStruct.setCreatedDate(attr.creationTime().toMillis());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fileStruct;
    }

    @Override
    public String putFile(String path, String filename, FileStruct file) throws TException {
        String dirPath = System.getProperty("user.dir") + "\\" + path;
        try {
            File targetStore = new File(dirPath + "\\" + filename);
            FileOutputStream store = new FileOutputStream(targetStore);
            FileChannel channel = store.getChannel();
            channel.write(file.content);
            channel.close();

            targetStore.setLastModified(file.getModDate());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FilesystemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "File Processed";
    }
}

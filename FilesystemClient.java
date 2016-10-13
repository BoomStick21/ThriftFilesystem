/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
/**
 *
 * @author Asus
 */
public class FilesystemClient {
    private static void perform(FilesystemService.Client client) throws TException, FileNotFoundException, IOException {
        Scanner in = new Scanner(System.in);
        String input;
        boolean exit = false;
        while (true) {
            if (exit) {
                break;        
            }    
            input = in.nextLine();
            List<String> arrInput = Arrays.asList(input.split(" "));
            switch (arrInput.get(0)) {
                case "EXIT":
                    exit = true;
                    break;
                case "CREATEDIR":
                    if (arrInput.size() != 3) {
                        System.out.println("Wrong Input");
                        break;
                    }

                    System.out.println(client.createDir(arrInput.get(1), arrInput.get(2)));
                    break;
                case "DIR":
                    if (arrInput.size() != 2) {
                        System.out.println("Wrong Input");
                        break;
                    }

                    List<String> result = client.getDir(arrInput.get(1));
                    Iterator iterator = result.iterator();
                    while(iterator.hasNext()) {
                        System.out.println(iterator.next());
                    }
                    break;
                case "GETFILE":
                    if (arrInput.size() != 4) {
                        System.out.println("Wrong Input");
                        break;
                    }

                    String fileResult = client.getFile(arrInput.get(1), arrInput.get(2));
                    if (fileResult.equals("Found")) {
                        ByteBuffer buff = client.getBinary(arrInput.get(1), arrInput.get(2));
                        String dirPath = System.getProperty("user.dir") + "\\" + arrInput.get(3);
                        File storeTarget = new File(dirPath + "\\" + arrInput.get(2));
                        FileOutputStream store = new FileOutputStream(storeTarget);
                        FileChannel channel = store.getChannel();
                        channel.write(buff);
                        channel.close();
                        System.out.println("File Processed");
                    }
                    else {
                        System.out.println(fileResult);
                    }
                    break;
                case "PUTFILE":
                    if (arrInput.size() != 4) {
                        System.out.println("Wrong Input");
                        break;
                    }

                    String dirPath = System.getProperty("user.dir") + "\\" + arrInput.get(3);
                    try {
                        File targetFile = new File(dirPath + "\\" + arrInput.get(2));
                        FileInputStream payload = new FileInputStream(targetFile);
                        FileChannel channel = payload.getChannel();
                        ByteBuffer writeBuff = ByteBuffer.allocate((int) channel.size());
                        channel.read(writeBuff);
                        writeBuff.rewind();
                        System.out.println(client.putFile(arrInput.get(1), arrInput.get(2), writeBuff));
                        channel.close();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Command not recognized, please try again");
                    break;
            }
        }
    }
    
    public static void main(String [] args) {
        try {
            TTransport transport;
            transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            FilesystemService.Client client = new FilesystemService.Client(protocol);
            perform(client);
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
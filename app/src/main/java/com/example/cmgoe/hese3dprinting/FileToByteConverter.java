package com.example.cmgoe.hese3dprinting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by cmgoe on 10/23/2017.
 */

public class FileToByteConverter {

    public static byte[] convertFile(File fileToConvert){

        int size = (int) fileToConvert.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileToConvert));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("something failed");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("something failed");
        }

        return bytes;
    }
}

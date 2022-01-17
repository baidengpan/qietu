package com.video.processor.library;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * date:2022/1/13
 **/
public class ModelUtils {
    public static void copyToFile(InputStream inputStream, File outputFile) {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            int len;
            byte[] buf = new byte[4096];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

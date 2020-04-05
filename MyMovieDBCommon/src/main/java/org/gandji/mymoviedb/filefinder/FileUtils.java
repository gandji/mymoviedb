/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gandji.mymoviedb.filefinder;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Slf4j
public class FileUtils {

    private static ContentInfoUtil ciu = null;

    private static final String[] videoTypes = {"matroska", "Matroska", "avi", "mp4", "mpeg"};

    public static String computeHash(Path path) {
        log.debug("COMPUTING HASH FOR "+path.toString());
        byte[] hashCode = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(path.toFile());

            final Integer lengthInKB = 100;
            int totalBytesToRead = lengthInKB * 1024;
            byte[] dataBytes = new byte[totalBytesToRead];

            int nread = 0;
            int offset = 0;
            /* hash on first 100 kilobytes */
            while (totalBytesToRead > 0) {
                nread = fis.read(dataBytes, offset, totalBytesToRead);
                // just make sure the file is large enough
                if (nread == -1) {
                    break;
                }
                totalBytesToRead = totalBytesToRead - nread;
                offset = offset + nread;
                md.update(dataBytes, 0, nread);
            }
            hashCode = md.digest();
        /*
            StringBuffer sb = new StringBuffer();
            //convert the byte to hex format method 1
            for (int i = 0; i < hashCode.length; i++) {
                sb.append(Integer.toString((hashCode[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Digest(in hex format):: " + sb.toString());

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hashCode.length; i++) {
                String hex = Integer.toHexString(0xff & hashCode[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            System.out.println("Digest(in hex format):: " + hexString.toString());
        */
        } catch (NoSuchAlgorithmException | IOException ex) {
            log.warn(ex.toString());
        }

        if (null != hashCode) {
            return DatatypeConverter.printHexBinary(hashCode).toUpperCase();
        } else {
            return null;
        }
        //log.info("Hash of file " + path.getFileName() + " = " + sb.toString());
        //return sb.toString();
    }

    public static boolean isVideoFile(Path file) throws IOException {
        if (null == ciu) {
            ciu = new ContentInfoUtil();
        }
        ContentInfo info = ciu.findMatch(new File(file.toString()));
        if (info == null) {
            log.info("FILE " + file.toString() + " :     <Unknown content-type>");
            return false;
        }
        // grrrr java cannot do better?
        for (String videoType : videoTypes) {
            if (info.getName().contains(videoType)) {
                log.debug("FILE " + file.toString() + "      IS A VIDEO FILE.");
                return true;
            }
        }
        log.info("FILE " + file.toString() + "      IS NOT A VIDEO FILE: " + info.getName());
        return false;

    }
}

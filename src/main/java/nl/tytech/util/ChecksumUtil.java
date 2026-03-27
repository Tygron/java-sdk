/*******************************************************************************************************************************************
 * Copyright 2006-2026 TyTech B.V., Lange Vijverberg 4, 2513 AC, The Hague, The Netherlands. All rights reserved under the copyright laws of
 * The Netherlands and applicable international laws, treaties, and conventions. TyTech B.V. is a subsidiary company of Tygron Group B.V..
 *
 * This software is proprietary information of TyTech B.V.. You may freely redistribute and use this SDK code, with or without modification,
 * provided you include the original copyright notice and use it in compliance with your Tygron Platform License Agreement.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************************************************************************/
package nl.tytech.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import nl.tytech.util.FileUtils.BufferedFileInputStream;
import nl.tytech.util.logger.TLogger;

/**
 * MD5 Checksum utils, partly based on code from various web sources.
 *
 * @author Maxim Knepfle
 */
public class ChecksumUtil {

    /**
     * Tweaked optimal size for speed in buffer
     */
    private static final int BUFFER_SIZE = 16 * 1024;

    /**
     * MD5 Algorithm
     */
    private static final String MD5 = "MD5";

    public static final String getMD5Checksum(File file, final byte[] buffer) {

        try {
            return getMD5Checksum(new BufferedFileInputStream(file), buffer);
        } catch (FileNotFoundException e) {
            TLogger.exception(e);
            return null;
        }
    }

    public static final String getMD5Checksum(final InputStream fis) {
        return getMD5Checksum(fis, new byte[BUFFER_SIZE]);
    }

    public static final String getMD5Checksum(final InputStream fis, final byte[] buffer) {

        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            int numRead;
            while ((numRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, numRead);
            }
            fis.close();
            byte[] b = digest.digest();

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
            }
            return result.toString();
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    /**
     * Get MD5 checksum as String for given filename
     */
    public static final String getMD5Checksum(String fileName) {
        return getMD5Checksum(new File(fileName), new byte[BUFFER_SIZE]);
    }

    public static final Map<String, String> getMD5ChecksumMap(File[] files) {

        final byte[] buffer = new byte[BUFFER_SIZE];
        Map<String, String> map = HashMap.newHashMap(files.length);
        for (File file : files) {
            map.put(file.getName(), getMD5Checksum(file, buffer));
        }
        return map;
    }

    public static final String getMD5ChecksumOfString(String data) {
        try {
            return getMD5Checksum(new StringBufferInputStream(data));
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }
}

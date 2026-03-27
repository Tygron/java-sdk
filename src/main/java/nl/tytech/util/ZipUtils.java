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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.ws.rs.core.StreamingOutput;
import nl.tytech.util.FileUtils.BufferedFileInputStream;
import nl.tytech.util.logger.TLogger;

/**
 * ZipUtils can compress and decompress objects into an byte[]
 *
 * @author Maxim Knepfle
 */
public class ZipUtils {

    /**
     * Level for compression, between 0 and 9
     *
     * Level 3 seems to give optimal speed at only slightly less compression
     *
     * Source: https://java-performance.info/performance-general-compression/
     */
    public static final int DEFAULT_COMPRESSION = 3;

    public static final void addToZip(ZipOutputStream zos, String fileName, byte[] filebytes) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(filebytes);
        zos.closeEntry();
    }

    public static final void addToZip(ZipOutputStream zos, String rootDirectoryName, String fileName) throws IOException {

        /**
         * Get the files for the given directory
         */
        byte[] buf = new byte[BufferUtils.SIZE];
        File d = new File(rootDirectoryName + fileName);

        if (!d.exists()) {
            TLogger.warning("File: " + d.getPath() + " does not exists, skip zipping it!");

        } else if (d.isDirectory()) {

            for (File file : d.listFiles()) {
                if (file.isDirectory()) {
                    addToZip(zos, rootDirectoryName, fileName + file.getName() + File.separator);
                    continue;
                }

                try (BufferedInputStream fis = new BufferedFileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(fileName + file.getName());
                    zos.putNextEntry(entry);
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        zos.write(buf, 0, len);
                    }
                    zos.closeEntry();
                }
            }
        } else {
            try (BufferedInputStream fis = new BufferedFileInputStream(d.getAbsolutePath())) {
                ZipEntry entry = new ZipEntry(d.getName());
                zos.putNextEntry(entry);
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
            }
        }
    }

    private static final void closeStream(Closeable closable) {

        try {
            if (closable != null) {
                closable.close();
            }
        } catch (Exception e) {
            // ignore closing error
        }
    }

    public static final String compressToJavaObjectBase64String(Object data) {

        byte[] bytes = compressToJavaObjectByteArray(data, true);
        return bytes != null ? Base64.encode(bytes) : null;
    }

    /**
     * Compress object into Java-Object-byte[] using optional GZIP.
     * @param data
     * @return
     */
    public static final byte[] compressToJavaObjectByteArray(Object data, boolean compressed) {

        if (data == null) {
            return null;
        }
        try {
            ByteArrayOutputStream fos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            if (compressed) {
                oos = new ObjectOutputStream(new GZIPOutputStream(fos) {

                    {
                        def.setLevel(DEFAULT_COMPRESSION);
                    }
                });
            } else {
                oos = new ObjectOutputStream(fos);
            }
            oos.writeObject(data);
            oos.flush();
            oos.close();
            fos.close();
            return fos.toByteArray();
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return null;
    }

    public static final void copy(InputStream input, OutputStream output) throws IOException {

        int bytesRead;
        byte[] data = new byte[BufferUtils.SIZE];
        while ((bytesRead = input.read(data)) != -1) {
            output.write(data, 0, bytesRead);
        }
    }

    /**
     * Decompress Java Object from a base 64 string using GZIP.
     * @param data
     * @return
     */
    public static final <T> T decompressJavaObjectBase64String(final String data) {

        if (!StringUtils.containsData(data)) {
            return null;
        }

        try {
            byte[] bytes = Base64.decode(data);
            // call normal byte decompres
            return decompressJavaObjectByteArray(bytes);
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static final <T> T decompressJavaObjectByteArray(byte[] bytes) {

        if (bytes == null) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)))) {
            Object result = ois.readObject();
            return (T) result;

        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    public static final byte[] fromByteStream(InputStream inputStream, boolean compressed) throws IOException {
        return fromByteStream(inputStream, compressed, false);
    }

    public static final byte[] fromByteStream(InputStream inputStream, boolean compressed, boolean zeroDetermined) throws IOException {

        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream outputStream = null;
        InputStream extendedInputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            if (compressed) {
                extendedInputStream = new GZIPInputStream(inputStream);
            } else {
                extendedInputStream = inputStream;
            }

            int nRead;
            byte[] data = new byte[BufferUtils.SIZE];
            while ((nRead = extendedInputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
            if (zeroDetermined) {
                outputStream.write(0);
            }
            outputStream.close();
            return outputStream.toByteArray();

        } finally {
            // always close streams
            closeStream(inputStream);
            closeStream(extendedInputStream);
            closeStream(outputStream);
        }
    }

    public static final void toBuffer(byte[] bytes, ByteBuffer buffer, boolean compressed) {

        InputStream inputStream = null;
        try {
            if (compressed) { // stream bytes
                inputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            } else {
                inputStream = new ByteArrayInputStream(bytes);
            }

            int nRead; // copy bytes to buffer
            byte[] data = new byte[BufferUtils.SIZE];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.put(data, 0, nRead);
            }

        } catch (Exception e) {
            TLogger.exception(e);

        } finally {
            // always close streams
            closeStream(inputStream);
        }
    }

    public static final void toByteStream(InputStream inputStream, OutputStream outputStream, boolean compressed) {
        toByteStream(inputStream, outputStream, compressed ? DEFAULT_COMPRESSION : null);
    }

    public static final void toByteStream(InputStream inputStream, OutputStream outputStream, Integer compressionLevel) {

        if (outputStream == null || inputStream == null) {
            return;
        }

        OutputStream extOutputStream = null;
        try {
            if (compressionLevel != null) {
                extOutputStream = new GZIPOutputStream(outputStream) {

                    {
                        def.setLevel(MathUtils.clamp(compressionLevel, Deflater.NO_COMPRESSION, Deflater.BEST_COMPRESSION));
                    }
                };
            } else {
                extOutputStream = outputStream;
            }

            int nRead;
            byte[] data = new byte[BufferUtils.SIZE];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                extOutputStream.write(data, 0, nRead);
            }
            extOutputStream.flush();
            extOutputStream.close();

        } catch (IOException iox) {
            TLogger.warning("IO Error during transport: " + iox.getMessage());

        } catch (Exception e) {
            TLogger.exception(e);

        } finally {
            // always close streams
            closeStream(inputStream);
            closeStream(extOutputStream);
            closeStream(outputStream);
        }
    }

    public static final StreamingOutput toStreamingOutput(byte[] bytes) {
        return toStreamingOutput(new ByteArrayInputStream(bytes), false);
    }

    public static final StreamingOutput toStreamingOutput(File file) {
        return toStreamingOutput(file, false);
    }

    public static final StreamingOutput toStreamingOutput(File file, boolean outputCompressed) {
        return toStreamingOutput(file, false, outputCompressed);
    }

    public static final StreamingOutput toStreamingOutput(File file, boolean inputCompressed, boolean outputCompressed) {

        try {
            BufferedInputStream stream = new BufferedFileInputStream(file);
            return toStreamingOutput(inputCompressed ? new GZIPInputStream(stream) : stream, outputCompressed);
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    public static final StreamingOutput toStreamingOutput(InputStream inputStream, boolean compressed) {
        return os -> toByteStream(inputStream, new BufferedOutputStream(os, BufferUtils.SIZE), compressed);
    }
}

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.EnumSet;
import nl.tytech.core.net.event.InputException;
import nl.tytech.util.logger.TLogger;

/**
 * File Utility Functions
 *
 * @author Maxim Knepfle
 */
public class FileUtils {

    /**
     * Wrapper around: new BufferedInputStream(new FileInputStream(file), BufferUtils.SIZE)
     */
    public static final class BufferedFileInputStream extends BufferedInputStream {

        /**
         * Wrapper around: new BufferedInputStream(new FileInputStream(file), BufferUtils.SIZE)
         */
        public BufferedFileInputStream(File file) throws FileNotFoundException {
            super(new FileInputStream(file), BufferUtils.SIZE);
        }

        /**
         * Wrapper around: new BufferedInputStream(new FileInputStream(new File(fileName)), BufferUtils.SIZE)
         */
        public BufferedFileInputStream(String fileName) throws FileNotFoundException {
            this(fileName != null ? new File(fileName) : null);
        }
    }

    /**
     * Wrapper around: new BufferedOutputStream(new FileOutputStream(file), BufferUtils.SIZE)
     */
    public static final class BufferedFileOutputStream extends BufferedOutputStream {

        /**
         * Wrapper around: new BufferedOutputStream(new FileOutputStream(file), BufferUtils.SIZE)
         */
        public BufferedFileOutputStream(File file) throws FileNotFoundException {
            super(new FileOutputStream(file), BufferUtils.SIZE);
        }

        /**
         * Wrapper around: new BufferedOutputStream(new FileOutputStream(new File(fileName)), BufferUtils.SIZE)
         */
        public BufferedFileOutputStream(String fileName) throws FileNotFoundException {
            this(fileName != null ? new File(fileName) : null);
        }
    }

    private static final class CopyDirVisitor extends SimpleFileVisitor<Path> {

        private Path fromPath;
        private Path toPath;

        private CopyDirVisitor(Path fromPath, Path toPath) {
            this.fromPath = fromPath;
            this.toPath = toPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path targetPath = toPath.resolve(fromPath.relativize(dir));
            if (!Files.exists(targetPath)) {
                Files.createDirectory(targetPath);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.copy(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }
    }

    public static final boolean containsDirectories(String directory) {

        File d = new File(directory);
        // bail out if directory does not exist
        if (!d.exists()) {
            return false;
        }
        // bail out if it's a file
        if (!d.isDirectory()) {
            return false;
        }

        // iterate over entries and check for dirs
        for (String fileEntry : d.list()) {
            File file = new File(directory + File.separator + fileEntry);
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public static final boolean copy(File source, File target) {

        try {
            if (!source.exists()) {
                TLogger.warning("Cannot copy source file: " + source + " since it does not exist.");
                return false;
            }
            if (!source.canRead()) {
                TLogger.severe("Cannot copy source file: " + source + " since it exists but cannot be read.");
                return false;
            }
            if (source.isDirectory() && !target.exists()) {
                target.mkdirs();
            }

            if (source.isDirectory()) {
                // do the walker for Trees!
                Files.walkFileTree(source.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                        new CopyDirVisitor(source.toPath(), target.toPath()));
            } else {
                // copy single file
                Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (Exception e) {
            TLogger.exception(e);
            return false;
        }
    }

    public static final boolean copy(String source, String target) {
        return copy(new File(source), new File(target));
    }

    public static final boolean createDirectory(File dir) {

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                return true;
            } else {
                TLogger.warning("Failed to create missing filedir: " + dir.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    public static final boolean createDirectory(String path) {
        return createDirectory(new File(path));
    }

    public static final boolean deleteDirectory(File path) {

        boolean result = true;
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (!deleteDirectory(files[i])) {
                        result = false;
                    }
                } else {
                    if (!files[i].delete()) {
                        result = false;
                    }
                }
            }
        }
        if (!path.delete()) {
            result = false;
        }
        return result;
    }

    public static final boolean deleteDirectory(String path) {
        return deleteDirectory(new File(path));
    }

    public static final boolean deleteFile(File file) {
        return file != null && file.exists() && file.delete();
    }

    public static final boolean deleteFiles(Collection<File> files) {

        boolean succes = true;
        for (File file : files) {
            succes &= deleteFile(file);
        }
        return succes;
    }

    public static final boolean exists(String location) {
        File file = new File(location);
        return file.exists();
    }

    public static final long getByteSize(File dir) {

        long byteCount = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                byteCount += file.isFile() ? file.length() : getByteSize(file);
            }
        }
        return byteCount;
    }

    public static final long getByteSize(String location) {
        File file = new File(location);
        return file.exists() ? getByteSize(file) : 0;
    }

    public static final String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static final String getExtension(String filename) {

        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return StringUtils.EMPTY; // no extension found
        }
        return filename.substring(dotIndex);
    }

    public static final String getExtensionWithoutDot(File file) {
        return getExtensionWithoutDot(file.getName());
    }

    public static final String getExtensionWithoutDot(String fileName) {

        String extension = getExtension(fileName);
        if (extension.length() > 1) {
            return extension.substring(1, extension.length());
        }
        return extension;
    }

    public static final String getName(String path) {

        int index = path != null ? path.lastIndexOf(File.separatorChar) : -1;
        return index >= 0 ? path.substring(index + 1) : path;
    }

    public static final String getWithoutExtension(File file) {
        return getWithoutExtension(file.getName());
    }

    /**
     * e.g. "testfile.txt" is returned as "testfile".
     *
     * @param fileName
     * @return
     */
    public static final String getWithoutExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static final boolean move(File source, File target) {

        try {
            if (!source.exists() || !source.canWrite()) {
                TLogger.severe("Cannot move source file: " + source);
                return false;
            }
            if (source.isDirectory() && !target.exists()) {
                target.mkdirs();
            }
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            TLogger.exception(e);
            return false;
        }
    }

    public static final boolean move(String source, String target) {
        return move(new File(source), new File(target));
    }

    /**
     * Reads a File to byte[]. Will return null on files > 2 GB, security issues or other generic IO issues
     *
     * @param file
     * @return
     */
    public static final byte[] toByteArray(File file) {
        try {
            return toByteArrayIO(file);
        } catch (IOException io) {
            TLogger.exception(io);
            return null;
        }
    }

    /**
     * Reads a File to byte[]. Will return null on files > 2 GB, security issues or other generic IO issues
     *
     * @param file
     * @return
     */
    public static final byte[] toByteArrayIO(File file) throws IOException {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (OutOfMemoryError error) {
            throw new InputException("File too large, max 2GB allowed.");
        } catch (IOException ie) {
            throw ie;
        } catch (Exception e) {
            TLogger.exception(e);
            return null;
        }
    }

    /**
     * Checks the location of the file. Creates the directory when it's missing.
     * @param file name and location of the file.
     * @return the newly optionally incremented file as a File instance (or null when it fails)
     */
    public static final File validateLocation(String file) {
        return validateLocation(file, false);
    }

    /**
     * Checks the location of the file. Creates the directory when it's missing and optionally increment the file name number if it already
     * exists.
     * @param file name and location of the file.
     * @param boolean whether to increment the filename or not
     * @return the newly optionally incremented file as a File instance (or null when it fails)
     */
    public static final File validateLocation(String file, boolean increment) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        File localFile = new File(file);
        boolean existed = localFile.exists();

        if (!createDirectory(localFile)) {
            return null;
        }

        int count = 2;
        File newFile = new File(file);
        while (existed && newFile.exists() && increment) {
            String extension = FileUtils.getExtension(localFile);
            newFile = new File(localFile.getPath().substring(0, localFile.getPath().length() - extension.length()) + count + extension);
            count++;
        }

        return newFile;
    }

    public static final void writeByteArray(File file, byte[] byteArray) {

        // store the bytes Array to the given file
        try (BufferedOutputStream bos = new BufferedFileOutputStream(file)) {
            bos.write(byteArray);
        } catch (Exception e) {
            TLogger.exception(e);
        }
    }

    public static final void writeFile(File targetFile, InputStream is) throws IOException {
        writeFile(targetFile, is, false);
    }

    public static final void writeFile(File targetFile, InputStream is, boolean compress) throws IOException {
        ZipUtils.toByteStream(is, new BufferedFileOutputStream(targetFile), compress);
    }

    public static final boolean writeString(File file, String contents) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
            writer.write(contents);
            return true;
        } catch (IOException e) {
            TLogger.exception(e);
            return false;
        }
    }

    public static final boolean writeString(String filePath, String contents) {
        return writeString(new File(filePath), contents);
    }
}

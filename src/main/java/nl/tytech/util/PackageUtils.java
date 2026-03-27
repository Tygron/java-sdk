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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import nl.tytech.util.FileUtils.BufferedFileInputStream;
import nl.tytech.util.RestManager.TWebApplicationException;
import nl.tytech.util.logger.TLogger;

/**
 * @author Maxim Knepfle
 */
public class PackageUtils {

    public static final String SLASH = "/";

    public static final void addURLToClassPath(URL url) {

        try {
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);
            method.invoke(urlClassLoader, new Object[] { url });

            TLogger.info("Added " + url.getFile() + " to classpath");
        } catch (Exception e) {
            TLogger.exception(e, "Failed to add" + url.getFile() + " to classpath.");
        }
    }

    /**
     * checks if a file/directory exists
     */
    public static final boolean exists(File f) {
        try {
            return f.exists();
        } catch (SecurityException e) {
            // we can ignore this, if there is a security exception, then we can't read it can we?
        }
        return false;
    }

    /**
     * Corrects the file's path for different platforms and with or without working directory.
     *
     * @param filePath
     * @return
     */
    public static final String fixFileName(String filePath) {

        // return when empty string when null
        if (filePath == null) {
            TLogger.warning("File path is null, this might be incorrect.");
            filePath = StringUtils.EMPTY;
        }

        // set correct package name first
        if (filePath.contains("\\")) {
            filePath = filePath.replace('\\', '/');
        }

        return filePath;
    }

    /**
     * Corrects the package name for different platform and or java dot naming.
     *
     * @param packageName
     * @return
     */
    public static final String fixPackageName(String packageName) {

        packageName = fixFileName(packageName);

        // fix missing slash
        if (!packageName.endsWith("/")) {
            packageName = packageName + "/";
        }
        return packageName;
    }

    public static final byte[] getBytesFromResource(String resourceLocation) {

        try {
            URL url = PackageUtils.getURL(resourceLocation);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipUtils.toByteStream(url.openStream(), outputStream, false);
            return outputStream.toByteArray();

        } catch (Exception e) {
            TLogger.exception(e, "Failed to bytes for resource: " + resourceLocation);
            return null;
        }
    }

    private static final List<String> getCandidatesFromFile(URL url, String packageName) {

        List<String> candidates = new ArrayList<>();
        try {

            List<File> directories = new ArrayList<>();
            directories.add(new File(URLDecoder.decode(url.getPath(), StringUtils.DEFAULT_ENCODING.name())));

            // For every directory identified capture all the files
            for (File directory : directories) {
                if (isDirectory(directory)) {
                    if (!exists(directory)) {
                        TLogger.warning(packageName + " (" + directory.getPath() + ") does not appear to be a valid package");
                    } else {
                        // Get the list of the files contained in the package
                        for (String fileName : directory.list()) {
                            // complete the name
                            String completeName = packageName + fileName;
                            candidates.add(completeName);
                        }
                    }
                }
            }

        } catch (Exception e) {
            TLogger.exception(e);
        }
        return candidates;
    }

    private static final List<String> getCandidatesFromJar(URL url, String packageName) {

        List<String> candidates = new ArrayList<>();
        try {
            // must be a jar resource
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            JarFile jar = jarConnection.getJarFile();

            // walk the jar file
            for (JarEntry entry : Collections.list(jar.entries())) {
                String name = entry.getName();
                // only add when from correct package
                if (name.startsWith(packageName)) {
                    candidates.add(name);
                }
            }
        } catch (Exception e) {
            TLogger.exception(e);
        }
        return candidates;
    }

    /**
     * list all the FILES in a directory.
     *
     * @param directory
     * @param recurse
     * @return
     */
    public static final List<File> getDirectoryListing(String directory, boolean recursive) {
        return getDirectoryListing(directory, recursive, false);
    }

    public static final List<File> getDirectoryListing(String directory, boolean recursive, boolean includeDirs) {

        // bail out if directory does not exist or if it's a file
        File d = new File(directory);
        if (!exists(d) || !isDirectory(d)) {
            return null;
        }

        // iterate over entries and recurse into subdirectories if needed
        List<File> r = new ArrayList<>();
        for (String fileEntry : d.list()) {
            File file = new File(directory + File.separator + fileEntry);
            if (isDirectory(file)) {
                if (recursive) {
                    r.addAll(getDirectoryListing(file.getPath(), recursive, includeDirs));
                }
                if (includeDirs) {
                    r.add(file);
                }
            } else {
                r.add(file);
            }
        }
        return r;
    }

    /**
     * Method retrieves the names of all classes in the given package. The package can be located in a jar or directly on the file system.
     *
     * @param packageName Name of package
     * @param recursive Whether to also check the subpackages
     * @return List of class names
     */
    public static final List<String> getPackageClassNames(String packageName) {

        packageName = fixPackageName(packageName);
        // replace dot with slashes
        if (packageName.contains(".")) {
            packageName = packageName.replace('.', '/');
        }

        List<String> contents = getPackageContents(packageName);
        List<String> candidates = new ArrayList<>();

        for (String candidate : contents) {
            // candidates must be classes and not sub classes
            if (candidate.endsWith(".class")) {

                // change back to dots
                candidate = candidate.replace("/", ".");

                // remove .class at end
                candidate = candidate.substring(0, candidate.length() - 6);

                // add to classes, when unique
                if (!candidates.contains(candidate)) {
                    candidates.add(candidate);
                }
            }
        }
        return candidates;
    }

    /**
     * Helper method that does the actual retrieval from the package.
     *
     * @param packageName
     * @param recursive Whether to also check the subpackages
     * @return
     */
    private static final List<String> getPackageContents(String packageName) {

        // This will hold a list of directories matching the pckgname.
        // There may be more than one if a package is split over multiple
        // jars/paths
        List<String> candidates = new ArrayList<>();

        try {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                TLogger.severe("Cannot get class loader, return empty list!");
                return candidates;
            }

            // Ask for all resources for the path
            List<URL> resourceURLs = Collections.list(classLoader.getResources(packageName));

            // try file
            File file = new File(packageName);
            if (exists(file)) {
                resourceURLs.add(file.toURI().toURL());
            } else {
                File file2 = new File("/" + packageName);
                if (exists(file2)) {
                    resourceURLs.add(file2.toURI().toURL());
                }
            }

            for (URL url : resourceURLs) {
                // if not a jar it must be a directory
                if (url.getProtocol().equalsIgnoreCase("jar")) {
                    candidates.addAll(getCandidatesFromJar(url, packageName));

                } else if (url.getProtocol().equalsIgnoreCase("file")) {
                    candidates.addAll(getCandidatesFromFile(url, packageName));

                } else {
                    TLogger.severe("Cannot spider resource of type: " + url.getProtocol());
                }
            }

        } catch (Exception exp) {
            TLogger.exception(exp);
        }
        return candidates;
    }

    /**
     * Method retrieves the names of all non-hidden files in the given package. The package can be located in a jar or directly on the file
     * system.
     *
     * @param packageName Name of package
     * @param recursive Whether to also check the subpackages
     * @return List of file names
     */
    public static final List<String> getPackageListing(String packageName) {

        // fill with entries from the filesystem
        List<String> candidates = new ArrayList<>();

        packageName = fixPackageName(packageName);
        List<String> contents = getPackageContents(packageName);

        for (String item : contents) {
            String[] splits = item.split(packageName, 2);
            if (splits.length > 1) {
                String name = splits[1];
                splits = name.split("/", 2);
                if (splits.length > 0) {
                    name = splits[0];
                    // add to classes, when unique and not hidden (.)
                    if (!candidates.contains(name) && !name.equals(StringUtils.EMPTY) && !name.startsWith(".")) {
                        candidates.add(name);
                    }
                }
            }
        }
        return candidates;
    }

    public static final String getParentURL(String url) {

        try {
            if (!StringUtils.containsData(url)) {
                return null;
            }
            url = new StringBuilder(url.trim()).reverse().toString();
            while (url.startsWith(SLASH)) {
                url = url.replaceFirst(SLASH, StringUtils.EMPTY);
            }
            int index = url.indexOf(SLASH);
            if (index < 0) {
                return null;
            }
            url = url.substring(index, url.length());
            while (url.startsWith(SLASH)) {
                url = url.replaceFirst(SLASH, StringUtils.EMPTY);
            }
            return new StringBuilder(url).reverse().toString();

        } catch (Exception e) {
            TLogger.warning("unable to convert url");
            return null;
        }
    }

    /**
     * Read a string from the given resource.
     * @param resourceLocation
     * @return
     */
    public static final String getStringFromResource(String resourceLocation) {

        InputStream stream = null;
        boolean web = false;

        try {
            URL url = PackageUtils.getURL(resourceLocation);
            if (url == null) {
                return null;
            }

            if (url.getProtocol().equalsIgnoreCase("jar")) {
                stream = StringUtils.class.getClassLoader().getResourceAsStream(resourceLocation);
            } else if (url.getProtocol().equalsIgnoreCase("http") || url.getProtocol().equalsIgnoreCase("https")) {
                web = true;
                URLConnection con = url.openConnection();
                con.connect();
                stream = con.getInputStream();
            } else {
                stream = new BufferedFileInputStream(url.toURI().getPath());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StringUtils.DEFAULT_ENCODING));
            String record;
            StringBuilder builder = new StringBuilder();
            String newLine = System.lineSeparator();

            while ((record = br.readLine()) != null) {
                builder.append(record);
                builder.append(newLine);
            }
            return builder.toString().trim();

        } catch (FileNotFoundException fe) {
            return null; // return empty

        } catch (IOException e) {
            if (web) {
                TLogger.networkNotification(new TWebApplicationException(TStatus.CONNECTION_FAILED, e.getMessage(), resourceLocation));
            } else {
                TLogger.exception(e); // must be a serious bug
            }
        } catch (Exception e) {
            TLogger.exception(e); // must be a serious bug

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    TLogger.warning("Error closing connection stream: " + e.getMessage());
                } finally {
                    stream = null;
                }
            }
        }
        return null;
    }

    /**
     * Get the URI for a given path string. When the method returns NULL, the path does not exist.
     *
     * @param path
     * @return
     */
    public static final URI getURI(String path) {
        try {
            return getURL(path).toURI();
        } catch (URISyntaxException e) {
            TLogger.exception(e);
            return null;
        }
    }

    /**
     * Get the URL for a given path string. When the method returns NULL, the path does not exist.
     *
     * @param path
     * @return
     */
    public static final URL getURL(String path) {

        path = fixFileName(path);

        /**
         * 1: Try from JAR contents
         */
        ClassLoader classLoader = PackageUtils.class.getClassLoader();
        URL locationURL = classLoader.getResource(path);
        if (locationURL != null) {
            return locationURL;
        }
        locationURL = ClassLoader.getSystemResource(path);
        if (locationURL != null) {
            return locationURL;
        }

        /**
         * 2: Try from FILE contents
         */
        try {
            File file = new File(path);
            if (exists(file)) {
                return file.toURI().toURL();
            }
        } catch (Exception e) {
            TLogger.exception(e);
        }

        /**
         * 3: Maybe already a valid url?
         */
        if (path.startsWith("http")) {
            // web url
            try {
                return new URI(path).toURL();
            } catch (Exception e) {
                TLogger.exception(e);
            }
        }
        return null;
    }

    /**
     * checks if a file is directory
     */
    public static final boolean isDirectory(File f) {

        try {
            return f.isDirectory();
        } catch (SecurityException e) {
            // we can ignore this, if there is a security exception, then we can't read it can we?
        }
        return false;
    }

    /**
     * When true the path is writable and in file system (not resource package).
     *
     * @param path
     * @return
     */
    public static final boolean isWritable(final String path) {

        try {
            File file = new File(path);
            return file.exists() && file.canWrite();
        } catch (SecurityException e) {
            // means false, do nothing
            TLogger.warning("SecurityException while testing [" + path + "] for writability.");
        }
        return false;
    }

    // do not instantiate
    private PackageUtils() {

    }
}

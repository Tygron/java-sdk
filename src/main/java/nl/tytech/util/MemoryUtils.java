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

/**
 * This Util class for memory printing
 *
 * @author Maxim Knepfle
 */
public class MemoryUtils {

    public static final long KB = 1024l;

    public static final long MB = 1024l * KB;

    public static final long GB = 1024l * MB;

    public static final long TB = 1024l * GB;

    public static final long PB = 1024l * TB;

    public static final String toString(long bytes) {

        if (bytes < KB) {
            return bytes + " B";
        }
        if (bytes < MB) {
            return Math.round(bytes / (double) KB) + " KB";
        }
        if (bytes < GB) {
            return Math.round(bytes / (double) MB) + " MB";
        }
        if (bytes < TB) {
            return Math.round(bytes / (double) GB) + " GB";
        }
        if (bytes < PB) {
            return Math.round(bytes / (double) TB) + " TB";
        }
        return Math.round(bytes / (double) PB) + " PB";
    }

    public static final String toStringMB(long megabytes) {
        return megabytes == 0 ? "0 MB" : toString(megabytes * MB);
    }
}

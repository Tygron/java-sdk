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
 * Thread related utils.
 * @author Maxim Knepfle
 *
 */
public final class ThreadUtils {

    public static final String getReportStackTrace() {
        try {
            return getStackTrace(new Exception());
        } catch (Exception e) {
            return "Unable to get StackTrace";
        }
    }

    public static final String getStackTrace(Throwable exp) {

        try {
            // Recreate the stack trace in a String
            StringBuilder builder = new StringBuilder();
            StackTraceElement[] stackArray = exp.getStackTrace();

            for (int i = 0; i < stackArray.length; i++) {
                builder.append(stackArray[i]);
                if (i < stackArray.length - 1) {
                    builder.append("\n");
                }
            }
            return builder.toString();

        } catch (Exception e) {
            return "Unable to get StackTrace";
        }
    }

    public static final void sleepInterruptible(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
        }
    }

    private ThreadUtils() {

    }
}

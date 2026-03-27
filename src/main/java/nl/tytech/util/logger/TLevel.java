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
package nl.tytech.util.logger;

import java.util.logging.Level;

/**
 * Extra log severity settings
 *
 * @author Maxim Knepfle
 */
public class TLevel extends Level {

    private static final long serialVersionUID = 7727487931637590482L;

    /**
     * SHOWSTOPPER is a message level indicating a serious failure. Inmidiate stop of program.
     *
     * In general SHOWSTOPPER messages should describe events that are of considerable importance and which will prevent normal program
     * execution. They should be reasonably intelligible to end users and to system administrators. This level is initialized to
     * <CODE>1100</CODE>.
     */
    public static final TLevel SHOWSTOPPER = new TLevel("SHOWSTOPPER", 1100);

    /**
     * CRITICAL is a message level indicating a serious failure, just one step below showstoppers.
     *
     * <CODE>1090</CODE>.
     */
    public static final TLevel CRITICAL = new TLevel("CRITICAL", 1090);

    /**
     * SECURITY_NOTIFICATION means that it will be emailed/notified to RD, but is not a bug, e.g. security breach attempts
     *
     * <CODE>1050</CODE>.
     */
    public static final TLevel SECURITY_NOTIFICATION = new TLevel("SECURITY_NOTIFICATION", 1050);

    /**
     * NETWORK_NOTIFICATION means that it will be emailed/notified to RD, but is not a bug, e.g. network failure
     *
     * <CODE>1040</CODE>.
     */
    public static final TLevel NETWORK_NOTIFICATION = new TLevel("NETWORK_NOTIFICATION", 1040);

    /**
     * LOG level just above info, notification triggered by user
     *
     * <CODE>850</CODE>.
     */
    public static final TLevel NOTICE = new TLevel("NOTICE", 850);

    /**
     * DEBUG only logged in NOT release mode
     *
     * <CODE>750</CODE>.
     */
    public static final TLevel DEBUG = new TLevel("DEBUG", 750);

    public static synchronized Level parse(String name) {

        if (DEBUG.getName().equals(name)) {
            return DEBUG;
        } else if (NETWORK_NOTIFICATION.getName().equals(name)) {
            return NETWORK_NOTIFICATION;
        } else if (SECURITY_NOTIFICATION.getName().equals(name)) {
            return SECURITY_NOTIFICATION;
        } else if (CRITICAL.getName().equals(name)) {
            return CRITICAL;
        } else if (SHOWSTOPPER.getName().equals(name)) {
            return SHOWSTOPPER;
        } else {
            return Level.parse(name);
        }
    }

    private TLevel(String name, int value) {
        super(name, value);
    }
}

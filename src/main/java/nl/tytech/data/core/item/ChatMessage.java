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
package nl.tytech.data.core.item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 *
 * Messages between sessions in domains.
 *
 * @author Maxim Knepfle
 *
 */
public class ChatMessage extends Item {

    public enum Type {
        SYSTEM, MESSAGE
    }

    private static final long serialVersionUID = 6153041251983174279L;

    @XMLValue
    private String message = StringUtils.EMPTY;

    @XMLValue
    private String fullName = StringUtils.EMPTY;

    @XMLValue
    private String userName = StringUtils.EMPTY;

    @XMLValue
    private Type type = null;

    @XMLValue
    private long time = Item.NONE;

    public ChatMessage() {

    }

    public ChatMessage(String userName, String fullName, String message, Type type, long time) {
        this.userName = userName;
        this.fullName = fullName;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        if (time > 0) {
            calendar.setTimeInMillis(time);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ");
        return sdf.format(calendar.getTime());
    }

    public String getFullName() {
        return fullName;
    }

    public String getMessage() {
        return message;
    }

    public String getText() {
        return type == ChatMessage.Type.MESSAGE ? getFullName() + ": " + StringUtils.capitalizeFirstLetter(message) : message;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return getText();
    }
}

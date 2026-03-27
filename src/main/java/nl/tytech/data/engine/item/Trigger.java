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
package nl.tytech.data.engine.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.util.StringUtils;

/**
 * API Trigger
 *
 * @author Maxim Knepfle
 */
public class Trigger extends UniqueNamedItem implements ActiveItem {

    public enum Method {
        GET, POST
    }

    public enum Type {
        TQL, EVENT
    }

    /** Generated serialVersionUID */
    private static final long serialVersionUID = 5059475154747750959L;

    @XMLValue
    private boolean active = true;

    @XMLValue
    private Timing timing = Timing.BEFORE;

    @XMLValue
    private Method method = Method.GET;

    @XMLValue
    private Type type = Type.TQL;

    @XMLValue
    private String url = StringUtils.EMPTY;

    @XMLValue
    private String warnings = StringUtils.EMPTY;

    @XMLValue
    private int timeoutMS = (int) (10 * Moment.SECOND);

    @XMLValue
    private long calcTimeMS = 0;

    public Trigger() {

    }

    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    public Method getMethod() {
        return method;
    }

    public int getTimeoutMS() {
        return timeoutMS;
    }

    public Timing getTiming() {
        return timing;
    }

    public Type getType() {
        return type;
    }

    public String getURL() {
        return url;
    }

    public String getWarnings() {
        return warnings;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCalcTimeMS(long calcTimeMS) {
        this.calcTimeMS = calcTimeMS;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setTimeoutMS(int timeoutMS) {
        this.timeoutMS = timeoutMS;
    }

    public void setTiming(Timing timing) {
        this.timing = timing;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return getName();
    }
}

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
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Stakeholder;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * @author Jurrian Hartveldt
 */
public abstract class GeoLink extends Item {

    private static final long serialVersionUID = 3227809616030984800L;

    private static final int DEFAULT_PRIORITY = 50;

    public static final GeoLink getBestPriority(GeoLink one, GeoLink two) {
        if (one == null) {
            return two;
        } else if (two == null) {
            return one;
        } else {
            return two.getPriority() > one.getPriority() ? two : one;
        }
    }

    @XMLValue
    private int priority = DEFAULT_PRIORITY;

    public abstract Stakeholder.Type getDefaultStakeholderType();

    public abstract String getName();

    public Integer getPriority() {
        return priority;
    }

    public boolean isTree() {
        return false;
    }

    public abstract boolean isWater();

    public void setPriority(int priority) {
        if (priority < 0) {
            TLogger.warning("Warning: cannot set GeoLink priority with id '" + this.getID() + "' to value: " + priority
                    + ". Keeping previous value '" + this.priority + "' instead.");
            return;
        }
        this.priority = priority;
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY + priority + ") " + getName();
    }
}

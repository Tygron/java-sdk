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
package nl.tytech.data.engine.other;

import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.other.NamedItem;

/**
 * Action: A Stakeholder can select a ActionMenu to build, then he can select an Action.
 *
 * @author Maxim Knepfle
 */
public interface Action extends NamedItem {

    public static final String ACTION_IMAGE_LOCATION = "Gui/Images/Actions/";
    public static final String DEFAULT_IMAGE = "empty.png";

    /**
     * Get the basic description for this action.
     * @return
     */
    public String getDescription();

    /**
     * Return action ID or Item.NONE when it has none.
     * @return
     */
    @Override
    public Integer getID();

    /**
     * The name of the image for this action.
     * @return
     */
    public String getImageLocation();

    /**
     * The MapLink of this action.
     * @return
     */
    public MapLink getMapLink();

    /**
     * Get my name
     * @return
     */
    @Override
    public String getName();

    /**
     * When true this action is available for this stakeholder.
     * @return
     */
    public boolean isBuildable();

    /**
     * Is the location of the action fixed on a certain location (e.g. a measure) or is the location defined by the user (e.g. a new house).
     * @return
     */
    public boolean isFixedLocation();

}

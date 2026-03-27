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
package nl.tytech.data.engine.serializable;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.data.engine.other.ModelObject;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.ColorUtils;
import nl.tytech.util.color.TColor;

/**
 * PopupModelType defines the visualization model used for a popup.
 *
 * @author Frank Baars
 */
public enum PopupModelType implements ModelObject {

    /**
     * WARNING: This enum is referenced by ordinal value via attribute POPUP_TYPE (do not sort values)
     */
    //
    APPROVED("approved", new TColor(36, 128, 0)),
    //
    ARROW("arrow", ColorUtils.COLOR_INTERFACE),
    //
    BUY("buy", ColorUtils.COLOR_INTERFACE),
    //
    CONNECT_ON("connect_on", new TColor(36, 128, 0)),
    //
    CONNECT_OFF("connect_off", new TColor(179, 26, 26)),
    //
    CONSTRUCTING("constructing", ColorUtils.COLOR_INTERFACE),
    //
    ELEVATION("terrain_elevation", ColorUtils.COLOR_INTERFACE),
    //
    DECLINED("declined", new TColor(179, 26, 26)),
    //
    DEMOLISHING("demolishing", ColorUtils.COLOR_INTERFACE),
    //
    WAITING("waiting", ColorUtils.COLOR_INTERFACE),
    //
    QUESTION_MARK("questionmark", ColorUtils.COLOR_INTERFACE),
    //
    TRAFFIC_NOISE("traffic_noise", ColorUtils.COLOR_INTERFACE),
    //
    WATER_CRANE("water_crane", ColorUtils.COLOR_INTERFACE),
    //
    WATER_ARROW("waterpeil", ColorUtils.COLOR_INTERFACE),
    //
    WATER_WEIR("weir", ColorUtils.COLOR_INTERFACE),
    //
    LIGHT_BULB("light_bulb", ColorUtils.COLOR_INTERFACE),
    //
    LIGHTNING("lightning", ColorUtils.COLOR_INTERFACE),
    //
    BAR_CHART("bar_chart", new TColor(36, 128, 0)),
    //
    EXCLAMATION("exclamation", new TColor(179, 26, 26));

    public static final String COMBINED_ICON = "combined";

    private static final String ICON_LOCATION = "Textures/Popups/Icon/";

    private static final String ICON_MODEL_LOCATION = "Models/Popups/circle.j3o";

    public static final String MODEL_LOCATION = "Models/Popups/";

    public static final PopupModelType[] VALUES = values();

    public static final String getCombinedIconTextureLocation(String format) {
        return ICON_LOCATION + COMBINED_ICON + "." + format;
    }

    private final String assetName;

    private final TColor defaultColor;

    private PopupModelType(String assetName, TColor defaultColor) {
        this.assetName = assetName;
        this.defaultColor = defaultColor;
    }

    @Override
    public List<Addition> getAdditions() {
        return new ArrayList<>();
    }

    public TColor getDefaultColor() {
        return defaultColor;
    }

    @Override
    public String getFileName() {
        return MODEL_LOCATION + assetName;
    }

    public String getIconTextureLocation(boolean combined) {
        return ICON_LOCATION + (combined ? COMBINED_ICON : assetName) + ".dds";
    }

    public String getModelLocation(boolean icon) {
        return icon ? ICON_MODEL_LOCATION : MODEL_LOCATION + assetName + ".j3o";
    }

    @Override
    public String getName() {
        return StringUtils.capitalizeWithSpacedUnderScores(this);
    }

    @Override
    public ParticleEmitterCoordinatePair getPair(Integer id) {
        return null;
    }

    @Override
    public List<ParticleEmitterCoordinatePair> getPairs() {
        return new ArrayList<>();
    }

    @Override
    public Show getShow() {
        return Show.FAR;
    }

    @Override
    public boolean hasRoots() {
        return false;
    }

    @Override
    public boolean isAlpha() {
        return false;
    }

    @Override
    public boolean skipLOD() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }
}

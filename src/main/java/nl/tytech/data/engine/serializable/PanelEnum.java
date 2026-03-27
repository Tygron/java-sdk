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

import java.util.HashMap;
import java.util.Map;
import nl.tytech.core.structure.PanelType;

/**
 * PanelEnum: defines the available panels for the user in the client viewer.
 *
 * @author Maxim Knepfle
 */
public enum PanelEnum implements PanelType {

    //
    ATTRIBUTE_ACTION_PANEL("AttributeActionPanelController"),

    //
    BUDGET_PERSONAL_PANEL("BudgetPanelController"),

    //
    CATEGORY_PANEL("CategoryPanelController"),

    //
    ERROR_PANEL("ErrorPanelController"),

    //
    WEB_BROWSER_PANEL("WebPanelController"),

    //
    FEEDBACK_PANEL("FeedbackPanelController"),

    //
    FLYTHROUGH_PANEL("CinematicPanelController"),

    //
    GLOBAL_EDIT_PANEL("GlobalPanelController"),

    //
    GRID_OPTION_PANEL("GridOptionPanelController"),

    //
    HOVER_PANEL("HoverPanelController"),

    //
    INDICATOR_PANEL("IndicatorPanelController"),

    //
    INDICATOR_PARENT_PANEL("IndicatorParentPanelController"),

    //
    LAND_BUY_PANEL("LandBuyPanelController"),

    //
    LAND_SELL_PANEL("LandSellPanelController"),

    //
    LEFT_MENU_PANEL("LeftMenuPanelController"),

    //
    OVERLAY_LEGEND_PANEL("OverlayLegendPanelController"),

    //
    SELECTION_LEGEND_PANEL("SelectionLegendPanelController"),

    //
    NAVIGATION_PANEL("NavigationPanelController"),

    //
    MENU_PANEL("MenuPanelController"),

    //
    MONEY_TRANSFER_PANEL("MoneyTransferPanelController"),

    //
    LOGO_PANEL("LogoPanelController"),

    //
    POPUP_INFO_PANEL("PopupPanelController"),

    //
    POPUP_ARROW_TOP_PANEL("ArrowPanelController"), //
    POPUP_ARROW_BOTTOM_PANEL("ArrowPanelController"), //
    POPUP_ARROW_RIGHT_PANEL("ArrowPanelController"), //
    POPUP_ARROW_LEFT_PANEL("ArrowPanelController"), //
    POPUP_ARROW_TOP_LEFT_PANEL("ArrowPanelController"), //
    POPUP_ARROW_TOP_RIGHT_PANEL("ArrowPanelController"), //
    POPUP_ARROW_BOTTOM_RIGHT_PANEL("ArrowPanelController"), //
    POPUP_ARROW_BOTTOM_LEFT_PANEL("ArrowPanelController"), //

    LOG_PANEL("LogPanelController"), //

    //
    SETTINGS_PANEL("SettingsPanelController"),

    //
    SWITCH_PANEL("SwitchPanelController"),

    //
    TOPBAR_PANEL("TopPanelController"),

    ACTION_LOG_PANEL("ActionLogPanelController"),

    ZONING_PERMIT_PANEL("ZoningPermitPanelController");

    private static final Map<String, PanelEnum> idMap = new HashMap<>();

    public static final PanelEnum[] VALUES = PanelEnum.values();

    static {
        for (PanelEnum pve : VALUES) {
            idMap.put(pve.getPanelName().toLowerCase(), pve);
        }
    }

    private String panelName;

    private PanelEnum(String name) {
        this.panelName = name;
    }

    public String getPanelName() {
        return panelName;
    }

    /**
     * Back panels are placed on the back of the ui.
     * @return
     */
    public boolean isBackPanel() {
        return this.isBasePanel() || this == OVERLAY_LEGEND_PANEL || this == SELECTION_LEGEND_PANEL;
    }

    /**
     * Base panels are always visible and not related to special functionality like e.e. a message triggers a message panel.
     * @return
     */
    public boolean isBasePanel() {
        return this == TOPBAR_PANEL || this == LEFT_MENU_PANEL || this == HOVER_PANEL || this == FEEDBACK_PANEL || this == NAVIGATION_PANEL
                || this == POPUP_ARROW_TOP_PANEL || this == POPUP_ARROW_BOTTOM_PANEL || this == POPUP_ARROW_RIGHT_PANEL
                || this == POPUP_ARROW_LEFT_PANEL || this == POPUP_ARROW_TOP_LEFT_PANEL || this == POPUP_ARROW_TOP_RIGHT_PANEL
                || this == POPUP_ARROW_BOTTOM_RIGHT_PANEL || this == POPUP_ARROW_BOTTOM_LEFT_PANEL;
    }

    /**
     * Helper panels are base panels or not special functionality related
     */
    public boolean isHelper() {
        return isBasePanel() || this == OVERLAY_LEGEND_PANEL || this == SELECTION_LEGEND_PANEL || this == ACTION_LOG_PANEL
                || this == SWITCH_PANEL;
    }
}

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

import java.util.ArrayList;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.EnumOrderedItem;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.other.Action;
import nl.tytech.util.StringUtils;

/**
 * SpecialOption
 *
 * SpecialOption's enum wrapped in an item.
 *
 * @author Maxim Knepfle
 */
public class SpecialOption extends EnumOrderedItem<SpecialOption.Type> implements Action {

    public enum Group {

        LAND("category_buyland.png", ClientTerms.BUY_LAND),

        SCULPTING("category_waterway.png", ClientTerms.LANDSCAPE),

        FINANCE("category_finance.png", ClientTerms.FINANCE),

        DEMOLISH("category_bulldozer.png", ClientTerms.DEMOLISH);

        private String iconName;
        private ClientTerms clientWord;

        private Group(String iconName, ClientTerms clientWord) {
            this.iconName = iconName;
            this.clientWord = clientWord;
        }

        public String getIconName() {
            return iconName;
        }

        public ClientTerms getLocalisedTerm() {
            return clientWord;
        }
    }

    public enum Type {

        BUY_LAND(Group.LAND, true),

        SELL_LAND(Group.LAND, true),

        DEMOLISH(Group.DEMOLISH),

        REVERT(Group.DEMOLISH),

        DEMOLISH_UNDERGROUND(Group.DEMOLISH),

        RAISE_LAND(Group.SCULPTING),

        SHOW_BUDGETPANEL(Group.FINANCE, true, false),

        SHOW_MONEY_TRANSFER_PANEL(Group.FINANCE, true, false),

        LOWER_LAND(Group.SCULPTING);

        private boolean buildable = true;
        private boolean requiresSelection = true;
        private boolean showSpecialPanel = false;
        private Group group;

        private Type() {

        }

        private Type(Group group) {
            this.group = group;
        }

        private Type(Group group, boolean showSpecialPanel) {
            this.group = group;
            this.showSpecialPanel = showSpecialPanel;
        }

        private Type(Group group, boolean buildable, boolean requiresSelection) {
            this.group = group;
            this.buildable = buildable;
            this.requiresSelection = requiresSelection;
            this.showSpecialPanel = true;
        }

        public Group getGroup() {
            return group;
        }

        public boolean isBuildable() {
            return buildable;
        }

        /**
         * When true this special option demolishes something
         * @return
         */
        public boolean isDemolisher() {
            return this.getGroup() == Group.DEMOLISH;
        }

        public boolean isSelectionRequired() {
            return requiresSelection;
        }

        public boolean isShowSpecialPanel() {
            return showSpecialPanel;
        }

        public boolean isTypeOf(Action option) {
            if (option instanceof SpecialOption special) {
                return special.getType() == this;
            }
            return false;
        }
    }

    private static final long serialVersionUID = 7100716881313651171L;

    private static final String GUI_IMAGES_ACTION_ICONS = "Gui/Images/Actions/Icons/";

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private String description = StringUtils.EMPTY;

    @XMLValue
    @ListOfClass(Stakeholder.Type.class)
    private ArrayList<Stakeholder.Type> defaults = new ArrayList<>();

    @AssetDirectory(GUI_IMAGES_ACTION_ICONS)
    @XMLValue
    private String imageName = DEFAULT_IMAGE;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }

    public Group getGroup() {
        return this.getType().getGroup();
    }

    @Override
    public String getImageLocation() {
        return GUI_IMAGES_ACTION_ICONS + imageName;
    }

    @Override
    public MapLink getMapLink() {
        return MapLink.SPECIAL_OPTIONS;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBuildable() {
        return this.getType().isBuildable();
    }

    public boolean isDefaultOption(Stakeholder stakeholder) {
        return defaults.contains(stakeholder.getType());
    }

    @Override
    public boolean isFixedLocation() {
        return false;
    }

    public boolean isSelectionRequired() {
        return this.getType().isSelectionRequired();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

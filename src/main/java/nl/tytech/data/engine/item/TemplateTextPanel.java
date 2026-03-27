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

import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.other.TemplatePanel;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.StringUtils;

/**
 * Panel that serves as a template for Text panels.
 *
 * @author Maxim Knepfle
 */
public class TemplateTextPanel extends TextPanel implements TemplatePanel {

    private static final long serialVersionUID = 2805451253961720901L;

    private static final String ID_TAG = "_IS_ID";

    @XMLValue
    private MapLink mapLink = MapLink.NEIGHBORHOODS;

    @XMLValue
    private String attribute = StringUtils.EMPTY;

    @XMLValue
    private Relation relation = null;

    @XMLValue
    private boolean useOwner = false;

    @XMLValue
    private boolean autoApplied = false;

    public TemplateTextPanel() {
        super(PanelType.TEMPLATE_TEXT_PANEL);
    }

    public TemplateTextPanel(MapLink mapLink) {
        this();
        this.setMapLink(mapLink);
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public long getCalcTimeMS() {

        long totalCalcTimeMS = 0;
        ItemMap<Panel> panels = getMap(MapLink.PANELS);

        for (Panel panel : panels.values()) {
            if (panel.getType() == PanelType.TEXT_PANEL) {
                ExcelPanel ePanel = (ExcelPanel) panel;
                if (this.getID().equals(ePanel.getParentID())) {
                    totalCalcTimeMS += ePanel.getCalcTimeMS();
                }
            }
        }
        return totalCalcTimeMS;
    }

    @Override
    public MapLink getMapLink() {
        return mapLink;
    }

    @Override
    public Point getPoint() {
        return null;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public String getWarnings() {

        String text = getText();
        if (text.contains(ID_TAG)) {
            for (MapLink mapLink : TemplatePanel.TEMPLATES) {
                if (mapLink != getMapLink() && text.contains(mapLink.getTQLName() + ID_TAG)) {
                    return "WARNING: " + mapLink.getTQLName() + ID_TAG + " not templated!";
                }
            }
        }
        return super.getWarnings();
    }

    @Override
    public boolean hasRelation() {
        return relation != null;
    }

    @Override
    public boolean isAutoApplied() {
        return autoApplied;
    }

    @Override
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public void setAutoApplied(boolean autoApplied) {
        this.autoApplied = autoApplied;
    }

    @Override
    public void setMapLink(MapLink mapLink) {
        this.mapLink = mapLink;
    }

    @Override
    public void setPoint(Point point) {
        throw new UnsupportedOperationException("Template panels are not allow to have a Map Point!");
    }

    @Override
    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    @Override
    public void setUseOwner(boolean useOwner) {
        this.useOwner = useOwner;
    }

    @Override
    public boolean useOwner() {
        return useOwner;
    }
}

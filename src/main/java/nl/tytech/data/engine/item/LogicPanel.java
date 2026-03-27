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

import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.LogicItem;
import nl.tytech.util.StringUtils;

/**
 * Panel that contains calculation logic, e.g. an excelheet or javascript/tql
 *
 * @author Maxim Knepfle
 */
public abstract class LogicPanel extends Panel implements LogicItem {

    private static final String PANEL_TQL_NAME = MapLink.PANELS.getTQLName();

    private static final Pattern STYLE = Pattern.compile("<style>", Pattern.CASE_INSENSITIVE);

    private static final Pattern HEAD = Pattern.compile("<head>", Pattern.CASE_INSENSITIVE);

    private static final Pattern BODY = Pattern.compile("<body>", Pattern.CASE_INSENSITIVE);

    private static final Pattern APP_TOKEN = Pattern.compile("app.token\\(\\)", Pattern.CASE_INSENSITIVE);

    private static final long serialVersionUID = -526843113040439637L;

    @XMLValue
    private MapLink contentMapLink = null;

    @XMLValue
    private Integer contentID = null;

    @XMLValue
    private Integer parentID = null;

    @XMLValue
    @NoDefaultText
    private String warnings = StringUtils.EMPTY;

    @XMLValue
    @NoDefaultText
    protected String text = StringUtils.EMPTY;

    @XMLValue
    private long calcTimeMS = 0;

    @XMLValue
    private Timing updateTiming = Timing.BEFORE;

    @JsonIgnore
    private transient int contentVersion = NONE;

    @JsonIgnore
    private transient boolean logicUpdated = true;

    protected LogicPanel(PanelType type) {
        super(type);
    }

    @Override
    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    @Override
    public Integer getContentID() {
        return contentID;
    }

    @Override
    public final MapLink getContentMapLink() {
        return contentMapLink;
    }

    @Override
    public final String getContentParam() {
        return contentMapLink != null ? contentMapLink.getTQLName() : null;
    }

    @Override
    public final String getMyParam() {
        return PANEL_TQL_NAME;
    }

    public Panel getParent() {
        return getItem(MapLink.PANELS, getParentID());
    }

    public Integer getParentID() {
        return parentID;
    }

    public String getText() {
        return text;
    }

    public Timing getUpdateTiming() {
        return updateTiming;
    }

    public String getWarnings() {

        if (!StringUtils.containsData(warnings)) {
            if (HEAD.matcher(getText()).find()) {
                return "WARNING: <head> tags are not allowed!";
            }
            if (BODY.matcher(getText()).find()) {
                return "WARNING: <body> tags are not allowed!";
            }
            if (APP_TOKEN.matcher(getText()).find()) {
                return "WARNING: Can edit Project via API token!";
            }
        }
        return warnings;
    }

    public boolean hasStyle() {
        return STYLE.matcher(getText()).find();
    }

    @Override
    public boolean isLogicUpdated() {
        return logicUpdated;
    }

    public boolean isTemplated() {
        return parentID != null || contentMapLink != null || contentID != null;
    }

    public void setCalcTimeMS(long calcTime) {
        this.calcTimeMS = calcTime;
    }

    @Override
    public void setLogicUpdated(boolean updated) {
        this.logicUpdated = updated;
    }

    public void setTemplatedContent(Integer parentID, MapLink contentMapLink, Integer contentID) {

        this.parentID = parentID;
        this.contentMapLink = contentMapLink;
        this.contentID = contentID;
    }

    public void setUpdateTiming(Timing timing) {
        this.updateTiming = timing;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public boolean updateContentVersion() {

        if (contentMapLink == null) {
            return false;
        }

        Item contentItem = getItem(getContentMapLink(), getContentID());
        int newVersion = contentItem == null ? NONE : contentItem.getVersion();
        if (newVersion == contentVersion) {
            return false;
        }

        contentVersion = newVersion;
        return true;
    }
}

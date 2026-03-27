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
import nl.tytech.data.engine.serializable.Relation;

/**
 * Template panel interface
 *
 * @author Maxim Knepfle
 */
public interface TemplatePanel extends NamedItem, TextItem {

    public static final MapLink[] TEMPLATES = new MapLink[] { MapLink.AREAS, MapLink.BUILDINGS, MapLink.MEASURES, MapLink.NEIGHBORHOODS,
            MapLink.NET_CLUSTERS, MapLink.ZONES };

    public String getAttribute();

    @Override
    public Integer getID();

    public MapLink getMapLink();

    public Relation getRelation();

    public Integer getStakeholderID();

    public boolean hasRelation();

    public boolean isAutoApplied();

    public void setAttribute(String attribute);

    public void setAutoApplied(boolean autoApplied);

    public void setMapLink(MapLink mapLink);

    public void setRelation(Relation relation);

    public void setUseOwner(boolean useOwner);

    public boolean useOwner();

}

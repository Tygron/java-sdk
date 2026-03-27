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
package nl.tytech.data.editor.event;

import static nl.tytech.core.net.serializable.MapLink.NET_CLUSTERS;
import static nl.tytech.core.net.serializable.MapLink.SCENARIOS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.editor.serializable.ClusteringType;
import nl.tytech.data.engine.item.NetLine.NetType;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(NET_CLUSTERS)
public enum EditorNetClusterEventType implements IndicatorEventTypeEnum {

    ADD,

    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    ADD_LOADS_IN_AREA_TO_NET_CLUSTER(Integer.class, MultiPolygon.class),

    @EventIDField(links = { STAKEHOLDERS }, nullable = { 8 }, params = { 5 })
    GENERATE_CLUSTERS(NetType[].class, ClusteringType.class, MultiPolygon.class, Integer[].class, Double.class, Integer.class, String.class,
            Boolean.class, Double.class),

    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    REMOVE(Integer[].class),

    REMOVE_ALL_CLUSTERS(NetType.class),

    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    REMOVE_LOADS_IN_AREA_FROM_NET_CLUSTER(Integer.class, MultiPolygon.class),

    REMOVE_NETWORK(NetType.class, Boolean.class),

    @EventIDField(links = { NET_CLUSTERS }, params = { 0 }, nullable = { 1 })
    SET_FRACTION_CONNECTED(Integer.class, Double.class),

    @EventIDField(links = { NET_CLUSTERS, SCENARIOS }, params = { 0, 1 })
    SET_SCENARIO(Integer.class, Integer.class),

    @EventIDField(sameLength = true, links = { NET_CLUSTERS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(links = { NET_CLUSTERS, STAKEHOLDERS }, params = { 0, 1 })
    SET_OWNER(Integer.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorNetClusterEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {

        if (this == ADD) {
            return Integer.class;
        }
        if (this == REMOVE) {
            return Boolean.class;
        }
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return NET_CLUSTERS;
    }
}

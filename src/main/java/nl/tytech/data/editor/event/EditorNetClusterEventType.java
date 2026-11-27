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
import nl.tytech.core.item.annotations.EventParamData;
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

    @EventParamData(desc = "Add unclustered Net Loads to a Net Cluster given a MultiPolygon", params = { "Net Cluster", "MultiPolygon" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    ADD_LOADS_IN_AREA_TO_NET_CLUSTER(Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Generate Net Clusters for a set of Net Types, "
            + "using a Clustering Type, a Region, allowed owners of Buildings, cluster area size, "
            + "default Stakeholder for created Net Clusters. A default stakeholder for Net Loads without an associated Building. "
            + "A Cluster Attribute and optional specific value can be used to only include specific Net Loads. "
            + "Optionally, Net Loads identified as Collective can be clustered separately", //
            params = { "Net Types", "Clustering Type", "Region", "Stakeholders (allowed Building owners)", "Cluster Area Size",
                    "Stakeholder (default Cluster owner)", "Cluster Attribute", "Cluster Collective Loads Separately",
                    "Specific Cluster Attribute Value" })
    @EventIDField(links = { STAKEHOLDERS, STAKEHOLDERS }, params = { 3, 5 }, nullable = { 8 })
    GENERATE_CLUSTERS(NetType[].class, ClusteringType.class, MultiPolygon.class, Integer[].class, Double.class, Integer.class, String.class,
            Boolean.class, Double.class),

    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove all Net Clusters with Net Loads belonging only to a particular Net Type", params = { "Net Type" })
    REMOVE_ALL_CLUSTERS(NetType.class),

    @EventParamData(desc = "Remove Net Loads from a Net Cluster given a MultiPolygon", params = { "Net Cluster", "MultiPolygon" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 })
    REMOVE_LOADS_IN_AREA_FROM_NET_CLUSTER(Integer.class, MultiPolygon.class),

    @EventParamData(desc = "Remove a Network of a particular Net Type, with the option to keep the Root Load", params = { "Net Cluster",
            "Keep Root Load" })
    REMOVE_NETWORK(NetType.class, Boolean.class),

    @EventParamData(desc = "Set the fraction with Net Loads of this Cluster that are set to connected", params = { "Net Cluster",
            "Fraction" })
    @EventIDField(links = { NET_CLUSTERS }, params = { 0 }, nullable = { 1 })
    SET_FRACTION_CONNECTED(Integer.class, Double.class),

    @EventParamData(desc = "Set the Scenario in which this Net Cluster is active", params = { "Net Cluster", "Scenario" })
    @EventIDField(links = { NET_CLUSTERS, SCENARIOS }, params = { 0, 1 })
    SET_SCENARIO(Integer.class, Integer.class),

    @EventParamData(desc = "Set the name of a Net Cluster", params = { "Net Clusters", "Names" })
    @EventIDField(sameLength = true, links = { NET_CLUSTERS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventParamData(desc = "Set the owner of a Net Cluster", params = { "Net Cluster", "Stakeholder (owner)" })
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

        return switch (this) {

            case ADD -> Integer.class;

            case REMOVE -> Boolean.class;

            default -> null;
        };
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

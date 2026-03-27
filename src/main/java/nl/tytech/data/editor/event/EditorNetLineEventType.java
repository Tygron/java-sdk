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

import static nl.tytech.core.net.serializable.MapLink.NET_FUNCTIONS;
import static nl.tytech.core.net.serializable.MapLink.NET_LINES;
import static nl.tytech.core.net.serializable.MapLink.NET_NODES;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.NetLine;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(NET_LINES)
public enum EditorNetLineEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a Net Line with the provided Net Function ", params = { "Net Function ID" }, response = "Net Line ID")
    @EventIDField(links = { NET_FUNCTIONS }, params = { 0 })
    ADD(Integer.class),

    @EventParamData(desc = "Connect start and end point with one (or more) Net Line(s) of a specified Net Function. Nodes are created for start and end point. Optionally connection the two points using the road system. Also specify at what distance the new Nodes are merged with existing Nodes.", params = {
            "Net Function ID", "Start Point", "End Point", "Connect via Roads (false = connect direct between points)",
            "Node merge distance (optional, default: " + NetLine.DEFAULT_MERGE_M + "m)" }, defaults = { "", "", "", "false",
                    "" + NetLine.DEFAULT_MERGE_M }, dim3 = { 1, 2 })
    @EventIDField(sameLength = true, links = { NET_FUNCTIONS }, params = { 0 }, nullable = { 3, 4 })
    ADD_FOR_POINTS(Integer[].class, Point[].class, Point[].class, Boolean.class, Double.class),

    @EventParamData(desc = "Insert one point into the first Net Line and a second point into the Second Net Line and potentially connect these two points with a new Net Line with a specified Net Function."
            + " Nodes are created for start and end point. Optionally connection the two points using the road system. Also specify at what distance the new Nodes are merged with existing Nodes.", params = {
                    "Net Function ID", "First Net Line ID", "Start Point", "Second Net Line ID", "End Point",
                    "Connect via Roads (false = connect direct between points)",
                    "Node merge distance (optional, default: " + NetLine.DEFAULT_MERGE_M + "m)" }, defaults = { "", "", "", "", "", "false",
                            "" + NetLine.DEFAULT_MERGE_M }, dim3 = { 2, 4 })
    @EventIDField(links = { NET_FUNCTIONS }, params = { 0 }, nullable = { 5, 6 })
    INSERT_AND_ADD_FOR_POINTS(Integer.class, Integer.class, Point.class, Integer.class, Point.class, Boolean.class, Double.class),

    @EventParamData(desc = "Insert a new Node in an existing Net Line, potentially splitting the Net Line into two parts. Also specify at what distance the new Nodes are merged with existing Nodes.", params = {
            "Net Line ID", "Inserted Node Point",
            "Node merge distance (optional, default: " + NetLine.DEFAULT_MERGE_M + "m)" }, defaults = { "", "",
                    "" + NetLine.DEFAULT_MERGE_M }, dim3 = { 1 })
    @EventIDField(links = { NET_LINES }, params = { 0 }, nullable = { 2 })
    INSERT_POINT(Integer.class, Point.class, Double.class),

    @EventParamData(desc = "Place an existing Node at a new location and insert it in an existing Net Line, potentially splitting the Net Line into two parts. Also specify at what distance the new Nodes are merged with existing Nodes.", params = {
            "Net Node ID ", "Net Line ID", "New Node Point",
            "Node merge distance (optional, default: " + NetLine.DEFAULT_MERGE_M + "m)" }, defaults = { "", "", "",
                    "" + NetLine.DEFAULT_MERGE_M }, dim3 = { 2 })
    @EventIDField(links = { NET_NODES, NET_LINES }, params = { 0, 1 }, nullable = { 3 })
    MOVE_AND_INSERT(Integer.class, Integer.class, Point.class, Double.class),

    @EventParamData(desc = "Remove Net Lines", params = { "Net Line IDs" })
    @EventIDField(links = { NET_LINES }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Remove all attributes for each specified Net Line", params = { "Net Line IDs" })
    @EventIDField(links = { NET_LINES }, params = { 0 })
    REMOVE_ALL_ATTRIBUTES(Integer[].class),

    @EventParamData(desc = "Remove the specified attributes for each specified Net Line", params = { "Net Line IDs", "Attributes" })
    @EventIDField(links = { NET_LINES }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    @EventParamData(desc = "Remove the Net Lines, and optionally continue removing for Net Nodes that are connected the removed Net Line and that are only connected to one other Net Line of the same Net Type of the specified Net Line.", params = {
            "Net Line IDs", "Is repeated removal" })
    @EventIDField(links = { NET_LINES }, params = { 0 })
    REMOVE_REPEATED(Integer[].class, Boolean.class),

    @EventParamData(desc = "For each Net Line, set a particular Attribute to a specified array of values, optionally providing its Source.", params = {
            "Net Lines", "Attribute Name", "Attribute Values", "Source (optional)" })
    @EventIDField(links = { NET_LINES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Net Lines", "Attribute Name", "Attribute Values appended to existing values", "Source (optional)" })
    @EventIDField(links = { NET_LINES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Net Lines", "Attribute Names", "Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { NET_LINES, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Set the Net Function for a Net Line", params = { "Net Lines", "Net Functions" })
    @EventIDField(links = { NET_LINES, NET_FUNCTIONS }, params = { 0, 1 }, sameLength = true)
    SET_NET_FUNCTION(Integer[].class, Integer[].class);

    private final List<Class<?>> classes;

    private EditorNetLineEventType(Class<?>... classes) {
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
        return NET_LINES;
    }
}

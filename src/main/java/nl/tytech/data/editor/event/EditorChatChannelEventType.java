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

import static nl.tytech.core.net.serializable.MapLink.CHAT_CHANNELS;
import static nl.tytech.core.net.serializable.MapLink.NEURAL_NETWORKS;
import static nl.tytech.core.net.serializable.MapLink.SOURCES;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Internal;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.editor.item.ChatChannel.Tool;

/**
 *
 * @author Maxim Knepfle
 *
 */
@Linked(CHAT_CHANNELS)
public enum EditorChatChannelEventType implements EventTypeEnum {

    @EventParamData(response = "ChatChannel ID")
    ADD(),

    @EventParamData(desc = "Add ChatChannel related to specfic Item", params = { "Item MapLink", "Item ID" }, response = "ChatChannel IDs")
    @EventIDField(sameLength = true)
    ADD_ITEM_CHANNEL(MapLink[].class, Integer[].class),

    @EventIDField(sameLength = true, links = { CHAT_CHANNELS }, params = { 0 })
    SET_NAME(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { CHAT_CHANNELS }, params = { 0 })
    SET_INSTRUCTIONS(Integer[].class, String[].class),

    @EventIDField(sameLength = true, links = { CHAT_CHANNELS }, params = { 0 })
    SET_TOOLS(Integer[].class, Tool[][].class),

    @EventParamData(desc = "Set the LLM Neural Network for the Channel.", params = { "Chat Channels", "LLM Neural Networks" })
    @EventIDField(links = { CHAT_CHANNELS, NEURAL_NETWORKS }, params = { 0, 1 })
    SET_NEURAL_NETWORK(Integer[].class, Integer[].class),

    @EventParamData(params = { "Chat Channels", "Attribute Name", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(links = { CHAT_CHANNELS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Chat Channels", "Attribute Name", "Numeric Attribute Values appended to existing values",
            "Source (optional)" })
    @EventIDField(links = { CHAT_CHANNELS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    APPEND_ATTRIBUTE(Integer[].class, String.class, double[].class, Integer.class),

    @EventParamData(params = { "Chat Channels", "Attribute Names", "Numeric Attribute Values", "Source (optional)" })
    @EventIDField(sameLength = true, links = { CHAT_CHANNELS, SOURCES }, params = { 0, 3 }, nullable = { 3 })
    SET_ATTRIBUTES(Integer[].class, String[].class, double[][].class, Integer.class),

    @EventParamData(desc = "Reset chat channel contents", params = {
            "Channel ID" }, response = "Returns true if the channel was found and messages were removed, otherwise false.")
    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    RESET(Integer[].class),

    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    DUPLICATE(Integer[].class),

    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    REMOVE_ATTRIBUTE(Integer[].class, String[].class),

    /**
     * Internal events
     */

    @Internal
    @EventParamData(desc = "Internal event used by AI Agents", params = { "Chat Channels", "Task content" })
    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    ADD_AGENT_TASK(Integer.class, String.class),

    @Internal
    @EventParamData(desc = "Internal event used by AI Agents", params = { "Chat Channels", "Task ID", "Result" })
    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    SET_AGENT_RESULT(Integer.class, Integer.class, String.class),

    @Internal
    @EventParamData(desc = "Internal event used by AI Agents", params = { "Chat Channels", "Task ID" })
    @EventIDField(links = { CHAT_CHANNELS }, params = { 0 })
    REMOVE_AGENT_RESULT(Integer.class, Integer.class);

    private final List<Class<?>> classes;

    private EditorChatChannelEventType(Class<?>... classes) {
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
            case ADD_ITEM_CHANNEL -> Integer[].class;
            case SET_NAME -> Boolean.class;
            case ADD_AGENT_TASK -> Boolean.class;
            case SET_AGENT_RESULT -> Boolean.class;
            case REMOVE_AGENT_RESULT -> String.class;
            case SET_INSTRUCTIONS -> Boolean.class;
            case RESET -> Boolean.class;
            case DUPLICATE -> Integer[].class;
            case REMOVE -> Boolean.class;
            case SET_ATTRIBUTE -> Boolean.class;
            case SET_ATTRIBUTES -> Boolean.class;
            case APPEND_ATTRIBUTE -> Boolean.class;
            case REMOVE_ATTRIBUTE -> Boolean.class;
            case SET_TOOLS -> Boolean.class;
            case SET_NEURAL_NETWORK -> Boolean.class;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}

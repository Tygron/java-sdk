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
package nl.tytech.data.editor.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.editor.serializable.AgentAttribute;
import nl.tytech.data.editor.serializable.AgentState;
import nl.tytech.data.editor.serializable.AgentTool;
import nl.tytech.data.engine.item.AttributeItem;
import nl.tytech.data.engine.item.GenAI;
import nl.tytech.data.engine.item.Setting;
import nl.tytech.util.StringUtils;

/**
 *
 * Chat Channel
 *
 * @author Maxim Knepfle
 *
 */
public class ChatChannel extends AttributeItem {

    public static final String CHANNEL = "channelid";

    public static final String STAKEHOLDER = "stakeholderid";

    public static final String EDITING = "edit";

    public static final Integer DOMAIN_CHANNEL = 0;

    private static final long serialVersionUID = 6153041331983174279L;

    @XMLValue
    @ItemIDField(MapLink.NEURAL_NETWORKS)
    private Integer neuralNetworkID = Item.NONE;

    @XMLValue
    @NoDefaultText
    private String instructions = StringUtils.EMPTY;

    @XMLValue
    @JsonIgnore
    @NoDefaultText
    private String intro = StringUtils.EMPTY;

    @XMLValue
    private ArrayList<AgentTool> tools = new ArrayList<>();

    @NoDefaultText
    private HashMap<Integer, String> agentTasks = new HashMap<>();

    @JsonIgnore
    @NoDefaultText
    private HashMap<Integer, String> agentResults = new HashMap<>();

    public ChatChannel() {

    }

    public HashMap<Integer, String> getAgentResults() {
        return agentResults;
    }

    public HashMap<Integer, String> getAgentTasks() {
        return agentTasks;
    }

    public final AgentState getAIState() {
        return AgentState.fromStep(getMessageStream().filter(m -> m instanceof AgentChatMessage).mapToInt(m -> m.getAgentState().getStep()).min()
                .orElse(AgentState.NONE.getStep()));
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {

        // first check channel
        if (hasAttribute(mapType, key)) {
            return super.getAttributeArray(mapType, key);
        }

        // optional fallback to original GenAI attribute
        if (getNeuralNetwork() instanceof GenAI genAI && genAI.hasAttribute(key)) {
            return genAI.getAttributeArray(mapType, key);
        }

        // default to empty
        return EMPTY;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return AgentAttribute.values();
    }

    public String getInstructions() {
        return instructions;
    }

    public String getIntro() {
        return intro;
    }

    public List<ChatMessage> getMessages() {
        return getMessageStream().collect(Collectors.toList());
    }

    private Stream<ChatMessage> getMessageStream() {
        return this.<ChatMessage> getMap(MapLink.CHAT_MESSAGES).stream().filter(m -> getID().equals(m.getChannelID()));
    }

    public GenAI getNeuralNetwork() {
        return getItem(MapLink.NEURAL_NETWORKS, getNeuralNetworkID());
    }

    public Integer getNeuralNetworkID() {
        return neuralNetworkID;
    }

    @Override
    public double[] getOrDefaultArray(MapType mapType, ReservedAttribute attribute) {

        // first check channel
        if (hasAttribute(mapType, attribute)) {
            return super.getAttributeArray(mapType, attribute);
        }

        // optional fallback to original GenAI attribute
        if (getNeuralNetwork() instanceof GenAI genAI && genAI.hasAttribute(attribute)) {
            return genAI.getAttributeArray(mapType, attribute.name());
        }

        // default
        return attribute.defaultArray();
    }

    public List<AgentTool> getTools() {
        return tools;
    }

    public final boolean hasInstructions() {
        return StringUtils.containsData(instructions);
    }

    public boolean isDomain() {
        return DOMAIN_CHANNEL.equals(getID());
    }

    public final boolean isRead() {
        return getAttribute(AgentAttribute.PROJECT_INFO) > 0 || tools.stream().anyMatch(t -> t != null && t.isRead());
    }

    public final boolean isSee() {
        return tools.stream().anyMatch(t -> t != null && t.isSee());
    }

    public final boolean isWrite() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.AI_EDITING);
        return setting.getBooleanValue() && tools.stream().anyMatch(t -> t != null && t.isWrite());
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setNeuralNetworkID(Integer neuralNetworkID) {
        this.neuralNetworkID = neuralNetworkID;
    }

    public void setTools(List<AgentTool> tools) {
        this.tools = new ArrayList<>(tools);
    }
}

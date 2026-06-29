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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.Description;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.editor.item.ChatMessage.AIState;
import nl.tytech.data.engine.item.AttributeItem;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 *
 * Chat Channel
 *
 * @author Maxim Knepfle
 *
 */
public class ChatChannel extends AttributeItem {

    public enum ChatChannelAttribute implements ReservedAttribute {

        TEMPERATURE(Double.class, 0.8, 2),

        TOP_P(Double.class, 0.95, 2),

        TOP_K(Integer.class, 64, 1024),

        NUM_PREDICT(Integer.class, 5000, Integer.MAX_VALUE), // https://openllmbridge.com/blog/why-gemma-4-overthinks

        TIMEOUT_SEC(Integer.class, 10 * 60, 20 * 60), // default 10min timeout

        THINK_MODE(Boolean.class, 1, 1),

        CACHEABLE(Boolean.class, 1, 1), // when > 0 WIKI queries will be cached for faster tool execution

        PROJECT_INFO(Boolean.class, 1, 1);

        private final Class<?> type;
        private final double[] defaultArray;
        private final double maxValue;

        private ChatChannelAttribute(Class<?> type, double defaultValue, double maxValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
            this.maxValue = maxValue;
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        public double getMaxValue() {
            return maxValue;
        }

        public double getMinValue() {
            return 0;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        public final boolean isOption() {
            return switch (this) {
                case PROJECT_INFO -> false;
                default -> true;
            };
        }
    }

    public enum Tool {

        @Description("Get AI Summary on a Tygron Wiki subject")
        GET_SUMMARY,

        @Description("Search Tygron Wiki Pages")
        SEARCH_PAGE,

        @Description("Get specific Tygron Wiki Page")
        GET_PAGE,

        @Description("Validate Tygron API Endpoint")
        VALIDATE_ENDPOINT,

        @Description("Validate Tygron TQL Query")
        VALIDATE_QUERY,

        @Description("Execute Tygron TQL Query")
        EXECUTE_QUERY,

        @Description("Execute Tygron API Endpoint")
        EXECUTE_ENDPOINT;

        public static final Tool fromText(String text) {
            return Arrays.stream(values()).filter(t -> t.toString().equals(text) || t.name().equals(text)).findAny().orElse(null);
        }

        public final String getDescription() {
            return ObjectUtils.getDescription(this);
        }

        public final boolean isRead() {
            return switch (this) {
                case VALIDATE_QUERY, VALIDATE_ENDPOINT, EXECUTE_QUERY, EXECUTE_ENDPOINT -> true;
                default -> false;
            };
        }

        public final boolean isWrite() {
            return switch (this) {
                case EXECUTE_ENDPOINT -> true;
                default -> false;
            };
        }

        @Override
        public final String toString() {
            return name().toLowerCase().replace("_", "-");
        }
    }

    public static final Integer DOMAIN_CHANNEL = 0;

    public static final Integer WIKI_CHANNEL = 1;

    public static final Integer PROJECT_CHANNEL = 2;

    private static final long serialVersionUID = 6153041331983174279L;

    @XMLValue
    @NoDefaultText
    private String instructions = StringUtils.EMPTY;

    @XMLValue
    @JsonIgnore
    @NoDefaultText
    private String intro = StringUtils.EMPTY;

    @XMLValue
    private ArrayList<Tool> tools = new ArrayList<>();

    public ChatChannel() {

    }

    public final AIState getAIState() {
        return AIState.fromStep(getMessageStream().filter(m -> m instanceof AIChatMessage).mapToInt(m -> m.getAIState().getStep()).min()
                .orElse(AIState.NONE.getStep()));
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return ChatChannelAttribute.values();
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

    public List<Tool> getTools() {
        return tools;
    }

    public final boolean hasInstructions() {
        return StringUtils.containsData(instructions);
    }

    public boolean isDomain() {
        return DOMAIN_CHANNEL.equals(getID());
    }

    public final boolean isRead() {
        return getAttribute(ChatChannelAttribute.PROJECT_INFO) > 0 || tools.stream().anyMatch(t -> t != null && t.isRead());
    }

    public final boolean isWrite() {
        return tools.stream().anyMatch(t -> t != null && t.isWrite());
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setTools(List<Tool> tools) {
        this.tools = new ArrayList<>(tools);
    }
}

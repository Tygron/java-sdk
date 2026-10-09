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
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GPUJob;
import nl.tytech.data.editor.serializable.AgentState;
import nl.tytech.data.editor.serializable.AgentToolCall;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * An AgentChatMessage that contains the reasoning process of the Agent
 *
 * @author Maxim Knepfle
 */
public class AgentChatMessage extends ChatMessage {

    private static final long serialVersionUID = -5830351765378047529L;

    public static final String ERROR_FORMAT = "***%s***";

    private static final String NO_RESPONSE = ERROR_FORMAT.formatted("No response");

    private static final String CANCEL_RESPONSE = ERROR_FORMAT.formatted("Response cancelled");

    public static final String QUEUING_TAG = "Queuing...";

    public static final String READING_TAG = "Reading...";

    public static final String BUSY_THINKING_TAG = "Thinking...";

    public static final String THINK_TAG = "Thinking:\n";

    public static final String ANSWER_TAG = "Answer:\n";

    @XMLValue
    private String thinking = StringUtils.EMPTY;

    @XMLValue
    private AgentState state = AgentState.QUEUING;

    @XMLValue
    @ListOfClass(AgentToolCall.class)
    private ArrayList<AgentToolCall> toolCalls = new ArrayList<>(0);

    @XMLValue
    private long calcTimeMS = 0;

    @JsonIgnore
    private transient int thinkCounter = 0;

    public AgentChatMessage() {

    }

    public AgentChatMessage(Integer channelID) {
        super(channelID, Role.ASSISTANT, StringUtils.EMPTY);
    }

    public final void cancel() {

        if (StringUtils.containsData(message)) {
            message += "\n\n";
        }
        message += CANCEL_RESPONSE;
        state = AgentState.FINISHED;
    }

    @Override
    public AgentState getAgentState() {
        return state;
    }

    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    @Override
    public String getMessage(boolean showThinking) {

        if (getAgentState() == AgentState.QUEUING) {
            return QUEUING_TAG;
        }
        if (getAgentState() == AgentState.READING) {
            return READING_TAG;
        }

        StringBuilder result = new StringBuilder();
        boolean reasoning = showThinking && StringUtils.containsData(thinking);
        if (reasoning) {
            result.append(THINK_TAG);
            result.append(thinking);
        }
        String response = message;
        if (getAgentState() == AgentState.FINISHED && !StringUtils.containsData(response)) {
            response = NO_RESPONSE;
        }
        if (StringUtils.containsData(response)) {
            if (reasoning) {
                result.append("\n\n" + ANSWER_TAG);
            }
            result.append(response);
        }
        String r = result.toString();
        if (StringUtils.containsData(r)) {
            return r;
        }
        if (!showThinking && StringUtils.containsData(thinking)) {
            return BUSY_THINKING_TAG;
        }
        return randomDots(".");
    }

    public String getThinking() {
        return thinking;
    }

    public List<AgentToolCall> getToolCalls() {
        return toolCalls;
    }

    @Override
    public boolean isError() {
        return calcTimeMS == GPUJob.ERROR;
    }

    private final String randomDots(String base) {

        StringBuilder builder = new StringBuilder(base);
        for (int i = 0; i < MathUtils.randomInt(10); i++) {
            builder.append(".");
        }
        return builder.toString();
    }

    @Override
    public void reset() {
        super.reset();
        // reset messages are always finished (only new can generate)
        state = AgentState.FINISHED;
    }

    public void setCalcTimeMS(long calcTimeMS) {
        this.calcTimeMS = calcTimeMS;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }

    public void setToolCalls(List<AgentToolCall> toolCalls) {
        this.toolCalls = new ArrayList<>(toolCalls);
    }
}

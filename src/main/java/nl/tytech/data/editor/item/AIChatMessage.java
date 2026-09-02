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
import nl.tytech.data.editor.other.AIToolCall;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * An AIChatMessage that contains the reasoning process of the LLM
 *
 * @author Maxim Knepfle
 */
public class AIChatMessage extends ChatMessage {

    private static final long serialVersionUID = -5830351765378047529L;

    public static final String QUEUING_TAG = "Queuing...";

    public static final String READING_TAG = "Reading...";

    public static final String BUSY_THINKING_TAG = "Thinking...";

    public static final String THINK_TAG = "Thinking:\n";

    public static final String ANSWER_TAG = "Answer:\n";

    @XMLValue
    private String thinking = StringUtils.EMPTY;

    @XMLValue
    private AIState state = AIState.QUEUING;

    @XMLValue
    @ListOfClass(AIToolCall.class)
    private ArrayList<AIToolCall> toolCalls = new ArrayList<>(0);

    @XMLValue
    private long calcTimeMS = 0;

    @JsonIgnore
    private transient int thinkCounter = 0;

    public AIChatMessage() {

    }

    public AIChatMessage(Integer channelID) {
        super(channelID, Role.ASSISTANT, StringUtils.EMPTY);
    }

    @Override
    public AIState getAIState() {
        return state;
    }

    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    @Override
    public String getMessage(boolean showThinking) {

        if (getAIState() == AIState.QUEUING) {
            return QUEUING_TAG;
        }
        if (getAIState() == AIState.READING) {
            return READING_TAG;
        }

        StringBuilder result = new StringBuilder();
        boolean reasoning = showThinking && StringUtils.containsData(thinking);
        if (reasoning) {
            result.append(THINK_TAG);
            result.append(thinking);
        }
        if (StringUtils.containsData(message)) {
            if (reasoning) {
                result.append("\n\n" + ANSWER_TAG);
            }
            result.append(message);
        }
        String r = result.toString();
        if (StringUtils.containsData(r)) {
            return r;
        }
        if (!showThinking && StringUtils.containsData(thinking)) {
            return randomDots(BUSY_THINKING_TAG);
        }
        return randomDots(".");
    }

    public String getThinking() {
        return thinking;
    }

    public List<AIToolCall> getToolCalls() {
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
        state = AIState.FINISHED;
    }

    public void setCalcTimeMS(long calcTimeMS) {
        this.calcTimeMS = calcTimeMS;
    }

    public void setState(AIState state) {
        this.state = state;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }

    public void setToolCalls(List<AIToolCall> toolCalls) {
        this.toolCalls = new ArrayList<>(toolCalls);
    }
}

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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * AIChatMessage that contains the thinking of the LLM
 *
 * @author Maxim Knepfle
 */
public class AIChatMessage extends ChatMessage {

    private static final long serialVersionUID = -5830351765378047529L;

    public static final String QUEUING_TAG = "Queing...";

    public static final String THINK_TAG = "Thinking:\n";

    public static final String ANSWER_TAG = "Answer:\n";

    @XMLValue
    private String thinking = StringUtils.EMPTY;

    @XMLValue
    private AIState state = AIState.QUEUING;

    @XMLValue
    private long calcTimeMS = 0;

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
    public String getMessage(boolean includeReasoning) {

        if (getAIState() == AIState.QUEUING) {
            return QUEUING_TAG;
        }

        StringBuilder result = new StringBuilder();
        boolean reasoning = includeReasoning && StringUtils.containsData(thinking);
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
        return StringUtils.containsData(r) ? r : "...";
    }

    public String getThinking() {
        return thinking;
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
}

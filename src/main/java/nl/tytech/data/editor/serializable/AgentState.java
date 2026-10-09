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
package nl.tytech.data.editor.serializable;

import nl.tytech.core.item.annotations.Description;
import nl.tytech.naming.EngineNC;

/**
 * AI Agent State
 *
 * @author Maxim Knepfle
 */
public enum AgentState {

    @Description("Message has not been processed yet")
    NONE(0),

    @Description("Message queued for processing by " + EngineNC.AI_AGENT)
    QUEUING(1),

    @Description(EngineNC.AI_AGENT + " is reading the prompt")
    READING(2),

    @Description(EngineNC.AI_AGENT + " is generating the contents")
    GENERATING(3),

    @Description(EngineNC.AI_AGENT + " has finished")
    FINISHED(4);

    public static final AgentState fromStep(int step) {

        for (AgentState s : AgentState.values()) {
            if (s.getStep() == step) {
                return s;
            }
        }
        return null;
    }

    private final int step;

    private AgentState(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public final boolean isBusy() {
        return this == QUEUING || this == READING || this == GENERATING;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

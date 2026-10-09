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

import nl.tytech.data.engine.item.AttributeItem.ReservedAttribute;

/**
 * AI Agent Attributes
 *
 * @author Maxim Knepfle
 */
public enum AgentAttribute implements ReservedAttribute {

    TEMPERATURE(Double.class, 0.8, 2),

    TOP_P(Double.class, 0.95, 2),

    TOP_K(Integer.class, 64, 1024),

    MAX_TOKENS(Integer.class, 5_000, Integer.MAX_VALUE), // https://openllmbridge.com/blog/why-gemma-4-overthinks

    THINKING_BUDGET_TOKENS(Integer.class, 1_000, Integer.MAX_VALUE),

    TIMEOUT_SEC(Integer.class, 10 * 60, 60 * 60), // default 10min timeout, max 1 hour

    THINK_MODE(Boolean.class, 1, 1),

    CACHEABLE(Boolean.class, 1, 1), // when > 0 WIKI queries will be cached for faster tool execution

    PROJECT_INFO(Boolean.class, 1, 1);

    public static final AgentAttribute fromText(String text) {

        text = text.toUpperCase();
        for (AgentAttribute a : AgentAttribute.values()) {
            if (a.name().equals(text)) {
                return a;
            }
        }
        return null;
    }

    private final Class<?> type;
    private final double[] defaultArray;
    private final double maxValue;

    private AgentAttribute(Class<?> type, double defaultValue, double maxValue) {
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
        return switch (this) {
            case MAX_TOKENS, THINKING_BUDGET_TOKENS, TIMEOUT_SEC -> 1.0;
            default -> 0.0;
        };
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

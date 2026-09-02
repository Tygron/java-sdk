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
package nl.tytech.data.editor.other;

import java.io.Serializable;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * Represents a tool call made by an AI.
 *
 * @author Maxim Knepfle
 */
public class AIToolCall implements Serializable {

    private static final long serialVersionUID = 5107188523631763204L;

    @XMLValue
    private String id = StringUtils.EMPTY;

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    private String arguments = StringUtils.EMPTY;

    public AIToolCall() {

    }

    public AIToolCall(String id, String name, String arguments) {

        this.id = id;
        this.name = name;
        this.arguments = arguments;
    }

    public String getArguments() {
        return arguments;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

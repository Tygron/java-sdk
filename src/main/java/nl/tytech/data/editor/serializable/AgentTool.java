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

import java.util.Arrays;
import nl.tytech.core.item.annotations.Description;
import nl.tytech.util.ObjectUtils;

/**
 * AI Tools 
 * 
 * @author Maxim Knepfle
 */
public enum AgentTool {

    @Description("Get AI Summary on a Tygron Wiki subject")
    GET_SUMMARY,

    @Description("Search Tygron Wiki Pages")
    SEARCH_PAGE,

    @Description("Get specific Tygron Wiki Page")
    GET_PAGE,

    @Description("Get specific Tygron Wiki Image")
    GET_IMAGE,

    @Description("Validate Tygron API Endpoint")
    VALIDATE_ENDPOINT,

    @Description("Validate Tygron TQL Query")
    VALIDATE_QUERY,

    @Description("Execute Tygron TQL Query")
    EXECUTE_QUERY,

    @Description("Execute Tygron API Endpoint")
    EXECUTE_ENDPOINT,

    @Description("Get screenshot of user interface")
    GET_SCREEN,

    @Description("Open a panel in user interface")
    OPEN_PANEL,

    @Description("Go to location in user interface")
    GO_TO_LOCATION;

    public static final AgentTool fromText(String text) {
        return Arrays.stream(values()).filter(t -> t.toString().equals(text) || t.name().equals(text)).findAny().orElse(null);
    }

    public final String getDescription() {
        return ObjectUtils.getDescription(this);
    }

    public final boolean isRead() {
        return switch (this) {
            case VALIDATE_QUERY, EXECUTE_QUERY, EXECUTE_ENDPOINT -> true;
            default -> false;
        };
    }

    public final boolean isSee() {
        return switch (this) {
            case GET_SCREEN -> true;
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

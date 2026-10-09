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

import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.util.StringUtils;

/**
 *
 * AI Skill file
 *
 * @author Maxim Knepfle
 *
 */
public class Skill extends UniqueNamedItem implements ActiveItem {

    private static final long serialVersionUID = 6153041251982274279L;

    public static final String toValidName(String name) {
        return name.toLowerCase().replaceAll(" ", "-");
    }

    @XMLValue
    @NoDefaultText
    private String description = StringUtils.EMPTY;

    @XMLValue
    @NoDefaultText
    private String content = StringUtils.EMPTY;

    @XMLValue
    private boolean active = true;

    public Skill() {

    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

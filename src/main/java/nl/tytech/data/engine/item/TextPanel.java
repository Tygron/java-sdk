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
package nl.tytech.data.engine.item;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.other.TextItem;
import nl.tytech.util.StringUtils;

/**
 * @author Frank Baars
 */
public class TextPanel extends LogicPanel implements TextItem {

    private static final long serialVersionUID = -526843113040439635L;

    @XMLValue
    @JsonIgnore
    private TreeMap<String, String> tags = new TreeMap<>(); // server side only

    public TextPanel() {
        this(PanelType.TEXT_PANEL);
    }

    protected TextPanel(PanelType type) {
        super(type);
    }

    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public String getText() {

        // custom own text
        if (StringUtils.containsData(text)) {
            return text;
        }

        // get text form parent
        Panel parent = getParent();
        if (parent instanceof TemplateTextPanel ttp) {
            return ttp.getText();
        }

        // default
        return StringUtils.EMPTY;
    }

    public boolean hasTags() {
        return tags.size() > 0;
    }

    public void setTags(TreeMap<String, String> tags) {
        this.tags = tags;
    }

    public void setText(String text) {
        this.text = text;
        setLogicUpdated(true);
    }

    public final String updateTags(String text) {

        // filter empty values and sort results by longest tag first, to prevent replacing large queries by smaller sub versions
        for (Entry<String, String> entry : tags.entrySet().stream().filter(e -> e.getValue() != null)
                .sorted((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length())).toList()) {
            text = text.replaceAll("\\" + entry.getKey(), entry.getValue());
        }
        return text;
    }
}

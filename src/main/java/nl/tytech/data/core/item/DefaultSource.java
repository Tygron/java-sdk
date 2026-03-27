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
package nl.tytech.data.core.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.color.TColor;

/**
 * Default geo source, that cannot be edited
 *
 * @author Maxim Knepfle
 */
public class DefaultSource extends Source {

    private static final long serialVersionUID = -3168978219302967938L;

    @XMLValue
    private TColor color = TColor.RED;

    public DefaultSource() {

    }

    public DefaultSource(String name, String uploaderName, String description, TColor color) {
        super(name);
        this.setUploaderName(uploaderName);
        this.setDescription(description);
        this.color = color;
    }

    @Override
    public TColor getColor() {
        return color;
    }

    @Override
    public String getTypeName() {
        return "Default";
    }
}

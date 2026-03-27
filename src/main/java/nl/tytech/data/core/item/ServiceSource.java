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
import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Online Service source location
 *
 * @author Maxim Knepfle
 */
public class ServiceSource extends WebSource {

    private static final long serialVersionUID = -3168978219302967936L;

    @XMLValue
    private GeoFormat format;

    @XMLValue
    private TColor color = TColor.RED;

    public ServiceSource() {

    }

    public ServiceSource(GeoFormat format, String name, String url, String owner) {
        super(name, url);
        this.format = format;
        setUploaderName(owner);
    }

    @Override
    public TColor getColor() {
        return color;
    }

    public GeoFormat getFormat() {
        return format;
    }

    @Override
    public String getTypeName() {

        if (format == null) {
            return "Unknown Service";
        }
        return format.name().replaceAll(StringUtils.UNDER_SCORE, StringUtils.WHITESPACE) + " Service";
    }

    public void setFormat(GeoFormat format) {
        this.format = format;
    }

}

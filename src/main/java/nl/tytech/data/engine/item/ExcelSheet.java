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

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.ExcelItem;

/**
 * Excelsheet Item
 *
 * @author Maxim Knepfle
 */
public class ExcelSheet extends CertifiedDataItem {

    private static final long serialVersionUID = -2928409400327713874L;

    public static final String EXCEL_EXTENSION = "xlsx";

    @JsonIgnore // keep on cloning for test run, dont send over web to client and dont save to xml
    private byte[] cachedBytes = null;

    public byte[] getCachedBytes() {
        return cachedBytes;
    }

    @Override
    public String getExtension() {
        return EXCEL_EXTENSION;
    }

    @Override
    public List<Item> getLinks() {
        List<Item> links = new ArrayList<>();
        importLinks(links, MapLink.PANELS);
        importLinks(links, MapLink.INDICATORS);
        importLinks(links, MapLink.ZONES);
        return links;
    }

    private void importLinks(List<Item> list, MapLink mapLink) {
        for (Item item : getMap(mapLink)) {
            if (item instanceof ExcelItem ei && this.equals(ei.getExcelSheet())) {
                list.add(item);
            }
        }
    }

    public void setCachedBytes(byte[] cachedBytes) {
        this.cachedBytes = cachedBytes;
    }
}

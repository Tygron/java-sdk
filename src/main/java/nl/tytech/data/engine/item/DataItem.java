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

import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.StringUtils;

/**
 * DataItem
 *
 * @author Frank Baars & Maxim Knepfle
 */
public abstract class DataItem extends UniqueNamedItem {

    private static final long serialVersionUID = -9216539646337136296L;

    @XMLValue
    private Long uploadDate = null;

    @XMLValue
    private String uploaderName = StringUtils.EMPTY;

    private String warnings = StringUtils.EMPTY;

    public abstract String getExtension();

    public String getFileName() {
        return getName() + "." + getExtension();
    }

    public abstract List<Item> getLinks();

    public Long getUploadDate() {
        return uploadDate;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public String getWarnings() {
        return warnings;
    }

    public boolean isDefault() {
        return false;
    }

    public boolean isLinked() {
        return !this.getLinks().isEmpty();
    }

    public void setUploadDate(Long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

}

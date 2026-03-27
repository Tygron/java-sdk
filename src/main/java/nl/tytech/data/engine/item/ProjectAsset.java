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

import java.io.File;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.Setting.Type;
import nl.tytech.util.ChecksumUtil;
import nl.tytech.util.StringUtils;

/**
 * Asset uploaded to project
 *
 * @author Maxim Knepfle
 */
public class ProjectAsset extends Item {

    private static final long serialVersionUID = -6754893695766076519L;

    private static final String STATIC = "STATIC";

    public static final String SATELLITE_DEFAULT = Setting.SATELLITE_IMAGE_LOCATION + Type.SATELLITE_FILE_NAME.getDefaultValue() + ".jpg";

    public static final String getChecksum(String location, File file) {

        if (location != null && location.startsWith(Setting.SATELLITE_IMAGE_LOCATION) && !location.equals(SATELLITE_DEFAULT)) {
            return STATIC; // sat images are static, never changes
        } else {
            return ChecksumUtil.getMD5Checksum(file.getPath());
        }
    }

    @XMLValue
    private String location = StringUtils.EMPTY;

    @XMLValue
    private String checksum = StringUtils.EMPTY;

    public ProjectAsset() {

    }

    public ProjectAsset(String location, File file) {
        this.location = location;
        this.updateChecksum(file);
    }

    public String getChecksum() {
        return checksum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return location;
    }

    public void updateChecksum(File file) {
        this.checksum = getChecksum(getLocation(), file);
    }
}

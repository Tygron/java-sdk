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
package nl.tytech.data.engine.other;

import nl.tytech.core.net.event.InputException;
import nl.tytech.util.StringUtils;

/**
 * Interface for Image based Items
 * @author Maxim Knepfle
 *
 */
public interface ImageItem {

    public static final String EXTENSION = "png";

    public static final String DOT_EXTENSION = "." + EXTENSION;

    public Integer getID();

    public String getImageLocation();

    public String getImageName();

    public int getImageVersion();

    public void setImageName(String name);

    /**
     * Return true when image was changed
     * @throws InputException for invalid image names
     */
    public default boolean setValidatedImageName(String fileName) throws InputException {

        if (!StringUtils.validFilename(fileName, EXTENSION)) {
            throw new InputException("Invalid " + EXTENSION + " image name: " + fileName + " for: " + getID());
        }
        if (!getImageName().equals(fileName)) {
            setImageName(fileName);
            return true;
        }
        return false;
    }
}

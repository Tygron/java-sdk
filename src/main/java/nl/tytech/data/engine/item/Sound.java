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

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Sound file
 *
 * @author Jeroen Warmerdam & Maxim Knepfle
 */
public class Sound extends Item {

    private static final long serialVersionUID = -6754893695766076516L;

    public static final String SOUND_DIR = "Sounds/";
    public static final String DEFAULT = "beep.ogg";

    @XMLValue
    private String name = StringUtils.EMPTY;

    @XMLValue
    @AssetDirectory(SOUND_DIR)
    private String filename = DEFAULT;

    @XMLValue
    private double volume = 1.0; // LOUD!!

    @XMLValue
    private boolean loop = false;

    @XMLValue
    private boolean background = false;

    public String getFilename() {

        if (!StringUtils.containsData(filename)) {
            return StringUtils.EMPTY;
        }
        return SOUND_DIR + filename;
    }

    public boolean getLoopIt() {
        return loop;
    }

    public String getName() {
        return name;
    }

    public double getVolume() {
        return volume;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean isBackgroundSound) {
        this.background = isBackgroundSound;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setLoop(boolean loopIt) {
        this.loop = loopIt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return name;
    }
}

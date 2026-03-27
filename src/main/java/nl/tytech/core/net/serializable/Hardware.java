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
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Hardware defintion
 *
 * @author Maxim Knepfle
 *
 */
public class Hardware implements Serializable, Comparable<Hardware> {

    private static final long serialVersionUID = -45482385613618096L;

    private String cpu = StringUtils.EMPTY;

    private String video = StringUtils.EMPTY;

    private String mem = StringUtils.EMPTY;

    private String mac = StringUtils.EMPTY;

    private String name = StringUtils.EMPTY;

    @Deprecated
    private boolean suggest64Bit = false;

    private long lastLogin = Item.NONE;

    @Override
    public int compareTo(Hardware o) {
        // sort on reverse date (last login first)
        if (o.lastLogin < this.lastLogin) {
            return -1;
        } else if (o.lastLogin > this.lastLogin) {
            return 1;
        }
        return 0;
    }

    public String getCpu() {
        return cpu;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getMac() {
        return mac;
    }

    public String getMem() {
        return mem;
    }

    public String getName() {
        return name;
    }

    public String getVideo() {
        return video;
    }

    @Deprecated
    public boolean isSuggest64Bit() {
        return suggest64Bit;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setMem(String mem) {
        this.mem = mem;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Deprecated
    public void setSuggest64Bit(boolean suggest64Bit) {
        this.suggest64Bit = suggest64Bit;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return this.cpu + "\n" + this.video + "\n" + this.mem + "\nLast used (by any user): "
                + StringUtils.dateToHumanString(this.lastLogin, true);
    }
}

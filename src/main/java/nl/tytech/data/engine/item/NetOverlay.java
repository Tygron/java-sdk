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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.NetLine.NetType;

/**
 * Network overlay
 *
 * @author Frank Baars
 */
public class NetOverlay extends Overlay {

    private static final long serialVersionUID = -7986943253600640478L;

    @XMLValue
    private NetType netType = NetType.ELECTRICITY;

    @XMLValue
    private boolean showActive = true;

    @XMLValue
    private boolean showNetwork = false;

    public NetType getNetType() {
        return netType;
    }

    public boolean isShowActive() {
        return showActive;
    }

    public boolean isShowNetwork() {
        return showNetwork;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public void setShowActive(boolean showActive) {
        this.showActive = showActive;
    }

    public void setShowNetwork(boolean showNetwork) {
        this.showNetwork = showNetwork;
    }
}

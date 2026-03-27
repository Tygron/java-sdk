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
package nl.tytech.data.engine.serializable;

/**
 * PopupImageType: defines the visualization image used in a popup.
 *
 * @author Frank Baars
 */
public enum PopupImageType {

    PICK_DATE, //
    REQUEST_PERMIT, //
    RECEIVE_REQUEST, //
    WAITING, //
    DECLINE, //
    APPROVE, //
    WAIT_BUILD, //
    WAIT_DEMOLISH, //
    BUILD_PROGRESS, //
    DEMOLISH_PROGRESS, //
    BUY_LAND, //
    SELL_LAND, //
    ;

    public static final String GUI_IMAGES_POPUPS = "Gui/Images/Panels/Popups/";

    public String getImageLocation() {
        return GUI_IMAGES_POPUPS + this.name().toLowerCase() + ".png";
    }

}

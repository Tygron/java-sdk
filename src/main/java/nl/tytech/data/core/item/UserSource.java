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

import nl.tytech.naming.EngineNC;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * User made edits
 *
 * @author Maxim Knepfle
 */
public class UserSource extends Source {

    private static final long serialVersionUID = -3692578257066535397L;

    public UserSource() {

    }

    public UserSource(String name, String userName) {
        super(name);
        setUploaderName(userName);
    }

    @Override
    public TColor getColor() {
        return TColor.BLUE;
    }

    @Override
    public String getDescription() {

        if (StringUtils.containsData(super.getDescription())) {
            return super.getDescription();
        } else {
            return "Edits in the " + EngineNC.CLIENT_NAME + " by: " + this.getUploaderName(); // default fallback
        }
    }

    @Override
    public String getTypeName() {
        return "User Edits";
    }
}

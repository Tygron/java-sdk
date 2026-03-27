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
import nl.tytech.util.StringUtils;

/**
 * Date and name of created project
 *
 * @author Maxim Knepfle
 */
public class Creation implements Serializable {

    private static final long serialVersionUID = -5979797842188489964L;

    private long date = 0;

    private String projectName = StringUtils.EMPTY;

    public Creation() {

    }

    public Creation(long date, String projectName) {
        this.date = date;
        this.projectName = projectName;
    }

    public long getDate() {
        return date;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return StringUtils.dateToHumanString(date) + ": " + StringUtils.capitalizeWithSpacedUnderScores(projectName);
    }
}

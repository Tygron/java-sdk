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

import nl.tytech.util.DateUtils;
import nl.tytech.util.StringUtils;

/**
 * Logging of user noticeable actions
 *
 * @author Maxim Knepfle
 *
 */
public class BackupLog extends Log {

    private static final long serialVersionUID = 8505302259584226577L;

    private long time = System.currentTimeMillis();

    private int success = 0;

    private int total = 0;

    public BackupLog() {
        super();
    }

    public BackupLog(Long domainID, String domainName, Integer success, Integer total) {

        super(domainID, domainName, StringUtils.randomTimeHex());
        this.success = success == null ? 0 : success.intValue();
        this.total = total == null ? 0 : total.intValue();
    }

    @Override
    public String getLogDescription() {
        return success >= total ? "Successful backup of " + success + "/" + total + " Items."
                : "Failed backup for " + (total - success) + "/" + total + " Items, contact support for more details.";
    }

    @Override
    public long getLogTime() {
        return getTime();
    };

    @Override
    public String getLogTitle() {
        return success >= total ? "Success" : "Failed";
    }

    @Override
    public Type getLogType() {
        return Type.BACKUP;
    }

    public int getSuccess() {
        return success;
    }

    public long getTime() {
        return time;
    }

    public int getTotal() {
        return total;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(DateUtils.formatLocal(time));
        builder.append(StringUtils.WHITESPACE);
        builder.append(getDomainName());
        builder.append(StringUtils.WHITESPACE);
        builder.append(getLogDescription());
        return builder.toString();
    }
}

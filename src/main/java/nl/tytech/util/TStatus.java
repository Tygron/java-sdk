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
package nl.tytech.util;

import jakarta.ws.rs.core.Response.Status;
import nl.tytech.util.RestManager.TWebApplicationException;

/**
 * Used HTML codes, most are default HTML codes, some Tygron Extensions
 *
 * @author Maxim Knepfle
 *
 */
public enum TStatus {

    // 2xx Success

    OK(Status.OK),

    ACCEPTED(Status.ACCEPTED),

    CREATED(Status.CREATED),

    NO_CONTENT(Status.NO_CONTENT),

    // 3xx Move

    FOUND(Status.FOUND),

    MOVED_PERMANENTLY(Status.MOVED_PERMANENTLY),

    // 4xx Client errors

    BAD_REQUEST(Status.BAD_REQUEST),

    /**
     * Authentication failed
     */
    UNAUTHORIZED(Status.UNAUTHORIZED),

    /**
     * Authentication is success, invalid rights for your role
     */
    FORBIDDEN(Status.FORBIDDEN),

    NOT_FOUND(Status.NOT_FOUND),

    REQUEST_TIMEOUT(Status.REQUEST_TIMEOUT),

    CLIENT_RELEASED(495, "Your client is released from the Session, please exit."), // tygron unique

    SERVER_REBOOT(496, "The Server was rebooted, please restart your application."), // tygron unique

    NO_SESSION(497, "The Session you are trying to connect to is not active."), // tygron unique

    INVALID_TOKEN(498, "Please provide the correct token for this Session."), // identical to ESRI error code

    // 5xx Server errors

    INTERNAL_SERVER_ERROR(Status.INTERNAL_SERVER_ERROR),

    SERVICE_UNAVAILABLE(Status.SERVICE_UNAVAILABLE),

    GATEWAY_TIMEOUT(Status.GATEWAY_TIMEOUT),

    SSL_HANDSHAKE_FAILED(525, "Failed to do a correct SSL Handshake"), // identical to Cloudflare error code

    CONNECTION_FAILED(526, "Cannot establish a connection to Server"), // tygron unique

    FAIR_CAPACITY(598,
            "Fair Usage Limit: The Server is almost at full capacity and therefore only two sessions are allowed per domain.\nIf possible close (keep-alive) sessions or try again later."), // unique

    MAX_CAPACITY(599,
            "Maximum Server capacity reached, cannot start new session.\nIf possible close (keep-alive) sessions or try again later."), // unique

    ;

    public static TStatus getTStatus(int code) {

        // find valid T Status
        for (TStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }

        // unknown to me
        return null;
    }

    private final int code;
    private final String description;

    private TStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private TStatus(Status status) {
        this(status.getStatusCode(), status.getReasonPhrase());
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {

        if (this == NO_CONTENT) {
            return StringUtils.EMPTY;
        } else {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public boolean isCode(int code) {
        return this.code == code;
    }

    public boolean isCode(TWebApplicationException exp) {
        return exp != null && this.code == exp.getStatusCode();
    }
}

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
package nl.tytech.core.util;

import nl.tytech.util.RestManager.TWebApplicationException;
import nl.tytech.util.TStatus;

/**
 * Exception thrown when Project session is shutdown
 *
 * @author Maxim Knepfle
 */
public class ShutdownException extends RuntimeException {

    private static final long serialVersionUID = 4149763704244278323L;

    public ShutdownException(String message) {
        super(message);
    }

    /**
     * Always throws a WebApplicationException exception
     */
    public TWebApplicationException throwWeb() throws TWebApplicationException {
        return new TWebApplicationException(TStatus.NO_SESSION, "Session is not active anymore.");
    }

    /**
     * Always throws a WebApplicationException exception
     */
    public <T> T throwWeb(Integer sessionID) throws TWebApplicationException {
        throw new TWebApplicationException(TStatus.NO_SESSION, "Session with ID: " + sessionID + " is not active anymore.");
    }
}

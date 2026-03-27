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

/**
 * Server types defintion
 *
 * @author Maxim Knepfle
 *
 */
public enum ServerType {

    /**
     * For Tygron Team RD purposes only
     */
    Development(false),

    /**
     * For Tygron Team Test purposes only
     */
    Test(false),

    /**
     * Stable version with latest features
     */
    Preview(true),

    /**
     * Long Term Supported
     */
    LTS(true);

    public static final ServerType getTypeForAddress(String myAddress) {

        for (ServerConfig server : ServerConfig.values()) {
            if (server.getAddress().equalsIgnoreCase(myAddress)) {
                return server.getType();
            }
        }
        return ServerType.Development; // default Development
    }

    public static ServerType getTypeForName(String serverTypeName) {

        for (ServerType type : ServerType.values()) {
            if (type.name().equalsIgnoreCase(serverTypeName)) {
                return type;
            }
        }
        return null; // default null
    }

    private final boolean publicAccess;

    private ServerType(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }
}

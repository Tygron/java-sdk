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
 * Listing of default Tygron servers
 *
 * @author Maxim Knepfle
 *
 */
public enum ServerConfig {

    Development1("development.tygron.com", ServerType.Development, "devshare.tygron.com"),

    Experimental("simnl.tygron.com", ServerType.Development, "devshare.tygron.com"),

    DevShare("devshare.tygron.com", ServerType.Development, "devshare.tygron.com"),

    Test1("test.tygron.com", ServerType.Test, "devshare.tygron.com"),

    Preview1("preview.tygron.com", ServerType.Preview),

    LTS1("engine.tygron.com", ServerType.LTS),

    LTS2("power.tygron.com", ServerType.LTS),

    GeoShare("geo.tygron.com", ServerType.LTS),

    Localhost("localhost", ServerType.Development, "localhost"), // local dev only

    ;

    public static final ServerConfig getDefault(ServerType type) {

        for (ServerConfig server : ServerConfig.values()) {
            if (server.getType() == type) {
                return server; // first is default server
            }
        }
        return Localhost; // fallback server
    }

    public static final ServerConfig getForAddress(String address) {

        for (ServerConfig server : ServerConfig.values()) {
            if (server.address.equalsIgnoreCase(address)) {
                return server; // first is default server
            }
        }
        return Localhost; // fallback server
    }

    private final String address, shareAddress;

    private final ServerType type;

    private ServerConfig(String address, ServerType type) {
        this(address, type, "geo.tygron.com"); // default geo share
    }

    private ServerConfig(String address, ServerType type, String shareAddress) {
        this.address = address;
        this.type = type;
        this.shareAddress = shareAddress;
    }

    public String getAddress() {
        return address;
    }

    public final String getShareAddress() {
        return shareAddress;
    }

    public ServerType getType() {
        return type;
    }

    public boolean isActive() {
        return this != Localhost;
    }

    public boolean isProjectServer() {
        return this != GeoShare && this != DevShare;
    }

    public boolean isPublicAccess() {
        return type.isPublicAccess();
    }

    @Override
    public String toString() {
        return this.name() + " (" + this.address + ")";
    }
}

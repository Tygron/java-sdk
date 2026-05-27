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
package nl.tytech.sdk.example;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import nl.tytech.core.client.net.ServicesConnection;
import nl.tytech.core.event.Event;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.Rest;
import nl.tytech.core.net.event.RemoteServicesEvent.ServiceEventType;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.util.Base64;
import nl.tytech.util.RestManager;
import nl.tytech.util.ServerType;
import nl.tytech.util.RestManager.Format;
import nl.tytech.util.logger.TLogger;

/**
 * Dummy example API connection setup
 *
 * @author Maxim Knepfle
 */
public class ExampleAPIConnection implements ServicesConnection {

    private final String apiTarget;

    public ExampleAPIConnection() {

        // setup server, user, etc
        JTextField srv = new JTextField(20);
        JOptionPane.showConfirmDialog(null, srv, "Enter Host name (e.g. engine.tygron.com): ", JOptionPane.OK_CANCEL_OPTION);
        JTextField usr = new JTextField(20);
        JOptionPane.showConfirmDialog(null, usr, "Enter User name: ", JOptionPane.OK_CANCEL_OPTION);
        JPasswordField pwd = new JPasswordField(20);
        JOptionPane.showConfirmDialog(null, pwd, "Enter Password for user: " + usr.getText(), JOptionPane.OK_CANCEL_OPTION);

        SettingsManager.setup(SettingsManager.class, Network.AppType.EDITOR);
        SettingsManager.setServer(ServerType.getTypeForAddress(srv.getText()), srv.getText());

        this.apiTarget = "https://" + srv.getText() + "/" + Rest.API;
        TLogger.info("API endpoint: " + apiTarget);

        // use simple basic authentication via headers
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        String authHeader = "Basic " + Base64.encode(usr.getText() + ":" + new String(pwd.getPassword()));
        headers.putSingle(HttpHeaders.AUTHORIZATION, authHeader);
        RestManager.setHeaders(apiTarget, headers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T fireServerEvent(ServiceEventType type, Object... args) {
        return (T) RestManager.post(apiTarget, Rest.EVENT + type.getClass().getSimpleName() + "/" + type.name(), null, args,
                type.getResponseClass(args), Format.DEFAULT_EVENT, Event.getResponseFormat(type, args), type.getTimeoutOverride());
    }

    public User getMyUserAccount() {
        return fireServerEvent(UserServiceEventType.GET_MY_USER);
    }
}

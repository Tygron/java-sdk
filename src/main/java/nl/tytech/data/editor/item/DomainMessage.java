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
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 *
 * Chat Messages between sessions in domains.
 *
 * @author Maxim Knepfle
 *
 */
public class DomainMessage extends ChatMessage {

    private static final long serialVersionUID = 6933493823803956568L;

    @XMLValue
    private String fullName = StringUtils.EMPTY;

    @XMLValue
    private String userName = StringUtils.EMPTY;

    public DomainMessage() {

    }

    public DomainMessage(String userName, String fullName, String message) {

        super(ChatChannel.DOMAIN_CHANNEL, Role.USER, message);
        this.userName = userName;
        this.fullName = fullName;
    }

    @Override
    public String getName() {
        return fullName;
    }

    public String getText() {
        return getName() + ": " + StringUtils.capitalizeFirstLetter(getMessage(false));
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return getText();
    }
}

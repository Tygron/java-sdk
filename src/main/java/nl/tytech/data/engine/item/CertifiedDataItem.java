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
package nl.tytech.data.engine.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 *
 * Data Item that can be certified via GeoShare certificate
 *
 * @author Maxim Knepfle
 *
 */
public abstract class CertifiedDataItem extends DataItem {

    public enum State {
        NONE, CERTIFIED, UNCERTIFIED
    }

    private static final long serialVersionUID = 2237810967274558477L;

    @XMLValue
    @JsonIgnore
    private String certificate = StringUtils.EMPTY;

    @XMLValue
    private String certificateAuthor = StringUtils.EMPTY;

    @XMLValue
    private String certificatePath = StringUtils.EMPTY;

    @XMLValue
    private State certificateState = State.NONE;

    public String getCertificate() {
        return certificate;
    }

    public String getCertificateAuthor() {
        return certificateAuthor;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public State getCertificateState() {
        return certificateState;
    }

    public void resetCertificate() {
        this.certificateState = State.NONE;
        this.certificateAuthor = StringUtils.EMPTY;
        this.certificatePath = StringUtils.EMPTY;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setCertificateAuthor(String author) {
        this.certificateAuthor = author;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public void setCertificateState(State certificateState) {
        this.certificateState = certificateState;
    }
}

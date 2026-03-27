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
package nl.tytech.core.client.net;

import nl.tytech.naming.EngineNC;

/**
 * Stages of loading
 *
 * @author Maxim Knepfle
 */
public enum LoadingStage {

    SERVER_START("Starting new Simulation in " + EngineNC.SERVER_NAME),

    CONNECT("Connecting to " + EngineNC.SERVER_NAME + " Simulation"),

    DOWNLOAD("Downloading Project Data"),

    GENERATE("Visualizing Digital Twin"),

    FAILED("Failed to start Session in " + EngineNC.SERVER_NAME),

    OCUCLUS_MODE("Activate Oculus in 3D-World using CTRL-SHIFT-O"),

    ;

    public static final String IMAGE_DIR = "Gui/Images/Loading/";

    private String text;

    private LoadingStage(String text) {
        this.text = text;

    }

    public String getImageLocation() {
        return IMAGE_DIR + this.name().toLowerCase() + ".gif";
    }

    public String getText() {
        return text;
    }

}

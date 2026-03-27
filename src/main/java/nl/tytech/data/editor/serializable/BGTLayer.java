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
package nl.tytech.data.editor.serializable;

/**
 * @author Maxim Knepfle
 */
public enum BGTLayer {

    BAK, //
    BEGROEIDTERREINDEEL("plus-fysiekVoorkomen", "class"), //
    BORD, //
    BUURT, //
    FUNCTIONEELGEBIED, //
    GEBOUWINSTALLATIE, //
    INSTALLATIE, //
    KAST, //
    KUNSTWERKDEEL("bgt-type", "plus-type"), //
    MAST, //
    ONBEGROEIDTERREINDEEL("bgt-fysiekVoorkomen", "plus-fysiekvoorkomen"), //
    ONDERSTEUNENDWATERDEEL, //
    ONDERSTEUNENDWEGDEEL, //
    ONGECLASSIFICEERDOBJECT, //
    OPENBARERUIMTE, //
    OPENBARERUIMTELABEL, //
    OVERBRUGGINGSDEEL, //
    OVERIGBOUWWERK("bgt-type", null), //
    OVERIGESCHEIDING, //
    PAAL, //
    PUBLIC, //
    PAND, //
    PLAATSBEPALINGSPUNT, //
    PUT, //
    SCHEIDING("bgt-type", null), //
    SENSOR, //
    SPOOR, //
    STRAATMEUBILAIR, //
    TUNNELDEEL, //
    VEGETATIEOBJECT, //
    WATERDEEL("plus-type", "class"), //
    WATERINRICHTINGSELEMENT, //
    WEGDEEL, //
    WEGINRICHTINGSELEMENT, //
    WIJK,//

    ;

    public static BGTLayer[] VALUES = values();

    private static final String BGT_GML_FILENAME = "bgt_%s.gml";
    private static final String BGT_GFS_FILENAME = "bgt_%s.gfs";
    private static final String BGT_ZIP_FILENAME = "bgt_%s.zip";

    private final String typeAttribute, classAttribute;

    private BGTLayer() {
        this("plus-type", null);
    }

    private BGTLayer(String typeAttribute, String classAttribute) {
        this.typeAttribute = typeAttribute;
        this.classAttribute = classAttribute;
    }

    public String getClassAttribute() {
        return classAttribute;
    }

    public String getGFSFilename() {
        return BGT_GFS_FILENAME.formatted(this.name().toLowerCase());
    }

    public String getGMLFilename() {
        return BGT_GML_FILENAME.formatted(this.name().toLowerCase());
    }

    public String getTypeAttribute() {
        return typeAttribute;
    }

    public String getZIPFilename() {
        return BGT_ZIP_FILENAME.formatted(this.name().toLowerCase());
    }
}

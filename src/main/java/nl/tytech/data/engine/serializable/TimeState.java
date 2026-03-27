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
package nl.tytech.data.engine.serializable;

import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 * TimeState: Defines the state that the measure or building is in. E.g. "constructing" means that this building is now being constructed.
 *
 * @author Maxim Knepfle
 */
public enum TimeState {

    /**
     * Default base state of the building, nothing is happening.
     */
    NOTHING(ClientTerms.STATE_NOTHING),

    /**
     * Building is planned and waiting for owner to give a date when to start building.
     */
    WAITING_FOR_DATE(ClientTerms.STATE_WAITING_FOR_CONSTRUCTION_DATE),

    /**
     * Building or Measure is planned on other stakeholder's land and requires approval
     */
    REQUEST_PLOT_OWNER_APPROVAL(ClientTerms.STATE_REQUEST_PLOT_APPROVAL),

    /**
     * Building requires Zoning Approval
     */
    REQUEST_ZONING_APPROVAL(ClientTerms.STATE_REQUEST_ZONING_APPROVAL),

    /**
     * Owner has given the start date and is asked if he wants to request a permit from the permitting stakeholders.
     */
    REQUEST_CONSTRUCTION_APPROVAL(ClientTerms.STATE_REQUEST_CONSTRUCTION_APPROVAL),

    /**
     * Municipality has given the permit, owner must confirm the permit.
     */
    CONSTRUCTION_APPROVED(ClientTerms.STATE_CONSTRUCTION_APPROVED),

    /**
     * Municipality has given a negative permit. Owner must confirm the negative result.
     */
    CONSTRUCTION_DENIED(ClientTerms.STATE_CONSTRUCTION_DENIED),

    /**
     * Permit is given and owner has accepted. Now waiting for construction to start. During this period the owner can still cancel the
     * construction for free.
     */
    PENDING_CONSTRUCTION(ClientTerms.STATE_PENDING_CONSTRUCTION),

    /**
     * Building is being constructed.
     */
    CONSTRUCTING(ClientTerms.STATE_CONSTRUCTING),

    /**
     * Building is constructed and ready for usage.
     */
    READY(ClientTerms.STATE_READY),

    /**
     * SPECIAL STATE for building that are waiting to be upgraded! (others do not reach this state!).
     */
    PENDING_UPGRADE(ClientTerms.STATE_PENDING_UPGRADE),

    /**
     * Building will be demolished and waiting for the destroyer to provide the start date of demolishing.
     */
    WAITING_FOR_DEMOLISH_DATE(ClientTerms.STATE_WAITING_FOR_DEMOLISH_DATE),

    /**
     * Building demolition requires Zoning Approval
     */
    REQUEST_DEMOLISH_ZONING_APPROVAL(ClientTerms.STATE_REQUEST_ZONING_APPROVAL),

    /**
     * The destroyer has to ask the building's owner for permission to demolish the building. When destoryer is the owner this state is
     * skipped.
     */
    REQUEST_DEMOLISH_APPROVAL(ClientTerms.STATE_REQUEST_DEMOLISH_APPROVAL),

    /**
     * The owner accepts the destruction of the building. Building is waiting for destruction.
     */
    DEMOLISH_APPROVED(ClientTerms.STATE_DEMOLISH_APPROVED),

    /**
     * Owner has given a negative demolish approval. Destroyer must confirm the negative result.
     */
    DEMOLISH_DENIED(ClientTerms.STATE_DEMOLISH_DENIED),

    /**
     * Demolish allowance is given and owner has accepted. Now waiting for demolish to start. During this period the owner can still cancel
     * the demolish for free.
     */
    PENDING_DEMOLISHING(ClientTerms.STATE_PENDING_DEMOLISHING),

    /**
     * Building is being destroyed (bulldozer time)
     */
    DEMOLISHING(ClientTerms.STATE_DEMOLISHING),

    /**
     * The destruction is completed, building is now gone.
     */
    DEMOLISH_FINISHED(ClientTerms.STATE_DEMOLISH_FINISHED);

    public static final TimeState[] VALUES = values();

    private ClientTerms term;

    private TimeState(ClientTerms term) {
        this.term = term;
    }

    /**
     * True when that TimeState is after this TimeState in the simulation time line.
     * @param that
     * @return
     */
    public boolean after(TimeState that) {
        return this.ordinal() > that.ordinal();
    }

    public boolean afterOrEqualTo(TimeState that) {
        return this.ordinal() >= that.ordinal();
    }

    /**
     * True when that TimeState is before this TimeState in the simulation time line.
     * @param that
     * @return
     */
    public boolean before(TimeState that) {
        return this.ordinal() < that.ordinal();
    }

    public boolean beforeOrEqualTo(TimeState that) {
        return this.ordinal() <= that.ordinal();
    }

    public ClientTerms getTranslationTerm() {
        return term;
    }

    /**
     * When true an answer is required to continue the session. when false this popup will disappear automatically.
     * @return
     */
    public boolean isAnswerRequired() {
        return this != TimeState.PENDING_CONSTRUCTION && this != TimeState.PENDING_DEMOLISHING;
    }

    public boolean isEditorValue() {
        return this == TimeState.READY || this == TimeState.NOTHING;
    }

    /**
     * In this timestate the building in in the map (either current or maquette)
     * @return
     */
    public boolean isInMap() {
        return this != TimeState.NOTHING && this != TimeState.DEMOLISH_FINISHED;
    }

    public boolean isInMap(MapType mapType) {

        if (mapType == null) {
            return true;
        }
        if (this == TimeState.NOTHING || this == TimeState.DEMOLISH_FINISHED) {
            return false;
        }
        if (this.before(TimeState.CONSTRUCTING)) {
            /**
             * Before construction CURRENT is Empty and MAQUTTE has coordinates.
             */
            return mapType == MapType.MAQUETTE;
        }
        if (this.beforeOrEqualTo(TimeState.READY)) {
            /**
             * In state construction through ready both map has coordinates.
             */
            return true;
        }
        if (this.before(TimeState.DEMOLISHING)) {
            /**
             * Before construction CURRENT is Empty and MAQUTTE has coordinates.
             */
            return mapType == MapType.CURRENT;
        }
        /**
         * In demolish states only in current.
         */
        return mapType == MapType.CURRENT;

    }
}

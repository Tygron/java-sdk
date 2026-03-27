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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.engine.item.Building.Detail;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;

/**
 * CostBookValue value that is negative (costing you something)
 *
 * @author Maxim Knepfle
 *
 */
public class CostBookValue extends StakeholderBookValue {

    public enum Cost implements Type {

        /**
         * Default cost type when now state is relevant.
         */
        DEFAULT(ClientTerms.COST_DEFAULT),

        /**
         * Money that is reserved to construct something.
         */
        RESERVED_CONSTRUCTION(ClientTerms.COST_RESERVED_CONSTRUCTION, Detail.CONSTRUCTION_COST),

        /**
         * Money that is reserved to upgrade something.
         */
        RESERVED_UPGRADE(ClientTerms.COST_RESERVED_UPGRADE),

        /**
         * Costs to construct something
         */
        CONSTRUCTION(ClientTerms.COST_CONSTRUCTION, Detail.CONSTRUCTION_COST),

        /**
         * Money that is reserved to construct something.
         */
        RESERVED_DEMOLISH(ClientTerms.COST_RESERVED_DEMOLISH, Detail.DEMOLISH_COST),

        /**
         * Costs to demolish something
         */
        DEMOLISH(ClientTerms.COST_DEMOLISH, Detail.DEMOLISH_COST),

        /**
         * Maintenance costs
         *
         */
        MAINTENANCE(ClientTerms.COST_MAINTENANCE),

        /**
         * Upgrade cost
         */
        UPGRADE(ClientTerms.COST_UPGRADE),

        BUY_OUT(ClientTerms.COST_BUYOUT);

        private ClientTerms term = null;

        private Detail buildingDetail = null;

        private Cost(ClientTerms term) {
            this.term = term;
        }

        private Cost(ClientTerms term, Detail buildingDetail) {

            this.buildingDetail = buildingDetail;
            this.term = term;
        }

        public Detail getDetail() {
            return buildingDetail;
        }

        @Override
        public ClientTerms getTranslationTerm() {
            return term;
        }
    }

    private static final long serialVersionUID = 946868301600695158L;

    @XMLValue
    private Cost costType = Cost.DEFAULT;

    public CostBookValue() {
    }

    public CostBookValue(final Stakeholder stakeholder, final MapLink mapLink, Integer linkID, Cost cost, final String name,
            final double value) {
        super(stakeholder, mapLink, linkID, name, value);
        this.costType = cost;
    }

    @Override
    public Cost getType() {
        return costType;
    }

    public void setType(Cost newType) {
        this.costType = newType;
    }
}

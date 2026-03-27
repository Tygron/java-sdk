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
 * IncomeBookValue that is positive (generates income).
 *
 * @author Maxim Knepfle
 */
public class IncomeBookValue extends StakeholderBookValue {

    public enum Income implements Type {

        /**
         * Default cost type when now state is relevant.
         */
        DEFAULT(ClientTerms.INCOME_DEFAULT),

        /**
         * Budget at start of session
         */
        START_BUDGET(ClientTerms.BUDGET),

        /**
         * Money that received by selling a building.
         */
        BUILDING_SALE(ClientTerms.INCOME_SALES, Detail.SELL_PRICE),

        CONTRIBUTION(ClientTerms.COST_CONTRIBUTION),

        ;

        private ClientTerms term;
        private Detail buildingDetail;

        private Income(ClientTerms term) {
            this(term, null);
        }

        private Income(ClientTerms term, Detail buildingDetail) {
            this.term = term;
            this.buildingDetail = buildingDetail;
        }

        public Detail getDetail() {
            return this.buildingDetail;
        }

        @Override
        public ClientTerms getTranslationTerm() {
            return term;
        }
    }

    private static final long serialVersionUID = -1096671485716525649L;

    @XMLValue
    private Income incomeType = Income.DEFAULT;

    public IncomeBookValue() {

    }

    public IncomeBookValue(final Stakeholder stakeholder, final MapLink mapLink, Integer linkID, Income incomeType, final String name,
            final double value) {
        super(stakeholder, mapLink, linkID, name, value);
        this.incomeType = incomeType;
    }

    @Override
    public Income getType() {
        return incomeType;
    }
}

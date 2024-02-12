/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dto;

import java.io.Serializable;


/**
 * Enriched version of {@link Trade} which includes the company name
 *
 * We use java.io.{@link java.io.Serializable} here for the sake of simplicity.
 * In production, Hazelcast Custom Serialization should be used.
 */
public class EnrichedTrade extends Trade implements Serializable {

    private final String companyName;

    public EnrichedTrade(Trade trade, String companyNme) {
        super(trade.getTime(),trade.getSymbol(),trade.getQuantity(), trade.getPrice());
        this.companyName = companyNme;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String toString() {
        return "Trade{time=" + getTime() + ", company name='" + getCompanyName() + '\''
                + ", quantity=" + getQuantity() + ", price=" + getPrice() + '}';
    }
}
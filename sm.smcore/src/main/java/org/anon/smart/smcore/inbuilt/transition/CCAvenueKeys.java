/**
 * SMART - State Machine ARchiTecture
 *
 * Copyright (C) 2012 Individual contributors as indicated by
 * the @authors tag
 *
 * This file is a part of SMART.
 *
 * SMART is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SMART is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * */
 
/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.smcore.inbuilt.transition.CCAvenueKeys
 * Author:              rsankar
 * Revision:            1.0
 * Date:                02-08-2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of mandatory keys for posting to CC Avenue
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.inbuilt.transition;

public interface CCAvenueKeys
{
    public static final String MERCHANT_ID = "merchant_id";
    public static final String ORDER_ID = "order_id";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String DEFAULT_CURRENCY = "INR";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String REDIRECT_URL_DEFAULT = "CCAvenueResponse";
    public static final String REDIRECT_URL_CANCEL = "CCAvenueCancel";
    public static final String CANCEL_URL = "cancel_url";
    public static final String LANGUAGE = "language";
    public static final String LANGUAGE_DEFAULT = "EN";

    public static final String PAY_OPTION = "pay_option";
    public static final String CARD_TYPE = "card_type";
    public static final String CARD_NAME = "card_name";
    public static final String CARD_NUMBER = "card_number";
    public static final String EXPIRY_MONTH = "expiry_month";
    public static final String EXPIRY_YEAR = "expiry_year";
    public static final String CVV_NUMBER = "cvv_number";
    public static final String ISSUING_BANK = "issuing_bank";
    public static final String MOBILE_NUMBER = "mobile_number";

    public static final String MERCHANT_PARAM1 = "merchant_param1";
    public static final String MERCHANT_PARAM2 = "merchant_param2";

    public static final String FORMAT = "form";
    public static final String INITTRANSACTION = "/transaction/transaction.do?command=initiateTransaction";

    public static final String ENCREQUEST = "encRequest";
    public static final String ACCESSCODE = "access_code";
    public static final String ENCRESPONSE = "encResp";
}


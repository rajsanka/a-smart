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
 * File:                org.anon.smart.smcore.inbuilt.transition.CCAvenueServices
 * Author:              rsankar
 * Revision:            1.0
 * Date:                02-08-2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of services provided from CCAvenue
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.inbuilt.transition;

import java.util.Map;
import java.util.HashMap;

//import com.ccavenue.security.AesCryptUtil;

import org.anon.smart.base.tenant.CrossLinkSmartTenant;
import org.anon.smart.smcore.data.ConfigData;
import org.anon.smart.smcore.config.ConfigService;
import org.anon.smart.smcore.events.SmartEvent;
import org.anon.smart.smcore.inbuilt.events.CCAvenueCancel;
import org.anon.smart.smcore.inbuilt.events.CCAvenueResponse;
import org.anon.smart.smcore.inbuilt.config.CCAvenueConfig;
import org.anon.smart.smcore.inbuilt.transition.ccavenue.AesCryptUtil;
import org.anon.smart.smcore.channel.client.FormFormat;
import org.anon.smart.smcore.channel.client.pool.ClientConfig;
import org.anon.smart.smcore.channel.client.pool.ClientObjectCreator;
import org.anon.smart.smcore.channel.client.pool.HTTPClientObject;
import org.anon.smart.smcore.channel.distill.translation.FormTranslator;
import org.anon.smart.smcore.anatomy.SMCoreContext;

import org.anon.utilities.pool.Pool;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class CCAvenueServices implements CCAvenueKeys
{
    private enum payoption { OPTCRDC, OPTDBCRD, OPTNBK, OPTCASHC, OPTMOBP };


    public CCAvenueServices()
    {
    }

    private Pool getCCAvenueAPIPool(CCAvenueConfig cfg)
        throws CtxException
    {
        ClientConfig ccfg = new ClientConfig("CCAVEPOOL", cfg.getCCAvenueServer(), 80, 1, FORMAT);
        Pool p = ClientObjectCreator.getPool(ccfg);
        return p;
    }

    private CCAvenueConfig getConfig()
        throws CtxException
    {
        Class cls = CCAvenueConfig.class;
        Class<? extends ConfigData> ccls = (Class<? extends ConfigData>)cls;
        Object cfg = ConfigService.configFor("CCAVENUE", ccls);
        CCAvenueConfig scfg = (CCAvenueConfig)cfg;
        assertion().assertNotNull(scfg, "Please setup an CCAvenue config for key CCAVENUE before calling this service");
        return scfg;
    }

    private void startCCAvenueTransaction(Map data, CCAvenueConfig scfg)
        throws CtxException
    {
        Pool p = getCCAvenueAPIPool(scfg);
        HTTPClientObject hclient = (HTTPClientObject)p.lockone();
        String post = hclient.getFormatted(data, FORMAT);
        String uri = INITTRANSACTION;
        AesCryptUtil aesUtil = new AesCryptUtil(scfg.getWorkingKey());
        String encreq = aesUtil.encrypt(post);
        Map postdata = new HashMap();
        postdata.put(ENCREQUEST, encreq);
        postdata.put(ACCESSCODE, scfg.getAccessCode());
        String pdata = hclient.getFormatted(data, FORMAT);
        hclient.postData(uri, pdata, false);
        p.unlockone(hclient);
    }

    public boolean initiateTransaction(SmartEvent event, Map values, String primeType, String key)
        throws CtxException
    {
        CCAvenueConfig cfg = getConfig();

        assertion().assertNotNull(event, "Please provide the event that is triggering this payment.");
        assertion().assertNotNull(values, "Please provide the values to post to ccavenue");

        assertion().assertNotNull(cfg.getMerchantId(), "Please configure the ccavenue with correct merchant id");
        assertion().assertTrue((cfg.getMerchantId().length() > 0), "Please configure the ccavenue with correct merchant id");
        assertion().assertNotNull(cfg.getWorkingKey(), "Please configure the ccavenue with correct working key");
        assertion().assertTrue((cfg.getWorkingKey().length() > 0), "Please configure the ccavenue with correct working key");
        assertion().assertNotNull(cfg.getAccessCode(), "Please configure the ccavenue with correct access code");
        assertion().assertTrue((cfg.getAccessCode().length() > 0), "Please configure the ccavenue with correct access code");

        String[] requiredFlds = new String[] 
            {
                ORDER_ID,
                AMOUNT,
                PAY_OPTION,
                CARD_TYPE,
                CARD_NAME,
                CARD_NUMBER,
                EXPIRY_MONTH,
                EXPIRY_YEAR,
                CVV_NUMBER,
                ISSUING_BANK
            };

        for (int i = 0; i < requiredFlds.length; i++)
            assertion().assertTrue(values.containsKey(requiredFlds[i]), "CCAvenue Transaction requires field " + requiredFlds[i] + ". Not present, please provide.");

        if (!values.containsKey(CURRENCY)) values.put(CURRENCY, DEFAULT_CURRENCY);
        if (!values.containsKey(LANGUAGE)) values.put(LANGUAGE, LANGUAGE_DEFAULT);

		CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        String flow = event.smart___flowname();
        //need to get the server and port and add it here?
        String svr = SMCoreContext.server("paymentgateway");
        int port = SMCoreContext.port("paymentgateway");
        String redirect = "http://" + svr + ":" + port + "/" + tenant.getName() + "/" + flow + "/" + REDIRECT_URL_DEFAULT;
        String cancel = "http://" + svr + ":" + port + "/" + tenant.getName() + "/" + flow + "/" + REDIRECT_URL_CANCEL;
        values.put(REDIRECT_URL, redirect);
        values.put(CANCEL_URL, cancel);
        values.put(MERCHANT_ID, cfg.getMerchantId());
        values.put(MERCHANT_PARAM1, primeType);
        values.put(MERCHANT_PARAM2, key);
        startCCAvenueTransaction(values, cfg);
        return false;
    }

    public boolean handleResponse(CCAvenueResponse resp)
        throws CtxException
    {
        String encResp = resp.getEncryptedResponse();
        CCAvenueConfig scfg = getConfig();
        AesCryptUtil aesUtil = new AesCryptUtil(scfg.getWorkingKey());
        String response = aesUtil.decrypt(encResp);

        FormTranslator translate = new FormTranslator();
        Map<String, Object> respvalues = translate.translateString(response);
        System.out.println("Got back from payment gateway " + respvalues);

        return false;
    }

    public boolean handleCancel(CCAvenueCancel resp)
        throws CtxException
    {
        handleResponse(resp);
        System.out.println("CANCELLED...");
        return false;
    }
}


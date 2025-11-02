package com.sky.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.sky.properties.AlipayProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * 支付宝沙箱支付工具类
 */
@Component
public class AliPaySandboxUtil {

    private static final String SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    @Autowired
    private AlipayProperties aliPayProperties;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AliPaySandboxUtil.class);

    private AlipayClient getClient() {
        return new DefaultAlipayClient(
                SERVER_URL,
                aliPayProperties.getAppId(),
                aliPayProperties.getPrivateKey(),
                "json",
                "UTF-8",
                aliPayProperties.getPublicKey(),
                "RSA2");
    }

    public String pay(String outTradeNo, BigDecimal totalAmount, String subject) throws AlipayApiException {
        log.info("=== 支付宝支付请求 ===");
        log.info("订单号: {}", outTradeNo);

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setReturnUrl(aliPayProperties.getReturnUrl());
        request.setNotifyUrl(aliPayProperties.getNotifyUrl());

        com.alipay.api.domain.AlipayTradeWapPayModel model = new com.alipay.api.domain.AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(totalAmount.toPlainString());
        model.setSubject(subject);
        model.setProductCode("QUICK_WAP_WAY");
        request.setBizModel(model);

        try {
            String result = getClient().pageExecute(request).getBody();
            return result;
        } catch (AlipayApiException e) {
            log.error("支付宝API异常: ", e);
            throw e;
        }
    }

    // 在 AliPaySandboxUtil 中添加
    public String getAppId() {
        return aliPayProperties.getAppId();
    }

    public String getNotifyUrl() {
        return aliPayProperties.getNotifyUrl();
    }

    public String getReturnUrl() {
        return aliPayProperties.getReturnUrl();
    }
}
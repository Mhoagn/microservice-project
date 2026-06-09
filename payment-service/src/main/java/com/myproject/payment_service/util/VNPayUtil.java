package com.myproject.payment_service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class VNPayUtil {

    private static final Logger log = LoggerFactory.getLogger(VNPayUtil.class);

    public static String hmacSHA512(String key, String data) {

        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");

            hmac512.init(secretKey);

            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean verifySignature(
            HttpServletRequest request,
            String secretKey
    ) {

        Map<String, String> fields =
                new TreeMap<>();

        Enumeration<String> parameterNames =
                request.getParameterNames();

        while (parameterNames.hasMoreElements()) {

            String fieldName =
                    parameterNames.nextElement();

            String fieldValue =
                    request.getParameter(fieldName);

            if (fieldValue != null
                    && !fieldValue.isEmpty()) {

                if (!fieldName.equals("vnp_SecureHash")
                        && !fieldName.equals("vnp_SecureHashType")) {

                    fields.put(
                            fieldName,
                            fieldValue
                    );
                }
            }
        }

        StringBuilder hashData =
                new StringBuilder();

        Iterator<Map.Entry<String, String>> iterator =
                fields.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, String> entry =
                    iterator.next();

            hashData.append(
                    entry.getKey()
            );

            hashData.append("=");

            hashData.append(
                    URLEncoder.encode(
                            entry.getValue(),
                            StandardCharsets.US_ASCII
                    )
            );

            if (iterator.hasNext()) {
                hashData.append("&");
            }
        }

        String calculatedHash =
                hmacSHA512(
                        secretKey,
                        hashData.toString()
                );

        String receivedHash =
                request.getParameter(
                        "vnp_SecureHash"
                );

        log.debug("[VNPay] hashData    : {}", hashData);
        log.debug("[VNPay] calculated  : {}", calculatedHash);
        log.debug("[VNPay] received    : {}", receivedHash);

        return calculatedHash.equalsIgnoreCase(
                receivedHash
        );
    }
}

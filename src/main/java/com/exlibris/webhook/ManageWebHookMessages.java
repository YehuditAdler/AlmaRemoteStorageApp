package com.exlibris.webhook;

import org.json.JSONObject;

import com.exlibris.items.ItemData;
import com.exlibris.util.SCFUtil;

public class ManageWebHookMessages {

    final private static org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(ManageWebHookMessages.class);

    public static void getWebhookMessage(String webhookMessage) {
        if (webhookMessage.isEmpty()) {
            logger.info("message is empty");
            return;
        }
        JSONObject webhookJsonMessage = new JSONObject(webhookMessage);

        logger.info("institution is :" + webhookJsonMessage.getJSONObject("institution").get("value"));
        logger.info("action is :" + webhookJsonMessage.get("action"));
        logger.info("event is :" + webhookJsonMessage.getJSONObject("event").get("value"));

        messageHandling(webhookJsonMessage);
    }

    public static void messageHandling(JSONObject webhookMessage) {
        if (webhookMessage.getString("action").equals("LOAN")
                && webhookMessage.getJSONObject("event").getString("value").equals("LOAN_RETURNED")) {

            String userId = webhookMessage.getJSONObject("item_loan").getString("user_id");
            String institution = getInsByUserId(userId);
            if (institution == null) {
                logger.info("Request Not Part Of RCF. userId: " + userId);
                return;
            }
            String barcode = webhookMessage.getJSONObject("item_loan").getString("item_barcode");
            if (barcode.startsWith("X")) {
                barcode = barcode.substring(1);
            }
            ItemData itemData = new ItemData(barcode, institution, null, null,
                    webhookMessage.getJSONObject("item_loan").getString("mms_id"));
            logger.info("Scan In Request. Barcode: " + itemData.getBarcode());
            JSONObject jsonItemObject = SCFUtil.getINSItem(itemData);
            if (jsonItemObject != null) {
                SCFUtil.scanINSRequest(jsonItemObject, itemData);
            }
        }
    }

    private static String getInsByUserId(String userId) {
        try {
            return userId.substring(0, userId.indexOf("-"));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }
}

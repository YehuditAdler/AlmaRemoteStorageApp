package com.exlibris.webhook;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/webhook")
public class WebhookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    final private static Logger logger = Logger.getLogger(WebhookServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String challenge = req.getParameter("challenge");
        logger.info("challenge is :" + challenge);
        logger.debug("challenge is :" + challenge + " - DEBUG");
        resp.setContentType("application/json");
        JSONObject json = new JSONObject();
        json.put("challenge", challenge);
        resp.getWriter().write(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String str, body = "";
        while ((str = request.getReader().readLine()) != null) {
            body += str;
        }
        logger.info("message is :" + body);
        ManageWebHookMessages.getWebhookMessage(body);

    }
}
package com.commerce;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import javax.servlet.ServletException;
import java.io.IOException;

public class CustomDroplet extends DynamoServlet {
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.1.2/Java/com/commerce/CustomDroplet.java#1 $$Change: 713790 $";

    static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    private String text = "Default text";

    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        pRequest.setParameter("text", text);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
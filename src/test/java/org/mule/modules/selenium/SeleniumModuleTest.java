/**
 Mule Selenium Module

 Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com

 The software in this package is published under the terms of the CPAL v1.0
 license, a copy of which has been included with this distribution in the
 LICENSE.txt file.
 */
package org.mule.modules.selenium;

import org.junit.Test;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.http.HttpConnector;

public class SeleniumModuleTest extends FunctionalTestCase {

    @Override
    protected MuleContext createMuleContext() throws Exception {
        MuleContext muleContext = super.createMuleContext();
        muleContext.getRegistry().registerObject("connector.http.mule.default", new HttpConnector(muleContext));
        return muleContext;
    }

    @Override
    protected String getConfigResources() {
        return "mule-config.xml";
    }

    @Test
    public void testQueryGoogle() throws Exception {
        runFlow("testQueryGoogle");
    }

    /**
     * Run the flow specified by name
     *
     * @param flowName The name of the flow to run
     */
    protected void runFlow(String flowName) throws Exception {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);
    }


    /**
     * Run the flow specified by name and assert equality on the expected output
     *
     * @param flowName The name of the flow to run
     * @param expect   The expected output
     */
    protected <T> void runFlowAndExpect(String flowName, T expect) throws Exception {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);

        assertEquals(expect, responseEvent.getMessage().getPayload());
    }

    /**
     * Run the flow specified by name using the specified payload and assert
     * equality on the expected output
     *
     * @param flowName The name of the flow to run
     * @param expect   The expected output
     * @param payload  The payload of the input event
     */
    protected <T, U> void runFlowWithPayloadAndExpect(String flowName, T expect, U payload) throws Exception {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleTestCase.getTestEvent(payload);
        MuleEvent responseEvent = flow.process(event);

        assertEquals(expect, responseEvent.getMessage().getPayload());
    }

    /**
     * Retrieve a flow by name from the registry
     *
     * @param name Name of the flow to retrieve
     */
    protected Flow lookupFlowConstruct(String name) {
        return (Flow) AbstractMuleTestCase.muleContext.getRegistry().lookupFlowConstruct(name);
    }
}

Mule TwiML Module
=================

A Mule module for generating Twilios Markup Language. Twilio can handle instructions for calls and SMS messages in
real time from iON applications. When an SMS or incoming call is received, Twilio looks up the iON app associated
with the phone number called and makes a request to it. iON will respond to the request and that response will
decides how the call should proceed by returning a Twilio Markup XML (TwiML) document telling Twilio to say text
to the caller, send an SMS message, play audio files, get input from the keypad, record audio, connect the call
to another phone and more.

TwiML is similar to HTML. Just as HTML is rendered in a browser to display a webpage, TwiML is 'rendered' by Twilio
to the caller. Only one TwiML document is rendered to the caller at once but many documents can be linked together
to build complex interactive voice applications.

Outgoing calls are controlled in the same manner as incoming calls using TwiML. The initial flow for the call is
provided as a parameter to the Twilio Cloud Connector.

Installation
------------

The module can either be installed for all applications running within the Mule instance or can be setup to be used
for a single application.

*All Applications*

Download the module from the link above and place the resulting jar file in
/lib/user directory of the Mule installation folder.

*Single Application*

To make the module available only to single application then place it in the
lib directory of the application otherwise if using Maven to compile and deploy
your application the following can be done:

Add the connector's maven repo to your pom.xml:

    <repositories>
        <repository>
            <id>mulesoft-snapshots</id>
            <name>MuleForge Snapshot Repository</name>
            <url>https://repository.mulesoft.org/snapshots/</url>
            <layout>default</layout>
        </repsitory>
    </repositories>

Add the connector as a dependency to your project. This can be done by adding
the following under the dependencies element in the pom.xml file of the
application:

    <dependency>
        <groupId>org.mule.modules</groupId>
        <artifactId>mule-module-twiml</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

Usage
-----

The following is an example on how to retrieve an invoice based on the account number using our Zuora Cloud Connector:

	<?xml version="1.0" encoding="UTF-8"?>
	<mule xmlns="http://www.mulesoft.org/schema/mule/core"
	      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	      xmlns:spring="http://www.springframework.org/schema/beans"
	      xmlns:http="http://www.mulesoft.org/schema/mule/http"
	      xmlns:json="http://www.mulesoft.org/schema/mule/json"
	      xmlns:script="http://www.mulesoft.org/schema/mule/scripting"
	      xmlns:twiml="http://repository.mulesoft.org/releases/org/mule/modules/mule-module-twiml"
	      xmlns:zuora="http://repository.mulesoft.org/releases/org/mule/modules/mule-module-zuora"
	      xsi:schemaLocation="
	        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/3.1/mule.xsd
	        http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/3.1/mule-http.xsd
	        http://www.mulesoft.org/schema/mule/json http://www.mulesoft.org/schema/mule/json/3.1/mule-json.xsd
	        http://www.mulesoft.org/schema/mule/scripting http://www.mulesoft.org/schema/mule/scripting/3.1/mule-scripting.xsd
	        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
	        http://repository.mulesoft.org/releases/org/mule/modules/mule-module-twiml http://repository.mulesoft.org/releases/org/mule/modules/mule-module-twiml/1.0/mule-twiml.xsd
	        http://repository.mulesoft.org/releases/org/mule/modules/mule-module-zuora http://repository.mulesoft.org/releases/org/mule/modules/mule-module-zuora/1.0/mule-zuora.xsd">

	    <description>
	        Zuora TwiML App
	    </description>

	    <zuora:config username="xxx"
	                  password="yyy" endpoint="https://apisandbox.zuora.com/apps/services/a/29.0"/>

	    <twiml:config>
	        <twiml:http-callback-config localPort="8081" remotePort="80"/>
	    </twiml:config>

	    <flow name="main">
	        <http:inbound-endpoint host="localhost" port="8081"/>
	        <twiml:response>
	            <twiml:say>You have reached our support line</twiml:say>
	            <twiml:gather action-flow-ref="get-account">
	                <twiml:say>Please enter your account number, followed by pound sign.</twiml:say>
	            </twiml:gather>
	            <twiml:say>Sorry I didn't recognize that. Goodbye!</twiml:say>
	        </twiml:response>
	    </flow>

	    <flow name="input-account">
	        <twiml:gather action-flow-ref="get-account">
	            <twiml:say>Please enter your account number, followed by pound sign.</twiml:say>
	        </twiml:gather>
	        <twiml:say>Sorry I didn't recognize that. Goodbye!</twiml:say>
	    </flow>

	    <flow name="get-account">
	        <logger level="INFO" message="Retrieving account A#[groovy:message.getInboundProperty('digits')]"/>
	        <zuora:find zquery="SELECT Id, Name FROM Account WHERE AccountNumber='A#[groovy:message.getInboundProperty('digits')]'"/>
	        <script:component>
	            <script:script engine="groovy">
	                payload.firstPage().getRecords().get(0)
	            </script:script>
	        </script:component>
	        <logger level="INFO"/>
	        <choice>
	            <when>
	                <payload-type-filter expectedType="com.zuora.api.object.Account"/>
	                <script:component>
	                    <script:script engine="groovy">
	                        message.setInboundProperty("AccountId", payload.getId())
	                        message.setInboundProperty("Name", payload.getName())
	                        payload
	                    </script:script>
	                </script:component>
	                <flow-ref name="retrieve-invoices"/>
	            </when>
	            <otherwise>
	                <twiml:response>
	                    <twiml:say>I'm sorry I don't know any account by that number.</twiml:say>
	                    <!-- <flow-ref name="input-account"/> -->
	                    <twiml:gather action-flow-ref="get-account">
	                        <twiml:say>Please enter your account number, followed by pound sign.</twiml:say>
	                    </twiml:gather>
	                    <twiml:say>Sorry I didn't recognize that. Goodbye!</twiml:say>
	                </twiml:response>
	                <logger level="INFO" message="#[payload]"/>
	            </otherwise>
	        </choice>
	    </flow>

	    <flow name="retrieve-invoices">
	        <zuora:find zquery="SELECT Balance, DueDate FROM Invoice WHERE AccountId='#[bean:id]'"/>
	        <script:component>
	            <script:script engine="groovy">
	                payload.firstPage().getRecords().get(payload.firstPage().getSize()-1)
	            </script:script>
	        </script:component>
	        <logger level="INFO" message="#[payload]"/>
	        <script:component>
	            <script:script engine="groovy">
	                message.setInboundProperty("FormattedDueDate", payload.getDueDate().toGregorianCalendar().getTime().format("MMMMM dd yyyy"))
	                payload
	            </script:script>
	        </script:component>
	        <logger level="INFO"/>
	        <twiml:response>
	            <twiml:say>
	                <expression-transformer evaluator="string" expression="Hello #[groovy:message.getInboundProperty('Name')]! You have an invoice with a balance of #[groovy:payload.getBalance()] due on #[groovy:message.getInboundProperty('FormattedDueDate')]."/>
	            </twiml:say>
	        </twiml:response>
	    </flow>

	</mule>

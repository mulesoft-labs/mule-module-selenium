** THIS MODULE IS UNDER HEAVY DEVELOPMENT **

Mule Selenium Module
=================

Mule Selenium Module accepts commands and sends them to a browser. This is implemented internally using Selenium WebDriver, which sends commands to a browser, and retrieves results. Most browser drivers actually launch and access a browser application (such as Firefox or Internet Explorer); there is also a HtmlUnit browser driver, which simulates a browser using HtmlUnit.

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
        <artifactId>mule-module-selenium</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

Usage
-----

TO BE UPDATED
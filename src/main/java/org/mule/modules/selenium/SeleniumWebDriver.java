/**
 Mule Selenium Module

 Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com

 The software in this package is published under the terms of the CPAL v1.0
 license, a copy of which has been included with this distribution in the
 LICENSE.txt file.
 */
package org.mule.modules.selenium;

import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.iphone.IPhoneDriver;

/**
 * List of Selenium Web drivers.
 */
public enum SeleniumWebDriver {
    /**
     * Firefox
     */
    FIREFOX(FirefoxDriver.class),

    /**
     * Google Chrome
     */
    CHROME(ChromeDriver.class),

    /**
     * Html Unit
     */
    HTMLUNIT(HtmlUnitDriver.class),

    /**
     * Android
     */
    ANDROID(AndroidDriver.class),

    /**
     * iPhone
     */
    IPHONE(IPhoneDriver.class),

    /**
     * Internet Explorer
     */
    INTERNET_EXPLORER(InternetExplorerDriver.class);

    private Class<?> driverClass;

    private SeleniumWebDriver(Class<?> driverClass) {
        this.driverClass = driverClass;
    }

    public Class<?> getDriverClass() {
        return driverClass;
    }
}

/**
 Mule Selenium Module

 Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com

 The software in this package is published under the terms of the CPAL v1.0
 license, a copy of which has been included with this distribution in the
 LICENSE.txt file.
 */
package org.mule.modules.selenium;

import org.mule.api.NestedProcessor;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.Payload;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Selenium WebDriver is a tool for automating testing web applications, and in particular to verify that they work as
 * expected. It aims to provide a friendly API that's easy to explore and understand, which will help make your tests
 * easier to read and maintain.
 *
 * @author MuleSoft, Inc.
 */
@Module(name = "selenium")
public class SeleniumModule {

    private static Logger logger = LoggerFactory.getLogger(SeleniumModule.class);

    /**
     * Web driver to use
     */
    @Configurable
    @Optional
    @Default("HTMLUNIT")
    private SeleniumWebDriver driver;

    /**
     * Internal Selenium web driver
     */
    private WebDriver webDriver;

    @PostConstruct
    public void initDriver() throws IllegalAccessException, InstantiationException {
        webDriver = (WebDriver) driver.getDriverClass().newInstance();
    }

    @PreDestroy
    public void destroyDriver() {
        webDriver.quit();
    }

    /**
     * Load a new web page in the current browser window. This is done using an HTTP GET operation,
     * and the method will block until the load is complete. This will follow redirects issued either
     * by the server or as a meta-redirect from within the returned HTML. Should a meta-redirect
     * "rest" for any duration of time, it is best to wait until this timeout is over, since should
     * the underlying page change whilst your test is executing the results of future calls against
     * this interface will be against the freshly loaded page. Synonym for
     * {@link org.openqa.selenium.WebDriver.Navigation#to(String)}.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get}
     *
     * @param url The URL to load. It is best to use a fully qualified URL
     */
    @Processor
    public void get(String url) {
        webDriver.get(url);
    }

    /**
     * Get a string representing the current URL that the browser is looking at.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-current-url}
     *
     * @return The URL of the page currently loaded in the browser
     */
    @Processor
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    /**
     * The title of the current page.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-title}
     *
     * @return The title of the current page, with leading and trailing whitespace stripped, or null
     *         if one is not already set
     */
    @Processor
    public String getTitle() {
        return webDriver.getTitle();
    }

    /**
     * Find all elements within the current page using the given mechanism.
     * This method is affected by the 'implicit wait' times in force at the time of execution. When
     * implicitly waiting, this method will return as soon as there are more than 0 items in the
     * found collection, or will return an empty list if the timeout is reached.
     * <p/>
     * Only one of the attributes can be used at any given time.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:find-elements}
     *
     * @param id              The value of the "id" attribute to search for
     * @param linkText        The exact text to match against
     * @param partialLinkText The text to match against
     * @param name            The value of the "name" attribute to search for
     * @param tagName         The element's tagName
     * @param xpathExpression The xpath to use
     * @param className       The value of the "class" attribute to search for
     * @return A list of all {@link WebElement}s, or an empty list if nothing matches
     */
    @Processor
    public List<WebElement> findElements(@Optional String id,
                                         @Optional String linkText,
                                         @Optional String partialLinkText,
                                         @Optional String name,
                                         @Optional String tagName,
                                         @Optional String xpathExpression,
                                         @Optional String className) {

        if (id == null && linkText == null && partialLinkText == null &&
                name == null && tagName == null && xpathExpression == null &&
                className == null) {
            throw new IllegalArgumentException("At least one find criteria must be specified.");
        }

        if (!onlyOne(id, linkText, partialLinkText, name, tagName, xpathExpression, className)) {
            throw new IllegalArgumentException("Only one attribute can be used");
        }

        if (id != null) {
            return webDriver.findElements(By.id(id));
        } else if (linkText != null) {
            return webDriver.findElements(By.linkText(linkText));
        } else if (partialLinkText != null) {
            return webDriver.findElements(By.partialLinkText(partialLinkText));
        } else if (name != null) {
            return webDriver.findElements(By.name(name));
        } else if (tagName != null) {
            return webDriver.findElements(By.tagName(tagName));
        } else if (xpathExpression != null) {
            return webDriver.findElements(By.xpath(xpathExpression));
        } else if (className != null) {
            return webDriver.findElements(By.className(className));
        }

        return null;
    }

    /**
     * Find the first {@link WebElement} using the given method.
     * This method is affected by the 'implicit wait' times in force at the time of execution.
     * The findElement(..) invocation will return a matching row, or try again repeatedly until
     * the configured timeout is reached.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:find-element}
     *
     * @param id              The value of the "id" attribute to search for
     * @param linkText        The exact text to match against
     * @param partialLinkText The text to match against
     * @param name            The value of the "name" attribute to search for
     * @param tagName         The element's tagName
     * @param xpathExpression The xpath to use
     * @param className       The value of the "class" attribute to search for   * @return The first matching element on the current page
     * @return A singlel {@link WebElement}, or null if nothing matches
     * @throws org.openqa.selenium.NoSuchElementException
     *          If no matching elements are found
     */
    @Processor
    public WebElement findElement(@Optional String id,
                                  @Optional String linkText,
                                  @Optional String partialLinkText,
                                  @Optional String name,
                                  @Optional String tagName,
                                  @Optional String xpathExpression,
                                  @Optional String className) {

        if (id == null && linkText == null && partialLinkText == null &&
                name == null && tagName == null && xpathExpression == null &&
                className == null) {
            throw new IllegalArgumentException("At least one find criteria must be specified.");
        }

        if (!onlyOne(id, linkText, partialLinkText, name, tagName, xpathExpression, className)) {
            throw new IllegalArgumentException("Only one attribute can be used");
        }

        if (id != null) {
            return webDriver.findElement(By.id(id));
        } else if (linkText != null) {
            return webDriver.findElement(By.linkText(linkText));
        } else if (partialLinkText != null) {
            return webDriver.findElement(By.partialLinkText(partialLinkText));
        } else if (name != null) {
            return webDriver.findElement(By.name(name));
        } else if (tagName != null) {
            return webDriver.findElement(By.tagName(tagName));
        } else if (xpathExpression != null) {
            return webDriver.findElement(By.xpath(xpathExpression));
        } else if (className != null) {
            return webDriver.findElement(By.className(className));
        }

        return null;
    }

    /**
     * Click the element at the payload. If this causes a new page to load, this method will block until the page
     * has loaded. At this point, you should discard all references to this element and any further
     * operations performed on this element will have undefined behaviour unless you know that the
     * element and the page will still be present. If click() causes a new page to be loaded via an
     * event or is done by sending a native event (which is a common case on Firefox, IE on Windows)
     * then the method will *not* wait for it to be loaded and the caller should verify that a new
     * page has been loaded.
     * <p/>
     * If this element is not clickable, then this operation is a no-op since it's pretty common for
     * someone to accidentally miss the target when clicking in Real Life
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:click}
     *
     * @param element Element located at the payload of the message
     */
    @Processor
    public void click(@Payload WebElement element) {
        element.click();
    }

    /**
     * If the element at the payload is a form, or an element within a form, then this will be submitted to
     * the remote server. If this causes the current page to change, then this method will block until
     * the new page is loaded.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:submit}
     *
     * @param element Element located at the payload of the message
     * @throws {@link java.util.NoSuchElementException} If the given element is not within a form
     */
    @Processor
    public void submit(@Payload WebElement element) {
        element.submit();
    }

    /**
     * Use this method to simulate typing into an element, which may set its value. The payload
     * must be of type {@link WebElement}
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:send-keys}
     *
     * @param element Element located at the payload of the message
     * @param keys    Keys to send
     */
    @Processor
    public void sendKeys(@Payload WebElement element, String keys) {
        element.sendKeys(keys);
    }

    /**
     * If the element at the payload is a text entry element, this will clear the value. Has no effect on other
     * elements. Text entry elements are INPUT and TEXTAREA elements.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:clear}
     *
     * @param element Element located at the payload of the message
     */
    @Processor
    public void clear(@Payload WebElement element) {
        element.clear();
    }

    /**
     * Get the tag name of this element. <b>Not</b> the value of the name attribute: will return
     * <code>"input"</code> for the element <code>&lt;input name="foo" /&gt;</code>.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-tag-name}
     *
     * @return The tag name of this element.
     */
    public String getTagName(@Payload WebElement element) {
        return element.getTagName();
    }

    /**
     * Get the value of a the given attribute of the element. Will return the current value, even if
     * this has been modified after the page has been loaded. More exactly, this method will return
     * the value of the given attribute, unless that attribute is not present, in which case the value
     * of the property with the same name is returned. If neither value is set, null is returned. The
     * "style" attribute is converted as best can be to a text representation with a trailing
     * semi-colon. The following are deemed to be "boolean" attributes, and will return either "true"
     * or "false":
     * <p/>
     * async, autofocus, autoplay, checked, compact, complete, controls, declare, defaultchecked,
     * defaultselected, defer, disabled, draggable, ended, formnovalidate, hidden, indeterminate,
     * iscontenteditable, ismap, itemscope, loop, multiple, muted, nohref, noresize, noshade,
     * novalidate, nowrap, open, paused, pubdate, readonly, required, reversed, scoped, seamless,
     * seeking, selected, spellcheck, truespeed, willvalidate
     * <p/>
     * Finally, the following commonly mis-capitalized attribute/property names are evaluated as
     * expected:
     * <p/>
     * <ul>
     * <li>"class"
     * <li>"readonly"
     * </ul>
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-attribute}
     *
     * @param element Element located at the payload of the message
     * @param name    The name of the attribute.
     * @return The attribute's current value or null if the value is not set.
     */
    @Processor
    public String getAttribute(@Payload WebElement element, String name) {
        return element.getAttribute(name);
    }

    /**
     * Determine whether or not this element is selected or not. This operation only applies to input
     * elements such as checkboxes, options in a select and radio buttons.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:is-selected}
     *
     * @param element Element located at the payload of the message
     * @return True if the element is currently selected or checked, false otherwise.
     */
    @Processor
    public boolean isSelected(@Payload WebElement element) {
        return element.isSelected();
    }

    /**
     * Is the element currently enabled or not? This will generally return true for everything but
     * disabled input elements.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:is-enabled}
     *
     * @param element Element located at the payload of the message
     * @return True if the element is enabled, false otherwise.
     */
    @Processor
    public boolean isEnabled(@Payload WebElement element) {
        return element.isEnabled();
    }

    /**
     * Get the visible (i.e. not hidden by CSS) innerText of this element, including sub-elements,
     * without any leading or trailing whitespace.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-text}
     *
     * @param element Element located at the payload of the message
     * @return The innerText of this element.
     */
    @Processor
    public String getText(@Payload WebElement element) {
        return element.getText();
    }

    /**
     * Is this element displayed or not? This method avoids the problem of having to parse an
     * element's "style" attribute.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:is-displayed}
     *
     * @param element Element located at the payload of the message
     * @return Whether or not the element is displayed
     */
    public boolean isDisplayed(@Payload WebElement element) {
        return element.isDisplayed();
    }

    /**
     * Where on the page is the top left-hand corner of the rendered element?
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-location}
     *
     * @param element Element located at the payload of the message
     * @return A point, containing the location of the top left-hand corner of the element
     */
    public Point getLocation(@Payload WebElement element) {
        return element.getLocation();
    }

    /**
     * What is the width and height of the rendered element?
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:get-size}
     *
     * @param element Element located at the payload of the message
     * @return The size of the element on the page.
     */
    public Dimension getSize(@Payload WebElement element) {
        return element.getSize();
    }

    /**
     * Wait until the condition is successful
     * <p/>
     * {@sample.xml ../../../doc/mule-module-selenium.xml.sample selenium:until}
     *
     * @param timeOutInSeconds The timeout in seconds when an expectation is called
     * @param conditional      Nested processor to be executed for evaluating conditions
     */
    @Processor
    public void until(@Optional @Default("10") long timeOutInSeconds,
                      final NestedProcessor conditional) {
        (new WebDriverWait(webDriver, timeOutInSeconds)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                try {
                    return Boolean.valueOf(conditional.process().toString());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return true;
                }
            }
        });
    }

    private boolean onlyOne(String... args) {
        int i = 0;
        for (String arg : args) {
            if (arg != null) {
                i++;
            }
        }
        return i == 1;
    }

    public SeleniumWebDriver getDriver() {
        return driver;
    }

    public void setDriver(SeleniumWebDriver driver) {
        this.driver = driver;
    }
}

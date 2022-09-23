package com.lambdatest.single.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GooglePage {
  private String DEFAULT_GOOGLE_URL = "https://www.google.com/ncr";
  private WebDriver driver;
  private WebElement element;

  public GooglePage(WebDriver driver) {
    this.driver = driver;
  }


  public void clickOnColor() throws InterruptedException {
        element = driver.findElement(By.id("com.lambdatest.proverbial:id/color"));
        element.click();
  }

  public void clickOnText() throws InterruptedException {
    element = driver.findElement(By.id("com.lambdatest.proverbial:id/Text"));
    element.click();
  }

  public void clickOnNotification() throws InterruptedException {
    element = driver.findElement(By.id("com.lambdatest.proverbial:id/toast"));
    element.click();
  }

  public void clickOnToast() throws InterruptedException {
    element = driver.findElement(By.id("com.lambdatest.proverbial:id/notification"));
    element.click();
  }

  public void clickOnGeolocation() throws InterruptedException {
    element = driver.findElement(By.id("com.lambdatest.proverbial:id/geoLocation"));
    element.click();
  }
}

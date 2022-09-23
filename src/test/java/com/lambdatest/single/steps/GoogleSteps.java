package com.lambdatest.single.steps;

import com.lambdatest.single.pages.GooglePage;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.annotations.Given;
import org.junit.Assert;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GoogleSteps {
  private GooglePage todo;

  public GoogleSteps(WebDriver driver) {
    todo = new GooglePage(driver);
  }

  @Given("user is on the App home page")
  public void user_already_on_home_page() {
    System.out.println("On Home page");
  }

  @When("click on color element")
  public void click_color()throws InterruptedException {
    todo.clickOnColor();
    System.out.println("Clicked Color button");
    Thread.sleep(3000);
  }

  @Then("click on text element")
  public void click_text()throws InterruptedException {
    todo.clickOnText();
    System.out.println("Clicked text button");
    Thread.sleep(3000);
  }

  @Then("click on notification element")
  public void click_notification()throws InterruptedException {
    todo.clickOnNotification();
    System.out.println("Clicked Notification button");
    Thread.sleep(3000);
  }

  @Then("click on toast element")
  public void click_toast()throws InterruptedException {
    todo.clickOnToast();
    System.out.println("Clicked Toast button");
    Thread.sleep(3000);
  }

  @Then("click on geolocation element")
  public void click_geolocation()throws InterruptedException {
    todo.clickOnGeolocation();
    System.out.println("Clicked geolocation button");
    Thread.sleep(3000);
  }
}

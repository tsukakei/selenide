package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface WebElementsCollection {
  /**
   * get elements of this collection (probably cached).
   */
  List<WebElement> getElements();

  String description();
}

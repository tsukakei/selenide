package integration;

import java.io.File;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

class ErrorMessagesWithScreenshotsTest extends IntegrationTest {
  private String reportsUrl;

  @BeforeEach
  final void setTimeout() {
    timeout = 0;
    openFile("page_with_selects_without_jquery.html");
  }

  @BeforeEach
  void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    Configuration.reportsUrl = "http://ci.org/";
    Screenshots.screenshots = new ScreenShotLaboratory() {
      @Override
      public String takeScreenShot() {
        return new File(reportsFolder, "1.jpg").getAbsolutePath();
      }
    };
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Screenshots.screenshots = new ScreenShotLaboratory();
  }

  @Test
  void parentNotFound() {
    try {
      $("#nonexisting-form")
        .findAll(byText("mymail@gmail.com"))
        .find(cssClass("trash"))
        .shouldBe(visible);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertThat(e)
        .hasMessageContaining("Element not found {#nonexisting-form}");
    }
  }

  @Test
  void itShouldBeReportedWhichParentElementIsNotFound() {
    try {
      $("#multirowTable")
        .find("thead")
        .find(byText("mymail@gmail.com"))
        .find(".trash")
        .shouldBe(visible);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertThat(e)
        .hasMessageContaining("Element not found {thead}");
      assertThat(e.getScreenshot())
        .isEqualTo("http://ci.org/build/reports/tests/" + browser + "/1.jpg");
    }
  }

  @Test
  void itShouldBeReportedIfParentCollectionIsNotFound() {
    try {
      $("#multirowTable")
        .findAll("thead")
        .findBy(text("mymail@gmail.com"))
        .find(".trash")
        .shouldBe(visible);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertThat(e)
        .hasMessageContaining("Element not found {#multirowTable/thead");
      assertThat(e.getScreenshot())
        .isEqualTo("http://ci.org/build/reports/tests/" + browser + "/1.jpg");
    }
  }

  @Test
  void elementNotFoundInsideParent() {
    try {
      $("#multirowTable")
        .findAll("tbody tr")
        .findBy(text("Norris"))
        .find(".second_row")
        .shouldBe(visible);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound e) {
      assertThat(e)
        .hasMessageContaining("Element not found {.second_row}");
    }
  }

  @Test
  void elementShouldNotBeFoundAndParentAlsoNotFound() {
    $("#multirowTable")
      .find("theeeead")
      .find(".second_row")
      .shouldNotBe(visible);
  }
}

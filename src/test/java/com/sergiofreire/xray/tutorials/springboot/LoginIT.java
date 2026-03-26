package com.sergiofreire.xray.tutorials.springboot;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;

import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;

class LoginIT {

    @Test
    @XrayTest(key = "ST-306")
    void loginWithValidCredentials() throws IOException {
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);

            // Step 1: Open website https://robotwebdemo.onrender.com
            HtmlPage loginPage = webClient.getPage("https://robotwebdemo.onrender.com");

            // Step 2: Enter "demo" for username and "mode" for the password field,
            // then submit it pressing the Login button
            HtmlInput usernameField = loginPage.getHtmlElementById("username_field");
            HtmlInput passwordField = loginPage.getHtmlElementById("password_field");
            HtmlInput loginButton = loginPage.getHtmlElementById("login_button");

            usernameField.setValueAttribute("demo");
            passwordField.setValueAttribute("mode");
            HtmlPage welcomePage = loginButton.click();

            // Expected result: the user should be redirected to a page showing
            // the "Welcome Page" header
            assertThat(welcomePage.getTitleText()).isEqualTo("Welcome Page");
        }
    }
}

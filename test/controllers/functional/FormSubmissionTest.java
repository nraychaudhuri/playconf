package controllers.functional;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.libs.F.Callback;
import play.test.TestBrowser;
import controllers.routes;

public class FormSubmissionTest {
    
    @Test
    public void runInBrowser() {
        Map<String, Object> dbSettings = new HashMap<String, Object>();
        dbSettings.put("db.default.url", "jdbc:mysql://localhost:3306/playconftest");
        
        running(testServer(3333, fakeApplication(dbSettings)), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
               browser.goTo("http://localhost:3333" + routes.Application.newProposal().url());
               assertThat(browser.title()).isEqualTo("PlayConf 2014 - Submit new talk");
               browser.fill("#title").with("This is a test play presentation");
               browser.fill("#proposal").with("This presention is going to talk about testing in play");
               browser.fill("#speaker_name").with("Nilanjan Raychaudhuri");
               browser.fill("#speaker_email").with("nilanjan@typesafe.com");
               browser.fill("#speaker_bio").with("Developer/Consultant/Author and overall nice guy");
               browser.fill("#speaker_pictureUrl").with("mug shot");
               browser.fill("#speaker_twitterId").with("nraychaudhuri");
               browser.submit("#submitForm");
               assertThat(browser.findFirst("#message").getText()).isEqualTo("Thanks for submitting the proposal. We will get back to you soon.");
            }
        });
    }
}

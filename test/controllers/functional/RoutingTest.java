package controllers.functional;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import play.mvc.Result;

public class RoutingTest {

    @Test
    public void requestIndexActionWithKeynote() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                Result result = route(fakeRequest("GET", "/"));
                assertThat(status(result)).isEqualTo(200);
                Document doc = Jsoup.parse(contentAsString(result));
                assertThat(doc.select("#title").text()).isEqualTo("Keynote - History of playframework");
                assertThat(doc.select("#speakerName").text()).isEqualTo("Guillaume Bort");
                assertThat(doc.select("#twitterId").text()).isEqualTo("guillaumebort");
            }
        });
    }
}

package views;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

import models.Speaker;
import models.Submission;

import org.junit.Test;

import play.api.templates.Html;
import static org.mockito.Mockito.*;

public class IndexViewTest {

    //test render home page
    @Test
    public void renderHomePage() {
        
        Speaker speaker = mock(Speaker.class);
        speaker.twitterId = "nraychaudhuri";
        speaker.pictureUrl = "picture url";

        Submission s = mock(Submission.class);
        s.title = "No Work just Play";
        s.proposal = "This is the description";
        s.speaker = speaker;
        Html html = views.html.index.render(s);
        assertThat(contentType(html)).isEqualTo("text/html");
    }
}

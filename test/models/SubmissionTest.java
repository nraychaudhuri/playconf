package models;

import static helpers.TestSetup.testGlobalSettings;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Test;

import com.avaje.ebean.Ebean;

public class SubmissionTest {
    @Test
    public void saveNewSubmission() {
        running(fakeApplication(testGlobalSettings()), new Runnable() {
            @Override
            public void run() {
                Submission s = sampleSubmission();
                s.save();
                assertThat(rowCount()).isEqualTo(1);
            }
        });
    }

    @Test
    public void savingSubmissionAlsoSavesSpeaker() {
        running(fakeApplication(testGlobalSettings()), new Runnable() {
            @Override
            public void run() {
                Submission s = sampleSubmission();
                s.speaker = sampleSpeaker();
                s.save();
                assertThat(rowCount()).isEqualTo(1);
                assertThat(Ebean.find(Speaker.class).findUnique().name)
                        .isEqualTo("Nilanjan");
            }
        });
    }

    private int rowCount() {
        return Ebean.find(Submission.class).findRowCount();
    }
      
    private Submission sampleSubmission() {
        Submission s = new Submission();
        s.title = "Best Java web development experience";
        s.proposal = "I enjoy web development and Play makes that experience even better";
        return s;
    }

    private Speaker sampleSpeaker() {
        Speaker speaker = new Speaker();
        speaker.name = "Nilanjan";
        speaker.bio = "Play core developer";
        speaker.pictureUrl = "my picture url";
        return speaker;
    }
}
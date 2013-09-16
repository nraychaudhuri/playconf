package models;

import static helpers.TestSetup.*;
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
                Proposal s = sampleSubmission();
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
                Proposal s = sampleSubmission();
                s.speaker = sampleSpeaker();
                s.save();
                assertThat(rowCount()).isEqualTo(1);
                assertThat(Ebean.find(Speaker.class).findUnique().name)
                        .isEqualTo("Nilanjan");
            }
        });
    }

    private int rowCount() {
        return Ebean.find(Proposal.class).findRowCount();
    }

}
package models;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;

import play.Configuration;
import play.GlobalSettings;
import static org.fest.assertions.Assertions.*;

public class SubmissionTest {

    private GlobalSettings testSettings = new GlobalSettings() {
        @Override
        public Configuration onLoadConfig(Configuration config, File path,
                ClassLoader classloader) {
            Map<String, Object> dbSettings = new HashMap<String, Object>();
            dbSettings.put("db.testdb.driver", "org.h2.Driver");
            dbSettings.put("db.testdb.user", "sa");
            dbSettings.put("db.testdb.url",
                    "jdbc:h2:mem:playconftest;MODE=MySQL");
            dbSettings.put("ebean.testdb", "models.*, helpers.*");
            return new Configuration(ConfigFactory.parseMap(dbSettings));
        }
    };

    @Test
    public void saveNewSubmission() {
        running(fakeApplication(testSettings), new Runnable() {
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
        running(fakeApplication(testSettings), new Runnable() {
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
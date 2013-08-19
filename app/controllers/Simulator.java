package controllers;

import static actors.EventPublisher.publisher;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.RegisteredUser;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import actors.messages.UserRegistrationEvent;
import external.services.OAuthService;
import external.services.TwitterOAuthService;
import global.Global;

public class Simulator extends Controller {

    private static List<String> twitterIds = Arrays.asList("nraychaudhuri",
            "mmahawar", "playframework", "springrod", "huntchr", "jroper",
            "derekhenninger", "StefanZeiger", "h3nk3", "chadfowler",
            "mabrewer7", "havocp", "Sadache", "guillaumebort", "tbjerkes",
            "_JamesWard", "retronym", "patriknw", "adriaanm", "rit",
            "jsuereth", "jamie_allen", "viktorklang", "hseeberger");

    private static int counter = 0;

    public static Result simulateRegistration() throws Exception {
        Akka.system()
                .scheduler()
                .schedule(Duration.create(1, TimeUnit.SECONDS),
                        Duration.create(10, TimeUnit.SECONDS), new Runnable() {
                            public void run() {
                                String twitterId = twitterIds.get(counter);
                                fireUserRegistrationEvent(twitterId);
                                counter++;
                                if (counter == twitterIds.size()) {
                                    counter = 0;
                                }
                            }
                        }, Akka.system().dispatcher());

        return ok("done");
    }

    private static void fireUserRegistrationEvent(String twitterId) {
        try {
            Promise<JsonNode> userProfile = ((TwitterOAuthService) Global.injector
                    .getInstance(OAuthService.class)).userProfile
                    .apply(twitterId);
            userProfile.onRedeem(new Callback<JsonNode>() {
                @Override
                public void invoke(JsonNode json) throws Throwable {
                    RegisteredUser u = RegisteredUser.fromJson(json);
                    publisher.tell(new UserRegistrationEvent(u), null);
                }
            });
        } catch (Throwable e) {
            Logger.error("Something went wrong", e);
        }
    }
}

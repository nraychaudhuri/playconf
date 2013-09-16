package global;

import java.util.concurrent.TimeUnit;

import models.Proposal;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.Duration;
import actors.EventPublisher;
import actors.messages.RandomlySelectTalkEvent;
import akka.actor.ActorRef;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

import external.services.OAuthService;
import external.services.TwitterOAuthService;

public class Global extends GlobalSettings {

    public static Injector injector = Guice
            .createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(ActorRef.class).toProvider(new Provider<ActorRef>() {
                        @Override
                        public ActorRef get() {
                            return EventPublisher.publisher;
                        }
                    });
                    bind(OAuthService.class)
                            .toProvider(new Provider<OAuthService>() {
                        @Override
                        public OAuthService get() {
                            String consumerKey = 
                                    Play.application().configuration().getString("twitter.consumer.key");
                            String consumerSecret = 
                                    Play.application().configuration().getString("twitter.consumer.secret");
                            return new TwitterOAuthService(consumerKey,
                                    consumerSecret);
                        }
                    });
                }
            });

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        Akka.system()
                .scheduler()
                .schedule(Duration.create(1, TimeUnit.SECONDS),
                        Duration.create(10, TimeUnit.SECONDS),
                        selectRandomTalk(), Akka.system().dispatcher());
    }

    private Runnable selectRandomTalk() {
        return new Runnable() {
            public void run() {
                Promise<Proposal> promiseOfJson = Proposal
                        .randomlyPickSession();
                promiseOfJson.onRedeem(new Callback<Proposal>() {
                    @Override
                    public void invoke(Proposal s) throws Throwable {
                        EventPublisher.publisher.tell(
                                new RandomlySelectTalkEvent(s), null);
                    }
                });

            }
        };
    }

    @Override
    public Result onHandlerNotFound(RequestHeader req) {
        return Results.notFound(views.html.error.render());
    }

    @Override
    public Result onError(RequestHeader rh, Throwable error) {
        return Results.internalServerError(views.html.error.render());
    }

    @Override
    public <A> A getControllerInstance(Class<A> clazz) throws Exception {
        return injector.getInstance(clazz);
    }
}

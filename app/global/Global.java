package global;

import java.util.concurrent.TimeUnit;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

import external.services.OAuthService;
import external.services.TwitterOAuthService;

import actors.EventPublisher;
import actors.messages.RandomlySelectTalkEvent;
import akka.actor.ActorRef;


import models.Submission;
import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Promise;
import play.mvc.Result;
import play.mvc.Http.RequestHeader;
import play.mvc.Results;
import scala.concurrent.duration.Duration;

public class Global extends GlobalSettings {
	
	private static String consumerKey = "ZH15OspjNAfn5cyfLGm8KA";
	private static String consumerSecret = "IKgL5u3KORkRDh9Ay78iBhrl3N4JWbQXxazCJNc";

	public static Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(ActorRef.class).toProvider(new Provider<ActorRef>() {
					@Override
					public ActorRef get() {
						return EventPublisher.publisher;
					}
				});
				bind(OAuthService.class).toInstance(new TwitterOAuthService(consumerKey, consumerSecret));
			}
		});

	@Override
	public void onStart(Application app) {
		super.onStart(app);
		Akka.system()
				.scheduler()
				.schedule(Duration.create(1, TimeUnit.SECONDS),
						Duration.create(10, TimeUnit.SECONDS), 
						selectRandomTalk(), 
						Akka.system().dispatcher());		
	}

	private Runnable selectRandomTalk() {
		return new Runnable() {
			public void run() {
				Promise<Submission> promiseOfJson = Submission
						.randomlyPickSession();
				promiseOfJson
						.onRedeem(new Callback<Submission>() {
							@Override
							public void invoke(Submission s)
									throws Throwable {
								EventPublisher.publisher
										.tell(new RandomlySelectTalkEvent(
												s), null);
							}
						});

			}
		};
	}

	@Override
	public Result onHandlerNotFound(RequestHeader arg0) {
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

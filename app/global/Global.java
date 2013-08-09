package global;

import java.util.concurrent.TimeUnit;

import actors.EventPublisher;
import actors.messages.RandomlySelectTalkEvent;


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
}

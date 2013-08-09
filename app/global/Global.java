package global;

import java.util.concurrent.TimeUnit;

import models.Submission;
import models.messages.RandomlySelectTalkEvent;
import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Promise;
import scala.concurrent.duration.Duration;

import common.UserActor;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		super.onStart(app);
		Akka.system()
				.scheduler()
				.schedule(Duration.create(1, TimeUnit.SECONDS),
						Duration.create(10, TimeUnit.SECONDS),
						new Runnable() {
							public void run() {
								Promise<Submission> promiseOfJson = 
										Submission.randomlyPickSession();
								promiseOfJson.onRedeem(new Callback<Submission>() {
									@Override
									public void invoke(Submission s) throws Throwable {
									  UserActor.users().tell(new RandomlySelectTalkEvent(s));
									}
								});

							}
						}, 
						Akka.system().dispatcher());

	}
}

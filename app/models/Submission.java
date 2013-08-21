package models;

import java.util.concurrent.Callable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.Akka;
import play.libs.F.Promise;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

@Entity
public class Submission extends Model {

    private static final long serialVersionUID = -2772900967190704959L;

    @Id
    public Long id;

    @Required
    public String title;

    @Required
    @MinLength(value = 10)
    @MaxLength(value = 1000)
    @Column(length = 1000)
    public String proposal;

    @Required
    public SessionType type = SessionType.OneHourTalk;

    @Required
    public Boolean isApproved = false;

    public String keywords;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Speaker speaker;

    public static Finder<Long, Submission> find = new Finder<Long, Submission>(
            Long.class, Submission.class);

    private static ExecutionContext ctx = Akka.system().dispatchers().lookup("akka.db-dispatcher");

    public static Promise<Submission> randomlyPickSession() {
        Future<Submission> f = akka.dispatch.Futures.future(new Callable<Submission>() {
            public Submission call() {
                // randomly select one if the first
                Long randomId = (long) (1 + Math.random() * (5 - 1));
                return Submission.find.byId(randomId);
            }
        }, ctx);
        return Akka.asPromise(f);
    }

    public static Promise<Submission> findKeynote() {
        Future<Submission> f = akka.dispatch.Futures.future(new Callable<Submission>() {
            public Submission call() {
                return find.where().eq("type", SessionType.Keynote).findUnique();
            }
        }, ctx);

        return Akka.asPromise(f);
    }
}

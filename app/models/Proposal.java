package models;

import static akka.dispatch.Futures.future;

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
import scala.concurrent.Future;

@Entity
public class Proposal extends Model {

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

    public static Finder<Long, Proposal> find = new Finder<Long, Proposal>(
            Long.class, Proposal.class);

    public static Promise<Proposal> randomlyPickSession() {
        Future<Proposal> f = akka.dispatch.Futures.future(
                new Callable<Proposal>() {
                    public Proposal call() {
                        // randomly select one if the first
                        Long randomId = (long) (1 + Math.random() * (5 - 1));
                        return Proposal.find.byId(randomId);
                    }
                }, DbExecutionContext.ctx);
        return Akka.asPromise(f);
    }

    public static Promise<Proposal> findKeynote() {
        Future<Proposal> f = future(new Callable<Proposal>() {
            public Proposal call() {
                return find.where().eq("type", SessionType.Keynote)
                        .findUnique();
            }
        }, DbExecutionContext.ctx);

        return Akka.asPromise(f);
    }

    public Promise<Void> asyncSave() {
        Future<Void> f = future(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                save();
                return null;
            }
        }, DbExecutionContext.ctx);
        return Akka.asPromise(f);
    }
}

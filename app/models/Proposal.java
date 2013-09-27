package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import common.DbExecutionContext;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;

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
        return F.Promise.promise(new Function0<Proposal>() {
            @Override
            public Proposal apply() throws Throwable {
                // randomly select one if the first
                Long randomId = (long) (1 + Math.random() * (5 - 1));
                return Proposal.find.byId(randomId);
            }
        } , DbExecutionContext.ctx);
    }

    public static Promise<Proposal> findKeynote() {
        return F.Promise.promise(new Function0<Proposal>() {
            @Override
            public Proposal apply() throws Throwable {
                return find.where().eq("type", SessionType.Keynote)
                        .findUnique();
            }
        } , DbExecutionContext.ctx);
    }

    public Promise<Void> asyncSave() {
        return F.Promise.promise(new Function0<Void>() {
            @Override
            public Void apply() throws Throwable {
                save();
                return null;
            }
        } , DbExecutionContext.ctx);
    }
}

package models;

import java.util.concurrent.Callable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.F.Promise;

import com.avaje.ebean.Ebean;

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

    @Required
    @OneToOne(cascade=CascadeType.ALL)
    public Speaker speaker;
    
    public void save() {
      	
      Ebean.save(this);	
    }
    
    public static Finder<Long, Submission> find = 
       new Finder<Long, Submission>(Long.class, Submission.class); 
    
	public static Promise<Submission> randomlyPickSession() {
		Promise<Submission> promiseOfSubmission = play.libs.Akka
				.future(new Callable<Submission>() {
					public Submission call() {
						// randomly select one if the first
						Long randomId = (long) (1 + Math.random() * (5 - 1));
						return Submission.find.byId(randomId);
					}
				});
		return promiseOfSubmission;
	}
}


package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

@Entity
public class Submission extends Model {

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
}


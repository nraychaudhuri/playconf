package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Speaker extends Model {
	
	@Id
	public Long id;
	
	@Required
	public String name;

	@Required
	@Email
	public String email;

	@Required
	public String bio;

	@Required
	public String pictureUrl;

	public String twitterId;
	
	@OneToMany
	List<Submission> submissions;

}
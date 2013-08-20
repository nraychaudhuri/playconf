package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
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
    @MinLength(value = 10)
    @MaxLength(value = 1000)
    @Column(length = 1000)
    public String bio;

    @Required
    public String pictureUrl;

    public String twitterId;

    @OneToMany
    List<Submission> submissions;

}
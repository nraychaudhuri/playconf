package models;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;

import com.avaje.ebean.PagingList;
import com.fasterxml.jackson.databind.JsonNode;
import common.DbExecutionContext;

@Entity
public class RegisteredUser extends Model {

    @Id
    public Long id;

    @Required
    public String name;

    @Required
    public String twitterId;

    @Required
    @MaxLength(value = 200)
    public String description;

    @Required
    public String pictureUrl;

    @Required
    public Date registrationDate = new Date(System.currentTimeMillis());

    public static Finder<Long, RegisteredUser> find = new Finder<Long, RegisteredUser>(
            Long.class, RegisteredUser.class);

    public static RegisteredUser fromJson(JsonNode twitterJson) {
        RegisteredUser u = new RegisteredUser();
        u.name = twitterJson.findPath("name").asText();
        u.twitterId = twitterJson.findPath("screen_name").asText();
        u.description = twitterJson.findPath("description").asText();
        u.pictureUrl = twitterJson.findPath("profile_image_url").asText();
        return u;
    }

    public static Promise<List<RegisteredUser>> recentUsers(final int count) {
        return F.Promise.promise(new Function0<List<RegisteredUser>>() {
            @Override
            public List<RegisteredUser> apply() throws Throwable {
                PagingList<RegisteredUser> xs = find.order().desc("registrationDate")
                        .findPagingList(count);
                return xs.getAsList();
            }
            
        }, DbExecutionContext.ctx);
    }

}

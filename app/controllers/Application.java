package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Filter;

import models.Submission;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
  
	private static Form<Submission> form = Form.form(Submission.class);
	
    public static Result index() {
        return ok(index.render(form));
    }
    
    public static Result submit() {
    	Form<Submission> filledForm = form.bindFromRequest();
    	if(filledForm.hasErrors()) {
    		return badRequest(index.render(filledForm));
    	} else {
    		Submission s = filledForm.get();    
    		s.save();
    		return redirect(routes.Application.index());
    	}
    }
    
    public static Result approvedSessions() {
    	Filter<Submission> eq = Submission.find.filter().eq("isApproved", true);
    	List<Submission> a = eq.filter(Submission.find.all());
    	return ok("");
    }
          
}

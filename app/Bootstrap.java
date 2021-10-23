import models.*;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        // Load default data if the database is empty
        if(User.count() == 0) {
            new User("s@gmail.com", "123", "Sergi").save();
            new User("j@gmail.com", "123", "Joan").save();
            new User("g@gmail.com", "123", "Gerard").save();
        }
    }
}
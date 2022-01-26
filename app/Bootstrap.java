import models.*;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import java.util.Calendar;
import java.util.Date;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        // Load default data if the database is empty
        if(User.count() == 0) {
            //Load three users
            User u1 = new User("s@gmail.com", "123", "Sergi").save();
            User u2 = new User("j@gmail.com", "123", "Joan").save();
            User u3 = new User("g@gmail.com", "123", "Gerard").save();
            User u4 = new User("admin@gmail.com", "123", "Admin");
            u4.admin = true;
            u4.save();

            //Load three messages
            Message m1 = new Message("Hello default", "Hello").save();
            Message m2 = new Message("Answer default", "Answer").save();
            Message m3 = new Message("Goodbye default", "Goodbye").save();

            //Load four relation between a message and a user
            new Message_User(m1, u1, u2, "main", new Date(2021 - 1900, Calendar.OCTOBER, 23, 13, 34, 0), false).save();
            new Message_User(m1, u1, u2, "spam", new Date(2021 - 1900, Calendar.OCTOBER, 23, 16, 34, 3), false).save();
            new Message_User(m2, u2, u3, "main", new Date(2021 - 1900, Calendar.OCTOBER, 23, 20, 57, 0), false).save();
            new Message_User(m3, u1, u2, "main", new Date(2021 - 1900, Calendar.OCTOBER, 13, 13, 34, 0), false).save();
        }
    }
}
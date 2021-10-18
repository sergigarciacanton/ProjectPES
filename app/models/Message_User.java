package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class Message_User extends Model {

    @ManyToOne
    public Message message;

    @ManyToOne
    public User user;

    public String inbox;
    public Date date;
    public Boolean forward;

    public Message_User(Message message, User user, String inbox, Date date, Boolean forward) {
        this.message = message;
        this.user = user;
        this.inbox = inbox;
        this.date = date;
        this.forward = forward;
    }
}

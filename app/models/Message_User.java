package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class Message_User extends Model {

    @ManyToOne
    public Message message;

    @ManyToOne
    public User sender;

    @ManyToOne
    public User receiver;

    public String inbox; //Cases: main, spam, sent
    public Date date;
    public Boolean forward;

    public Message_User(Message message, User sender, User receiver, String inbox, Date date, Boolean forward) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.inbox = inbox;
        this.date = date;
        this.forward = forward;
    }
}

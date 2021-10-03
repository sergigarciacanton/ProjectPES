package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity(name = "MessageUser")
@Table(name = "message_user")
public class Message_User extends Model {

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    public Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    public User user;

    @Column(name = "inbox")
    public String inbox;

    @Column(name = "date")
    public Date date = new Date();

    @Column(name = "forward")
    public Boolean forward;

    public Message_User(Message message, User user, String inbox, Date date, Boolean forward) {
        this.message = message;
        this.user = user;
        this.inbox = inbox;
        this.date = date;
        this.forward = forward;
    }

    public String getInbox() {
        return inbox;
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getForward() {
        return forward;
    }

    public void setForward(Boolean forward) {
        this.forward = forward;
    }
}

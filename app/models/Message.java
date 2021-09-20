package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class Message extends Model {

    public String title;
    public String body;

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
    }
}

package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class Message extends Model {

    @OneToMany (mappedBy = "message")
    public List<Message_User> list = new ArrayList<>();

    @OneToMany (mappedBy = "message")
    public List<File> filesList;

    public String title;
    public String body;

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @Override
    public String toString() {
        return "\n     {" +
                "\n          \"title\": \"" + title + "\"," +
                "\n          \"body\": \"" + body + '\"' +
                "\n     }\n";
    }
}
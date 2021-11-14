package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class User extends Model {

    @OneToMany (mappedBy = "sender")
    public List<Message_User> sendersList = new ArrayList<>();

    @OneToMany (mappedBy = "receiver")
    public List<Message_User> receiversList = new ArrayList<>();

    public String mail;
    public String password;
    public String fullName;

    public User(String mail, String password, String fullName) {
        this.mail = mail;
        this.password = password;
        this.fullName = fullName;
    }
}

package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

@Entity
public class User extends Model {

    @ManyToMany
    public List<Message> messagesList = new ArrayList<>();

    public String email;
    public String password;
    public String fullname;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
}

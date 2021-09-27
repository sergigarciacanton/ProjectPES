package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

public class File {

    @ManyToOne
    public Message message;

    public String name;

    public File(String name) {
        this.name = name;
    }
}

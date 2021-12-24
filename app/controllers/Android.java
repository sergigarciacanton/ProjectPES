package controllers;

import com.google.gson.Gson;
import models.Message;
import models.User;
import play.db.jpa.JPA;
import play.mvc.*;

import javax.persistence.Query;
import java.util.List;

public class Android extends Controller {

    public static String connectedUser;

    @Before
    static void connectedUser() {
        connectedUser = session.get("user");
    }

    public static void login(String mail, String password) {
        //localhost:9000/Android/login?mail=s@gmail.com&password=123
        User user = User.find("byMailAndPassword", mail, password).first();
        if (user == null) {
            user = User.find("byMail", mail).first();
            if (user == null) renderText("-1");
            else renderText("-2");
        }
        else {
            connectedUser = mail;
            session.put("user", mail);
            renderText("0");
        }
    }

    public static void register(String mail, String password, String fullName) {
        //localhost:9000/Android/register?mail=a@gmail.com&password=123&fullName=Albert
        User user = User.find("byMail", mail).first();
        if (user != null) {
            renderText("-1");
        }
        else {
            new User(mail, password, fullName).save();
            renderText("0");
        }
    }

    public static void getInbox(String mail, String inboxCode) {
        //localhost:9000/Android/getInbox?mail=j@gmail.com&inboxCode=main
        if(inboxCode.equals("sent")) {
            Query query = JPA.em().createQuery("SELECT m.message, m.receiver.mail, m.date FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", mail);
            List<Message> messages = query.getResultList();
            if (messages.size() == 0) renderText("-1");
            else {
                Gson g = new Gson();
                g = g.newBuilder().excludeFieldsWithoutExposeAnnotation().create();
                renderJSON(g.toJson(messages));
            }
        }
        else {
            Query query = JPA.em().createQuery("SELECT m.message, m.sender.mail, m.date FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", mail)
                    .setParameter("inboxCode", inboxCode);
            List<Message> messages = query.getResultList();
            if (messages.size() == 0) renderText("-1");
            else {
                Gson g = new Gson();
                g = g.newBuilder().excludeFieldsWithoutExposeAnnotation().create();
                renderJSON(g.toJson(messages));
            }
        }
    }
}

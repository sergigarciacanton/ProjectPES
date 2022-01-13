package controllers;

import com.google.gson.Gson;
import models.Message;
import models.Message_User;
import models.User;
import play.db.jpa.JPA;
import play.mvc.*;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

public class Android extends Controller {

    //Here the username who sent the query to server is saved
    public static String connectedUser;

    @Before
    static void connectedUser() {
        connectedUser = session.get("user");
    }

    public static void login(String mail, String password) {
        //localhost:9000/Android/login?mail=s@gmail.com&password=123

        //Query asks for users given its credentials as parameters
        User user = User.find("byMailAndPassword", mail, password).first();

        //In case there are no results, check if password given is wrong
        if (user == null) {
            //Query asks for users given its username as parameter
            user = User.find("byMail", mail).first();

            //In case there are no results, this username does not exist. Send error
            if (user == null) renderText("-1");

            //If it does exist, password is wrong. Send error
            else renderText("-2");
        }
        //If there is a result, credentials are well. Log in and return result
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

        //In case that user asks for mails sent by it, database query is different (asks for receivers giving sender as data)
        if(inboxCode.equals("sent")) {
            //Query asks for message, receiver's mail and date for messages sent to the mail given as parameter
            Query query = JPA.em().createQuery("SELECT m.message, m.receiver.mail, m.date FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", mail);
            List<Message> messages = query.getResultList();

            //In case there are no results, send error
            if (messages.size() == 0) renderText("-1");

            //If there are results, cast them to JSON and send
            else {
                Gson g = new Gson();
                g = g.newBuilder().excludeFieldsWithoutExposeAnnotation().create();
                renderJSON(g.toJson(messages));
            }
        }

        //Rest of cases: ask for senders giving receiver
        else {
            //Ask for message, sender's mail and date for messages sent by the mail given as parameter
            Query query = JPA.em().createQuery("SELECT m.message, m.sender.mail, m.date FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", mail)
                    .setParameter("inboxCode", inboxCode);
            List<Message> messages = query.getResultList();

            //In case there are no results, send error
            if (messages.size() == 0) renderText("-1");

            //If there are results, cast them to JSON and send
            else {
                Gson g = new Gson();
                g = g.newBuilder().excludeFieldsWithoutExposeAnnotation().create();
                renderJSON(g.toJson(messages));
            }
        }
    }

    //Query for sending a message to other users
    public static void send (String subject, String message, String receiverMail, String senderMail) {

        //Queries for getting sender and receiver users given their mails
        User user1 = User.find("byMail", senderMail).first();
        String[] receiverSplit = receiverMail.split(" ");
        Message mess = new Message(subject, message);

        int error = 0;
        for (String s : receiverSplit) {
            User user2 = User.find("byMail", s).first();

            //In case users are found, get current date and send message
            if (user1 != null && user2 != null && error == 0) {
                //Save the message on database
                mess.save();
                Date hoy = new Date();
                new Message_User(mess, user1, user2, "main", hoy, false).save();
            }
            //In rest of cases, there is an error. Render message
            else {
                error = -1;
            }
        }
        renderText(error);
    }
}
package controllers;

import com.google.gson.Gson;
import models.Mail;
import models.Message;
import models.Message_User;
import models.User;
import play.db.jpa.JPA;
import play.mvc.*;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Android extends Controller {

    //Here the username who sent the query to server is saved
    public static String connectedUser;

    @Before
    static void connectedUser() {
        connectedUser = session.get("user");
    }

    //Query for logging in
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

    //Query for registering a new user
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

    //Query for getting a specific inbox given its mail
    public static void getInbox(String mail, String inboxCode) {
        //localhost:9000/Android/getInbox?mail=j@gmail.com&inboxCode=main

        //In case that user asks for mails sent by it, database query is different (asks for receivers giving sender as data)
        List<String> title;
        List<String> body;
        List<String> sender;
        List<Date> date;

        if(inboxCode.equals("sent")) {
            //Query asks for message, receiver's mail and date for messages sent to the mail given as parameter
            Query query4 = JPA.em().createQuery("SELECT m.message.body FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", mail);
            body = query4.getResultList();
            Query query = JPA.em().createQuery("SELECT m.message.title FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", mail);
            title = query.getResultList();
            Query query2 = JPA.em().createQuery("SELECT m.receiver.mail FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", mail);
            sender = query2.getResultList();
            Query query3 = JPA.em().createQuery("SELECT m.date FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", mail);
            date = query3.getResultList();

            List<Mail> messages = new ArrayList<>();
            if(title.size() > 0 && body.size() > 0 && sender.size() > 0 && date.size() > 0)
                for(int j = 0; j < title.size(); j++) {
                    messages.add(new Mail(title.get(j), body.get(j), sender.get(j), date.get(j).toString()));
                }

            //In case there are no results, send error
            if (messages.size() == 0) renderText("-1");

            //If there are results, cast them to JSON and send
            else {
                int i = 1;
                while (i < messages.size()){
                    if(messages.get(i).date.split(":")[0].equals(messages.get(i - 1).date.split(":")[0]) && messages.get(i).date.split(":")[1].equals(messages.get(i - 1).date.split(":")[1])) {
                        messages.get(i - 1).mail += ", " + messages.get(i).mail;
                        messages.remove(i);
                    }
                    else i++;
                }

                Gson g = new Gson();
                g = g.newBuilder().create();
                renderJSON(g.toJson(messages));
            }
        }

        //Rest of cases: ask for senders giving receiver
        else {
            //Ask for message, sender's mail and date for messages sent by the mail given as parameter
            Query query4 = JPA.em().createQuery("SELECT m.message.body FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", mail)
                    .setParameter("inboxCode", inboxCode);
            body = query4.getResultList();
            Query query = JPA.em().createQuery("SELECT m.message.title FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", mail)
                    .setParameter("inboxCode", inboxCode);
            title = query.getResultList();
            Query query2 = JPA.em().createQuery("SELECT m.sender.mail FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", mail)
                    .setParameter("inboxCode", inboxCode);
            sender = query2.getResultList();
            Query query3 = JPA.em().createQuery("SELECT m.date FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", mail)
                    .setParameter("inboxCode", inboxCode);
            date = query3.getResultList();

            List<Mail> messages = new ArrayList<>();
            if(title.size() > 0 && body.size() > 0 && sender.size() > 0 && date.size() > 0)
                for(int j = 0; j < title.size(); j++) {
                    messages.add(new Mail(title.get(j), body.get(j), sender.get(j), date.get(j).toString()));
                }

            //In case there are no results, send error
            if (messages.size() == 0) renderText("-1");

            //If there are results, cast them to JSON and send
            else {
                int i = 1;
                while (i < messages.size()){
                    if(messages.get(i).date.split(":")[0].equals(messages.get(i - 1).date.split(":")[0]) && messages.get(i).date.split(":")[1].equals(messages.get(i - 1).date.split(":")[1])) {
                        messages.get(i - 1).mail += ", " + messages.get(i).mail;
                        messages.remove(i);
                    }
                    else i++;
                }

                Gson g = new Gson();
                g = g.newBuilder().create();
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

    //Query for updating user's password and name
    public static void updateAccount(String mail, String newPassword, String password, String newName) {
        User user = User.find("byMail", mail).first();

        //In case there is a user, check if password is well
        if (user != null) {
            //If password is well, change to the new one
            if (password.equals(user.password)) {
                user.password = newPassword;
                user.fullName = newName;
                user.save();
                renderText("0");
            }
            else
                renderText("-1");
        }
        else
            renderText("-2");
    }

    //Query for deleting account
    public static void deleteAccount(String mail, String password) {
        //Query asks for users given its mail as parameter
        User user = User.find("byMail", mail).first();

        //If user exists, check if password is well
        if (user != null) {

            //If password is well, proceed to delete the account
            if (password.equals(user.password)) {

                //User references in sent messages must be previously deleted
                if(user.sendersList != null){
                    Message_User mu;
                    for(int i = 0; i<user.sendersList.size(); i++) {
                        mu = user.sendersList.get(i);
                        mu.sender = null;
                        mu.save();
                    }
                }
                //User references in received messages must be previously deleted
                if(user.receiversList != null){
                    Message_User mu;
                    for(int i = 0; i<user.receiversList.size(); i++) {
                        mu = user.receiversList.get(i);
                        mu.receiver = null;
                        mu.save();
                    }
                }
                user.delete();

                //Render login webpage
                renderText("0");
            } else {
                //Wrong password. Render error
                renderText("-1");
            }
        } else {
            //Username does not exist. Render error
            renderText("-2");
        }
    }
}
package controllers;

import play.mvc.*;
import play.db.jpa.JPA;
import java.util.*;
import models.*;
import javax.persistence.Query;

public class Application extends Controller {

    //Here the username who sent the query to server is saved
    public static String connectedUser;

    @Before
    static void connectedUser() {
        connectedUser = session.get("user");
    }

    //Renders login webpage
    public static void index() {render();}

    //Renders register webpage
    public static void indexRegister() {render();}

    //Renders webpage to send messages
    public static void indexSendMessage() {renderTemplate("Application/sendMessage.html");}

    //Renders webpage to delete the account
    public static void indexDeleteAccount() {renderTemplate("Application/deleteAccount.html");}

    //Renders webpage to update the account
    public static void indexUpdateAccount() {renderTemplate("Application/updateAccount.html");}

    //Query for logging in
    public static void login(String mail, String password) {
        //localhost:9000/Application/login?mail=s@gmail.com&password=123

        //Query asks for users given its credentials as parameters
        User user = User.find("byMailAndPassword", mail, password).first();

        //In case there are no results, check if password given is wrong
        if (user == null) {
            //Query asks for users given its username as parameter
            user = User.find("byMail", mail).first();

            //In case there are no results, this username does not exist. Render corresponding error
            if (user == null) renderTemplate("errors/loginMailError.html");

            //If it does exist, password is wrong. Render corresponding error
            else renderTemplate("errors/loginPasswordError.html");
        }
        //If there is a result, credentials are well. Log in and render inbox template
        else {
            connectedUser = mail;
            session.put("user", mail);
            renderTemplate("Application/inbox.html");
        }
    }

    //Query for registering
    public static void register(String mail, String password, String fullName) {
        //localhost:9000/Application/register?mail=a@gmail.com&password=123&fullName=Albert

        //Query asks for users given its username as parameter
        User user = User.find("byMail", mail).first();

        //In case there are results, username does already exist. Render error
        if (user != null) {
            renderTemplate("errors/registerMailError.html");
        }
        //If there are no results, user can be registered. Render message
        else {
            new User(mail, password, fullName).save();
            renderTemplate("messages/registerSuccess.html");
        }
    }

    //Query for deleting account
    public static void deleteAccount(String mail, String password) {
        //Query asks for users given its username as parameter
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
                renderTemplate("Application/index.html");
            } else {
                //Wrong password. Render error
                renderTemplate("Application/deleteAccountRetry.html");
            }
        } else {
            //Username does not exist. Render error
            renderTemplate("Application/deleteAccountRetry.html");
        }
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
                renderTemplate("Application/inbox.html");
            }
            else
                renderTemplate("Application/updateAccountRetry.html");
        }
        else
            renderTemplate("Application/updateAccountRetry.html");
    }

    //Query for sending a message to other users
    public static void send (String subject, String message, String receiverMail) {
        //Save the message on database
        Message mess = new Message(subject,message);
        mess.save();

        //Queries for getting sender and receiver users given their mails
        User user1 = User.find("byMail", connectedUser).first();
        User user2 = User.find("byMail", receiverMail).first();

        //In case users are found, get current date and send message
        if (user1 != null && user2 != null) {
            Date hoy = new Date();
            new Message_User( mess,  user1,  user2,  "main", hoy,  false).save();
            renderTemplate("Application/inbox.html");
        }
        //In rest of cases, there is an error. Render message
        else {
            renderTemplate("Application/sendMessageRetry.html");
            mess.delete();
        }
    }

    //Query for getting a user's inbox
    public static void getInbox(String inbox) {
        //localhost:9000/Application/getInbox?inbox=1
        if(inbox.equals("1")) inbox = "main";
        else if(inbox.equals("2")) inbox = "spam";
        else inbox = "sent";
        //Query asks for messages with the given inbox code for the given receiver
        List<String> title;
        List<String> body;
        List<String> sender;
        List<Date> date;
        if(!inbox.equals("sent")) {
            Query query4 = JPA.em().createQuery("SELECT m.message.body FROM Message_User m WHERE m.receiver.mail " +
                        "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                .setParameter("receiverMail", connectedUser)
                .setParameter("inboxCode", inbox);
            body = query4.getResultList();
            Query query = JPA.em().createQuery("SELECT m.message.title FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", connectedUser)
                    .setParameter("inboxCode", inbox);
            title = query.getResultList();
            Query query2 = JPA.em().createQuery("SELECT m.sender.mail FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", connectedUser)
                    .setParameter("inboxCode", inbox);
            sender = query2.getResultList();
            Query query3 = JPA.em().createQuery("SELECT m.date FROM Message_User m WHERE m.receiver.mail " +
                            "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                    .setParameter("receiverMail", connectedUser)
                    .setParameter("inboxCode", inbox);
            date = query3.getResultList();
        }
        else {
            Query query4 = JPA.em().createQuery("SELECT m.message.body FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", connectedUser);
            body = query4.getResultList();
            Query query = JPA.em().createQuery("SELECT m.message.title FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", connectedUser);
            title = query.getResultList();
            Query query2 = JPA.em().createQuery("SELECT m.receiver.mail FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", connectedUser);
            sender = query2.getResultList();
            Query query3 = JPA.em().createQuery("SELECT m.date FROM Message_User m WHERE m.sender.mail " +
                            "LIKE :receiverMail")
                    .setParameter("receiverMail", connectedUser);
            date = query3.getResultList();
        }

        List<Mail> mail = new ArrayList<>();
        if(title.size() > 0 && body.size() > 0 && sender.size() > 0 && date.size() > 0)
            for(int j = 0; j < title.size(); j++) {
                mail.add(new Mail(title.get(j), body.get(j), sender.get(j), date.get(j).toString()));
            }
        render(mail);
    }

    public static void viewMessage(String title, String body, String mail, String date) {
        Mail m = new Mail(title, body, mail, date);
        render(m);
    }
}
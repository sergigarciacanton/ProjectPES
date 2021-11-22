package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.JPA;

import java.util.*;

import models.*;

import javax.persistence.Query;

import com.google.gson.Gson;


public class Application extends Controller {

    public static String connectedUser;

    @Before
    static void connectedUser() {
        connectedUser = session.get("user");
    }

    public static void index() {render();}

    public static void indexRegister() {render();}

    public static void login(String mail, String password) {
        //localhost:9000/Application/login?mail=s@gmail.com&password=123
        User user = User.find("byMailAndPassword", mail, password).first();
        if (user == null) {
            user = User.find("byMail", mail).first();
            if (user == null) renderTemplate("errors/loginMailError.html");
            else renderTemplate("errors/loginPasswordError.html");
        }
        else {
            connectedUser = mail;
            session.put("user", mail);
            renderTemplate("Application/inbox.html");
        }
    }

    public static void register(String mail, String password, String fullName) {
        //localhost:9000/Application/register?mail=a@gmail.com&password=123&fullName=Albert
        User user = User.find("byMail", mail).first();
        if (user != null) {
            renderTemplate("errors/registerMailError.html");
        }
        else {
            new User(mail, password, fullName).save();
            renderTemplate("messages/registerSuccess.html");
        }
    }

    public static void deleteAccount(String name, String password) {
        User user = User.find("byFullName", name).first();
        if (user != null) {

            if (password.equals(user.password)) {
                user.delete();
                renderText(name + " has been deleted.");
            }
            else {
                renderText("Wrong password.");
            }

        } else {
            renderText("This user does not exist.");
        }
    }

    public static void updatePassword(String name, String newPassword, String password) {
        User user = User.find("byFullName", name).first();
        if (user != null) {
            if (password.equals(user.password)) {
                user.password = newPassword;
                user.save();
            }
            renderText("Wrong password.");
        }
    }

    public static void updateName(String name, String newName, String password) {
        User user = User.find("byFullName", name).first();
        if (user != null) {
            if (password.equals(user.password)) {
                user.fullName = newName;
                user.save();
            }
            renderText("Wrong password.");
        }
    }

    public static void send (Message message, User sender, User receiver, String inbox, Date date, Boolean forward) {
        User user1 = User.find("byFullName", sender.fullName).first();
        User user2 = User.find("byFullName", receiver.fullName).first();
        if (user1 != null && user2 != null) {
            new Message_User( message,  sender,  receiver,  inbox,  date,  forward).save();
        }
        else {
            renderText("This user does not exist");
        }
    }

    public static void getInbox(String mail, String inboxCode) {
        //localhost:9000/Application/getInbox?mail=j@gmail.com&inboxCode=main
        Query query = JPA.em().createQuery("SELECT m.message FROM Message_User m WHERE m.receiver.mail " +
                        "LIKE :receiverMail AND m.inbox LIKE :inboxCode")
                .setParameter("receiverMail", mail)
                .setParameter("inboxCode", inboxCode);
        List<Message> messages = query.getResultList();
        Gson g = new Gson();
        g = g.newBuilder().excludeFieldsWithoutExposeAnnotation().create();
        renderJSON(  g.toJson(messages));
    }
}
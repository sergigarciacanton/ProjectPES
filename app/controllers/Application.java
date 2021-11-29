package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.JPA;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

    public static void indexSendMessage() {renderTemplate("Application/sendMessage.html");}

    public static void indexDeleteAccount() {renderTemplate("Application/deleteAccount.html");}

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

    public static void deleteAccount(String mail, String password) {
        User user = User.find("byMail", mail).first();
            if (user != null) {

                if (password.equals(user.password)) {
                    if(user.sendersList != null){
                        Message_User mu;
                        for(int i = 0; i<user.sendersList.size(); i++) {
                            mu = user.sendersList.get(i);
                            mu.sender = null;
                            mu.save();
                        }
                    }

                    if(user.receiversList != null){
                        Message_User mu;
                        for(int i = 0; i<user.receiversList.size(); i++) {
                            mu = user.receiversList.get(i);
                            mu.receiver = null;
                            mu.save();
                        }
                    }
                    user.delete();
                    renderTemplate("Application/index.html");
                } else {
                    //Reintentamos entrar los datos
                    renderTemplate("Application/deleteAccountRetry.html");
                }
            } else {
                //Reintentamos entrar los datos
                renderTemplate("Application/deleteAccountRetry.html");
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

    public static void send (String subject, String message, String receivermail) {
        Message mess = new Message(subject,message);
        mess.save();
        User user1 = User.find("byMail", connectedUser).first();
        User user2 = User.find("byMail", receivermail).first();
        if (user1 != null && user2 != null) {
            Date hoy = new Date();
            new Message_User( mess,  user1,  user2,  "main", hoy,  false).save();
            renderTemplate("Application/inbox.html");
        }
        else {
            renderTemplate("Application/sendMessageRetry.html");
            mess.delete();
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
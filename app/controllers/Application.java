package controllers;

import play.*;
import play.mvc.*;
import play.db.jpa.JPA;

import java.util.*;

import models.*;


public class Application extends Controller {

    public static void index() {renderText("funciona");}

    public static void login(String mail, String password) {
        //localhost:9000/Application/login?mail=s@gmail.com&password=123
        User user = User.find("byEmailAndPassword", mail, password).first();
        if (user == null) {
            user = User.find("byEmail", mail).first();
            if (user == null) renderText("Error. Mail address does not exist.");
            else renderText("Error. Wrong password.");
        } else renderText("Login successful!");
    }

    public static void register(String email, String password, String fullName) {
        //localhost:9000/Application/register?email=a@gmail.com&password=123&fullName=Albert
        User user = User.find("byEmail", email).first();
        if (user != null) renderText("Error. Mail address does already exist.");
        else {
            new User(email, password, fullName).save();
            renderText("Successfully registered!");
        }
    }

    public static void deleteAccount(String name, String passw) {
        User user = User.find("byFullname", name).first();
        if (user != null) {

            if (passw.equals(user.password)) {
                user.delete();
                renderText(name + " has been deleted deleted");
            }
            else {
                renderText("Wrong password");
            }

        } else {
            renderText("This user does not exist");
        }
    }

    public static void updatePassword(String name, String newpswd, String passw) {
        User user = User.find("byFullname", name).first();
        if (user != null) {
            if (passw.equals(user.password)) {
                user.password = newpswd;
                user.save();
            }
            renderText("Wrong password");
        }
    }

    public static void updateName(String name, String newname, String passw) {
        User user = User.find("byFullname", name).first();
        if (user != null) {
            if (passw.equals(user.password)) {
                user.fullname = newname;
                user.save();
            }
            renderText("Wrong password");
        }
    }

    public static void send (Message message, User sender, User receiver, String inbox, Date date, Boolean forward) {
        User user1 = User.find("byFullname", sender.fullname).first();
        User user2 = User.find("byFullname", receiver.fullname).first();
        if (user1 != null && user2 != null) {

            new Message_User( message,  sender,  receiver,  inbox,  date,  forward).save();

        }

        else{
            renderText("This user does not exist");
        }

    }

    }
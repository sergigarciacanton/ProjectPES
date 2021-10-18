package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        new User("s@gmail.com", "123", "Sergi").save();

        render();
    }

    public static void login(String mail, String password) {
        //localhost:9000/Application/login?mail=s@gmail.com&password=123
        User user = User.find("byEmailAndPassword",mail, password).first();
        if(user == null) {
            user = User.find("byEmail",mail).first();
            if(user == null) renderText("Error. Mail address does not exist.");
            else renderText("Error. Wrong password.");
        }
        else renderText("Login successful!");
    }

    public static void register(String email, String password, String fullName) {
        //localhost:9000/Application/register?email=a@gmail.com&password=123&fullName=Albert
        User user = User.find("byEmail",email).first();
        if(user != null) renderText("Error. Mail address does already exist.");
        else {
            new User(email, password, fullName).save();
            renderText("Successfully registered!");
        }
    }
}
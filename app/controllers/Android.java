package controllers;

import models.User;
import play.mvc.*;

public class Android extends Controller {

    public static String connectedUser;

    @Before
    static void connectedUser() {
        connectedUser = session.get("user");
    }

    public static void login(String mail, String password) {
        //localhost:9000/Application/login?mail=s@gmail.com&password=123
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
        //localhost:9000/Application/register?mail=a@gmail.com&password=123&fullName=Albert
        User user = User.find("byMail", mail).first();
        if (user != null) {
            renderText("-1");
        }
        else {
            new User(mail, password, fullName).save();
            renderText("0");
        }
    }
}

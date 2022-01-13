package models;

public class Mail {

    public String title;
    public String body;
    public String mail;
    public String date;

    public Mail(String title, String body, String mail, String date) {
        this.title = title;
        this.body = body;
        this.mail = mail;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", mail='" + mail + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
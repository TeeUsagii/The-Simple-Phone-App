package exemple.com.simple_phone;

import java.io.Serializable;

public class Contact implements Serializable {
    private String id;
    private String title;
    private String phoneNumber;

    public Contact() {
    }

    public Contact(String id, String title, String phoneNumber) {
        this.id = id;
        this.title = title;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public Contact setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Contact setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Contact setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}

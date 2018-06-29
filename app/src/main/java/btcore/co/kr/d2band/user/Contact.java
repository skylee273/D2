package btcore.co.kr.d2band.user;

public class Contact {
    private static String[] phone = null;
    private static String[] name = null;

    public String[] getPhone() {
        return phone;
    }

    public void setPhone(String[] phone) {
        Contact.phone = phone;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        Contact.name = name;
    }

}

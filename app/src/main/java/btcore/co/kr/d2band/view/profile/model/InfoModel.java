package btcore.co.kr.d2band.view.profile.model;

public class InfoModel {

    public boolean checkUser(String name, String birthday, String gender, String height, String weight, String phone, String addr){
        try {
            return name.length() > 0 && birthday.length() > 0 && gender.length() > 0 && height.length() > 0 && weight.length() > 0 && phone.length() > 0 && addr.length() > 0;
        }catch (NullPointerException e){
            return false;
        }

    }

}

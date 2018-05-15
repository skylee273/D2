package btcore.co.kr.d2band.view.profile.model;

import btcore.co.kr.d2band.user.User;

public class InfoModel {

    private String name, birthday, gender, height, weight, phone, addr;

    public boolean checkUser(String na, String bi, String ge, String he, String we, String ph, String ad){
        this.name = na;
        this.birthday = bi;
        this.gender = ge;
        this.height = he;
        this.weight = we;
        this.phone = ph;
        this.addr = ad;

        try {
            if(name.length() > 0 && birthday.length() > 0 && gender.length() > 0 && height.length() > 0 && weight.length() > 0 && phone.length() > 0 && addr.length() > 0 ){
                return true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }

    }

}

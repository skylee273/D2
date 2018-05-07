package btcore.co.kr.d2band.view.profile.model;

import btcore.co.kr.d2band.user.User;

public class ProfileModel {
    private String name, email;
    User user;

    public boolean checkUser(){

        user = new User();
        try {
            if(user.getName().length() > 0 && user.getId().length() > 0){
                name = user.getName();
                email = user.getId();
                return true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

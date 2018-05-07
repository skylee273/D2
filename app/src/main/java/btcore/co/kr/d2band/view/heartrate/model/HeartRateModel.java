package btcore.co.kr.d2band.view.heartrate.model;

/**
 * Created by leehaneul on 2018-01-17.
 */

// 모델 : 데이터 + 상태 + 비즈니스 로직 ( 두뇌 역활) 만약 심박수 상태를 계산하기 위한 로직을 짜야 한다면 모델부분에 짜야한다.
public class HeartRateModel {

    String[] heartData;

    // 데이터 셋
    public void setHeartRateData(String [] heartData ){
        this.heartData[0] = heartData[0];
        this.heartData[1] = heartData[1];
        this.heartData[2] = heartData[2];
        this.heartData[3] = heartData[3];
        this.heartData[4] = heartData[4];
    }

    /**
     * 심박수 데이터 상태 체크
     * @param heartData
     * @return
     */
    public boolean checkHeartData(String[] heartData){
        if(heartData[0].length()  == 0){
            return false;
        }else if(heartData[1].length() == 0){
            return false;
        }else if(heartData[2].length() == 0){
            return false;
        }else if(heartData[3].length() == 0){
            return false;
        }else if(heartData[4].length() == 0){
            return false;
        }else{
            return true;
        }
    }

    // 로직이 있다면 로직을 걸친 후에 넘겨 줘야 하는 부분.
    public String[] makeHeartData(){
        return   heartData;
    }



}

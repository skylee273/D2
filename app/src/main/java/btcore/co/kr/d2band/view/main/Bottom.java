package btcore.co.kr.d2band.view.main;

/**
 * Created by leehaneul on 2018-02-26.
 */

public interface Bottom {
    interface View{
        void showImage(int current);
        void startMainActivity(int current);
    }
    interface Presenter{
        void chekCurrentPage(int page);
        void startActivity(int page);
    }


}

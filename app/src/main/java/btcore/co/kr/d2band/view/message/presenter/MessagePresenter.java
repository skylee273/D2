package btcore.co.kr.d2band.view.message.presenter;

import btcore.co.kr.d2band.view.message.Message;
import btcore.co.kr.d2band.view.message.model.MessageModel;
import btcore.co.kr.d2band.view.profile.model.ProfileModel;

public class MessagePresenter implements Message.Presenter {

    Message.View messageView;
    MessageModel messageModel;

    public MessagePresenter(Message.View messageView) {
        this.messageView = messageView;
        this.messageModel = new MessageModel();
    }

    public void onCreate() {
        messageModel = new MessageModel();
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public void onDestroy() {
    }

    @Override
    public void changeMessage(String msg) {
        if (messageModel.checkMessage(msg)) {
            messageView.MessageUpdate(msg);
        } else {
            messageView.showErrorMessage("변경하실 내용이 없습니다.");
        }
    }

    @Override
    public void saveMsg(String msg) {
        if (messageModel.checkMessage(msg)) {
            messageView.MessageUpdate(msg);
        }
    }

    @Override
    public void plusReceiver(String name, String phone) {

        if (messageModel.checkReceiver(name, phone)) {
            messageModel.InsertReceiver(new MessageModel.ApiListener() {
                @Override
                public void onSuccess() {
                    messageModel.getReceiver(new MessageModel.RecvApiListener() {
                        @Override
                        public void onSuccess(String[] Recv) {
                            messageView.updateListView(Recv);
                        }
                        @Override
                        public void onFail() {

                        }
                    });
                }
                @Override
                public void onFail() {
                    messageView.showErrorMessage("입력된 정보를 다시 확인하세요.");
                }
            });
            // messageView.addListView(name,phone);
        } else {
            messageView.showErrorMessage("수신자를 추가 할 수 없습니다.");
        }
    }

    @Override
    public void selectReceiver() {
        messageModel.getReceiver(new MessageModel.RecvApiListener() {
            @Override
            public void onSuccess(String[] Recv) {
                messageView.updateListView(Recv);
            }
            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void deleteReceiver(String name, String phone) {
        if(messageModel.checkReceiver(name, phone)){
            messageModel.deleteReceiver(new MessageModel.DeleteApiListener() {
                @Override
                public void onSuccess() {
                    messageView.updateView();
                }
                @Override
                public void onFail() {
                    messageView.showErrorMessage("삭제에 실패하였습니다.");
                }
            });
        }
    }


}

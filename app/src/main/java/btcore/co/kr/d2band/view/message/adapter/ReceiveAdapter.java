package btcore.co.kr.d2band.view.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.view.message.item.ReceiveItem;

public class ReceiveAdapter extends BaseAdapter {

    private ArrayList<ReceiveItem> listViewItemList = new ArrayList<>();

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.list_receive, parent, false);
        }
        TextView mName = convertView.findViewById(R.id.text_name);
        TextView mPhone = convertView.findViewById(R.id.text_phone);

        ReceiveItem listViewItem = (ReceiveItem) getItem(position);

        mName.setText(listViewItem.getName());
        mPhone.setText(listViewItem.getPhone());

        return convertView;
    }

    public void addItem(String name, String phone){
        ReceiveItem item = new ReceiveItem();
        item.setName(name);
        item.setPhone(phone);

        listViewItemList.add(item);
    }

    public void clearItem() {
        listViewItemList.clear();
    }
}

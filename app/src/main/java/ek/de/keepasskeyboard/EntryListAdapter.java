package ek.de.keepasskeyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.slackspace.openkeepass.domain.Entry;

/**
 * Created by Enrico on 10.06.2016.
 */
public class EntryListAdapter extends ArrayAdapter {
    private Context context;
    private List<Entry> data;

    public EntryListAdapter(Context context, List<Entry> data){
        super(context, -1, data);
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_selection, parent, false);

        TextView tv_title = (TextView) rowView.findViewById(R.id.tv_title);
        TextView tv_username = (TextView) rowView.findViewById(R.id.tv_username);
        TextView tv_groupname = (TextView) rowView.findViewById(R.id.tv_groupname);

        tv_title.setText(data.get(position).getTitle());
        tv_username.setText(data.get(position).getUsername());
        tv_groupname.setText(data.get(position).getGroupName());



        return rowView;
    }

    @Override
    public Entry getItem(int position) {
        return data.get(position);
    }
}

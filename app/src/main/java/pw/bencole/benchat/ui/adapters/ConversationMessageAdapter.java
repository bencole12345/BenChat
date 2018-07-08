package pw.bencole.benchat.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pw.bencole.benchat.R;
import pw.bencole.benchat.models.Message;

public class ConversationMessageAdapter extends ArrayAdapter<Message> {

    private static class ViewHolder {
        TextView username;
        TextView timestamp;
        TextView content;
    }

    public ConversationMessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);

        final View result;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.listelement_conversation_message, parent, false);
        } else {
            result = convertView;
        }

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.username = result.findViewById(R.id.usernameText);
        viewHolder.timestamp = result.findViewById(R.id.timestampText);
        viewHolder.content = result.findViewById(R.id.contentText);

        viewHolder.username.setText(message.getSender().getUsername());
        // TODO: Format as a proper timestamp
        viewHolder.timestamp.setText(Long.toString(message.getTimestamp()));
        viewHolder.content.setText(message.getContent());

        return result;
    }
}

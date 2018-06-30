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
import pw.bencole.benchat.models.Conversation;

/**
 * Populates the conversations overview list using an ArrayList of Conversation objects.
 *
 * @author Ben Cole
 */
public class ConversationArrayAdapter extends ArrayAdapter<Conversation> {

    private static class ViewHolder {
        TextView contactName;
        TextView messagePreview;
    }

    public ConversationArrayAdapter(@NonNull Context context, int resource, @NonNull List<Conversation> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Conversation conversation = getItem(position);
        ViewHolder viewHolder = new ViewHolder();

        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.listviewelement_conversation_overview, parent, false);
        } else {
            result = convertView;
        }

        viewHolder.contactName = result.findViewById(R.id.contactNameTextView);
        viewHolder.messagePreview = result.findViewById(R.id.messagePreviewTextView);
        viewHolder.contactName.setText(conversation.getOtherPerson().getUsername());
        viewHolder.messagePreview.setText(conversation.getFirstMessage());
        return result;
    }
}

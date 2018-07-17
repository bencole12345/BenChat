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
import pw.bencole.benchat.models.ConversationPreview;
import pw.bencole.benchat.models.LoggedInUser;

/**
 * Populates the conversations overview list using an ArrayList of ConversationPreview objects.
 *
 * @author Ben Cole
 */
public class ConversationPreviewAdapter extends ArrayAdapter<ConversationPreview> {

    /**
     * Reference to the current logged in user
     */
    private LoggedInUser mUser;

    /**
     * Stores references to UI elements to reduce the number of findViewById() calls made
     */
    private static class ViewHolder {
        TextView contactName;
        TextView messagePreview;
    }

    public ConversationPreviewAdapter(LoggedInUser user, @NonNull Context context, int resource, @NonNull List<ConversationPreview> objects) {
        super(context, resource, objects);
        mUser = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConversationPreview conversation = getItem(position);
        ViewHolder viewHolder = new ViewHolder();

        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.listelement_conversation_overview, parent, false);
        } else {
            result = convertView;
        }

        viewHolder.contactName = result.findViewById(R.id.contactNameTextView);
        viewHolder.messagePreview = result.findViewById(R.id.messagePreviewTextView);
        viewHolder.contactName.setText(conversation.getConversationName());
        viewHolder.messagePreview.setText(conversation.getMessagePreview());
        return result;
    }
}

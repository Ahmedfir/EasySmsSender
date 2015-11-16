package ahmed.com.easysmssender.smssender;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ahmed.com.easysmssender.R;
import ahmed.com.easysmssender.entities.User;
import ahmed.com.easysmssender.utils.LogUtils;

/**
 * Contact RecyclerView view holder.
 *
 * Created by ahmed on 7/30/15.
 */
class ContactRecyclerViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = ContactRecyclerViewHolder.class.getSimpleName();

    public static View inflateLocalContactView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell_contacts_local_contact, parent, false);
    }

    public TextView name;
    public SelectionListener selectionListener;

    public ContactRecyclerViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.user_name);
    }

    public void bindLocalUser(final User user, SelectionListener selectionListener) {

        name.setText(user.getName());
        this.selectionListener = selectionListener;

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContactRecyclerViewHolder.this.selectionListener != null) {
                    ContactRecyclerViewHolder.this.selectionListener.onSelectionChanged(ContactRecyclerViewHolder.this, user);
                } else {
                    LogUtils.w(TAG, "Implement SelectionListener to trigger connection events !", new Exception());
                }
            }
        });
    }

    public interface SelectionListener {
        void onSelectionChanged(final RecyclerView.ViewHolder holder, final User user);
    }
}

package ahmed.com.easysmssender.smssender;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ahmed.com.easysmssender.LocalSearchAdapter;
import ahmed.com.easysmssender.entities.User;
import ahmed.com.easysmssender.utils.LogUtils;

/**
 * Contacts adapter.
 *
 * Created by ahmed on 11/16/15.
 */
class ContactsAdapter extends LocalSearchAdapter<User, ContactsEntry> {

    private static final String TAG = ContactsAdapter.class.getSimpleName();
    private HashSet<Object> alphabetDividers;
    private ContactRecyclerViewHolder.SelectionListener selectionListener;

    public ContactsAdapter() {
        super();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return  entries.get(position).type.ordinal();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContactsEntry.Type type = ContactsEntry.Type.values()[viewType];

        switch (type) {
            case USER:
                return new ContactRecyclerViewHolder(ContactRecyclerViewHolder.inflateLocalContactView(parent));
            case ALPHABET_DIVIDER:
                return new DividerViewHolder(DividerViewHolder.inflateView(parent));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ContactsEntry entry =  entries.get(i);

        switch (entry.type) {
            case USER:
                if (!(entry.object instanceof User) || !(viewHolder instanceof ContactRecyclerViewHolder)) {
                    LogUtils.e(TAG, "Wrong types !");
                    return;
                }
                ((ContactRecyclerViewHolder) viewHolder).bindLocalUser((User) entry.object, selectionListener);
                break;
            case ALPHABET_DIVIDER:
                ((DividerViewHolder) viewHolder).bind(entry.object);
                break;
            default:
                LogUtils.e(TAG,"wrong entry type", new Exception());
        }
    }

    public void setSelectionListener(ContactRecyclerViewHolder.SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }


    @Override
    public void addResultToEntries(@NonNull List<User> itemsList) {
        alphabetDividers = new HashSet<>();

        List<User> shownUsers = new ArrayList<>(itemsList);

        LogUtils.i(TAG, "-----> addResultToEntries : entries size = " + entries.size());
        LogUtils.i(TAG, "-----> addResultToEntries : itemsList size = " + itemsList.size());
        Collections.sort(shownUsers);

        for (User user : shownUsers) {

            String firstLetterOfName = user.getFirstLetterOfName();

            if (!alphabetDividers.contains(firstLetterOfName)) {
                alphabetDividers.add(firstLetterOfName);
                entries.add(new ContactsEntry(ContactsEntry.Type.ALPHABET_DIVIDER, firstLetterOfName));
            }
            entries.add(new ContactsEntry(ContactsEntry.Type.USER, user));
        }
    }

    @Override
    public Filter getFilter() {
        return new ContactsFilter();
    }

    /**
     * implementation of the {@code FilterBase} to check that a user matches a filter or not.
     */
    private class ContactsFilter extends FilterBase {

        @Override
        protected boolean matchesFilter(User item, CharSequence filter) {
            return !item.isNotValidLocalUser() && item.userMatchesFilter(filter);
        }
    }

}

package ahmed.com.easysmssender.smssender;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ahmed.com.easysmssender.entities.User;

/**
 * Loads and sorts the Local contacts without duplicates asynchronously.
 *
 * @see {@link ContactsLoadingListener}.
 * <p/>
 * Created by ahmed on 11/15/15.
 */
class LocalContactsLoader extends AsyncTask<Void, Integer, List<User>> {

    private final Context context;
    private final ContactsLoadingListener loadingListener;

    public LocalContactsLoader(Context context, ContactsLoadingListener loadingListener) {
        this.context = context;
        this.loadingListener = loadingListener;
    }

    @Override
    protected List<User> doInBackground(Void... params) {

        final List<User> contacts = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                ContactsContract.Data.DISPLAY_NAME_SOURCE,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
        }, null, null, null);

        if (cursor == null) { // issue on loading the users.
            return null;
        }

        Map<Long, User> contactsById = new HashMap<>();
        Map<String, User> contactsByName = new HashMap<>();
        Map<String, User> contactsByEmail = new HashMap<>();
        Map<String, User> contactsByPhone = new HashMap<>();

        try {
            int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            int nameIdx = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
            int nameSourceIdx = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_SOURCE);
            int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIdx);
                String name = cursor.getString(nameIdx);
                int nameSource = cursor.getInt(nameSourceIdx);
                String email = cursor.getString(emailIdx);
                String phone = cursor.getString(phoneIdx);

                boolean isEmailNameSource = ContactsContract.DisplayNameSources.EMAIL == nameSource;
                boolean isPhoneNameSource = ContactsContract.DisplayNameSources.PHONE == nameSource;

                int fields = (TextUtils.isEmpty(name) ? 0 : 1) + (TextUtils.isEmpty(email) ? 0 : 1) + (TextUtils.isEmpty(phone) ? 0 : 1);

                if (!TextUtils.isEmpty(name)) {
                    User contactByName = isEmailNameSource ? contactsByEmail.get(name) : isPhoneNameSource ? contactsByPhone.get(name) : contactsByName.get(name);
                    if (contactByName != null) {
                        int contactByNameFields = (TextUtils.isEmpty(contactByName.getName()) ? 0 : 1) + (TextUtils.isEmpty(contactByName.getEmail()) ? 0 : 1) + (TextUtils.isEmpty(contactByName.getPhone()) ? 0 : 1);

                        if (fields > contactByNameFields) {
                            // this record has more data than what we currently have for a matching contact; delete that contact
                            contactsById.remove(contactByName.getId());
                        } else {
                            // we've already loaded a contact with a matching name with more data than this, so skip this one
                            continue;
                        }
                    }
                }

                if (!TextUtils.isEmpty(email)) {
                    User contactByEmail = contactsByEmail.get(email);
                    if (contactByEmail != null) {
                        int contactByEmailFields = (TextUtils.isEmpty(contactByEmail.getName()) ? 0 : 1) + (TextUtils.isEmpty(contactByEmail.getEmail()) ? 0 : 1) + (TextUtils.isEmpty(contactByEmail.getPhone()) ? 0 : 1);

                        if (fields > contactByEmailFields) {
                            // this record has more data than what we currently have for a matching contact; delete that contact
                            contactsById.remove(contactByEmail.getId());
                        } else {
                            // we've already loaded a contact with a matching name with more data than this, so skip this one
                            continue;
                        }
                    }
                }

                if (!TextUtils.isEmpty(phone)) {
                    User contactByPhone = contactsByPhone.get(phone);
                    if (contactByPhone != null) {
                        int contactByPhoneFields = (TextUtils.isEmpty(contactByPhone.getName()) ? 0 : 1) + (TextUtils.isEmpty(contactByPhone.getEmail()) ? 0 : 1) + (TextUtils.isEmpty(contactByPhone.getPhone()) ? 0 : 1);

                        if (fields > contactByPhoneFields) {
                            // this record has more data than what we currently have for a matching contact; delete that contact
                            contactsById.remove(contactByPhone.getId());
                        } else {
                            // we've already loaded a contact with a matching name with more data than this, so skip this one
                            continue;
                        }
                    }
                }


                User contact = new User(id, name, email, phone);


                contactsById.put(id, contact);
                contactsByName.put(name, contact);
                contactsByEmail.put(email, contact);
                contactsByPhone.put(phone, contact);
            }
        } finally {
            cursor.close();
        }

        // remove all the duplicates: double check.
        Set<User> users = new HashSet<>(contactsById.values());

        contacts.addAll(users);

        Collections.sort(contacts);

        return contacts;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (loadingListener == null) {
            return;
        }

        loadingListener.onStartedLoading();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (loadingListener == null || values == null || values.length < 1) {
            return;
        }
        loadingListener.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(List<User> localUsers) {
        super.onPostExecute(localUsers);

        if (loadingListener == null) {
            return;
        }

        if (localUsers == null) {
            loadingListener.onError();
        } else {
            loadingListener.onSuccess(localUsers);
        }
    }

    /**
     * Listener of the loading progress.
     */
    public interface ContactsLoadingListener {

        /**
         * implement this to trigger the starting of the loading event.
         */
        void onStartedLoading();

        /**
         * the error callback.
         */
        void onError();

        /**
         * the success callback.
         *
         * @param localUsers the returned list of the local users.
         */
        void onSuccess(List<User> localUsers);

        /**
         * implement this to trigger the progress status of the loading events.
         *
         * @param numberOfLoadedUsers number of the loaded users.
         */
        void onProgress(int numberOfLoadedUsers);
    }
}

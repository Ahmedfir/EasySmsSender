package ahmed.com.easysmssender.smssender;

/**
 * Contacts entries in teh adapter.
 *
 * Created by ahmed on 11/15/15.
 */
class ContactsEntry {
    public final Object object;
    public final Type type;

    public ContactsEntry(Type type, Object object) {
        this.object = object;
        this.type = type;
    }

    public enum Type {

        // a divider cell containing the first letter of the name of the entries' list.
        ALPHABET_DIVIDER,

        // a user.
        USER
    }
}

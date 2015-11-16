package ahmed.com.easysmssender.entities;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * User entity.
 *
 * Created by ahmed on 11/15/15.
 */
public class User implements Comparable<User>{
    private long id;
    private String name;
    private String email;
    private String phone;

    public User(long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public int compareTo(@NonNull User another) {
        String leftUser = null;
        String rightUser = null;

        if (!TextUtils.isEmpty(getName())) {
            leftUser = getName();
        } else if (!TextUtils.isEmpty(getEmail())) {
            leftUser = getEmail();
        } else if (!TextUtils.isEmpty(getPhone())){
            leftUser = getPhone();
        }

        if (!TextUtils.isEmpty(another.getName())) {
            rightUser = another.getName();
        } else if (!TextUtils.isEmpty(another.getEmail())) {
            rightUser = another.getEmail();
        } else if (!TextUtils.isEmpty(another.getPhone())){
            leftUser = another.getPhone();
        }

        if (leftUser == null) {
            return -1;
        }
        if (rightUser == null) {
            return 1;
        }
        return leftUser.compareToIgnoreCase(rightUser);
    }

    public String getFirstLetterOfName() {
        String firstLetterOfLastName;
        if (!TextUtils.isEmpty(getName())) {
            firstLetterOfLastName = getName().substring(0, 1).toUpperCase();
        } else if (!TextUtils.isEmpty(getEmail())) {
            firstLetterOfLastName = getEmail().substring(0, 1).toUpperCase();
        } else {
            firstLetterOfLastName = "";
        }
        return groupingCharacterString(firstLetterOfLastName);
    }

    private String groupingCharacterString(String forString) {
        if (forString != null && forString.length() > 0) {
            int charCode = (int) forString.charAt(0);
            if (charCode >= 65 && charCode <= 172) {
                return forString;
            }
        }
        return "#";
    }

    public boolean userMatchesFilter(CharSequence filter) {
        return filter == null
                || (getName() != null && getName().toLowerCase().contains(((String)filter).toLowerCase()));
    }


    public boolean isNotValidLocalUser() {
        return TextUtils.isEmpty(getEmail()) && TextUtils.isEmpty(getPhone());
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != user.getId()) return false;
        if (getName() != null ? !getName().equals(user.getName()) : user.getName() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(user.getEmail()) : user.getEmail() != null)
            return false;
        return !(getPhone() != null ? !getPhone().equals(user.getPhone()) : user.getPhone() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        return result;
    }
}

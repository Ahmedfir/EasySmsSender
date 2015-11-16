package ahmed.com.easysmssender.smssender;


import android.support.annotation.NonNull;

import ahmed.com.easysmssender.entities.User;

/**
 * Definition of the sms sender.
 *
 * Created by ahmed on 8/1/15.
 */
public interface SmsSender {
    void sendSms(@NonNull User user,@NonNull String smsBody);
}

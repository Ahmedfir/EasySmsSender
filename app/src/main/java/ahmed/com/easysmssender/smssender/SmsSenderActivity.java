package ahmed.com.easysmssender.smssender;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Deque;

import ahmed.com.easysmssender.R;
import ahmed.com.easysmssender.entities.User;

/**
 * The main activity. Implements the {@link SmsSender}.
 *
 * Created by ahmed on 11/16/15.
 */
public class SmsSenderActivity extends AppCompatActivity implements SmsSender {

    private static final String SMS_SENT = "SMS_SENT";
    private static final String SMS_DELIVERED = "SMS_DELIVERED";

    /**
     * Intent of the sending.
     */
    private PendingIntent sentPendingIntent;

    /**
     * Intent of the delivery.
     */
    private PendingIntent deliveredPendingIntent;

    /**
     * Broadcast receiver listening to the sending status intents.
     */
    private BroadcastReceiver sentReceiver;

    /**
     * Broadcast receiver listening to the delivery status intents.
     */
    private BroadcastReceiver deliveredReceiver;

    /**
     * {@code true} if the delivery {@code BroadcastReceiver} is registered, otherwise {@code false}.
     */
    private boolean registeredDeliveryReceiver = false;

    /**
     * {@code true} if the sending {@code BroadcastReceiver} is registered, otherwise {@code false}.
     */
    private boolean registeredSendingReceiver = false;

    /**
     * Queue of pairs <sms receiver, sms body>.
     */
    private Deque<Pair<User, String>> smsQueue;

    /**
     * SMS sender status. {@code true} if busy with sending an SMS, otherwise {@code false}.
     */
    private boolean busyWithSmsSending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SmsSenderFragment fragment = (SmsSenderFragment) getSupportFragmentManager().findFragmentByTag(SmsSenderFragment.TAG);
        if (fragment == null) {
            fragment = SmsSenderFragment.newInstance();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_container, fragment, SmsSenderFragment.TAG).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registeredDeliveryReceiver && deliveredReceiver != null) {
            unregisterReceiver(deliveredReceiver);
        }
        if (registeredSendingReceiver && sentReceiver != null) {
            unregisterReceiver(sentReceiver);
        }
    }

    @Override
    public void sendSms(@NonNull final User user, @NonNull String smsBody) {

        Pair<User, String> smsPair = new Pair<>(user, smsBody);
        if (smsQueue == null) {
            smsQueue = new ArrayDeque<>();
        }
        smsQueue.push(smsPair);

        if (!busyWithSmsSending && !smsQueue.isEmpty()) {
            startSmsSending();
        }
    }

    private void startSmsSending() {
        if (smsQueue == null || smsQueue.isEmpty()) {
            return;
        }
        busyWithSmsSending = true;
        Pair<User, String> pair = smsQueue.pollFirst();
        User u = pair.first;
        String phoneNumber = u.getPhone();

        if (TextUtils.isEmpty(phoneNumber)) { // ignore users without phone numbers.
            Toast.makeText(SmsSenderActivity.this, R.string.no_phone_number_provided, Toast.LENGTH_SHORT).show();
            startSmsSending();
            return;
        }

        if (sentPendingIntent == null)
            sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        if (deliveredPendingIntent == null)
            deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        // For when the SMS has been sent
        if (sentReceiver == null) {
            sentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(context, R.string.sms_sent_successfully, Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(context, R.string.failed_to_send_the_sms, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    busyWithSmsSending = false;
                    startSmsSending();
                }
            };
        }
        if (!registeredSendingReceiver) {
            registerReceiver(sentReceiver, new IntentFilter(SMS_SENT));
            registeredSendingReceiver = true;
        }

        // For when the SMS has been delivered
        if (deliveredReceiver == null) {
            deliveredReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(SmsSenderActivity.this, R.string.sms_delivered, Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(SmsSenderActivity.this, R.string.sms_not_delivered, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };
        }

        if (!registeredDeliveryReceiver) {
            registerReceiver(deliveredReceiver, new IntentFilter(SMS_DELIVERED));
            registeredDeliveryReceiver = true;
        }

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        String smsBody = pair.second;
        smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
    }
}

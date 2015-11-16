package ahmed.com.easysmssender.smssender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ahmed.com.easysmssender.R;
import ahmed.com.easysmssender.entities.User;
import ahmed.com.easysmssender.utils.DeviceUtils;
import ahmed.com.easysmssender.utils.DividerItemDecoration;
import ahmed.com.easysmssender.views.SearchView;

/**
 * The SMS sender fragment.
 *
 * Created by ahmed on 11/16/15.
 */
public class SmsSenderFragment extends Fragment {

    public static final String TAG = SmsSenderFragment.class.getSimpleName();

    protected ContactsAdapter contactsAdapter;
    private SearchView searchView;
    // search listeners.
    private View.OnClickListener mClearSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            contactsAdapter.search(null);
        }
    };
    private SearchView.OnSearchListener mSearchListener = new SearchView.OnSearchListener() {

        @Override
        public void onSearch(View v, final String text) {
            contactsAdapter.search(text);
        }
    };
    private TextView myNumberTextView;
    private EditText smsEditText;
    private ProgressDialog progressDialog;
    private SmsSender smsSender;

    public static SmsSenderFragment newInstance() {
        return new SmsSenderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SmsSender) {
            smsSender = (SmsSender) activity;
        } else {
            throw new RuntimeException("Your activity has to implement the interface SmsSender!");
        }
    }

    private void initAdapter() {

        contactsAdapter = new ContactsAdapter();

        contactsAdapter.setSelectionListener(new ContactRecyclerViewHolder.SelectionListener() {

            @Override
            public void onSelectionChanged(RecyclerView.ViewHolder holder, final User user) {
                String smsBody = smsEditText.getText().toString();
                if (TextUtils.isEmpty(smsBody)) {
                    Toast.makeText(getActivity(), getString(R.string.error_empty_message), Toast.LENGTH_SHORT).show();
                    smsEditText.performClick();
                } else {
                    if (smsSender != null) {
                        smsSender.sendSms(user,smsBody);
                    }
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms_sender, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSearchView(view);
        initSmsSenderLayout(view);
        initRecyclerView(view);
        loadLocalContacts();
    }

    private void initSmsSenderLayout(View view) {
        myNumberTextView = (TextView) view.findViewById(R.id.my_number);
        myNumberTextView.setText(DeviceUtils.getCurrentUserPhoneNumber(view.getContext()));
        smsEditText = (EditText) view.findViewById(R.id.sms_to_send);
    }


    private void loadLocalContacts() {
        new LocalContactsLoader(getActivity(), new LocalContactsLoader.ContactsLoadingListener() {
            @Override
            public void onStartedLoading() {
                if(getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) return;
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading_users));
                progressDialog.show();
            }

            @Override
            public void onError() {
                if(getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) return;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.an_error_occured), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<User> localUsers) {
                if(getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) return;
                progressDialog.dismiss();
                contactsAdapter.setFullDataList(localUsers);
                contactsAdapter.search(searchView.getSearchText());
            }

            @Override
            public void onProgress(int numberOfLoadedUsers) {
                if(getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) return;
                progressDialog.setMessage(String.format(getString(R.string.loading_users_by_number), numberOfLoadedUsers));
            }
        }).execute();
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(DividerItemDecoration.getTransparent(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(contactsAdapter);
    }

    private void initSearchView(View view) {
        searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setClearListener(mClearSearchListener);
        searchView.setSearchListener(mSearchListener);
    }

}

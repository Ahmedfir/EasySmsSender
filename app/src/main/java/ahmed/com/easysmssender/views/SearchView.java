package ahmed.com.easysmssender.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ahmed.com.easysmssender.R;
import ahmed.com.easysmssender.utils.LogUtils;
import ahmed.com.easysmssender.utils.SoftKeyboardUtils;

/**
 * Created by Ahmed on 15/10/2014.
 */
public class SearchView extends RelativeLayout {

    /**
     * Tag for logging.
     */
    private static final String TAG = SearchView.class.getSimpleName();

    private EditText searchEditText;

    private ImageView searchImageView;

    private ImageView clearImageView;

    private OnSearchListener searchListener;

    private boolean realTimeSearchEnabled = true;

    private OnClickListener clearClickListener;
    private TextView hintTextView;
    private ImageView hintImageView;
    private boolean hiding = false;
    private TextView cancelButton;
    private RelativeLayout hintRelativeLayout;

    public SearchView(Context context) {
        super(context);
        initLayout(context);
        initListeners();
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        initListeners();
    }

    public SearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
        initListeners();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE && searchEditText!= null){
            SoftKeyboardUtils.forceHide((Activity) getContext(),searchEditText);
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE && changedView.equals(this) && searchEditText!= null){
            SoftKeyboardUtils.forceHide((Activity) getContext(), searchEditText);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        SoftKeyboardUtils.forceHide((Activity) getContext(), searchEditText);
        super.onDetachedFromWindow();
    }

    public String getSearchText() {
        return String.valueOf(searchEditText.getText());
    }

    public void setSearchText(String searchText) {
        searchEditText.setText(searchText);
    }

    public OnSearchListener getSearchListener() {
        return searchListener;
    }

    public void setSearchListener(OnSearchListener searchListener) {
        this.searchListener = searchListener;
    }

    private void initLayout(Context context) {
        LayoutInflater.from(context).inflate(
                R.layout.view_search, this);
        searchEditText = (EditText) this
                .findViewById(R.id.search_editText);
        searchImageView = (ImageView) this.findViewById(R.id.search_imageView);
        searchImageView.getDrawable().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);

        clearImageView = (ImageView) this.findViewById(R.id.clear_imageView);
        clearImageView.getDrawable().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);


        setClearButtonVisible(getSearchText() != null);

        hintTextView = (TextView) this.findViewById(R.id.hint_textview);
        hintImageView = (ImageView) findViewById(R.id.hint_search_imageView);
        hintImageView.getDrawable().mutate().setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);

        hintRelativeLayout = (RelativeLayout) findViewById(R.id.hint_relativelayout);

        cancelButton = (TextView) this.findViewById(R.id.cancel_button);
    }

    private void initListeners() {

        searchImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSearch(v, true);
            }
        });

        setClearListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Clear on click remoteListener not set");
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {

            private String lastFilter;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtils.v(TAG, "s = " + s);
                LogUtils.v(TAG,"start = "+ start );
                LogUtils.v(TAG,"before = "+ before );
                LogUtils.v(TAG,"count = "+ count );
                if (realTimeSearchEnabled && count != before) {
                    if (!TextUtils.isEmpty(s)) {
                        attemptSearch(SearchView.this, false);
                    } else {
                        if (clearClickListener != null) clearClickListener.onClick(SearchView.this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                setClearButtonVisible(getSearchText() != null);
            }
        });

        searchEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    attemptSearch(v, true);
                }
                return false;
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity) {
                    SoftKeyboardUtils.forceHide((Activity) getContext(), searchEditText);
                }
                searchEditText.setVisibility(INVISIBLE);
                animateToHint();
            }
        });

        hintRelativeLayout.setClickable(true);
        hintRelativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHint();
            }
        });

    }

    private void hideHint() {
        if (hiding) return;
        hiding = true;
        TranslateAnimation animation = new TranslateAnimation(hintImageView.getX(),
                searchImageView.getX(),
                hintImageView.getY(),
                searchImageView.getY()
        );

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hintImageView.setVisibility(GONE);
                hintTextView.setVisibility(GONE);
                hintRelativeLayout.setVisibility(GONE);
                searchImageView.setVisibility(VISIBLE);
                cancelButton.setVisibility(VISIBLE);
                searchEditText.setVisibility(VISIBLE);
                searchEditText.setHint(R.string.Search);
                searchEditText.requestFocus();
                hiding = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        hintImageView.startAnimation(animation);
        SoftKeyboardUtils.forceShow((Activity) getContext());
    }

    private void animateToHint() {
        cancelButton.setVisibility(GONE);
        searchEditText.setHint(null);
        searchImageView.setVisibility(GONE);
        clearImageView.setVisibility(GONE);
        hintImageView.setVisibility(VISIBLE);
        hintTextView.setVisibility(VISIBLE);
        hintRelativeLayout.setVisibility(VISIBLE);
    }

    private void setClearButtonVisible(boolean visible) {
        if (visible) {
            visible = getSearchText().length() != 0;
        }
        if (visible) {
            clearImageView.setVisibility(View.VISIBLE);
        } else {
            clearImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void attemptSearch(View v, boolean hideKeyboard) {
        if (getSearchText() != null) {
            if (hideKeyboard) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
            LogUtils.v(TAG,"getSearchText = "+ getSearchText() );
            searchListener.onSearch(v, getSearchText());
        }
    }

    public void setClearListener(final OnClickListener onClickListener) {
        this.clearClickListener = onClickListener;
        clearImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSearchText() != null) {
                    setSearchText(null);
                    setClearButtonVisible(false);
                    clearClickListener.onClick(v);
                }
            }
        });
    }

    public interface OnSearchListener {
        void onSearch(View v, String text);
    }

    public boolean isRealTimeSearchEnabled() {
        return realTimeSearchEnabled;
    }

    public void setRealTimeSearchEnabled(boolean realTimeSearchEnabled) {
        this.realTimeSearchEnabled = realTimeSearchEnabled;
    }

}

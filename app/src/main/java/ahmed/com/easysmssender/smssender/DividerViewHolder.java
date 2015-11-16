package ahmed.com.easysmssender.smssender;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ahmed.com.easysmssender.R;

/**
 * The alphabet divider view holder.
 *
 * Created by ahmed on 7/30/15.
 */
class DividerViewHolder extends RecyclerView.ViewHolder {

    public static View inflateView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell_contacts_divider, parent, false);
    }

    public TextView label;

    public DividerViewHolder(View itemView) {
        super(itemView);
        label = (TextView) itemView.findViewById(R.id.divider_label);
    }

    public boolean bind(Object object) {
        if (object == null) return false;
        String dividerLabel = null;
        if (object instanceof String) {
            dividerLabel = (String) object;
        } else if (object instanceof Integer) {
            dividerLabel = label.getResources().getString((Integer) object);
        }
        label.setText(dividerLabel);
        return true;
    }

    private int dpToPixels(Context c, int dp) {
        float density = c.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}

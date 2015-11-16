package ahmed.com.easysmssender;

import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Filter;
import android.widget.Filterable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

import ahmed.com.easysmssender.utils.LogUtils;

/**
 * Abstract RecyclerView Adapter of the {@link android.support.v7.widget.RecyclerView.Adapter} that implements a filterable behavior.
 * Implement this to have a filterable Adapter of a list of items of type {@code T} with a filtering history cache (in memory cache).
 * The adapter entries are of type {@code E}.
 *
 * @see {@link LocalSearchAdapter#getFilter()}
 * <p/>
 * Created by ahmed on 8/4/15.
 */
public abstract class LocalSearchAdapter<T, E> extends RecyclerView.Adapter implements Filterable {

    private static final int DEFAULT_MAX_SEARCH_CACHE_SIZE = 50;

    /**
     * used for debugging reasons
     */
    private static final String TAG = LocalSearchAdapter.class.getSimpleName();

    /**
     * The entries that will be bound to the RecyclerView.
     */
    protected List<E> entries;

    /**
     * The full list of the provided items.
     */
    protected List<T> fullDataList;

    /**
     * The full list of the entries.
     */
    protected ArrayList<E> fullListOfEntries;

    /**
     * Cache for the searching history, the list of items that matches the applied filter.
     */
    protected LruCache<CharSequence, List<T>> searchCachedData;

    /**
     * Cache for the searching history, the list of entries that matches the applied filter.
     */
    protected LruCache<CharSequence, List<E>> searchCachedEntries;

    /**
     * the current applied filter.
     */
    private CharSequence currentFilter;


    /**
     * Creates a new instance of the {@code LocalSearchAdapter} with the default maximum size of the search cache.
     *
     * @see {@link LocalSearchAdapter#LocalSearchAdapter(int)}
     * @see {@link LocalSearchAdapter#DEFAULT_MAX_SEARCH_CACHE_SIZE}
     */
    protected LocalSearchAdapter() {
        this(DEFAULT_MAX_SEARCH_CACHE_SIZE);
    }

    /**
     * Creates a new instance of the {@code LocalSearchAdapter}.
     *
     * @param maxSearchCacheSize the maximum size of the search cache.
     */
    protected LocalSearchAdapter(int maxSearchCacheSize) {
        entries = new ArrayList<>();
        searchCachedEntries = new LruCache<>(maxSearchCacheSize);
        searchCachedData = new LruCache<>(maxSearchCacheSize);
        fullListOfEntries = new ArrayList<>();
    }

    /**
     * set all the provided items list.
     *
     * @param fullDataList the full list of the items.
     */
    public void setFullDataList(List<T> fullDataList) {
        LogUtils.i(TAG, "+++++ setFullDataList !!!");
        this.fullDataList = new ArrayList<>(fullDataList);
    }

    public void search(final CharSequence filter) {
        LogUtils.d(TAG, "search attempt : filter = " + filter);
        if (fullDataList == null || fullDataList.isEmpty()) return;
        LogUtils.d(TAG, "search attempt : fullDataList = " + fullDataList);
        this.currentFilter = filter;

        if (TextUtils.isEmpty(filter)) {
            if (fullListOfEntries.isEmpty()) {
                updateStaticItemToEntriesByFilter(null);
                addResultToEntries(fullDataList);
                fullListOfEntries.addAll(entries);
            } else {
                entries.clear();
                entries.addAll(fullListOfEntries);
            }
        } else {
            List<E> entryList = searchCachedEntries.get(filter);
            if (entryList != null) {
                entries = new ArrayList<>(entryList);
            } else {
                getFilter().filter(filter);
                return;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        throw new RuntimeException("Implement the getFilter method!");
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    private void saveToCache(CharSequence filter, List<T> filteredList) {
        if (!TextUtils.isEmpty(filter)) {
            searchCachedData.put(filter, new ArrayList<>(filteredList));
            searchCachedEntries.put(filter, new ArrayList<>(entries));
        } else {
            if (fullListOfEntries.isEmpty()) fullListOfEntries.addAll(entries);
        }
    }

    /**
     * example of static items: a footer or header of the list.
     */
    public void showStaticItem() {
        if (updateStaticItemToEntriesByFilter(null)) {
            notifyDataSetChanged();
        }
    }

    /**
     * Implement this to update the static items by filter.
     *
     * @param filter the applied filter.
     * @return {@code true} if the static items are added, otherwise {@code false}.
     */
    public boolean updateStaticItemToEntriesByFilter(CharSequence filter){
        entries.clear();
        return false;
    }

    /**
     * Implement this to add the result of the filtering to the entries list.
     *
     * @param itemsList
     */
    public abstract void addResultToEntries(@NonNull List<T> itemsList);

    /**
     * A Filter implementation that includes the cache checking.
     */
    protected abstract class FilterBase extends Filter {

        /**
         * Implement this to define the matching condition of an item with a filter.
         *
         * @param item   the item to check.
         * @param filter the applied filter.
         * @return {@code true} if the item matches the applied filter, otherwise {@code false}.
         */
        protected abstract boolean matchesFilter(T item, CharSequence filter);

        @Override
        protected FilterResults performFiltering(final CharSequence filter) {
            FilterResults results = new FilterResults();
            String filterParent = null;
            for (int i = filter.length() - 1; i >= 0; i--) {
                filterParent = ((String) filter).substring(0, i);
                if (((String) filter).toLowerCase().contains(filterParent.toLowerCase()))
                    break;
            }

            List<T> itemsList = new ArrayList<>();
            if (!TextUtils.isEmpty(filterParent)) {
                if (searchCachedData.get(filterParent) != null) {
                    itemsList.addAll(searchCachedData.get(filterParent));
                } else {
                    itemsList.addAll(fullDataList);
                }
            } else {
                itemsList.addAll(fullDataList);
            }

            List<T> filteredList = new ArrayList<>();
            CollectionUtils.select(itemsList, new Predicate<T>() {
                @Override
                public boolean evaluate(T item) {
                    return (matchesFilter(item, filter));
                }
            }, filteredList);

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            updateStaticItemToEntriesByFilter(constraint);

            List<T> filteredList = new ArrayList<>();
            if (results.count > 0) {
                filteredList.addAll((List<T>) results.values);
                addResultToEntries(filteredList);
            }

            saveToCache(constraint, filteredList);

            if (constraint == null && currentFilter == null || currentFilter.equals(constraint))
                notifyDataSetChanged();
        }
    }
}

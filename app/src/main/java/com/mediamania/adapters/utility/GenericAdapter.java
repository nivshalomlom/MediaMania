package com.mediamania.adapters.utility;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A generic recycler view adapter base class
 * @param <T> The item type
 */
public abstract class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {

    private final int layout_res_id;
    private final Collection<T> originalSet;
    private LinkedList<T> currentSet;

    private BiConsumer<T, Integer> onItemClick;

    /**
     * Creates a new generic adapter
     * @param layout_res_id The resources id of the item layout
     * @param items The items
     */
    public GenericAdapter(int layout_res_id, Collection<T> items) {
        this.layout_res_id = layout_res_id;
        this.originalSet = items;
        this.currentSet = new LinkedList<>(items);

        this.onItemClick = null;
    }

    /**
     * Fills a given view with the item details
     * @param itemView The item view in the recycler view
     * @param item The item
     * @param position The item position
     */
    protected abstract void setupView(View itemView, T item, int position);

    @NonNull
    @Override
    public GenericAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.layout_res_id, parent, false);
        return new GenericAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericAdapter.ViewHolder holder, int position) {
        // Get item
        T item = this.currentSet.get(position);

        // Setup view
        this.setupView(holder.itemView, item, position);

        // Set on click listener
        holder.itemView.setOnClickListener(view -> {
            if (this.onItemClick != null)
                this.onItemClick.accept(item, position);
        });
    }

    @Override
    public int getItemCount() { return this.currentSet.size(); }

    /**
     * Register a on item click listener
     * @param onItemClick The method to run on item click
     */
    public void setOnItemClickListener(BiConsumer<T, Integer> onItemClick) { this.onItemClick = onItemClick; }

    /**
     * Sorts the items with the given comparator
     * @param comparator The comparator
     */
    @SuppressLint("NotifyDataSetChanged")
    public void sortBy(Comparator<T> comparator) {
        this.currentSet.sort(comparator);
        this.notifyDataSetChanged();
    }

    /**
     * Filters the items with a given filter predicate
     * @param filter The filter
     */
    @SuppressLint("NotifyDataSetChanged")
    public void filterBy(Predicate<T> filter, boolean resetBeforeFilter) {
        Collection<T> base = resetBeforeFilter ? this.originalSet : this.currentSet;
        this.currentSet = base.stream().filter(filter).collect(Collectors.toCollection(LinkedList::new));
        this.notifyDataSetChanged();
    }

    /**
     * Resets the list to its original elements
     */
    @SuppressLint("NotifyDataSetChanged")
    public void resetFilter() {
        this.currentSet = new LinkedList<>(this.originalSet);
        this.notifyDataSetChanged();
    }

    /**
     * Adds the given item to the adapter list
     * @param item The item to add
     */
    public void add(T item) {
        int index = this.currentSet.size();

        this.originalSet.add(item);
        this.currentSet.add(item);

        this.notifyItemInserted(index);
    }

    /**
     * Removes the given item from the adapter list
     * @param item The item to remove
     */
    public void remove(T item) {
        int index = this.currentSet.size();

        this.originalSet.remove(item);
        this.currentSet.remove(item);

        this.notifyItemRemoved(index);
    }

    /**
     * Adds a given collection of items to the adapter
     * @param items The items to add
     */
    public void addAll(Collection<T> items) {
        int originalSize = this.originalSet.size();
        int newSize = originalSize + items.size();

        this.originalSet.addAll(items);
        this.currentSet.addAll(items);

        for (int i = originalSize; i < newSize; i++)
            this.notifyItemInserted(i);
    }

    /**
     * Removes a given collection of items from the adapter
     * @param items The items to remove
     */
    public void removeAll(Collection<T> items) {
        int originalSize = this.originalSet.size();
        int newSize = originalSize + items.size();

        this.originalSet.removeAll(items);
        this.currentSet.removeAll(items);

        for (int i = originalSize; i < newSize; i++)
            this.notifyItemRemoved(i);
    }

    /**
     * Updates the item at index to the new item
     * @param index The index of the item
     * @param item The new item
     */
    public void update(int index, T item) {
        // Update current set
        T original = this.currentSet.get(index);
        this.currentSet.set(index, item);

        // Update original
        this.originalSet.remove(original);
        this.originalSet.add(item);

        // Notify adapter
        this.notifyItemChanged(index);
    }

    /**
     * A empty view holder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder { public ViewHolder(@NonNull View itemView) { super(itemView); } }

}

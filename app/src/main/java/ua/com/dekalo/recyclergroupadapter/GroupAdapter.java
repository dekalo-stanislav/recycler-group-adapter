package ua.com.dekalo.recyclergroupadapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import java.util.List;

/**
 * Adapter that will group other adapter.
 * <p/>
 * Created by dekalo on 03.09.15.
 */
public class GroupAdapter extends RecyclerView.Adapter<ViewHolder> {

    private RecyclerView.Adapter<ViewHolder>[] adapters;
    private int maxViewType = 1000;

    /**
     * Mentioned adapter should have view types not more than 100.
     *
     * @param adapters
     */
    public GroupAdapter(RecyclerView.Adapter<ViewHolder>... adapters) {
        this.adapters = adapters;
        registerObserver();
    }

    public void setAdapters(RecyclerView.Adapter<ViewHolder>... adapters) {
        this.adapters = adapters;
        registerObserver();
        notifyDataSetChanged();
    }

    public void setMaxViewTypeValue(int maxViewType) {
        this.maxViewType = maxViewType;
    }

    private void registerObserver() {
        for (RecyclerView.Adapter<ViewHolder> adapter : adapters) {
            adapter.registerAdapterDataObserver(new LocalObserver());
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int absolutePosition) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(absolutePosition);
        adapters[indexAndPosition[0]].onBindViewHolder(holder, indexAndPosition[1]);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(holder.getAdapterPosition());
        adapters[indexAndPosition[0]].onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(holder.getAdapterPosition());
        adapters[indexAndPosition[0]].onViewDetachedFromWindow(holder);
    }

    @Override
    public boolean onFailedToRecycleView(ViewHolder holder) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(holder.getAdapterPosition());
        return adapters[indexAndPosition[0]].onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(holder.getAdapterPosition());
        adapters[indexAndPosition[0]].onViewAttachedToWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        for (RecyclerView.Adapter adapter : adapters) {
            adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(holder.getAdapterPosition());
        adapters[indexAndPosition[0]].onBindViewHolder(holder, indexAndPosition[1]);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        for (RecyclerView.Adapter adapter : adapters) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int relativeViewType = viewType % maxViewType;
        int adapterIndex = viewType / maxViewType;
        return adapters[adapterIndex].onCreateViewHolder(parent, relativeViewType);
    }

    @Override
    public int getItemViewType(int absolutePosition) {
        int[] indexAndPosition = getAdapterIndexAndRelativePosition(absolutePosition);
        return relativeViewTypeToAbsolute(indexAndPosition[0], adapters[indexAndPosition[0]].getItemViewType(indexAndPosition[1]));
    }

    private int relativeViewTypeToAbsolute(int adapterIndex, int relativeViewType) {
        if (relativeViewType > maxViewType || relativeViewType < 0) {
            throw new UnsupportedOperationException("Please use view type in range from specified in setMaxViewTypeValue(int), current value = " + maxViewType);
        }
        return maxViewType * adapterIndex + relativeViewType;
    }

    private int[] getAdapterIndexAndRelativePosition(int absolutePosition) {
        int relativePosition = absolutePosition;
        for (int i = 0; i < adapters.length; i++) {
            RecyclerView.Adapter adapter = adapters[i];
            if (adapter.getItemCount() > relativePosition) {
                return new int[]{i, relativePosition};
            } else {
                relativePosition -= adapter.getItemCount();
            }
        }
        throw new IllegalStateException("Unreachable " + absolutePosition);
    }

    @Override
    public int getItemCount() {
        int result = 0;
        for (RecyclerView.Adapter adapter : adapters) {
            result += adapter.getItemCount();
        }
        return result;
    }

    private class LocalObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }
    }
}

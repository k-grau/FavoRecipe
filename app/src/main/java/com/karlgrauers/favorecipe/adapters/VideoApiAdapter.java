package com.karlgrauers.favorecipe.adapters;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.models.video.VideoItem;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import java.util.ArrayList;
import java.util.List;



/*
 * Adapterklass för att hantera youtubevideos i recyclerview. Ärver
 * av androidinterna klassen RecyclerView. Ansvarsområden är föjande:
 * Skapa viewholder och binda videodata till viewholder. Hantera
 * lista med videos - alltså ta bort och lägga till i denna.
 * Initialisera varje video i en 'YouTubePlayerView'.
 * Youtube-playervyn kommer från ett externt biliotek
 * skapat av Pier Francesco Soffritti: https://github.com/PierfrancescoSoffritti/android-youtube-player
 */

public class VideoApiAdapter extends RecyclerView.Adapter<VideoApiAdapter.VideoHolder> {
    private final List<VideoItem> VIDEO_ITEMS = new ArrayList<>();
    private final OnAdapterClickListener ADAPTER_CLICK_LISTENER;
    private final Lifecycle LIFE_CYCLE;
    private final static String YT_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";


    /**
     * Konstruktor.
     * @param adapterClickListener innehåller klass som
     *                             ska implementera interface
     *                             'OnAdapterClickListener'
     * @param lifecycle innehåller livscykelobjekt från vy
     *                  högt i appens vyhierarki, alltså 'MainActivity'
     */
    public VideoApiAdapter(OnAdapterClickListener adapterClickListener, Lifecycle lifecycle) {
        ADAPTER_CLICK_LISTENER = adapterClickListener;
        LIFE_CYCLE = lifecycle;
    }


    /**
     * Skapa viewholder med layouten 'layout_video_item' som gäller
     * för varje videoobjekt i listan.
     * @param parent innehåller föräldravy att placera layouten i.
     * @param viewType innehåller id för den typ av vy som ska skapas
     *                 (används ej)
     * @return instans av statiska inre klassen 'VideoHolder'
     */
    @NonNull
    @Override
    public VideoApiAdapter.VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_video_item, parent, false);
        return new VideoApiAdapter.VideoHolder(itemView, LIFE_CYCLE);
    }


    /**
     * Bind video-data till viewholder. Metoden kallas för
     * varje video som finns i lista 'VIDEO_ITEMS'.
     * @param holder den viewholder till vilken datan ska bindas,
     *               i detta fall innehåller den 'layout_video_item'
     * @param position den position i listan på vilken videoobjektet
     *                 befinner sig.
     */
    @Override
    public void onBindViewHolder(@NonNull VideoApiAdapter.VideoHolder holder, int position) {
        VideoItem videoItem = VIDEO_ITEMS.get(position);
        holder.TV_TITLE.setText(videoItem.getSnippet().getTitle());
        holder.TV_TITLE.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemLinkClicked(String.format("%s%s",
                YT_VIDEO_BASE_URL, videoItem.getId().getVideoId()), v));
        initVideo(videoItem.getId().getVideoId(), holder.YT_VIDEO);
    }



    /**
     * Initialiserar video. Kallas för varje videoobjekt i listan
     * 'VIDEO_ITEMS.
     * @param videoId innehåller youtubes unika id för videon.
     * @param youTubePlayerView innehåller instans av youTubePlayerView.
     */
    private void initVideo(final String videoId, YouTubePlayerView youTubePlayerView) {

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {

            //När video är redo, cue:a den (alltså spela inte upp utan automatiskt utan låt användare själv trycka play)
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }

            //Kallas när video spelat färdigt.
            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                      @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
            }
        });
    }


    /**
     * Getter för videolista.
     * @return storlek på listan.
     */
    @Override
    public int getItemCount() {
        return VIDEO_ITEMS.size();
    }

    /**
     * Getter för vy-typ.
     * @return vy på position 'position' i videolista.
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }


    /**
     * Se till att varje video som ritas
     * ut har unikt id (detta måste användas annars
     * ritas videos ut fel vid uppdatering).
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }


    /**
     * Metod som måste överskuggas för
     * att metod 'setHasStableIds' ska fungera.
     * @return id för video på position 'position' i videolista.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Setter för samtliga videos. Rensar lista
     * 'VIDEO_ITEMS' och lägger till 'videoItems' i denna.
     * @param videoItems innehåller lista med videoobjekt.
     */
    public void setVideos(List<VideoItem> videoItems) {
        VIDEO_ITEMS.clear();
        VIDEO_ITEMS.addAll(videoItems);
        notifyDataSetChanged();
    }


    /**
     * Setter för enskild videoo. Rensar lista
     * 'VIDEO_ITEMS' och lägger till 'videoItems' i denna.
     * @param videoItem innehåller det videoobjekt som ska läggas
     *                  till i 'VIDEO_ITEMS'.
     */
    public void setVideo(VideoItem videoItem) {
        VIDEO_ITEMS.add(videoItem);
    }


    /**
     * Rensa videolista.
     */
    public void clearAll() {
        VIDEO_ITEMS.clear();
        notifyDataSetChanged();
    }


    /*
     * Inre statisk klass med ansvar för att definiera de vyer
     * som ska ingå i viewholdern.
     */
    static class VideoHolder extends RecyclerView.ViewHolder {
        final private TextView TV_TITLE;
        final private YouTubePlayerView YT_VIDEO;


        /**
         * Konstruktor.
         * @param itemView innehåller den vy som ska användas
         *                 för varje video-item, i detta fall
         *                 'layout_video_item'.
         */
        public VideoHolder(@NonNull View itemView, Lifecycle lifeCycle) {
            super(itemView);
            TV_TITLE = itemView.findViewById(R.id.video_item_title);
            YT_VIDEO = itemView.findViewById(R.id.video_item_youtube_view);
            TV_TITLE.setPaintFlags(TV_TITLE.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            lifeCycle.addObserver(YT_VIDEO);
        }
    }
}

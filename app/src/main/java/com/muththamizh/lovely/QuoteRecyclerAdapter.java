package com.muththamizh.lovely;

import static com.muththamizh.lovely.R.string.Facebook_Native_placement;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.muththamizh.lovely.R;

import java.util.ArrayList;
import java.util.List;

public class QuoteRecyclerAdapter extends RecyclerView.Adapter<QuoteViewHolder>{

    Context context;
    List<QuoteResponse> list;
    CopyListener listener;


    String TAG = "Tag";
    private NativeAd nativeAd;
    private LinearLayout ladView;
    View containerView;

    public QuoteRecyclerAdapter(Context context, List<QuoteResponse> list, CopyListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        containerView=LayoutInflater.from(context).inflate(R.layout.nativefacebook_ads,parent,false);
        View view=LayoutInflater.from(context).inflate(R.layout.list_quote,parent,false);

        return new QuoteViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {

        AudienceNetworkAds.initialize(context);

        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(context.getApplicationContext(),context.getString(Facebook_Native_placement));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }
            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }
            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                nativeAd.unregisterView();
                // Add the Ad view into the ad container.

                LayoutInflater inflater = LayoutInflater.from(context);
                // Inflate the Ad view. The layout referenced should be the one you created in the last step.
                ladView = (LinearLayout) inflater.inflate(R.layout.nativefacebook_ads, holder.nativeAdLayout, false);
                holder.nativeAdLayout.addView(ladView);
                // Add the AdOptionsView
                LinearLayout adChoicesContainer = containerView.findViewById(R.id.ad_choices_container);
                AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, holder.nativeAdLayout);
                adChoicesContainer.removeAllViews();
                adChoicesContainer.addView(adOptionsView, 0);
                // Create native UI using the ad metadata.
                MediaView nativeAdIcon = ladView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = ladView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = ladView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = ladView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = ladView.findViewById(R.id.native_ad_body);
                TextView sponsoredLabel = ladView.findViewById(R.id.native_ad_sponsored_label);
                Button nativeAdCallToAction = ladView.findViewById(R.id.native_ad_call_to_action);
                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                nativeAdBody.setText(nativeAd.getAdBodyText());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
                // Create a list of clickable views
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(
                        ladView,
                        nativeAdMedia,
                        nativeAdIcon,
                        clickableViews);

                holder.nativeAdLayout.setVisibility(View.VISIBLE);

            }
            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }
            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };
        // Request an ad
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());






        holder.textView_quote.setText(list.get(position).getText());
        holder.textView_author.setText(list.get(position).getAuthor());

        holder.button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCopyClicked(list.get(holder.getAdapterPosition()).getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class QuoteViewHolder extends RecyclerView.ViewHolder{

    TextView textView_quote,textView_author;
    Button button_copy;
    NativeAdLayout nativeAdLayout;

    public QuoteViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_quote = itemView.findViewById(R.id.textView_quote);
        textView_author = itemView.findViewById(R.id.textView_author);
        button_copy = itemView.findViewById(R.id.button_copy);
        nativeAdLayout = itemView.findViewById(R.id.native_ad_container);
    }
}

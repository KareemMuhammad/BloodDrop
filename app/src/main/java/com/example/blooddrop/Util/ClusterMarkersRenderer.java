package com.example.blooddrop.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.blooddrop.Models.ClusterMarkers;
import com.example.blooddrop.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class ClusterMarkersRenderer extends DefaultClusterRenderer<ClusterMarkers> {
    private IconGenerator iconGenerator;
    private ImageView imageView;
    private int markerHeight;
    private int markerWidth;

    public ClusterMarkersRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarkers> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerHeight));
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkers item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIconId());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarkers> cluster) {
        return false;
    }
}

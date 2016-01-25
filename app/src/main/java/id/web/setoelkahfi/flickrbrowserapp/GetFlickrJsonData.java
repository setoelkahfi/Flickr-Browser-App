package id.web.setoelkahfi.flickrbrowserapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eldin on 1/25/2016.
 * id.web.setoelkahfi.flickrbrowserapp
 */
public class GetFlickrJsonData extends GetRawData {

    private String LOG_TAG = GetFlickrJsonData.class.getSimpleName();
    private List<Photo> mPhotos;
    private Uri mDestinationUri;

    public GetFlickrJsonData(String searchCriteria, boolean matchAll) {
        super(null);
        createAndUpdateUri(searchCriteria, matchAll);
        mPhotos = new ArrayList<>();
    }

    public void execute() {
        super.setmRawURL(mDestinationUri.toString());
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.v(LOG_TAG, "Built URI = " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String searchCriteria, boolean matchAll) {
        final String FLICKR_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAGMODE_PARAM = "tagmode";
        final String FORMAT_PARAM = "format";
        final String NOJSONCALLBACK_PARAM = "nojsoncallback";

        mDestinationUri = Uri.parse(FLICKR_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NOJSONCALLBACK_PARAM, "1")
                .build();

        return mDestinationUri != null;

    }

    public void processResult() {
        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw file.");
            return;
        }

        final String FLICKR_ITEMS = "items";
        final String FLICKR_TITLE = "title";
        final String FLICKR_MEDIA = "media";
        final String FLICKR_PHOTO_URL = "m";
        final String FLICKR_AUTHOR = "author";
        final String FLICKR_AUTHOR_ID = "author_id";
        final String FLICKR_LINK = "link";
        final String FLICKR_TAGS = "tags";

        try {

            JSONObject jsonObject = new JSONObject(getmData());
            JSONArray jsonArray = jsonObject.getJSONArray(FLICKR_ITEMS);
            for (int i=0; i<jsonArray.length(); i++) {

                JSONObject jsonPhoto = jsonArray.getJSONObject(i);
                String title = jsonPhoto.getString(FLICKR_TITLE);
                String author = jsonPhoto.getString(FLICKR_AUTHOR);
                String authorId = jsonPhoto.getString(FLICKR_AUTHOR_ID);
                String link = jsonPhoto.getString(FLICKR_LINK);
                String tags = jsonPhoto.getString(FLICKR_TAGS);

                JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA);
                String photoUrl = jsonMedia.getString(FLICKR_PHOTO_URL);

                Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);

                this.mPhotos.add(photoObject);
            }

            for (Photo singlePhoto: mPhotos) {
                Log.v(LOG_TAG,singlePhoto.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error processing JSON data.");
        }

    }

    public class DownloadJsonData extends DownloadRawData {

        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }
    }

}

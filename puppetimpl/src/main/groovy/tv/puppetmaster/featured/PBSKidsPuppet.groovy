package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.regex.Matcher

public class PBSKidsPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def boolean mIsTopLevel
    def String mImageUrl

    public PBSKidsPuppet() {
        this(
                null,
                "http://pbskids.org/pbsk/video/api/getShows/?callback=&destination=national&return=images",
                "PBS Kids",
                "Watch videos and full episodes of your favorite PBS KIDS shows.",
                true,
                "http://pbshawaii.org/wordpress/wp-content/uploads/2014/09/PBS_Kids_Logo_640x480-300x225.jpg"
        )
    }

    public PBSKidsPuppet(ParentPuppet parent, String url, String name, String description, boolean isTopLevel, String imageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mIsTopLevel = isTopLevel
        mImageUrl = imageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new PBSKidsPuppetIterator()

        if (mUrl.contains("/api/getShows")) {
            String page = new URL(mUrl).getText()
            page = page.substring(1, page.length() - 1)
            JSONArray items = (JSONArray) new JSONObject(page).get("items")

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                String name = item.getString("title")
                String description = item.getString("description") + " (Ages " + item.getString("age_range") + ")"
                String url = "http://pbskids.org/pbsk/video/api/getVideos/?startindex=1&endindex=200&category=&group=&selectedID=&status=available&player=flash&flash=true&program=" + name.replace(" ","%20").replace("&", "%26")
                String image = item.getJSONObject("images").getJSONObject("program-kids-square").getString("url")
                children.add(new PBSKidsPuppet(this, url, name, description, false, image))
            }
        } else if (mUrl.contains("/api/getVideos") && !mUrl.contains("&type=")) {
            mUrl = mUrl + "&type=episode"
        }
        if (mUrl.contains("/api/getVideos")) {
            String page = new URL(mUrl + "&type=episode").getText()
            JSONArray items = (JSONArray) new JSONObject(page).get("items")

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                String name = item.getString("title")
                String description = item.getString("description")
                String image = item.getJSONObject("images").getJSONObject("kids-mezzannine-16x9").getString("url")

                String captions
                try {
                    captions = item.getJSONObject("captions").getJSONObject("srt").getString("url")
                } catch (ignore) {
                    captions = null
                }

                JSONObject videos = item.getJSONObject("videos").getJSONObject("flash")
                String videoUrl
                try {
                    videoUrl = videos.getJSONObject("mp4-2500k").getString("url")
                } catch (Exception ex) {
                    try {
                        videoUrl = videos.getJSONObject("mp4-1200k").getString("url")
                    } catch (Exception exx) {
                        videoUrl = videos.getString("url")
                    }
                }

                if (videoUrl != null) {
                    children.add(new PBSKidsSourcesPuppet(this, videoUrl, name, description, image, captions))
                }
            }
        }
        if (mUrl.contains("/api/getVideos") && !mUrl.contains("&type=")) {
            children.add(new PBSKidsPuppet(
                    this,
                    mUrl.replace("&type=episode", "&type=clip"),
                    "Clips",
                    null,
                    false,
                    "http://pbshawaii.org/wordpress/wp-content/uploads/2014/09/PBS_Kids_Logo_640x480-300x225.jpg")
            )
        }
        return children
    }

    @Override
    boolean isTopLevel() {
        return mIsTopLevel
    }

    @Override
    String getName() {
        return mName
    }

    @Override
    String getCategory() {
        return mParent == null ? "Kids" : mParent.getName()
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return mImageUrl
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://pbs.bento.storage.s3.amazonaws.com/hostedbento-prod/filer_public/kids%20and%20families/images/mobileapps.jpg"
    }

    @Override
    boolean isUnavailableIn(String region) {
        return false
    }

    @Override
    String getPreferredRegion() {
        return null
    }

    @Override
    int getShieldLevel() {
        return 0
    }

    @Override
    ParentPuppet getParent() {
        return mParent
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return null
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF9F479A
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFA4CD39
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFF41971
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF9F479A
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    PuppetIterator getRelated() {
        return null
    }

    @Override
    public String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    def class PBSKidsPuppetIterator extends PuppetIterator {

        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        @Override
        boolean hasNext() {
            return currentIndex < mPuppets.size()
        }

        @Override
        void add(Puppet puppet) {
            mPuppets.add(puppet)
        }

        @Override
        Puppet next() {
            return mPuppets.get(currentIndex++)
        }

        @Override
        void remove() {
        }
    }

    def static class PBSKidsSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def long mDuration
        def String mImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public PBSKidsSourcesPuppet(parent, url, name, shortDescription, imageUrl, captionsUrl) {
            mParent = parent
            mUrl = url
            mName = name
            mShortDescription = shortDescription
            mDuration = duration
            mImageUrl = imageUrl
            if (captionsUrl != null) {
                SourcesPuppet.SubtitleDescription subtitleDescription = new SourcesPuppet.SubtitleDescription()
                subtitleDescription.url = captionsUrl
            }
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return mDuration
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new PBSKidsSourceIterator()
        }

        @Override
        boolean isLive() {
            return false
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return mSubtitles
        }

        @Override
        String getName() {
            return mName
        }

        @Override
        String getCategory() {
            return null
        }

        @Override
        String getShortDescription() {
            return mShortDescription
        }

        @Override
        String getImageUrl() {
            return mImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            return mImageUrl
        }

        @Override
        boolean isUnavailableIn(String region) {
            return region != 'us'
        }

        @Override
        String getPreferredRegion() {
            return 'us'
        }

        @Override
        int getShieldLevel() {
            return 0
        }

        @Override
        ParentPuppet getParent() {
            return mParent
        }

        @Override
        PuppetIterator getRelated() {
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class PBSKidsSourceIterator implements SourcesPuppet.SourceIterator {

            def SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {
                    mSource = new SourceDescription()

                    def String url = mUrl
                    if (url.contains("redirect")) {
                        def HttpURLConnection conn = (HttpURLConnection) new URL(mUrl).openConnection()
                        conn.setInstanceFollowRedirects(false)
                        url = conn.getHeaderField("Location")
                    }

                    def Matcher matcher = url =~ /.+?:(videos\/.*)/
                    if (matcher.find()) {
                        mSource.url = "http://kids.video.cdn.pbs.org/" + matcher.group(1)
                    } else {
                        mSource.url = url
                    }
                    return true
                }
                return false
            }

            @Override
            SourceDescription next() {
                return mSource
            }

            @Override
            void remove() {
            }
        }
    }
}
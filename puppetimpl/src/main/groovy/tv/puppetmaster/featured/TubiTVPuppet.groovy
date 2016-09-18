package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.regex.Matcher

public class TubiTVPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl

    public TubiTVPuppet() {
        this(
                null,
                "http://www.tubitv.com",
                "Tubi TV",
                "TV shows and movies online streaming.",
                "http://f.tqn.com/y/freebies/1/L/0/w/1/tubi-tv-logo.PNG",
                "http://dzceab466r34n.cloudfront.net/Images/ArticleImages/105698-Tubi-TV-1500.jpg"
        )
    }

    public TubiTVPuppet(ParentPuppet parent, String url, String name, String description, String imageUrl, String backgroundImageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new TubiTVPuppetIterator()

        String html = new URL(mUrl).getText().replaceAll("\n", "")

        Matcher matcher = html =~ /window\.__data=\{(.+?)\};<\/script>/

        if (matcher.find()) {
            JSONObject json = new JSONObject("{" + matcher.group(1) + "}")
            JSONObject categories = json.getJSONObject("category").getJSONObject("catIdMap")
            for (String key : categories.keySet()) {
                JSONObject category = categories.getJSONObject(key)
                String categoryName = category.getString("title")
                String id = category.get("id").toString()
                children.add(new TubiTVCategoryPuppet(this, categoryName, "category/" + id))
            }
        }
        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return mName
    }

    @Override
    String getCategory() {
        return "Entertainment"
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
        return mBackgroundImageUrl
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
        return null
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFF4983B
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFF4983B
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

    def class TubiTVPuppetIterator extends PuppetIterator {

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

    def class TubiTVCategoryPuppet implements ParentPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mId

        public TubiTVCategoryPuppet(ParentPuppet parent, String name, String id) {
            mParent = parent
            mName = name
            mId = id
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new TubiTVPuppetIterator()

            String html = new URL("http://www.tubitv.com/" + mId).getText().replaceAll("\n", "")

            Matcher matcher = html =~ /window\.__data=\{(.+?)\};<\/script>/

            if (matcher.find()) {
                JSONObject json = new JSONObject("{" + matcher.group(1) + "}")
                JSONObject items = json.getJSONObject("category").getJSONObject("catChildrenIdMap")
                for (String key : items.keySet()) {
                    JSONArray videos = items.getJSONArray(key)
                    for (int i = 0; i < videos.length(); i++) {
                        JSONObject video = videos.getJSONObject(i)
                        if (video.getString("t") == "v") {
                            children.add(new TubiTVSourcesPuppet(this, video))
                        } else if (video.getString("t") == "s") {
                            children.add(new TubiTVCategoryPuppet(this, video.getString("n"), "series/" + video.getString("i")))
                        }
                    }
                }
                JSONArray seasons = json.has("series") ? json.getJSONObject("series").getJSONArray("seasons") : JSONArray()
                for (int i = 0; i < seasons.length(); i++) {
                    JSONArray videos = seasons.getJSONObject(i).getJSONArray("k")
                    for (int j = 0; j < videos.length(); j++) {
                        JSONObject video = videos.getJSONObject(j)
                        if (video.getString("t") == "v") {
                            children.add(new TubiTVSourcesPuppet(this, video))
                        }
                    }
                }
            }
            return children
        }

        @Override
        boolean isTopLevel() {
            return false
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
            return null
        }

        @Override
        String getImageUrl() {
            return TubiTVPuppet.this.mImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            return TubiTVPuppet.this.mBackgroundImageUrl
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
        PuppetIterator getRelated() {
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }
    }

    def static class TubiTVSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def String mImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public TubiTVSourcesPuppet(parent, JSONObject video) {
            mParent = parent

            mUrl = "http://www.tubitv.com/video/" + video.getString("i")
            mName = video.getString("n")
            mShortDescription = video.getString("d")
            mImageUrl = video.getString("pp")
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return -1
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new TubiTVSourceIterator()
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
        PuppetIterator getRelated() {
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class TubiTVSourceIterator implements SourcesPuppet.SourceIterator {

            def SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {

                    mSource = new SourceDescription()

                    String html = new URL(mUrl).getText().replaceAll("\n", "")

                    Matcher matcher = html =~ /window\.__data=\{(.+?)\};<\/script>/

                    if (matcher.find()) {
                        JSONObject json = new JSONObject("{" + matcher.group(1) + "}").getJSONObject("video").getJSONObject("data")
                        mSource.url = json.getString("mh")

                        JSONArray captions = json.getJSONArray("sb")
                        for (int i = 0; i < captions.length(); i++) {
                            JSONObject sb = captions.getJSONObject(i)

                            SourcesPuppet.SubtitleDescription subs = new SourcesPuppet.SubtitleDescription()

                            subs.url = sb.getString("u")
                            subs.locale = sb.getString("l")

                            TubiTVSourcesPuppet.this.mSubtitles.add(subs)
                        }
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
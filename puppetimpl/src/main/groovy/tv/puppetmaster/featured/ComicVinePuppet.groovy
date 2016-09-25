package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class ComicVinePuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mBaseUrl
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    public ComicVinePuppet() {
        this(
                null,
                "http://api.comicvine.com",
                "Comic Vine",
                "Comic Vine is the world's largest comic book website. Watch hundreds of original videos and interviews with comic creators and artists.",
                "/video_types/?api_key=c64ac7ddc90513cc15539f1d606e8670b1ef0ae4&format=json",
                "https://raw.githubusercontent.com/WhiskeyMedia/xbmc/master/plugin.video.comicvine/icon.png",
                "http://static.comicvine.com/uploads/original/8/85249/3158213-project",
                true
        )
    }

    protected ComicVinePuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
        mParent = parent
        mBaseUrl = baseUrl
        mName = name
        mDescription = description
        mUrl = url
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
        mIsTopLevel = isTopLevel
    }

    @Override
    PuppetIterator getChildren() {
        return new ComicVineIterator(this, mBaseUrl, mUrl)
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
        return mParent == null ? "Entertainment" : mParent.getName()
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
        return mParent
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return new ComicVineSearchesPuppet(this)
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF398A4B
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFFFDA78
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFEAB648
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFEAB648
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
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    void setUrl(String url) {
        mUrl = url
    }

    def static class ComicVineSearchesPuppet extends ComicVinePuppet implements SearchesPuppet {

        def static final String SEARCH_URL = "/search/?api_key=c64ac7ddc90513cc15539f1d606e8670b1ef0ae4&format=json&resources=video&query="

        public ComicVineSearchesPuppet(ParentPuppet parent) {
            super(
                    parent,
                    "http://api.comicvine.com",
                    "Search",
                    "Search Comic Vine",
                    SEARCH_URL,
                    "https://raw.githubusercontent.com/WhiskeyMedia/xbmc/master/plugin.video.comicvine/icon.png",
                    "http://static.comicvine.com/uploads/original/8/85249/3158213-project",
                    false
            )
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(SEARCH_URL + searchQuery.replace(" ", "%20"))
        }
    }

    def class ComicVineIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def ArrayList<Puppet> mPuppets

        def int numSources = 0

        def int currentCategoryIteration = 0
        def int currentSourcesIteration = 0

        transient def JSONArray categoryItems = new JSONArray()
        transient def JSONArray sourcesItems = new JSONArray()

        public ComicVineIterator(ParentPuppet parent, String baseUrl, String url) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
        }

        @Override
        boolean hasNext() {
            if (mPuppets == null) {
                mPuppets = new ArrayList<>()

                if(mParent.isTopLevel()) {
                    mPuppets.add(new ComicVinePuppet(
                            null,
                            "http://api.comicvine.com",
                            "Latest",
                            "Latest videos from Comic Vine",
                            "/videos/?api_key=c64ac7ddc90513cc15539f1d606e8670b1ef0ae4&sort=-publish_date&format=json",
                            "https://raw.githubusercontent.com/WhiskeyMedia/xbmc/master/plugin.video.comicvine/icon.png",
                            "http://static.comicvine.com/uploads/original/8/85249/3158213-project",
                            false
                    ))
                }

                String page = new URL(mBaseUrl + mUrl).getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11'])

                JSONArray items = (JSONArray) new JSONObject(page).get("results")

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i)
                    if (mUrl.startsWith("/videos") || mUrl.startsWith("/search")) {
                        sourcesItems.put(item)
                        mPuppets.add(new ComicVineSourcesPuppet(
                                mParent,
                                item.get("name").toString(),
                                item.get("deck").toString(),
                                item.get("high_url").toString(),
                                item.get("low_url").toString(),
                                item.get("publish_date").toString(),
                                item.get("length_seconds").toString(),
                                ((JSONObject) item.get("image")).get("small_url").toString(),
                                ((JSONObject) item.get("image")).get("super_url").toString()
                        ))
                    } else if (mUrl.startsWith("/video_types")) {
                        categoryItems.put(item)
                        mPuppets.add(new ComicVinePuppet(
                                mParent,
                                mBaseUrl,
                                item.get("name").toString(),
                                null,
                                "/videos/?api_key=c64ac7ddc90513cc15539f1d606e8670b1ef0ae4&format=json&video_type=" + item.get("id").toString(),
                                "https://raw.githubusercontent.com/WhiskeyMedia/xbmc/master/plugin.video.comicvine/icon.png",
                                "http://static.comicvine.com/uploads/original/8/85249/3158213-project",
                                false
                        ))
                    }
                }
            }
            return (currentCategoryIteration + currentSourcesIteration) < mPuppets.size()
        }

        @Override
        Puppet next() {
            if (currentSourcesIteration < numSources) {
                return mPuppets.get(currentSourcesIteration++)
            }
            return mPuppets.get(numSources + currentCategoryIteration++)
        }

        @Override
        void remove() {

        }

        @Override
        void add(Puppet puppet) {

        }
    }

    def static class ComicVineSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mHighQualityVideoUrl
        def String mLowQualityVideoUrl
        def String mPublicationDate
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl

        public ComicVineSourcesPuppet(ParentPuppet parent, String name, String description, String highUrl, String lowUrl, String publishDate, String lengthSeconds, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mName = name
            mDescription = description
            mHighQualityVideoUrl = highUrl
            mLowQualityVideoUrl = lowUrl
            mPublicationDate = publishDate.contains(" ") ? publishDate.split()[0] : publishDate
            mDuration = Long.parseLong(lengthSeconds) * 1000
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
        }

        @Override
        String getPublicationDate() {
            return mPublicationDate
        }

        @Override
        long getDuration() {
            return mDuration
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new ComicVineSourceIterator(mHighQualityVideoUrl, mLowQualityVideoUrl)
        }

        @Override
        boolean isLive() {
            return false
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return null
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
            return mParent
        }

        @Override
        public PuppetIterator getRelated() {
            mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class ComicVineSourceIterator implements SourcesPuppet.SourceIterator {

            def String mHighQualityVideoUrl
            def String mLowQualityVideoUrl
            def List<SourceDescription> mSources
            def int currentIndex = 0

            public ComicVineSourceIterator(String highQualityVideoUrl, lowQualityVideoUrl) {
                mHighQualityVideoUrl = highQualityVideoUrl
                mLowQualityVideoUrl = lowQualityVideoUrl
            }

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()

                    SourceDescription source = new SourceDescription()
                    source.url = mHighQualityVideoUrl
                    source.duration = ComicVineSourcesPuppet.this.getDuration()
                    mSources.add(source)

                    source = new SourceDescription()
                    source.url = mLowQualityVideoUrl
                    source.duration = ComicVineSourcesPuppet.this.getDuration()
                    mSources.add(source)
                }
                return currentIndex < mSources.size()
            }

            @Override
            SourceDescription next() {
                return mSources.get(currentIndex++)
            }

            @Override
            void remove() {

            }
        }
    }
}
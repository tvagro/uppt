package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.puppetmaster.data.i.InstallablePuppet
import tv.puppetmaster.data.i.ParentPuppet
import tv.puppetmaster.data.i.Puppet
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SearchesPuppet
import tv.puppetmaster.data.i.SettingsPuppet
import tv.puppetmaster.data.i.SourcesPuppet
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class DIYNetworkPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mBaseUrl
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    def transient DIYNetworkSearchesPuppet mSearchProvider

    public DIYNetworkPuppet() {
        this(
                null,
                "http://www.diynetwork.com",
                "DIY Network",
                "From remodeling to gardening to crafts DIYNetwork.com provides resources and knowledge through step-by-step photos and videos.",
                "/shows/full-episodes",
                "https://raw.githubusercontent.com/learningit/plugin.video.diy/master/icon.png",
                "http://www.diynetwork.com/content/dam/images/diy/fullset/2015/4/9/0/default-no-image-returned-3.jpg",
                true
        )
    }

    protected DIYNetworkPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
        mParent = parent
        mBaseUrl = baseUrl
        mName = name
        mDescription = description
        mUrl = url != null && url.startsWith("/") ? mBaseUrl + url : url
        mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl
        mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? mBaseUrl + backgroundImageUrl : backgroundImageUrl
        mIsTopLevel = isTopLevel
    }

    @Override
    PuppetIterator getChildren() {
        return new DIYNetworkIterator(this, mBaseUrl, mUrl)
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
        return mParent == null ? "Home" : mParent.getName()
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
        if (mSearchProvider == null) {
            mSearchProvider = new DIYNetworkSearchesPuppet(
                    mParent,
                    mBaseUrl,
                    "Search DIYNetwork.com",
                    "Find videos related to your search term.",
                    "/search/",
                    mImageUrl,
                    mBackgroundImageUrl
            )
        }
        return mSearchProvider
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF741036
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF2D7C9D
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF2D7C9D
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

    def class DIYNetworkIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def ArrayList<Puppet> mPuppets

        def int numSources = 0

        def int currentCategoryIteration = 0
        def int currentSourcesIteration = 0

        transient def Elements categoryItems
        transient def JSONArray sourcesItems

        public DIYNetworkIterator(ParentPuppet parent, String baseUrl, String url) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
        }

        @Override
        boolean hasNext() {
            if (mUrl == null || mUrl.trim() == "") {
                return false
            } else if (mPuppets == null) {
                mPuppets = new ArrayList<>()

                Document document = Jsoup.connect(mUrl.startsWith("/") ? mBaseUrl + mUrl : mUrl).get()

                try {
                    sourcesItems = (JSONArray) ((JSONObject) ((JSONArray) new JSONObject(
                            document.select("div.video-player-container script").html()
                    ).get("channels")).get(0)).get("videos")
                } catch (ignore) {
                    // I guess no videos on this page
                }

                for (int i = 0; sourcesItems != null && i < sourcesItems.length(); i++) {
                    JSONObject item = (JSONObject) sourcesItems.get(i)
                    String url = item.get("releaseUrl").toString()
                    String imageUrl = item.get("thumbnailUrl").toString()
                    mPuppets.add(new DIYNetworkSourcesPuppet(
                            mParent,
                            mBaseUrl,
                            url.startsWith("/") ? mBaseUrl + url : url,
                            item.get("title").toString(),
                            item.get("description").toString(),
                            imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl,
                            Long.parseLong(item.get("length").toString()) * 1000,
                            mUrl
                    ))
                    numSources++
                }

                categoryItems = document.select("div.m-MediaBlock")

                for (Element item in categoryItems) {
                    try {
                        Element a = item.getElementsByTag("a").first()
                        String url = a.absUrl("href")
                        Element img = a.getElementsByTag("img").first()
                        String imageUrl
                        try {
                            imageUrl = img.hasAttr("src") ? img.absUrl("src") : img.absUrl("data-src")
                        } catch (ignored) {
                            // No image I guess
                        }
                        Elements title = item.select(".m-MediaBlock__m-TextWrap a span")
                        String name = title.first().text()
                        String description = title.get(1).text()
                        mPuppets.add(new DIYNetworkPuppet(
                                mParent,
                                mBaseUrl,
                                name,
                                description,
                                url,
                                imageUrl,
                                imageUrl,
                                false
                        ))
                    } catch (ignore) {
                        // TODO: What heppened here?
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

    public static class DIYNetworkSearchesPuppet extends DIYNetworkPuppet implements SearchesPuppet {

        def String mSearchUrl

        public DIYNetworkSearchesPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl) {
            super(parent, baseUrl, name, description, url, imageUrl, backgroundImageUrl, false)
            mSearchUrl = url
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(mSearchUrl + searchQuery.replace("-", "_").replace(" ", "-").trim() + "-")
        }
    }

    def static class DIYNetworkSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def String mName
        def String mDescription
        def String mImageUrl
        def long mDuration
        def String mParseRelatedUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public DIYNetworkSourcesPuppet(ParentPuppet parent, String baseUrl, String url, String name, String description, String imageUrl, long duration, String parseRelatedUrl) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
            mName = name
            mDescription = description
            mImageUrl = imageUrl
            mDuration = duration
            mParseRelatedUrl = parseRelatedUrl
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
            return new DIYNetworkSourceIterator(mUrl)
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
            return mDescription
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
        public PuppetIterator getRelated() {
            return new DIYNetworkPuppet(
                    mParent,
                    mBaseUrl,
                    "Related: " + mName,
                    null,
                    mParseRelatedUrl,
                    null,
                    null,
                    false
            ).getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class DIYNetworkSourceIterator implements SourcesPuppet.SourceIterator {

            def String mUrl
            def List<SourceDescription> mSources
            def int currentIndex = 0

            public DIYNetworkSourceIterator(String url) {
                mUrl = url
            }

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()

                    Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

                    Elements videos = document.select("video")
                    for (Element video in videos) {
                        SourceDescription source = new SourceDescription()
                        source.url = video.attr("src")
                        mSources.add(source)
                    }

                    videos = document.select("ref")
                    for (Element video in videos) {
                        SourceDescription source = new SourceDescription()
                        source.url = video.attr("src")
                        mSources.add(source)
                    }
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
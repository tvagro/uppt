package tv.puppetmaster.featured

import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

class TheCWPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    static final CHANNELS = [
            /*[
                    name:           "Spaceholder",
                    description:    "Coming soon",
                    genres:         "ENTERTAINMENT",
                    urls:           [
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-720.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-576.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-360.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-270.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-180.m3u8",
                    ],
            ],*/
    ]

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl

    TheCWPuppet() {
        this(
                null,
                true,
                "The CW",
                "The CW Television Network",
                null,
                null,
                "http://www.cwtv.com/shows/"
        )
    }

    TheCWPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String url) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
        mUrl = url
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return new TheCWSearchesPuppet(this)
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF191919
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF00AA13
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF267911
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF267911
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        def list = []
        CHANNELS.each { source ->
            list << [
                    name       : source.name,
                    description: source.description,
                    genres     : source.genres,
                    logo       : getImageUrl(),
                    url        : source.urls[0]
            ]
        }
        return list
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new TheCWPuppetIterator()

        def Document document = Jsoup.connect(mUrl)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get()

        if (mParent == null) {
            CHANNELS.each {
                children.add(new TheCWLiveSourcesPuppet(
                        this,
                        it.name,
                        it.description,
                        it.urls as String[]
                ))
            }
        }

        document.select("ul.shows li").each {
            def String title = it.select(".t").first().text()
            def String description = it.select(".ti1").first().text()
            def String publicationDate = it.select(".ti1").first().nextElementSibling().text()
            def Element a = it.select("a").first()
            children.add(new TheCWPuppet(
                    this,
                    false,
                    title,
                    description + " " + publicationDate,
                    it.select("img").first().absUrl("src"),
                    null,
                    a.absUrl("href")
            ))
        }
        document.select(".videowrapped,.playvideo").each {
            def Element a = it.select("a").first()
            def String title = it.select(".videodetails1 p,.titles p").first().text()
            def String description = it.select(".videodetails2,.line2-cw") ? it.select(".videodetails2,line2-cw").first().text() : null
            def String publicationDate = null
            if (it.select(".videodate")) {
                publicationDate = it.select(".videodate").first().text()
            }
            children.add(new TheCWSourcesPuppet(
                    this,
                    title,
                    description,
                    publicationDate,
                    it.select("img").first().absUrl("src"),
                    a.absUrl("href")
            ))
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
        return "Entertainment"
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return mImageUrl != null ? mImageUrl : "https://lh3.googleusercontent.com/-YB5TAzQKpwA/AAAAAAAAAAI/AAAAAAAAPhk/RgQuwSFjnhw/s0-c-k-no-ns/photo.jpg"
    }

    @Override
    String getBackgroundImageUrl() {
        return mBackgroundImageUrl != null ? mBackgroundImageUrl : "http://www.tvgoodness.com/wp-content/uploads/2015/11/TheCWlogo.jpg"
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

    def static class TheCWPuppetIterator extends PuppetIterator {

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

    def static class TheCWSearchesPuppet extends TheCWPuppet implements SearchesPuppet {

        static final String URL_TEMPLATE = "http://www.cwtv.com/search/?q="

        public TheCWSearchesPuppet(ParentPuppet parent) {
            super(parent, false, "Search", "Search The CW", null, null, URL_TEMPLATE)
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mUrl = URL_TEMPLATE + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }

    def static class TheCWSourcesPuppet implements SourcesPuppet {

        def static final String SHOW_URL_TEMPLATE = 'http://metaframe.digitalsmiths.tv/v2/CWtv/assets/%1$s/partner/132?format=json'

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mPublicationDate
        def String mImageUrl
        def String mId

        TheCWSourcesPuppet(ParentPuppet parent, String name, String description, String publicationDate, String imageUrl, String url) {
            mParent = parent
            mName = name
            mDescription = description
            mPublicationDate = publicationDate
            mImageUrl = imageUrl

            mId = url.split("play=")[-1]
        }

        @Override
        String getPublicationDate() {
            return mPublicationDate
        }

        @Override
        long getDuration() {
            -1
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new TheCWSourceIterator()
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
            mName
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
            return mImageUrl != null ? mImageUrl : mParent.getImageUrl()
        }

        @Override
        String getBackgroundImageUrl() {
            return mParent.getBackgroundImageUrl()
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
        String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class TheCWSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int mCurrentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {

                    mSources = new ArrayList<>()

                    def String page = new URL(sprintf(SHOW_URL_TEMPLATE, mId)).getText(requestProperties: [
                            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
                    ])

                    def JSONObject json = new JSONObject(page)

                    def String url = json.getJSONObject("videos").getJSONObject("variantplaylist").getString("uri")
                    def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                    source.url = url
                    mSources.add(source)
                }
                return mCurrentIndex < mSources.size()
            }

            @Override
            SourcesPuppet.SourceDescription next() {
                return mSources.get(mCurrentIndex++)
            }

            @Override
            void remove() {
            }
        }
    }

    def static class TheCWLiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String[] mUrls

        TheCWLiveSourcesPuppet(ParentPuppet parent, String name, String description, String[] urls) {
            mParent = parent
            mName = name
            mDescription = description
            mUrls = urls
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            -1
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new TheCWLiveSourceIterator()
        }

        @Override
        boolean isLive() {
            return true
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return null
        }

        @Override
        String getName() {
            mName
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
            return mParent.getImageUrl()
        }

        @Override
        String getBackgroundImageUrl() {
            return mParent.getBackgroundImageUrl()
        }

        @Override
        boolean isUnavailableIn(String region) {
            false
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
        String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class TheCWLiveSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int mCurrentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {

                    mSources = new ArrayList<>()

                    mUrls.each {
                        SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                        source.url = it
                        mSources.add(source)
                    }
                }
                return mCurrentIndex < mSources.size()
            }

            @Override
            SourcesPuppet.SourceDescription next() {
                return mSources.get(mCurrentIndex++)
            }

            @Override
            void remove() {
            }
        }
    }
}
package tv.puppetmaster.featured

import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

import java.util.regex.Matcher

class RTEPlayerPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    static final CHANNELS = [
            [
                    name:           "RTÉ One",
                    description:    "Home grown entertainment, drama and factual programming including family favourites",
                    genres:         "ENTERTAINMENT",
                    urls:           [
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-720.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-576.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-360.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-270.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte1/rte1-180.m3u8",
                    ],
            ],
            [
                    name:           "RTÉ 2",
                    description:    "Irish TV Television, Programmes, Drama, Factual, Lifestyle, Entertainment, Young People, News, Arts, Religious, Sport",
                    genres:         "ENTERTAINMENT",
                    urls:           [
                            "http://cdn.rasset.ie/hls-live/_definst_/rte2/rte2-720.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte2/rte2-576.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte2/rte2-360.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte2/rte2-270.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/rte2/rte2-180.m3u8",
                    ],
            ],
            [
                    name:           "RTÉ News Now",
                    description:    "The latest Irish and International news from RTÉ. Followed by Weather.",
                    genres:         "NEWS",
                    urls:           [
                            "http://cdn.rasset.ie/hls-live/_definst_/newsnow/newsnow-576.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/newsnow/newsnow-360.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/newsnow/newsnow-270.m3u8",
                            "http://cdn.rasset.ie/hls-live/_definst_/newsnow/newsnow-180.m3u8",
                    ],
            ],
    ]

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl

    RTEPlayerPuppet() {
        this(
                null,
                true,
                "RTÉ Player",
                "Ireland's public service broadcaster Raidió Teilifís Éireann",
                null,
                null,
                "http://www.rte.ie/player/ie/"
        )
    }

    RTEPlayerPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String url) {
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
        return new RTEPlayerSearchesPuppet(this)
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
        return 0xFFFAD207
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF000000
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
        PuppetIterator children = new RTEPlayerPuppetIterator()

        def Document document = Jsoup.connect(mUrl)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get()

        if (mParent == null) {
            CHANNELS.each {
                children.add(new RTEPlayerLiveSourcesPuppet(
                        this,
                        it.name,
                        it.description,
                        it.urls as String[]
                ))
            }
        }

        document.select(".slide-homepage a,sidebar-list-most-popular a,.thumbnail-programme-link,.a-to-z a,.search-result").each {
            String title = it.text()
            if (it.select(".thumbnail-title,.most-popular-title,.search-programme-title")) {
                title = it.select(".thumbnail-title,.most-popular-title,.search-programme-title").first().text()
            }
            String description = null
            if (it.select(".thumbnail-description,.most-popular-description,.search-programme-description")) {
                description = it.select(".thumbnail-description,.most-popular-description,.search-programme-description").first().text()
            }
            String publicationDate = null
            if (it.select(".thumbnail-date,.most-popular-views,.search-programme-episodes")) {
                publicationDate = it.select(".thumbnail-date,.most-popular-views,.search-programme-episodes").first().text()
            }
            Element a = it.hasAttr("href") ? it : it.select("a").first()
            if ((a.attr("href").contains("/show/") && publicationDate == null) || (a.attr("href").contains("/show/") && !publicationDate.contains("episodes available"))) {
                children.add(new RTEPlayerSourcesPuppet(
                        this,
                        title,
                        description,
                        publicationDate,
                        it.select("img") ? it.select("img").first().absUrl("src") : null,
                        a.absUrl("href")
                ))
            } else if (!a.attr("href").contains("/live/")) {
                children.add(new RTEPlayerPuppet(
                        this,
                        false,
                        title,
                        publicationDate,
                        it.select("img") ? it.select("img").first().absUrl("src") : null,
                        null,
                        a.absUrl("href")
                ))
            }
        }

        if (mParent == null) {
            document.select(".dropdown-programmes a").each {
                children.add(new RTEPlayerPuppet(
                        this,
                        !it.attr("href").contains("/live/"),
                        it.text(),
                        null,
                        null,
                        null,
                        it.absUrl("href")
                ))
            }
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
        return "Public Service"
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return mImageUrl != null ? mImageUrl : "https://pbs.twimg.com/profile_images/533371112277557248/iJ7Xwp1i_400x400.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return mBackgroundImageUrl != null ? mBackgroundImageUrl : "https://superrepo.org/static/images/fanart/original/plugin.video.rteplayer.hq.jpg"
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

    def static class RTEPlayerPuppetIterator extends PuppetIterator {

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

    def static class RTEPlayerSearchesPuppet extends RTEPlayerPuppet implements SearchesPuppet {

        static final String URL_TEMPLATE = "http://www.rte.ie/player/ie/search/?q="

        public RTEPlayerSearchesPuppet(ParentPuppet parent) {
            super(parent, false, "Search", "Search RTÉ", null, null, URL_TEMPLATE)
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mUrl = URL_TEMPLATE + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }

    def static class RTEPlayerSourcesPuppet implements SourcesPuppet {

        def static final String SHOW_URL_TEMPLATE = "http://feeds.rasset.ie/rteavgen/player/playlist/?type=iptv&format=json&showId="

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mPublicationDate
        def String mImageUrl
        def String mId
        def boolean mIsUnavailableIn = false

        RTEPlayerSourcesPuppet(ParentPuppet parent, String name, String description, String publicationDate, String imageUrl, String url) {
            mParent = parent
            mName = name
            mDescription = description
            mPublicationDate = publicationDate
            mImageUrl = imageUrl

            if (url.endsWith("/")) {
                url = url[0..-2]
            }

            mId = url.split("/")[-1]
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
            return new RTEPlayerSourceIterator()
        }

        @Override
        boolean isLive() {
            return mName == "Live"
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
            if (region == 'ie') {
                return false
            }

            def String page = new URL(SHOW_URL_TEMPLATE + mId).getText(requestProperties: [
                    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
            ])
            mIsUnavailableIn = new JSONObject(page).getJSONArray("shows").length() == 0
            return mIsUnavailableIn
        }

        @Override
        String getPreferredRegion() {
            return 'ie'
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

        def class RTEPlayerSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int mCurrentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {

                    if (mIsUnavailableIn) {
                        sleep(4000) // Delay post-teleportation allows connection to settle prior to parse
                    }

                    mSources = new ArrayList<>()

                    def String page = new URL(SHOW_URL_TEMPLATE + mId).getText(requestProperties: [
                            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
                    ])

                    def JSONObject json = new JSONObject(page)
                    if (json.getJSONArray("shows").length() > 0) {
                        def JSONObject show = json.getJSONArray("shows").getJSONObject(0).getJSONArray("media:group").getJSONObject(0)
                        def String baseUrl = show.getString("rte:server")
                        def String path = show.getString("url")
                        def String streams = new URL(baseUrl.replaceFirst("/hds", "/hls") + path[0..-5] + ".m3u8").getText(requestProperties: [
                                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
                        ])
                        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"))
                        def Matcher matcher = streams =~ /(?s)BANDWIDTH=(.+?)\n(.+?)\n/
                        while (matcher.find()) {
                            def String s = matcher.group(2)
                            if (s.contains("1024k.mp4")) {
                                def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                                source.url = baseUrl + s.replaceFirst("1024k.mp4", "2048k.mp4")
                                source.bitrate = 2048L
                                mSources.add(source)
                            }
                            def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                            source.url = baseUrl + s
                            def Matcher bMatcher = s =~ /__(\d+)k./
                            if (bMatcher.find()) {
                                source.bitrate = Long.parseLong(bMatcher.group(1))
                            }
                            mSources.add(source)
                        }
                        Collections.sort(mSources, new Comparator<SourcesPuppet.SourceDescription>() {
                            @Override
                            public int compare(SourcesPuppet.SourceDescription lhs, SourcesPuppet.SourceDescription rhs) {
                                // Reverse sort so higher quality is tried first
                                return Long.compare(rhs.bitrate, lhs.bitrate)
                            }
                        })
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

    def static class RTEPlayerLiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String[] mUrls

        RTEPlayerLiveSourcesPuppet(ParentPuppet parent, String name, String description, String[] urls) {
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
            return new RTEPlayerLiveSourceIterator()
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

        def class RTEPlayerLiveSourceIterator implements SourcesPuppet.SourceIterator {

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
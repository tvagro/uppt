package tv.puppetmaster.extra

import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

import java.util.regex.Matcher

class RTEPlayerPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def static final String LIVE_URL_TEMPLATE = "http://feeds.rasset.ie/livelistings/playlist/?channelid="

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
        return null
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new CBCPlusPuppetIterator()

        def Document document = Jsoup.connect(mUrl)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .get()

        /*if (mParent == null) {
            ["7"].each {
                children.add(new RTEPlayerLiveSourcesPuppet(
                        this,
                        "Live " + it,
                        LIVE_URL_TEMPLATE + it
                ))
            }
        }*/

        document.select(".slide-homepage a,sidebar-list-most-popular a,.thumbnail-programme-link,.a-to-z a,.search-result a").each {
            if (it.attr("href").contains("/show/")) {
                children.add(new RTEPlayerSourcesPuppet(
                        this,
                        it.text(),
                        null,
                        it.select("img") ? it.select("img").first().absUrl("src") : null,
                        it.absUrl("href")
                ))
            } else if (!it.attr("href").contains("/live/")) {
                children.add(new RTEPlayerPuppet(
                        this,
                        false,
                        it.text(),
                        null,
                        it.select("img") ? it.select("img").first().absUrl("src") : null,
                        null,
                        it.absUrl("href")
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

    def static class CBCPlusPuppetIterator extends PuppetIterator {

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

        static final String URL_TEMPLATE = "http://www.rte.ie/player/ie/search/&q="

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
        def String mImageUrl
        def String mId
        def boolean mIsUnavailableIn = false

        RTEPlayerSourcesPuppet(ParentPuppet parent, String name, String description, String imageUrl, String url) {
            mParent = parent
            mName = name
            mDescription = description
            mImageUrl = imageUrl

            if (url.endsWith("/")) {
                url = url[0..-2]
            }

            mId = url.split("/")[-1]
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
                        sleep(4000) // Delay post-teleportation allows VPN to settle prior to parse
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
        def String mUrl

        RTEPlayerLiveSourcesPuppet(ParentPuppet parent, String name, String url) {
            mParent = parent
            mName = name
            mUrl = url
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
            return null
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

            def SourcesPuppet.SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {

                    def String page = new URL(mUrl).getText(requestProperties: [
                            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
                    ])
                            .replace("<media:", "<media_")
                            .replace("</media:", "</media_")

                    mSource = new SourcesPuppet.SourceDescription()
                    mSource.url = Jsoup.parse(page, "", Parser.xmlParser())
                            .select("feed").first()
                            .select("entry").first()
                            .select("media_group").first()
                            .select("media_content").first()

                    return true
                }
                return false
            }

            @Override
            SourcesPuppet.SourceDescription next() {
                return mSource
            }

            @Override
            void remove() {
            }
        }
    }
}
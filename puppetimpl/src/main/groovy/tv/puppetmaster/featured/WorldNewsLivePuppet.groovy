package tv.puppetmaster.featured

import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class WorldNewsLivePuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    static final SOURCES = [
            "Featured": [
                    [
                            name       : "ABC News",
                            description: "Breaking national and world news, broadcast video coverage, and exclusive interviews",
                            urls       : [
                                    "http://abclive.abcnews.com/i/abc_live4@136330/master.m3u8"
                            ],
                            image      : "https://yt3.ggpht.com/-vFNoLd1HnDs/AAAAAAAAAAI/AAAAAAAAAAA/Yt468AF7XKE/s900-c-k-no-rj-c0xffffff/photo.jpg",
                            background : "http://www.webhelper.biz/wp-content/uploads/ABC-News-Using-Virtual-Reality-for-Reports-May-Use-for-2016-Prez-Race.jpg"
                    ],
                    [
                            name       : "Arirang TV World (Korea)",
                            description: "Public service agency that spreads the uniqueness of Korea to the world.",
                            urls       : [
                                    "http://amdlive.ctnd.com.edgesuite.net/arirang_1ch/smil:arirang_1ch.smil/playlist.m3u8",
                                    "http://worldlive-ios.arirang.co.kr/arirang/arirangtvworldios.mp4.m3u8"
                            ],
                            image      : "http://www.digitaltveurope.net/wp-content/uploads/2013/01/arirang-app-logo1.png",
                            background : "http://www.mhznetworks.org/sites/default/files/styles/695x350_series/public/arirang_series_1.png"
                    ],
                    [
                            name       : "NHK World Japan Live",
                            description: "International broadcasting service of NHK (Japan Broadcasting Corporation), Japan's public broadcaster",
                            urls       : [
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/domestic/222467/live_tv.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/global/222714/live_tv.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/domestic/222467/live_bg.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/global/222714/live_bg.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/domestic/222467/live_low.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/global/222714/live_low.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/domestic/222467/live_mid.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/global/222714/live_mid.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/domestic/222467/live_high.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/global/222714/live_high.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/dwstv/222468/live.m3u8",
                                    "http://web-cache.stream.ne.jp/www11/nhkworld-tv/stv/225446/live.m3u8",
                            ],
                            image      : "https://raw.githubusercontent.com/learningit/plugin.video.nhklive/master/icon.png",
                            background : "http://www.livenewsbox.com/wp-content/uploads/2015/01/NHK-World.jpg"
                    ],
            ],
            "CCTV 综合": [
                    [
                            name       : "CCTV-NEWS",
                            description: "CNTV 9 China, World, Biz, Video, Live events",
                            urls       : [
                                    "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdcctv9"
                            ],
                            image      : "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                            background : "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
                    ],
                    [
                            name       : "CCTV 4 综合",
                            description: "CNTV China, World, Biz, Video, Live events",
                            urls       : [
                                    "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdcctv4"
                            ],
                            image      : "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                            background : "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
                    ],
            ],
            "Euronews": [
                    [
                            name:           "deutsch",
                            urls:           [
                                    "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/14-live.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsde.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "english",
                            urls:           [
                                    "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/ipad_en.m3u8"
                            ],
                            image:          "http://topsoundfm.com.ve/wp-content/uploads/2013/04/euronews.png",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "español",
                            urls:           [
                                    "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/ipad_es.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewses.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "français",
                            urls:           [
                                    "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_fr.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsfr.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "hungary",
                            urls:           [
                                    "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_hu.m3u8"
                            ],
                            image:          "http://irishsevensummits.com/wp-content/uploads/2015/05/EuronewsHungary.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "italiano",
                            urls:           [
                                    "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_it.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsit.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "português",
                            urls:           [
                                    "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/ipad_pt.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewspt1.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "türkçe",
                            urls:           [
                                    "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_tr.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewstr.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
                    [
                            name:           "العربية",
                            urls:           [
                                    "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_ar.m3u8"
                            ],
                            image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsar.jpg",
                            background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
                    ],
            ]
    ]

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def mSection = []

    WorldNewsLivePuppet() {
        this(null, true, "World News Live", "Arirang, ABC News, CCTV/CNTV, Euronews, NHK Japan", null)
    }

    WorldNewsLivePuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, def section) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mName = name
        mDescription = description
        mSection = section
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new WorldNewsLivePuppetIterator()
        if (!mSection) {
            SOURCES.each { k, section ->
                if (k == "Featured") {
                    section.each { source ->
                        WorldNewsLiveSourcesPuppet sourcesPuppet = new WorldNewsLiveSourcesPuppet(
                                this,
                                source.urls,
                                source.name,
                                source.containsKey("description") ? source.description : null,
                                source.image,
                                source.background
                        )
                        children.add(sourcesPuppet)
                    }
                } else {
                    children.add(new WorldNewsLivePuppet(this, true, k, null, section))
                }
            }
        } else {
            mSection.each { source ->
                WorldNewsLiveSourcesPuppet sourcesPuppet = new WorldNewsLiveSourcesPuppet(
                        this,
                        source.urls as ArrayList<String>,
                        source.name as String,
                        source.containsKey("description") ? source.description as String : null,
                        source.image as String,
                        source.background as String
                )
                children.add(sourcesPuppet)
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
        return "News"
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return "http://static.www.real.com/resources/wp-content/uploads/2012/11/online-news2.jpg"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://livedesignonline.com/site-files/livedesignonline.com/files/archive/blog.livedesignonline.com/briefingroom/wp-content/uploads/2012/01/news-set.jpg"
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
        return 0xFF00317D
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFDCA300
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF00317D
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF00317D
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        def list = []
        SOURCES.each { k, section ->
            section.each { source ->
                if (source.containsKey("description") && source.description.startsWith("CNTV")) {
                    // CCTV sources require a network call to determine actual sources so exclude
                } else {
                    list << [
                            name       : k == "Featured" ? source.name : k + ": " + source.name,
                            description: source.containsKey("description") ? source.description : null,
                            genres     : "NEWS",
                            logo       : source.image,
                            url        : source.urls[0]
                    ]
                }
            }
        }
        return list
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

    def class WorldNewsLivePuppetIterator extends PuppetIterator {

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

    def static class WorldNewsLiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def mUrls = []
        def String mName
        def String mDescription
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        WorldNewsLiveSourcesPuppet(ParentPuppet parent, ArrayList<String> urls, String name, String description, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mUrls = urls
            mName = name
            mDescription = description
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
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
            return new WorldNewsLiveSourceIterator()
        }

        @Override
        boolean isLive() {
            return true
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
        PuppetIterator getRelated() {
            return mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class WorldNewsLiveSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    if (WorldNewsLiveSourcesPuppet.this.mDescription != null && WorldNewsLiveSourcesPuppet.this.mDescription.startsWith("CNTV")) {
                        String content = new URL(WorldNewsLiveSourcesPuppet.this.mUrls[0].toString()).getText()
                        content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1)
                        JSONObject json = new JSONObject(content)
                        ["hds_url", "hls_url", "flv_url"].each{ videoType ->
                            if (json.has(videoType)) {
                                JSONObject video = json.getJSONObject(videoType)
                                for (String key : video.keys()) {
                                    SourceDescription source = new SourceDescription()
                                    source.url = video.getString(key).trim()
                                    if (source.url != "") {
                                        if (videoType == "hds_url") {
                                            source.url += "&hdcore=2.11.3"
                                        }
                                        mSources.add(source)
                                    }
                                }
                            }
                        }
                    } else {
                        for (String url : WorldNewsLiveSourcesPuppet.this.mUrls) {
                            SourceDescription source = new SourceDescription()
                            source.url = url
                            mSources.add(source)
                        }
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
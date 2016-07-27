package tv.puppetmaster.featured

import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class WorldNewsLivePuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    static final SOURCES = [
            [
                    name:           "ABC News",
                    description:    "Breaking national and world news, broadcast video coverage, and exclusive interviews",
                    urls:           [
                            "http://abclive.abcnews.com/i/abc_live4@136330/master.m3u8"
                    ],
                    image:          "https://yt3.ggpht.com/-vFNoLd1HnDs/AAAAAAAAAAI/AAAAAAAAAAA/Yt468AF7XKE/s900-c-k-no-rj-c0xffffff/photo.jpg",
                    background:     "http://www.webhelper.biz/wp-content/uploads/ABC-News-Using-Virtual-Reality-for-Reports-May-Use-for-2016-Prez-Race.jpg"
            ],
            [
                    name:           "Arirang TV World (Korea)",
                    description:    "Public service agency that spreads the uniqueness of Korea to the world.",
                    urls:           [
                            "http://amdlive.ctnd.com.edgesuite.net/arirang_1ch/smil:arirang_1ch.smil/playlist.m3u8",
                            "http://worldlive-ios.arirang.co.kr/arirang/arirangtvworldios.mp4.m3u8"
                    ],
                    image:          "http://www.digitaltveurope.net/wp-content/uploads/2013/01/arirang-app-logo1.png",
                    background:     "http://www.mhznetworks.org/sites/default/files/styles/695x350_series/public/arirang_series_1.png"
            ],
            [
                    name:           "CCTV 综合",
                    description:    "CNTV China, World, Biz, Video, Live events",
                    urls:           [
                            "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdcctv1"
                    ],
                    image:          "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                    background:     "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
            ],
            [
                    name:           "CCTV-NEWS",
                    description:    "CNTV China, World, Biz, Video, Live events",
                    urls:           [
                            "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdcctv9"
                    ],
                    image:          "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                    background:     "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
            ],
            [
                    name:           "NHK World Japan Live",
                    description:    "International broadcasting service of NHK (Japan Broadcasting Corporation), Japan's public broadcaster",
                    urls:           [
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
                    image:          "https://raw.githubusercontent.com/learningit/plugin.video.nhklive/master/icon.png",
                    background:     "http://www.livenewsbox.com/wp-content/uploads/2015/01/NHK-World.jpg"
            ],
            /*[
                    name:           "Antena 3",
                    description:    "Spanish news",
                    urls:           [
                            "http://antena3-aos1-apple-live.adaptive.level3.net/apple/antena3/channel01/antena_3_hd_1548K_1280x720_main.m3u8"
                    ],
                    image:          "https://pbs.twimg.com/profile_images/562240130761515008/0-y3GBYM.jpeg",
                    background:     "https://visualzink.files.wordpress.com/2011/03/02-postpack-naranja-logo-hd-0-00-03-24.jpg"
            ],
            [
                    name:           "Nanjing News 南京新闻",
                    description:    "CNTV China, World, Biz, Video, Live events",
                    urls:           [
                            "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdnanjingnews"
                    ],
                    image:          "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                    background:     "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
            ],
            [
                    name:           "Nantong Xinwen 南通新闻频道",
                    description:    "CNTV China, World, Biz, Video, Live events",
                    urls:           [
                            "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdnantongxinwen"
                    ],
                    image:          "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                    background:     "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
            ],
            [
                    name:           "CCTV-9 纪录",
                    description:    "CNTV China, World, Biz, Video, Live events",
                    urls:           [
                            "http://vdn.live.cntv.cn/api2/liveHtml5.do?channel=pa://cctv_p2p_hdcctvjilu"
                    ],
                    image:          "http://static.ishaohuang.com/2012/09/cctv-logo.jpg",
                    background:     "http://static.frontinc.com/uploads/2014/03/CCTV_02_1200x800.jpg"
            ],
            [
                    name:           "CNBC",
                    description:    "Get latest business news on stock markets, financial & earnings",
                    urls:           [
                            "http://origin2.live.web.tv.streamprovider.net/streams/3bc166ba3776c04e987eb242710e75c0/index.m3u8"
                    ],
                    image:          "https://pbs.twimg.com/profile_images/700355164734156800/1k4mmfUm.png",
                    background:     "http://tve-static-cnbc.nbcuni.com/prod/image/946/935/CNBC_HQ_3280x1560_HR_1280x725_554376771718.jpg"
            ],
            [
                    name:           "i24news English",
                    description:    "International news - 24 hours a day, 7 days a week",
                    urls:           [
                            "http://wpc.c1a9.edgecastcdn.net/hls-live/20C1A9/i24/ls_satlink/b_828.m3u8",
                            "http://bcoveliveios-i.akamaihd.net/hls/live/215102/master_english/398/master.m3u8"
                    ],
                    image:          "http://cdn.marketplaceimages.windowsphone.com/v8/images/d8e18462-25c0-40a0-9331-45caa99fca32",
                    background:     "http://cdn.i24news.tv/upload/default/default-content.jpg"
            ],
            [
                    name:           "i24news français",
                    description:    "International news - 24 hours a day, 7 days a week",
                    urls:           [
                            "http://bcoveliveios-i.akamaihd.net/hls/live/215102/master_french/412/master.m3u8"
                    ],
                    image:          "http://cdn.marketplaceimages.windowsphone.com/v8/images/d8e18462-25c0-40a0-9331-45caa99fca32",
                    background:     "http://cdn.i24news.tv/upload/default/default-content.jpg"
            ],
            [
                    name:           "i24news العربية",
                    description:    "International news - 24 hours a day, 7 days a week",
                    urls:           [
                            "http://bcoveliveios-i.akamaihd.net/hls/live/215102/master_arabic/391/master.m3u8"
                    ],
                    image:          "http://cdn.marketplaceimages.windowsphone.com/v8/images/d8e18462-25c0-40a0-9331-45caa99fca32",
                    background:     "http://cdn.i24news.tv/upload/default/default-content.jpg"
            ],*/
    ]

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new WorldNewsLivePuppetIterator(this)
        SOURCES.each { source ->
            WorldNewsLiveSourcesPuppet sourcesPuppet = new WorldNewsLiveSourcesPuppet()
            sourcesPuppet.setParent(this)
            sourcesPuppet.setName(source.name)
            sourcesPuppet.setShortDescription(source.description)
            sourcesPuppet.setUrls(source.urls)
            sourcesPuppet.setImageUrl(source.image)
            sourcesPuppet.setBackgroundImageUrl(source.background)
            children.add(sourcesPuppet)
        }

        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return "World News Live"
    }

    @Override
    String getCategory() {
        return "News"
    }

    @Override
    String getShortDescription() {
        return "Arirang, ABC News, CCTV/CNTV, NHK Japan"
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
    boolean isAvailable(String region) {
        return true
    }

    @Override
    String[] preferredRegions() {
        return null
    }

    @Override
    int immigrationStricture() {
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
        SOURCES.each { source ->
            // CCTV sources require a network call to determine actual sources so exclude
            if (!source.description.startsWith("CNTV")) {
                list << [
                        name       : source.name,
                        description: source.description,
                        genres     : "NEWS",
                        logo       : source.image,
                        url        : source.urls[0]
                ]
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
        return getName()
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    def class WorldNewsLivePuppetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        public WorldNewsLivePuppetIterator(ParentPuppet parent) {
            mParent = parent
        }

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
        def String mName
        def String mShortDescription
        def mUrls = []
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return -1
        }

        void setUrls(def urls) {
            mUrls = urls
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

        void setName(String name) {
            mName = name
        }

        @Override
        String getName() {
            return mName
        }

        @Override
        String getCategory() {
            return null
        }

        void setShortDescription(String shortDescription) {
            mShortDescription = shortDescription
        }

        @Override
        String getShortDescription() {
            return mShortDescription
        }

        void setImageUrl(String imageUrl) {
            mImageUrl = imageUrl
        }

        @Override
        String getImageUrl() {
            return mImageUrl
        }

        void setBackgroundImageUrl(String backgroundImageUrl) {
            mBackgroundImageUrl = backgroundImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            return mBackgroundImageUrl
        }

        @Override
        boolean isAvailable(String region) {
            return true
        }

        @Override
        String[] preferredRegions() {
            return null
        }

        @Override
        int immigrationStricture() {
            return 0
        }

        void setParent(ParentPuppet parent) {
            mParent = parent
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
            return getName()
        }

        def class WorldNewsLiveSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    if (WorldNewsLiveSourcesPuppet.this.mShortDescription.startsWith("CNTV")) {
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
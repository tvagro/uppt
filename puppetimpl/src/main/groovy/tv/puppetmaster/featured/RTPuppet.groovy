package tv.puppetmaster.featured

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import tv.puppetmaster.data.i.InstallablePuppet
import tv.puppetmaster.data.i.ParentPuppet
import tv.puppetmaster.data.i.Puppet
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SearchesPuppet
import tv.puppetmaster.data.i.SourcesPuppet
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class RTPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final LIVE_SOURCES = [
            [
                    name:                   "Russia Today News",
                    description:            "A 24/7 English-language news channel that is set to show you how any story can be another story altogether.",
                    urls:                   "http://rt-eng-live.hls.adaptive.level3.net/rt/eng/index2500.m3u8|http://rt-eng-live.hls.adaptive.level3.net/rt/eng/index1600.m3u8|http://rt-eng-live.hls.adaptive.level3.net/rt/eng/index800.m3u8|http://rt-eng-live.hls.adaptive.level3.net/rt/eng/index400.m3u8|http://rt-eng-live.hls.adaptive.level3.net/rt/eng/indexaudio.m3u8",
            ],
            [
                    name:                   "RT America",
                    description:            "RT America broadcasts from its studios in Washington, DC. Watch news reports, features and talk shows with a totally different perspective from the mainstream American television.",
                    urls:                   "http://rt-usa-live.hls.adaptive.level3.net/rt/usa/index2500.m3u8|http://rt-usa-live.hls.adaptive.level3.net/rt/usa/index1600.m3u8|http://rt-usa-live.hls.adaptive.level3.net/rt/usa/index800.m3u8|http://rt-usa-live.hls.adaptive.level3.net/rt/usa/index400.m3u8|http://rt-usa-live.hls.adaptive.level3.net/rt/usa/indexaudio.m3u8",
                    image:                  "https://www.lp.org/files/imagepicker/45/rt%20america.jpg",
                    background:             "https://img.rt.com/files/news/3e/e4/d0/00/rt-america-new.jpg",
            ],
            [
                    name:                   "RT UK",
                    description:            "RT UK broadcasts from its London Studio, focusing on the issues that matter most to Britons.",
                    urls:                   "http://rt-uk-live.hls.adaptive.level3.net/rt/uk/index2500.m3u8|http://rt-uk-live.hls.adaptive.level3.net/rt/uk/index1600.m3u8|http://rt-uk-live.hls.adaptive.level3.net/rt/uk/index800.m3u8|http://rt-uk-live.hls.adaptive.level3.net/rt/uk/index400.m3u8|http://rt-uk-live.hls.adaptive.level3.net/rt/uk/indexaudio.m3u8",
                    image:                  "https://d24j9r7lck9cin.cloudfront.net/l/o/1/1466.1414681039.png",
                    background:             "http://www.livenewsbox.com/wp-content/uploads/2015/01/RT.jpg",
            ],
            [
                    name:                   "RT Documentaries",
                    description:            "RT is the first Russian 24/7 English-language news channel which brings the Russian view on global news.",
                    urls:                   "http://rt-doc-live.hls.adaptive.level3.net/rt/doc/index2500.m3u8|http://rt-doc-live.hls.adaptive.level3.net/rt/doc/index1600.m3u8|http://rt-doc-live.hls.adaptive.level3.net/rt/doc/index800.m3u8|http://rt-doc-live.hls.adaptive.level3.net/rt/doc/index400.m3u8|http://rt-doc-live.hls.adaptive.level3.net/rt/doc/indexaudio.m3u8",
                    background:             "http://www.watchallchannels.com/wp-content/uploads/2015/02/RT-Documentary.jpg"
            ],
            [
                    name:                   "روسيا اليوم",
                    description:            "البث المباشر",
                    urls:                   "http://rt-ara-live.hls.adaptive.level3.net/rt/ara/index2500.m3u8|http://rt-ara-live.hls.adaptive.level3.net/rt/ara/index1600.m3u8|http://rt-ara-live.hls.adaptive.level3.net/rt/ara/index800.m3u8|http://rt-ara-live.hls.adaptive.level3.net/rt/ara/index400.m3u8|http://rt-ara-live.hls.adaptive.level3.net/rt/ara/indexaudio.m3u8",
                    image:                  "https://s-media-cache-ak0.pinimg.com/236x/75/b7/24/75b724d33aee32142c9da4444232ede7.jpg",
                    background:             "http://arabitec.com/wp-content/uploads/2016/01/54e608de611e9bf3308b45a4.JPG",
            ],
    ]

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new RTPuppetIterator(this, "https://www.rt.com",  null)

        LIVE_SOURCES.each { source ->
            RTLiveSourcesPuppet sourcesPuppet = new RTLiveSourcesPuppet()
            sourcesPuppet.setParent(this)
            sourcesPuppet.setName(source.name)
            sourcesPuppet.setShortDescription(source.description)
            sourcesPuppet.setUrl(source.urls)
            sourcesPuppet.setImageUrl(source.containsKey('image') ? source.image : getImageUrl())
            sourcesPuppet.setBackgroundImageUrl(source.containsKey('background') ? source.background : getBackgroundImageUrl())
            children.add(sourcesPuppet)
        }

        children.add(new RTShowsPuppet(
                this,
                "https://www.rt.com",
                "Shows",
                "RT is the first Russian 24/7 English-language news channel which brings the Russian view on global news.",
                "/shows/",
                getImageUrl(),
                getBackgroundImageUrl()
        ))
        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return "Russia Today"
    }

    @Override
    String getCategory() {
        return "News"
    }

    @Override
    String getShortDescription() {
        return "News from Russia Today"
    }

    @Override
    String getImageUrl() {
        return "http://www.marketoracle.co.uk/images/2013/Aug/Russia_Today.jpg"
    }

    @Override
    String getBackgroundImageUrl() {
        return "https://superrepo.org/static/images/fanart/original/plugin.video.rt.jpg"
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
        return 0xFF65BF2C
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF65BF2C
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF76BD1D
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        def list = []
        LIVE_SOURCES.each { source ->
            list << [
                    name       : source.name,
                    description: source.description,
                    genres     : "NEWS",
                    logo       : source.containsKey('image') ? source.image : getImageUrl(),
                    url        : source.urls.split("\\|")[0]
            ]
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

    def class RTShowsPuppet implements ParentPuppet {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mName
        def String mDescription
        def String mUrl
        def String mImageUrl
        def String mBackgroundImageUrl

        public RTShowsPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mBaseUrl = baseUrl
            mName = name
            mDescription = description
            mUrl = url != null && url.startsWith("/") ? mBaseUrl + url : url
            mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl
            mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? mBaseUrl + backgroundImageUrl : backgroundImageUrl
        }

        @Override
        String getName() {
            return mName
        }

        @Override
        String getCategory() {
            return mParent.getName()
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
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        @Override
        PuppetIterator getChildren() {
            return new RTPuppetIterator(this, mBaseUrl, mUrl)
        }

        @Override
        boolean isTopLevel() {
            return mParent == null || mParent.getParent() == null
        }
    }

    def class RTPuppetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mScrapeUrl
        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0
        def Document mDocument

        public RTPuppetIterator(ParentPuppet parent, String baseUrl, String scrapeUrl) {
            mParent = parent
            mBaseUrl = baseUrl
            mScrapeUrl = scrapeUrl
        }


        @Override
        boolean hasNext() {
            if (mPuppets != null && currentIndex < mPuppets.size()) {
                return true
            } else if (mScrapeUrl == null || mScrapeUrl.trim() == "") {
                return false
            }

            if (mDocument == null) {
                mDocument = Jsoup.connect(mScrapeUrl).get()
                if (mParent.getParent().getParent() == null) {
                    def items = mDocument.select("li.card-rows__item")
                    for (Element item in items) {
                        String imageUrl = item.select("img.media__item").first().absUrl("src")
                        mPuppets.add(new RTShowsPuppet(
                                mParent,
                                mBaseUrl,
                                item.select("a.link.link_hover").text().trim(),
                                item.select("a.link.link_disabled").text().trim(),
                                item.select("a.link.link_disabled").first().absUrl("href"),
                                imageUrl,
                                imageUrl
                        ))
                    }
                } else if (mDocument.select(".static-three_med-one") != null) {
                    for (Element item in mDocument.select(".static-three_med-one")) {
                        String imageUrl = item.select("img.media__item").first().absUrl("src")
                        String url = item.select("a.link.link_disabled").first().absUrl("href")
                        RTSourcesPuppet sourcesPuppet = new RTSourcesPuppet()
                        sourcesPuppet.setParent(mParent)
                        sourcesPuppet.setName(item.select("a.link.link_hover").text().trim())
                        sourcesPuppet.setUrl(url)
                        sourcesPuppet.setShortDescription(item.select(".card__summary").text())
                        sourcesPuppet.setImageUrl(imageUrl)
                        sourcesPuppet.setBackgroundImageUrl(imageUrl)
                        sourcesPuppet.setPublicationDate(item.select("time.date").text().trim())
                        mPuppets.add(sourcesPuppet)
                    }
                } else if (mDocument.select(".trc_spotlight_item") != null) {
                    for (Element item : mDocument.select(".trc_spotlight_item")) {
                        String relatedImageUrl = item.absUrl("data-item-thumb")
                        String relatedUrl = item.select("a.item-thumbnail-href").first().absUrl("href")
                        RTSourcesPuppet sourcesPuppet = new RTSourcesPuppet()
                        sourcesPuppet.setParent(mParent)
                        sourcesPuppet.setName(item.attr("data-item-title").trim())
                        sourcesPuppet.setUrl(relatedUrl)
                        sourcesPuppet.setImageUrl(relatedImageUrl)
                        sourcesPuppet.setBackgroundImageUrl(relatedImageUrl)
                        mPuppets.add(sourcesPuppet)
                    }
                }
            }
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

    def static class RTSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mUrl
        def String mShortDescription
        def String mImageUrl
        def String mBackgroundImageUrl
        def String mPublicationDate

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        void setPublicationDate(String publicationDate) {
            mPublicationDate = publicationDate
        }

        @Override
        String getPublicationDate() {
            return mPublicationDate
        }

        @Override
        long getDuration() {
            return -1
        }

        void setUrl(String url) {
            mUrl = url
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new RTSourceIterator(mUrl)
        }

        @Override
        boolean isLive() {
            return false
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

        void setParent(ParentPuppet parent) {
            mParent = parent
        }

        @Override
        ParentPuppet getParent() {
            return mParent
        }

        @Override
        public PuppetIterator getRelated() {
            return mParent != null ? mParent.getChildren() : null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class RTSourceIterator implements SourcesPuppet.SourceIterator {

            def String mUrl
            def List<SourceDescription> mSources
            def int currentIndex = 0

            public RTSourceIterator(String url) {
                mUrl = url
            }

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()

                    def String page = new URL(mUrl).getText().replaceAll("\n", "")
                    def String url

                    def matcher = page =~ /file:.+?"(.+?)"/
                    try {
                        url = matcher.findAll().get(0)[1]
                    } catch (Exception e) {
                        matcher = page =~ /http:\/\/feeds.soundcloud.com\/playlists\/soundcloud:playlists:(.+?)\/sounds.rss/
                        try {
                            url = matcher.findAll().get(0)[0]
                            page = new URL(url).getText().replaceAll("\n", "")
                            matcher = page =~ /http:\/\/feeds.soundcloud.com\/stream\/(.+?).mp3/
                            url = matcher.findAll().get(0)[0]
                        } catch (Exception ex) {
                            matcher = page =~ /<div class="rtcode">.+?src="(.+?)"/
                            try {
                                url = matcher.findAll().get(0)[1]
                            } catch (Exception exx) {
                                return false
                            }
                            if (!url.startsWith("http")) {
                                url = "http:" + url
                            }
                            page = new URL(url).getText().replaceAll("\n", "")
                            matcher = page =~ /"hls_stream":"(.+?)"/
                            url = matcher.findAll().get(0)[1]
                        }
                    }

                    SourceDescription sourceDescription = new SourceDescription()
                    sourceDescription.url = url
                    sourceDescription.isAudioOnly = url.endsWith(".mp3")
                    mSources.add(sourceDescription)
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

    def static class RTLiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String[] mUrls
        def String mShortDescription
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

        void setUrl(String url) {
            mUrls = url.split("\\|")
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new RTLiveSourceIterator(mUrls)
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
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class RTLiveSourceIterator implements SourcesPuppet.SourceIterator {

            def String[] mSourceUrls
            def int currentIndex

            public RTLiveSourceIterator(String[] sourceUrls) {
                mSourceUrls = sourceUrls
            }

            @Override
            boolean hasNext() {
                return mSourceUrls != null && currentIndex < mSourceUrls.length
            }

            @Override
            SourceDescription next() {
                SourceDescription sourceDescription = new SourceDescription()
                sourceDescription.url = mSourceUrls[currentIndex++]
                return sourceDescription
            }

            @Override
            void remove() {

            }
        }
    }
}
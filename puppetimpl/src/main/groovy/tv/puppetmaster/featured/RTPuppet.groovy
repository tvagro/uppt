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

import java.util.regex.Matcher

public class RTPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new RTPuppetIterator(this, "https://www.rt.com",  null)

        RTLiveSourcesPuppet sourcesPuppet = new RTLiveSourcesPuppet()
        sourcesPuppet.setParent(this)
        sourcesPuppet.setName("Russia Today Live")
        sourcesPuppet.setShortDescription("RT is the first Russian 24/7 English-language news channel which brings the Russian view on global news.")
        sourcesPuppet.setUrl("http://rt-a.akamaihd.net/ch_01@325605/master.m3u8|http://rt-a.akamaihd.net/ch_04@325608/720p.m3u8|http://rt-a.akamaihd.net/ch_05@325609/480p.m3u8|http://rt-a.akamaihd.net/ch_06@325610/320p.m3u8|http://rt-a.akamaihd.net/ch_02@325606/240p.m3u8|http://rt-a.akamaihd.net/ch_03@325607/320p.m3u8")
        sourcesPuppet.setImageUrl("http://www.marketoracle.co.uk/images/2013/Aug/Russia_Today.jpg")
        sourcesPuppet.setBackgroundImageUrl("https://superrepo.org/static/images/fanart/original/plugin.video.rt.jpg")
        children.add(sourcesPuppet)

        children.add(new RTShowsPuppet(
                this,
                "https://www.rt.com",
                "Shows",
                "RT is the first Russian 24/7 English-language news channel which brings the Russian view on global news.",
                "/shows/",
                "http://www.marketoracle.co.uk/images/2013/Aug/Russia_Today.jpg",
                "https://superrepo.org/static/images/fanart/original/plugin.video.rt.jpg"
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
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF76BD1D
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF76BD1D
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF76BD1D
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return [[
                        name:           "Russia Today",
                        description:    "Live stream from RT, a 24/7 English-language news channel",
                        genres:         "NEWS",
                        logo:           getImageUrl(),
                        url:            "http://rt-a.akamaihd.net/ch_01@325605/master.m3u8"
                ]]
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
            return mParent
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
        public PuppetIterator getRelated() {
            return mParent != null ? mParent.getChildren() : null
        }

        @Override
        public String toString() {
            return getName()
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
            return null
        }

        @Override
        public String toString() {
            return getName()
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
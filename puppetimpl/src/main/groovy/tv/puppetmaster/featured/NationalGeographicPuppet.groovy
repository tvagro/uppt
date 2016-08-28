package tv.puppetmaster.featured

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
import org.jsoup.parser.Parser
import tv.puppetmaster.data.i.*

class NationalGeographicPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final String BASE_URL = "http://video.nationalgeographic.com"

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl
    def int mStartIndex
    def int mOffset
    def String mExtraParams

    NationalGeographicPuppet() {
        this(
                null,
                true,
                "National Geographic",
                "A world leader in geography, cartography and exploration.",
                "https://i.vimeocdn.com/portrait/3197326_300x300",
                "http://www.nationalgeographic.com/content/dam/magazine/Logos/national-geographic.jpg",
                "http://feed.theplatform.com/f/ngs/dCCn2isYZ9N9",
                0,
                10,
                null,
        )
    }

    NationalGeographicPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String url, int startIndex, int offset, String extraParams) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
        mUrl = url
        mStartIndex = startIndex
        mOffset = offset
        mExtraParams = extraParams
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return new NationalGeographicSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFFFCC00
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF091B2B
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF091B2B
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    Puppet.PuppetIterator getChildren() {
        def Puppet.PuppetIterator children = new NationalGeographicPuppetIterator()

        def String feedUrl = mUrl + sprintf("?startIndex=%d&endIndex=%d", mStartIndex, mStartIndex + mOffset)
        if (mExtraParams) {
            feedUrl += "&" + mExtraParams
        }

        def feed = Jsoup.parse(new URL(feedUrl).getText().replace("<media:", "<media_"), "", Parser.htmlParser())
        feed.outputSettings().escapeMode(Entities.EscapeMode.base);
        feed.outputSettings().charset("UTF-8");

        def int numItems = 0

        feed.select("item").each {
            def String name = it.select("title").first().text()
            def String description = it.select("description").first().text()
            def String publicationDate = it.select("pubDate").first().text()
            publicationDate = publicationDate.substring(0, publicationDate.lastIndexOf(" "))
            publicationDate = publicationDate.substring(0, publicationDate.lastIndexOf(" "))
            def double duration = Double.parseDouble(it.select("media_content").first().attr("duration"))
            def imageUrls = []
            it.select("media_thumbnail").each { image ->
                def url = [
                        width:      Integer.parseInt(image.attr("width")),
                        url:        image.absUrl("url"),
                ]
                imageUrls << url
            }
            imageUrls.sort{ a, b -> a["width"] <=> b["width"] }
            def urls = []
            it.select("media_content").each { video ->
                def url = [
                        width:      Integer.parseInt(video.attr("width")),
                        height:     Integer.parseInt(video.attr("height")),
                        bitrate:    Double.parseDouble(video.attr("bitrate")),
                        url:        video.absUrl("url"),
                ]
                urls << url
            }
            urls.sort{ a, b -> a["width"] <=> b["width"] }.reverse(true)
            children.add(new NationalGeographicSourcesPuppet(this, name, description, publicationDate, duration, imageUrls, urls))
            numItems++
        }

        if (mParent == null) {
            children.add(new NationalGeographicPuppet(
                    this,
                    false,
                    "More",
                    null,
                    mImageUrl,
                    mBackgroundImageUrl,
                    mUrl,
                    mStartIndex + mOffset + 1,
                    38,
                    mExtraParams,
            ))

            def Document document = Jsoup.connect(BASE_URL).get()
            document.select(".dropdown-container.dropdown").first().select("a").each {
                children.add(new NationalGeographicPuppet(
                        this,
                        true,
                        it.text(),
                        null,
                        mImageUrl,
                        mBackgroundImageUrl,
                        mUrl,
                        mStartIndex,
                        7,
                        "&sort=pubDate%7Cdesc&q=ngsTax:NG_NavigationalCategory:" + URLEncoder.encode(it.text(), "UTF-8"),
                ))
            }
        } else if (numItems > 0) {
            children.add(new NationalGeographicPuppet(
                    this,
                    false,
                    "More",
                    null,
                    mImageUrl,
                    mBackgroundImageUrl,
                    mUrl,
                    mStartIndex + mOffset + 1,
                    38,
                    mExtraParams,
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
        return "Education"
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
    Puppet.PuppetIterator getRelated() {
        return null
    }

    @Override
    public String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    def static class NationalGeographicSearchesPuppet extends NationalGeographicPuppet implements SearchesPuppet {

        public NationalGeographicSearchesPuppet(ParentPuppet parent) {
            super(
                    parent,
                    true,
                    "Search",
                    "Search National Geographic",
                    "https://i.vimeocdn.com/portrait/3197326_300x300",
                    "http://www.nationalgeographic.com/content/dam/magazine/Logos/national-geographic.jpg",
                    "http://feed.theplatform.com/f/ngs/dCCn2isYZ9N9",
                    0,
                    10,
                    null
            )
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mExtraParams = "q=" + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }

    static class NationalGeographicPuppetIterator extends Puppet.PuppetIterator {

        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        @Override
        boolean hasNext() {
            if (currentIndex < mPuppets.size()) {
                return true
            } else {
                currentIndex = 0 // Reset for reuse
            }
            return false
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

    class NationalGeographicSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mPublicationDate
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl
        def mUrls = []

        NationalGeographicSourcesPuppet(ParentPuppet parent, String name, String description, String publicationDate, double duration, imageUrls, urls) {
            mParent = parent
            mName = name
            mDescription = description
            mPublicationDate = publicationDate
            mDuration = duration * 1000l
            mImageUrl = imageUrls.first()["url"]
            mBackgroundImageUrl = imageUrls.last()["url"]
            imageUrls.each {
                if (it["width"] < 400) {
                    mImageUrl = it["url"]
                }
            }
            mUrls = urls
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
            return new NationalGeographicSourceIterator()
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
        Puppet.PuppetIterator getRelated() {
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        class NationalGeographicSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourcesPuppet.SourceDescription>()

                    mUrls.each {
                        SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                        Jsoup.connect(it["url"] as String).ignoreContentType(true).get().select("video").each { video ->
                            source.url = video.absUrl("src").replace("/z/", "/i/") + "/master.m3u8"
                            source.bitrate = it["bitrate"] as long
                            source.width = it["width"]
                            source.height = it["height"]
                            mSources.add(source)
                        }
                    }
                }
                return currentIndex < mSources.size()
            }

            @Override
            SourcesPuppet.SourceDescription next() {
                return mSources.get(currentIndex++)
            }

            @Override
            void remove() {
            }
        }
    }
}
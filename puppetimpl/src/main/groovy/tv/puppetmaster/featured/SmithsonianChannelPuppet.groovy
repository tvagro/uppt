package tv.puppetmaster.featured

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.concurrent.TimeUnit
import java.util.regex.Matcher

public class SmithsonianChannelPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def boolean mIsTopLevel
    def String mImageUrl

    public SmithsonianChannelPuppet() {
        this(
                null,
                "http://www.smithsonianchannel.com/full-episodes",
                "Smithsonian Channel",
                "Brings original programs exploring science, nature, and pop culture to your TV.",
                true,
                "http://channels.roku.com/images/163b448114dc44a0a181d4f8752b1461-hd.jpg"
        )
    }

    public SmithsonianChannelPuppet(ParentPuppet parent, String url, String name, String description, boolean isTopLevel, String imageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mIsTopLevel = isTopLevel
        mImageUrl = imageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new SmithsonianChannelPuppetIterator()

        Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

        document.select(".mix.free,#search-results-shorts li").each { node ->
            String url = node.select("a").first().absUrl("href")
            String name = node.select("h2").first().text()
            String description = node.select(".promo-video-type,.promo-type").first().text()
            long duration = -1
            try {
                duration = convertDuration(node.select(".timecode").first().text().replace("|", "").trim())
            } catch (ignore) {

            }

            String imageUrl = node.select("img").first().absUrl("srcset")
            String backgroundImageUrl = "https://newsi.creativecow.com/i/877755/3.jpeg"

            if (url.contains("/tv-shows/")) {
                children.add(new SmithsonianChannelPuppet(this, url, name, description, false, imageUrl))
            } else {
                children.add(new SmithsonianChannelSourcesPuppet(this, url, name, description, duration, imageUrl, backgroundImageUrl))
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
        return mParent == null ? "Education" : mParent.getName()
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
        return "https://newsi.creativecow.com/i/877755/3.jpeg"
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
        return new SmithsonianChannelSearchesPuppet(this)
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFFCE1A06
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFFF0000
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFFF0000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFFF0000
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    PuppetIterator getRelated() {
        return null
    }

    void setUrl(String url) {
        mUrl = url
    }

    @Override
    public String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    private static long convertDuration(String str) {  // HH:MM[:SS] to milliseconds
        if (str.equals("")) {
            return -1
        }

        String[] data = str.split(":")

        int time
        if (data.length > 2) {
            int hours  = Integer.parseInt(data[0])
            int minutes = Integer.parseInt(data[1])
            int seconds = Integer.parseInt(data[2])
            time = seconds + 60 * minutes + 3600 * hours
        } else if (data.length > 1) {
            int minutes = Integer.parseInt(data[0])
            int seconds = Integer.parseInt(data[1])
            time = seconds + 60 * minutes
        } else {
            int seconds = Integer.parseInt(data[0])
            time = seconds
        }

        return TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS)
    }

    def static class SmithsonianChannelSearchesPuppet extends SmithsonianChannelPuppet implements SearchesPuppet {

        def static final String SEARCH_URL = "http://www.smithsonianchannel.com/search?q="

        public SmithsonianChannelSearchesPuppet(ParentPuppet parent) {
            super(
                    parent,
                    SEARCH_URL,
                    "Search",
                    "Search Smithsonian Channel",
                    false,
                    "http://channels.roku.com/images/163b448114dc44a0a181d4f8752b1461-hd.jpg"
            )
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(SEARCH_URL + searchQuery.replace(" ", "%20"))
        }
    }

    def class SmithsonianChannelPuppetIterator extends PuppetIterator {

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

    def static class SmithsonianChannelSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public SmithsonianChannelSourcesPuppet(parent, url, name, shortDescription, duration, imageUrl, backgroundImageUrl) {
            mParent = parent
            mUrl = url
            mName = name
            mShortDescription = shortDescription
            mDuration = duration
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
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
            return new SmithsonianChannelSourceIterator()
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
            return mShortDescription
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

        def class SmithsonianChannelSourceIterator implements SourcesPuppet.SourceIterator {

            def SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {
                    mSource = new SourceDescription()

                    String html = new URL(SmithsonianChannelSourcesPuppet.this.mUrl).getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11']).replaceAll("\n", "")
                    Matcher matcher = html =~ /data-vttfile="(.+?)".+?data-bcid="(.+?)"/
                    if (matcher.find()) {
                        String subUrl = matcher.group(1)
                        if (subUrl != "") {
                            SourcesPuppet.SubtitleDescription subtitle = new SourcesPuppet.SubtitleDescription()
                            subtitle.url = subUrl
                            SmithsonianChannelSourcesPuppet.this.mSubtitles.add(subtitle)
                        }
                        mSource.url = "http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?pubId=1466806621001&videoId=" + matcher.group(2)
                        return true
                    } else {
                        matcher = html =~ /data-bcid="(.+?)"/
                        if (matcher.find()) {
                            mSource.url = "http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?pubId=1466806621001&videoId=" + matcher.group(1)
                            return true
                        }
                    }
                }
                return false
            }

            @Override
            SourceDescription next() {
                return mSource
            }

            @Override
            void remove() {

            }
        }
    }
}
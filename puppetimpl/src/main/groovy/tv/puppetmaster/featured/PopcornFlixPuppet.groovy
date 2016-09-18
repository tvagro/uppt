package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.concurrent.TimeUnit

public class PopcornFlixPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def boolean mIsTopLevel
    def String mImageUrl

    public PopcornFlixPuppet() {
        this(
                null,
                "http://www.popcornflix.com",
                "Popcorn Flix",
                "Offers a broad collection of great movies you can watch right now.",
                true,
                "http://compass.xboxlive.com/assets/88/f9/88f9f3a7-51bf-4ad1-84aa-a4bf213abb45.jpg"
        )
    }

    public PopcornFlixPuppet(ParentPuppet parent, String url, String name, String description, boolean isTopLevel, String imageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mIsTopLevel = isTopLevel
        mImageUrl = imageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new PopcornFlixPuppetIterator()

        Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

        document.select(".item-block ul li").each { node ->
            String url = node.select("figure a").first().absUrl("href")
            String name = node.select("figure figcaption").first().text()
            String description = node.select(".film-data .film-data-desc").first().text()
            long duration = -1
            try {
                duration = convertDuration(node.select(".film-data .duration").first().text() + ":00")
            } catch (ignore) {

            }

            String imageUrl = node.select("figure a img").first().absUrl("src")
            String backgroundImageUrl = "http://icdn2.digitaltrends.com/image/popcornflix-screen-1200x630-c.jpg"

            if (url.contains("/tv-shows/")) {
                children.add(new PopcornFlixPuppet(this, url, name, description, false, imageUrl))
            } else {
                String id = url.substring(url.lastIndexOf('/') + 1, url.length())
                children.add(new PopcornFlixSourcesPuppet(this, id, name, description, duration, imageUrl, backgroundImageUrl))
            }
        }

        if (mParent == null) {
            document.select(".item-block h2:first-child a").each { node ->
                String url = node.absUrl("href")
                String name = node.text().trim()

                if (url.endsWith("/Pop%20Picks-movies")) {
                    url = url.replace("/Pop%20Picks-movies", "/most-popular-movies")
                }

                boolean isBrokenLink = name in ["Rock Stars", "Comedy", "Horror", "Urban", "Film School Originals"]

                if (!isBrokenLink) {
                    children.add(new PopcornFlixPuppet(this, url, name, null, true, imageUrl))
                }
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
        return "Entertainment"
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
        return "http://icdn2.digitaltrends.com/image/popcornflix-screen-1200x630-c.jpg"
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
        return new PopcornFlixSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFFFE3200
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF00B4F9
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFFE3200
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFFE3200
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

    def static class PopcornFlixSearchesPuppet extends PopcornFlixPuppet implements SearchesPuppet {

        def static final String SEARCH_URL = "http://www.popcornflix.com/search?query="

        public PopcornFlixSearchesPuppet(ParentPuppet parent) {
            super(
                    parent,
                    SEARCH_URL,
                    "Search",
                    "Search Popcorn Flix",
                    false,
                    "http://compass.xboxlive.com/assets/88/f9/88f9f3a7-51bf-4ad1-84aa-a4bf213abb45.jpg"
            )
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(SEARCH_URL + searchQuery.replace(" ", "+"))
        }
    }

    def class PopcornFlixPuppetIterator extends PuppetIterator {

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

    def static class PopcornFlixSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mId
        def String mName
        def String mShortDescription
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl

        public PopcornFlixSourcesPuppet(parent, id, name, shortDescription, duration, imageUrl, backgroundImageUrl) {
            mParent = parent
            mId = id
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
            return new PopcornFlixSourceIterator()
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

        def class PopcornFlixSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    String json = new URL("http://popcornflixv2.device.screenmedia.net/api/videos/" + PopcornFlixSourcesPuppet.this.mId).getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11'])
                    JSONObject data = (JSONObject) ((JSONArray) new JSONObject(json).get("movies")).get(0)

                    SourceDescription source = new SourceDescription()
                    source.url = ((JSONObject) data.get("urls")).get("Web v2 Player")
                    mSources.add(source)
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
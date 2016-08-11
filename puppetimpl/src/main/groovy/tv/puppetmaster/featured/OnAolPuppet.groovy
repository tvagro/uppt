package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription
import java.util.concurrent.TimeUnit

public class OnAolPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def String mImageUrl

    public OnAolPuppet() {
        this(null, "http://on.aol.com", "On Aol", "Watch a large variety of content from on.aol.com.", "https://i.ytimg.com/i/zpPE2ofEN93bmZoX1o9lEw/mq1.jpg")
    }

    public OnAolPuppet(ParentPuppet parent, String url, String name, String description, String imageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mImageUrl = imageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new OnAolPuppetIterator()

        Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

        document.select(".videoItem").each { node ->
            String id = node.select("meta[name=id]").first().attr("content")
            String name = node.select("meta[name=title]").first().attr("content")
            String description = node.select("meta[name=videoDescription]").first().attr("content")
            Element durationNode = node.select("meta[name=duration]").first()
            long duration = durationNode != null ? convertDuration(durationNode.attr("content")) : -1
            Element publishedNode = node.select("meta[name=published]").first()
            String publicationDate = publishedNode != null ? node.select("meta[name=published]").first().attr("content") : null
            String imageUrl = node.absUrl("data-img-regular")
            String backgroundImageUrl = node.absUrl("data-img-mobile")

            children.add(new OnAolSourcesPuppet(this, id, name, description, duration, publicationDate, imageUrl, backgroundImageUrl))
        }

        if (mUrl.endsWith("/showAll/DEFAULT")) {
            document.select(".flipper").each { node ->
                String showId = node.select("a").first().absUrl("href").split('-shw')[1].split('\\?')[0]
                String url = "http://on.aol.com/showAll/episodes-SHW" + showId
                String name = node.select(".movie-show-title-name").first().text()
                String description = node.select(".desc").first().text()
                String imageUrl = node.select("img.poster").first().absUrl("src")
                if (name != "Channels") {
                    children.add(new OnAolPuppet(this, url, name, description, imageUrl))
                }
            }
        } else if (mParent == null) {
            document.select("ul.nav li a").each { node ->
                String url = node.absUrl("href")
                if (url.endsWith("/shows")) {
                    url = url.replace("/shows", "/showAll/DEFAULT")
                }
                String name = node.text().trim()
                if (name != "Channels") {
                    children.add(new OnAolPuppet(this, url, node.text(), null, "https://i.ytimg.com/i/gr411xBwsRrOItic-G3jSg/mq1.jpg"))
                }
            }
        }
        return children
    }

    @Override
    boolean isTopLevel() {
        return true
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
        return "http://hometracks.nascar.com/files/aol_on_black.jpg"
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
        return 0xFF222222
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF11ABE1
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF11ABE1
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF11ABE1
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
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

    def class OnAolPuppetIterator extends PuppetIterator {

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

    def static class OnAolSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mId
        def String mName
        def String mShortDescription
        def long mDuration
        def String mPublicationDate
        def String mImageUrl
        def String mBackgroundImageUrl

        public OnAolSourcesPuppet(parent, id, name, shortDescription, duration, publicationDate, imageUrl, backgroundImageUrl) {
            mParent = parent
            mId = id
            mName = name
            mShortDescription = shortDescription
            mDuration = duration
            mPublicationDate = publicationDate
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
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
            return new OnAolSourceIterator()
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

        def class OnAolSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    String json = new URL("http://feedapi.b2c.on.aol.com/v1.0/app/videos/aolon/" + OnAolSourcesPuppet.this.mId + "/details").getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11'])
                    JSONObject data = (JSONObject) ((JSONObject) new JSONObject(json).get("response")).get("data")

                    JSONArray renditions = data.getJSONArray("renditions")
                    for (int i = 0; i < renditions.length(); i++) {
                        JSONObject item = renditions.getJSONObject(i)
                        SourceDescription mSource = new SourceDescription()
                        mSource.url = item.get("url")
                        if (mSource.url.contains("uplynk.com")) {
                            mSources.add(0, mSource) // Unreliable sources added to front and reversed in quality sort later
                        } else {
                            mSources.add(mSource)
                        }
                    }
                    mSources = mSources.reverse() // Sort to prioritize quality
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
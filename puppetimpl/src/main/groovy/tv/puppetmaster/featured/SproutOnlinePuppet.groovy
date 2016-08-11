package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.regex.Matcher

public class SproutOnlinePuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def String mUrl
    def String mName
    def String mDescription
    def boolean mIsTopLevel
    def String mImageUrl

    public SproutOnlinePuppet() {
        this(
                null,
                "http://www.sproutonline.com/watch",
                "Sprout Online",
                "Watch fun and educational videos with your favorite Sprout friends.",
                true,
                "http://www.ciplc.org/wp-content/uploads/2013/10/Sprout-Games.jpg"
        )
    }

    public SproutOnlinePuppet(ParentPuppet parent, String url, String name, String description, boolean isTopLevel, String imageUrl) {
        mParent = parent
        mUrl = url
        mName = name
        mDescription = description
        mIsTopLevel = isTopLevel
        mImageUrl = imageUrl
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new SproutOnlinePuppetIterator()

        Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

        if (mUrl.endsWith("/watch")) {
            document.select(".filter-option").each { node ->
                if (node.select("img")) {
                    String name = new JSONObject(node.attr("data-options")).getString("name")
                    String url = "http://www.sproutonline.com/watch?show=" + name
                    name = URLDecoder.decode(name, "UTF-8")
                    String image = node.select("img").first().absUrl("src")

                    children.add(new SproutOnlinePuppet(this, url, name, null, false, image))
                }
            }
        } else {
            document.select(".video-reference").each { node ->
                String name = node.select("figcaption h6").first().text()
                String url = node.select("a").first().absUrl("href")
                String description = node.select(".full-episode") ? "Full episode" : null
                String image = node.select("img").first().absUrl("src")

                children.add(new SproutOnlineSourcesPuppet(this, url, name, description, image))
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
        return mParent == null ? "Kids" : mParent.getName()
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
        return "http://www.sproutonline.com/sites/sprout/files/styles/og_image/public/2015/06/Sprout-General_0.jpg"
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
        return 0xFF0073CF
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF92D400
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF0073CF
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF0073CF
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

    def class SproutOnlinePuppetIterator extends PuppetIterator {

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

    def static class SproutOnlineSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def String mImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public SproutOnlineSourcesPuppet(parent, url, name, shortDescription, imageUrl) {
            mParent = parent
            mUrl = url
            mName = name
            mShortDescription = shortDescription
            mImageUrl = imageUrl
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
            return new SproutOnlineSourceIterator()
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
            return mImageUrl
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

        def class SproutOnlineSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {

                    mSources = new ArrayList<SourceDescription>()

                    String html = new URL(SproutOnlineSourcesPuppet.this.mUrl).getText().replaceAll("\n", "")
                    Matcher matcher = html =~ /"video": "(.+?)"/

                    String url = null
                    if (matcher.find()) {
                        url = matcher.group(1)
                    } else {
                        matcher = html =~ /http:\/\/player.theplatform.com\/(.+?)"/
                        if (matcher.find()) {
                            url = matcher.group(1)
                            html = new URL("http://player.theplatform.com/" + url).getText().replaceAll("\n", "")

                            matcher = html =~ /tp:releaseUrl="(.+?)"/
                            if (matcher.find()) {
                                url = matcher.group(1)
                                if (url.contains("?")) {
                                    url = url.split("\\?")[0]
                                }
                            }
                        }
                    }

                    if (url != null) {
                        String page = new URL(url + "?format=script").getText()

                        try {
                            JSONObject captions = (JSONObject) ((JSONArray) new JSONObject(page).get("captions")).get(0)

                            SourcesPuppet.SubtitleDescription subs = new SourcesPuppet.SubtitleDescription()
                            subs.url = captions.getString("src")
                            subs.mime = captions.getString("type")
                            subs.locale = captions.getString("lang")

                            SproutOnlineSourcesPuppet.this.mSubtitles.add(subs)
                        } catch (ignore) {

                        }

                        URL peek = new URL(url)
                        HttpURLConnection conn = (HttpURLConnection) peek.openConnection()
                        conn.setInstanceFollowRedirects(false)
                        conn.connect()
                        url = conn.getHeaderField("Location")

                        SourceDescription source = new SourceDescription()
                        source.url = url

                        mSources.add(source)
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
package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import tv.puppetmaster.data.i.*

import java.util.regex.Matcher

public class HowStuffWorksPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl

    HowStuffWorksPuppet() {
        this(
                null,
                true,
                "How Stuff Works",
                "Explains thousands of topics, from engines to lock-picking to ESP, with video and illustrations so you can learn how everything works.",
                "https://pbs.twimg.com/profile_images/464876932295692288/49jIYirM.jpeg",
                "http://www.aftermath.com/wp-content/uploads/How-Stuff-Works.jpg",
                "http://www.howstuffworks.com/videos",
        )
    }

    HowStuffWorksPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String url) {
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
        return null
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF007CB3
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF0A253E
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF0A253E
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF0A253E
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    Puppet.PuppetIterator getChildren() {
        Puppet.PuppetIterator children = new HowStuffWorksPuppetIterator()

        def String page = new URL(mUrl).getText()
        def Matcher matcher = page =~ /(?s)var clip = \{.*?clip_title.*?:.*?'(.+?)'.*?duration.*?:.*?(\d+).*?series_title.*?:.*?'(.+?)'.*?caption.*?:.*?'(.+?)'.*?m3u8.*?:.*?'(.+?)'.*?thumbnail_url.*?:.*?'(.+?)'.*?video_still_url.*?:.*?'(.+?)'.*?mp4.*?:.*?(\[.+?\])/
        matcher.findAll().each { match, name, duration, category, description, m3u8, imageUrl, backgroundImageUrl, mp4 ->
            children.add(new HowStuffWorksSourcesPuppet(this, name as String, duration as long, category as String, description as String, m3u8 as String, imageUrl as String, backgroundImageUrl as String, mp4 as String))
        }

        Jsoup.connect(mUrl).get().select(".sidebar .img-container").each {
            String url = it.select("a").first().absUrl("href")
            if (url.endsWith(".htm") && !url.contains("/now/")) {
                url = url.substring(0, url.lastIndexOf("/"))
            }
            String imageUrl = it.select("img").first().absUrl("data-src")
            String name = it.select("img").first().attr("title")
            children.add(new HowStuffWorksPuppet(this, true, name, null, imageUrl, mBackgroundImageUrl, url))
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

    def static class HowStuffWorksPuppetIterator extends Puppet.PuppetIterator {

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

    class HowStuffWorksSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def long mDuration
        def String mCategory
        def String mDescription
        def String mm3u8
        def String mImageUrl
        def String mBackgroundImageUrl
        def String mmp4

        HowStuffWorksSourcesPuppet(ParentPuppet parent, String name, long duration, String category, String description, String m3u8, String imageUrl, String backgroundImageUrl, String mp4) {
            mParent = parent
            mName = Jsoup.parse(name).text()
            mDuration = duration * 1000l
            mCategory = Jsoup.parse(category).text()
            mDescription = Jsoup.parse(description).text()
            mm3u8 = m3u8
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
            mmp4 = mp4
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
            return new HowStuffWorksSourceIterator()
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
            return mCategory
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

        class HowStuffWorksSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourcesPuppet.SourceDescription>()

                    JSONArray json = new JSONArray(mmp4.replace(",]", "]"))
                    for (int i = json.length(); i > 0; i--) {
                        JSONObject item = json.getJSONObject(i - 1)

                        SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                        source.url = item.getString("src")
                        source.bitrate = Long.parseLong(item.getString("bitrate").replace("k", ""))

                        mSources.add(source)
                    }

                    SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                    source.url = mm3u8

                    mSources.add(source)
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
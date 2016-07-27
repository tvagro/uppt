package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.concurrent.TimeUnit

public class RedBullTVPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    def ParentPuppet mParent
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    public RedBullTVPuppet() {
        this(
                null,
                "Red Bull TV",
                "An unrivaled world of action sports, live events, inspirational people, and breathtaking stories from Red Bull's global playground!",
                "https://api.redbull.tv/v1/",
                "https://lh5.ggpht.com/Ecvq_X7VNttbEQ6QA5vv7C1X19sXo2yufia5VQwQfyZpW0l3vuw9CfqGs1cJsHp5zA=w300",
                "http://springboardproductions.net/wp-content/gallery/news-items/redbulltv.jpg",
                true
        )
    }

    protected RedBullTVPuppet(ParentPuppet parent, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
        mParent = parent
        mName = name
        mDescription = description
        mUrl = url
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
        mIsTopLevel = isTopLevel
    }

    @Override
    PuppetIterator getChildren() {
        return new RedBullTVIterator(this, mUrl)
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
        return mParent == null ? "Sports" : mParent.getName()
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
    SearchesPuppet getSearchProvider() {
        return new RedBullTVSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF0C2044
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFCC1C4A
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFFACD34
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFFACD34
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
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    String toString() {
        return mName
    }

    void setUrl(String url) {
        mUrl = url
    }

    def static class RedBullTVSearchesPuppet extends RedBullTVPuppet implements SearchesPuppet {

        def static final String SEARCH_URL = "https://api.redbull.tv/v1/search?search="

        public RedBullTVSearchesPuppet(ParentPuppet parent) {
            super(
                    parent,
                    "Search",
                    "Search Red Bull TV",
                    SEARCH_URL,
                    "https://lh5.ggpht.com/Ecvq_X7VNttbEQ6QA5vv7C1X19sXo2yufia5VQwQfyZpW0l3vuw9CfqGs1cJsHp5zA=w300",
                    "http://springboardproductions.net/wp-content/gallery/news-items/redbulltv.jpg",
                    false
            )
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(SEARCH_URL + searchQuery.replace(" ", "%20"))
        }
    }

    def class RedBullTVIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mUrl
        def ArrayList<Puppet> mPuppets

        def int numSources = 0

        def int currentCategoryIteration = 0
        def int currentSourcesIteration = 0

        transient def JSONArray categoryItems = new JSONArray()
        transient def JSONArray sourcesItems = new JSONArray()

        public RedBullTVIterator(ParentPuppet parent, String url) {
            mParent = parent
            mUrl = url
        }

        @Override
        boolean hasNext() {
            if (mUrl == null || mUrl.trim() == "") {
                return false
            } else if (mPuppets == null) {
                mPuppets = new ArrayList<>()

                String page = new URL(mUrl).getText()
                JSONObject json = new JSONObject(page)

                if (mUrl == "https://api.redbull.tv/v1/") {
                    RedBullTVIterator videos = new RedBullTVIterator(mParent, json.get("videos").toString())
                    while (videos.hasNext()) {
                        Puppet p = videos.next()
                        sourcesItems.put(new JSONArray())
                        // Dummy, TODO: make this more efficient by excluding sourcesItems/categoryItems with count
                        mPuppets.add(p)
                    }
                    ["channels", "episodes", "films", "event_streams", "events"].each { videoType ->
                        mPuppets.add(new RedBullTVPuppet(
                                mParent,
                                videoType.replace("_", " ").capitalize(),
                                null,
                                json.get(videoType).toString(),
                                "https://lh5.ggpht.com/Ecvq_X7VNttbEQ6QA5vv7C1X19sXo2yufia5VQwQfyZpW0l3vuw9CfqGs1cJsHp5zA=w300",
                                "http://springboardproductions.net/wp-content/gallery/news-items/redbulltv.jpg",
                                true
                        ))
                    }
                } else if (mUrl.startsWith("https://api.redbull.tv/v1/search")) {
                    JSONArray items = (JSONArray) json.get("search_results")
                    for (int i = 0; items != null && i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i)
                        sourcesItems.put(item)
                        // Dummy, TODO: make this more efficient by excluding sourcesItems/categoryItems with count
                        mPuppets.add(new RedBullTVSourcesPuppet(
                                mParent,
                                item
                        ))
                    }
                } else if (json.has("videos")) {
                    JSONArray items = (JSONArray) json.get("videos")
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i)
                        sourcesItems.put(item)
                        mPuppets.add(new RedBullTVSourcesPuppet(
                                mParent,
                                item
                        ))
                    }
                } else {
                    JSONArray items = null
                    try {
                        items = (JSONArray) json.get("channels")
                    } catch (ignored) {
                        items = (JSONArray) json.get("sub_channels")
                    } finally {
                        for (int i = 0; items != null && i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i)
                            if (item.get("id").toString() != "main") { // Main always empty, not sure why
                                categoryItems.put(item)
                                mPuppets.add(new RedBullTVPuppet(
                                        mParent,
                                        item.get("title").toString(),
                                        null,
                                        "https://api.redbull.tv/v1/channels/" + item.get("id").toString(),
                                        ((JSONObject) ((JSONObject) item.get("images")).get("landscape")).get("uri").toString(),
                                        ((JSONObject) ((JSONObject) item.get("images")).get("background")).get("uri").toString(),
                                        false
                                ))
                            }
                        }
                        if (items == null || items.length() == 0) {
                            RedBullTVIterator videos = new RedBullTVIterator(mParent, ((JSONObject) ((JSONObject) json.get("meta")).get("links")).get("videos").toString())
                            while (videos.hasNext()) {
                                Puppet item = videos.next()
                                sourcesItems.put(new JSONArray()) // Dummy, TODO: make this more efficient by excluding sourcesItems/categoryItems with count
                                mPuppets.add(item)
                            }
                            ["episodes", "clips", "films", "event_streams", "events", "featured", "featured_shows"].each { videoType ->
                                mPuppets.add(new RedBullTVPuppet(
                                        mParent,
                                        videoType.replace("_", " ").capitalize(),
                                        null,
                                        ((JSONObject) ((JSONObject) json.get("meta")).get("links")).get(videoType).toString(),
                                        "https://lh5.ggpht.com/Ecvq_X7VNttbEQ6QA5vv7C1X19sXo2yufia5VQwQfyZpW0l3vuw9CfqGs1cJsHp5zA=w300",
                                        "http://springboardproductions.net/wp-content/gallery/news-items/redbulltv.jpg",
                                        false
                                ))
                            }
                        }
                    }
                }
            }
            return (currentCategoryIteration + currentSourcesIteration) < mPuppets.size()
        }

        @Override
        Puppet next() {
            if (currentSourcesIteration < numSources) {
                return mPuppets.get(currentSourcesIteration++)
            }
            return mPuppets.get(numSources + currentCategoryIteration++)
        }

        @Override
        void remove() {

        }

        @Override
        void add(Puppet puppet) {

        }
    }

    def class RedBullTVSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def JSONObject mItem
        def String mName
        def String mCategory
        def String mDescription
        def String mPublicationDate
        def long mDuration = -1
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public RedBullTVSourcesPuppet(ParentPuppet parent, JSONObject item) {
            mParent = parent
            mItem = item
            mName = item.get("title").toString()
            mCategory = item.get("subtitle").toString()
            mDescription = item.get("short_description").toString()
            mPublicationDate = item.get("published_on").toString()
            if (item.has("duration")) {
                mDuration = convertDuration(item.get("duration").toString())
            }
            mImageUrl = ((JSONObject) ((JSONObject) item.get("images")).get("landscape")).get("uri").toString()
            try {
                mBackgroundImageUrl = ((JSONObject) ((JSONObject) item.get("images")).get("background")).get("uri").toString()
            } catch (ignored) {
                mBackgroundImageUrl = ((JSONObject) ((JSONObject) item.get("images")).get("landscape")).get("uri").toString()
            }

            if (item.has("closed_captions")) {
                JSONObject subtitles = (JSONObject) item.get("closed_captions")
                Iterator<?> keys = subtitles.keys()
                while (keys.hasNext()) {
                    String key = (String) keys.next()
                    JSONObject s = subtitles.get(key)
                    SourcesPuppet.SubtitleDescription sd = new SourcesPuppet.SubtitleDescription()
                    sd.url = s.get("uri")
                    sd.mime = "txt/" + key
                }
            }
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
            return new RedBullTVSourceIterator()
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
        public PuppetIterator getRelated() {
            return new RedBullTVIterator(mParent, "https://api.redbull.tv/v1/videos/" + mItem.get("id").toString() + "/related?limit=10")
        }

        @Override
        public String toString() {
            return getName()
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

        def class RedBullTVSourceIterator implements SourcesPuppet.SourceIterator {
            def List<SourceDescription> mSources
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()

                    String id = RedBullTVSourcesPuppet.this.mItem.get("id")
                    String page = new URL("https://api.redbull.tv/v1/videos/" + id + "?include_renditions=true").getText()
                    JSONObject item = new JSONObject(page)


                    JSONObject videos = (JSONObject) item.get("videos")
                    Iterator<?> keys = videos.keys()
                    while (keys.hasNext()) {
                        String key = (String) keys.next()
                        JSONObject v = videos.getJSONObject(key)
                        SourceDescription source = new SourceDescription()
                        source.url = v.get("uri")
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
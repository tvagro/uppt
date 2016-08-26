package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.puppetmaster.data.i.*

import java.text.SimpleDateFormat

class ShowcaseCanadaPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final String CATEGORIES_URL = "http://common.farm1.smdg.ca/Forms/PlatformVideoFeed?platformUrl=http%3A//feed.theplatform.com/f/dtjsEC/9H6qyshBZU3E/categories%3Fpretty%3Dtrue%26byHasReleases%3Dtrue%26byCustomValue%3D%7Bplayertag%7D%7Bz/Showcase%20Video%20Centre%7D%26sort%3DfullTitle&callback="
    static final String CATEGORY_URL_TEMPLATE = 'http://feed.theplatform.com/f/dtjsEC/9H6qyshBZU3E?count=true&byCategoryIDs=%s&startIndex=1&endIndex=100&sort=pubDate|desc&callback='

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mUrl
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def JSONArray mEpisodes

    ShowcaseCanadaPuppet() {
        this(
                null,
                true,
                CATEGORIES_URL,
                "Showcase Canada",
                "Big. Bold. Hits. With a top-rated line-up of hit series, breakout cable exclusives and big-ticket movies, Showcase is a powerhouse of TVs best.",
                "https://d2t86ruphax0oa.cloudfront.net/sites/default/files/styles/large/public/field/image/Showcase%20(new%20logo).JPG",
                "http://tvloon.ca/wp-content/uploads/2013/07/showcase.jpg",
                null,
        )
    }

    ShowcaseCanadaPuppet(ParentPuppet parent, boolean isTopLevel, String url, String name, String description, String imageUrl, String backgroundImageUrl, JSONArray episodes) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mUrl = url
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
        mEpisodes = episodes
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
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF0084B0
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF0084B0
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF000000
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    Puppet.PuppetIterator getChildren() {
        Puppet.PuppetIterator children = new ShowcaseCanadaPuppetIterator()

        if (mEpisodes == null) {

            JSONArray json = new JSONObject(new URL(mUrl).getText()[1..-1]).getJSONArray("items")

            def less = []
            def more = []

            for (int i = 0; i < json.length(); i++) {
                JSONObject item = json.getJSONObject(i)
                if (item.getInt("depth") == 2) {

                    def String id = item.getString("id").replace("http://data.media.theplatform.com/media/data/Category/", "")
                    def String categoryUrl = sprintf(CATEGORY_URL_TEMPLATE, id)
                    JSONArray episodes = new JSONObject(new URL(categoryUrl).getText()).getJSONArray("entries")

                    if (episodes.length() > 0 && episodes.length() < 3) {
                        less << new ShowcaseCanadaPuppet(
                                this,
                                false,
                                null,
                                item.getString("title"),
                                null,
                                mImageUrl,
                                mBackgroundImageUrl,
                                episodes,
                        )
                    } else if (episodes.length() > 0) {
                        more << new ShowcaseCanadaPuppet(
                                this,
                                true,
                                null,
                                item.getString("title"),
                                null,
                                mImageUrl,
                                mBackgroundImageUrl,
                                episodes,
                        )
                    }
                }
            }
            less.each { children.add(it as Puppet) }
            more.each { children.add(it as Puppet) }

        } else {

            for (int j = 0; j < mEpisodes.length(); j++) {
                JSONObject episode = mEpisodes.getJSONObject(j)
                children.add(new ShowcaseCanadaSourcesPuppet(this, episode))
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

    static class ShowcaseCanadaPuppetIterator extends Puppet.PuppetIterator {

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

    class ShowcaseCanadaSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def JSONObject mItem
        def mImageUrls = []

        ShowcaseCanadaSourcesPuppet(parent, JSONObject item) {
            mParent = parent
            mItem = item

            JSONArray images = mItem.getJSONArray("thumbnails")
            for (int i = 0; i < images.length(); i++) {
                mImageUrls << images.getJSONObject(i).getString("url")
            }
        }

        @Override
        String getPublicationDate() {
            long publicationDate = mItem.getLong("pubDate")
            return new SimpleDateFormat("MMMM d, yyyy").format(publicationDate)
        }

        @Override
        long getDuration() {
            return (long) mItem.getJSONArray("content").getJSONObject(0).getDouble("duration") * 1000
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new ShowcaseCanadaSourceIterator()
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
            return mItem.getString("title")
        }

        @Override
        String getCategory() {
            return mItem.getString("author")
        }

        @Override
        String getShortDescription() {
            return mItem.getString("description")
        }

        @Override
        String getImageUrl() {
            return mImageUrls[0]
        }

        @Override
        String getBackgroundImageUrl() {
            return mImageUrls[-1]
        }

        @Override
        boolean isUnavailableIn(String region) {
            if (region == 'ca') {
                return false;
            }
            def countries = mItem.getJSONArray("countries")
            def boolean exclude = mItem.getBoolean("excludeCountries")
            for (int i = 0; i < countries.length(); i++) {
                if (countries.getString(i) in [region, region.toUpperCase()]) {
                    return exclude
                }
            }
            return !exclude
        }

        @Override
        String getPreferredRegion() {
            return 'ca'
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
        Puppet.PuppetIterator getRelated() {
            return null
        }

        class ShowcaseCanadaSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourcesPuppet.SourceDescription>()

                    JSONArray sources = mItem.getJSONArray("content")

                    for (int i = 0; i < sources.length(); i++) {
                        JSONObject item = sources.getJSONObject(i)
                        String url = item.getString("url").replaceFirst(/f4m/, "m3u")
                        Document document = Jsoup.connect(url).ignoreContentType(true).get()

                        Elements videos = document.select("video,ref")
                        for (Element video in videos) {
                            SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                            source.url = video.attr("src")
                            source.width = Integer.toString(item.getInt("width"))
                            source.height = Integer.toString(item.getInt("height"))
                            source.bitrate = item.getLong("bitrate")
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
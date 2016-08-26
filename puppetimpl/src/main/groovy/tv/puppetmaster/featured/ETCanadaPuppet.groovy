package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.puppetmaster.data.i.*

import java.text.SimpleDateFormat

class ETCanadaPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final String CATEGORIES_URL = "http://common.farm1.smdg.ca/Forms/PlatformVideoFeed?platformUrl=http%3A//feed.theplatform.com/f/dtjsEC/2dJJlS8TfWZc/categories%3Fpretty%3Dtrue%26byHasReleases%3Dtrue%26byCustomValue%3D%7Bplayertag%7D%7Bz/ETCanada%20Video%20Centre%7D%26sort%3DfullTitle&callback="
    static final String CATEGORY_URL_TEMPLATE = 'http://feed.theplatform.com/f/dtjsEC/2dJJlS8TfWZc?count=true&byCategoryIDs=%s&startIndex=1&endIndex=100&sort=pubDate|desc&callback='

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mUrl
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl

    ETCanadaPuppet() {
        this(
                null,
                true,
                CATEGORIES_URL,
                "ET Canada",
                "Latest Entertainment News from ETCanada.",
                "https://lh4.googleusercontent.com/-K6RrTFBOht8/AAAAAAAAAAI/AAAAAAAAABU/vlEchpBW4eg/photo.jpg",
                "http://www.iambishop.com/wp-content/uploads/2011/10/et_cdn_logo21.jpg",
        )
    }

    ETCanadaPuppet(ParentPuppet parent, boolean isTopLevel, String url, String name, String description, String imageUrl, String backgroundImageUrl) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mUrl = url
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundImageUrl
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
        return 0xFFD5B263
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF943924
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF943924
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    Puppet.PuppetIterator getChildren() {
        Puppet.PuppetIterator children = new ETCanadaPuppetIterator()

        if (mUrl == CATEGORIES_URL) {

            JSONArray json = new JSONObject(new URL(mUrl).getText()[1..-1]).getJSONArray("items")

            def ETCanadaPuppet fullEpisodes

            for (int i = 0; i < json.length(); i++) {
                JSONObject item = json.getJSONObject(i)
                if (item.getBoolean("hasReleases")) {

                    def String id = item.getString("id").replace("http://data.media.theplatform.com/media/data/Category/", "")
                    def String categoryUrl = sprintf(CATEGORY_URL_TEMPLATE, id)

                    if (item.getString("title") == "Full Episodes") {
                        fullEpisodes = new ETCanadaPuppet(
                                this,
                                true,
                                categoryUrl,
                                item.getString("title"),
                                null,
                                mImageUrl,
                                mBackgroundImageUrl,
                        )
                    } else {
                        children.add(new ETCanadaPuppet(
                                this,
                                false,
                                categoryUrl,
                                item.getString("title"),
                                null,
                                mImageUrl,
                                mBackgroundImageUrl,
                        ))
                    }
                }
            }
            if (fullEpisodes) {
                children.add(fullEpisodes)
            }

        } else {

            JSONArray episodes = new JSONObject(new URL(mUrl).getText()).getJSONArray("entries")
            for (int j = 0; j < episodes.length(); j++) {
                JSONObject episode = episodes.getJSONObject(j)
                children.add(new ETCanadaSourcesPuppet(this, episode))
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

    static class ETCanadaPuppetIterator extends Puppet.PuppetIterator {

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

    class ETCanadaSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def JSONObject mItem
        def mImageUrls = []

        ETCanadaSourcesPuppet(parent, JSONObject item) {
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
            return new ETCanadaSourceIterator()
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

        class ETCanadaSourceIterator implements SourcesPuppet.SourceIterator {

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
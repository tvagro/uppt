package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.puppetmaster.data.i.InstallablePuppet
import tv.puppetmaster.data.i.ParentPuppet
import tv.puppetmaster.data.i.Puppet
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SearchesPuppet
import tv.puppetmaster.data.i.SettingsPuppet
import tv.puppetmaster.data.i.SourcesPuppet
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.regex.Matcher

public class FoodNetworkPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mBaseUrl
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    def transient FoodNetworkSearchesPuppet mSearchProvider

    public FoodNetworkPuppet() {
        this(
                null,
                "http://www.foodnetwork.com",
                "Food Network",
                "Watch videos about Food Network: Full Episodes from Food Network.",
                "/videos/players/food-network-full-episodes.html",
                "http://foodnetwork.sndimg.com/content/dam/images/food/unsized/2013/4/16/0/FN_fn-logo_s500x500.jpg.rend.sni18col.landscape.jpeg",
                "http://www.chadhudsonevents.com/sites/default/files/styles/event_page_image/public/event-photos/food-network-emmy-consideration-party/food-network-emmy-consideration-party.jpg?itok=YwYzALUP",
                true
        )
    }

    protected FoodNetworkPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
        mParent = parent
        mBaseUrl = baseUrl
        mName = name
        mDescription = description
        mUrl = url != null && url.startsWith("/") ? mBaseUrl + url : url
        mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl
        mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? mBaseUrl + backgroundImageUrl : backgroundImageUrl
        mIsTopLevel = isTopLevel
    }

    @Override
    PuppetIterator getChildren() {
        return new FoodNetworkIterator(this, mBaseUrl, mUrl)
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
        return mParent == null ? "Home" : mParent.getName()
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
    SearchesPuppet getSearchProvider() {
        if (mSearchProvider == null && false) {
            mSearchProvider = new FoodNetworkSearchesPuppet(
                    mParent,
                    mBaseUrl,
                    "Search FoodNetwork.com",
                    "Find videos related to your search term.",
                    "http://www.foodnetwork.com/search/search-results.videos.html?searchTerm=",
                    mImageUrl,
                    mBackgroundImageUrl
            )
        }
        return mSearchProvider
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFEE3424
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFEE3424
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFEE3424
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
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    void setUrl(String url) {
        mUrl = url
    }

    def class FoodNetworkIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def ArrayList<Puppet> mPuppets

        def int numSources = 0

        def int currentCategoryIteration = 0
        def int currentSourcesIteration = 0

        transient def Elements categoryItems
        transient def JSONArray sourcesItems

        public FoodNetworkIterator(ParentPuppet parent, String baseUrl, String url) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
        }

        @Override
        boolean hasNext() {
            if (mUrl == null || mUrl.trim() == "") {
                return false
            } else if (mPuppets == null) {
                mPuppets = new ArrayList<>()

                String page = new URL(mUrl).getText().replaceAll("\n", "")

                Matcher matcher = page =~ /"videos": \[(.+?)\]/
                try {
                    sourcesItems = (JSONArray) new JSONObject("{" + matcher[0][0] + "}").get("videos")
                } catch (ignore) {
                    // I guess no videos on this page
                }

                for (int i = 0; sourcesItems != null && i < sourcesItems.length(); i++) {
                    JSONObject item = (JSONObject) sourcesItems.get(i)
                    String url = item.get("releaseUrl").toString()
                    String imageUrl
                    try {
                        imageUrl = item.get("thumbnailUrl16x9").toString().replace("126x71.jpg", "480x360.jpg")
                    } catch (Exception ex) {
                        imageUrl = item.get("thumbnailUrl").toString().replace("92x69", "231x130")
                    }
                    mPuppets.add(new FoodNetworkSourcesPuppet(
                            mParent,
                            mBaseUrl,
                            url.startsWith("/") ? mBaseUrl + url : url,
                            item.get("title").toString(),
                            item.get("description").toString(),
                            imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl,
                            Long.parseLong(item.get("length").toString()) * 1000,
                            mUrl
                    ))
                    numSources++
                }

                Document document = Jsoup.connect(mUrl.startsWith("/") ? mBaseUrl + mUrl : mUrl).get()
                categoryItems = document.select(".slat .group")

                for (Element item in categoryItems) {
                    try {
                        Element a = item.getElementsByTag("a").first()
                        String url = a.absUrl("href")
                        Element img = a.getElementsByTag("img").first()
                        String imageUrl = img.absUrl("src")
                        String name = item.getElementsByTag("h4").first().text()
                        mPuppets.add(new FoodNetworkPuppet(
                                mParent,
                                mBaseUrl,
                                name,
                                null,
                                url,
                                imageUrl,
                                imageUrl,
                                false
                        ))
                    } catch (ignore) {
                        // TODO: What heppened here?
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

    public static class FoodNetworkSearchesPuppet extends FoodNetworkPuppet implements SearchesPuppet {

        def String mSearchUrl

        public FoodNetworkSearchesPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl) {
            super(parent, baseUrl, name, description, url, imageUrl, backgroundImageUrl, false)
            mSearchUrl = url
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(mSearchUrl + searchQuery.replace(" ", "-").trim())
        }
    }

    def static class FoodNetworkSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def String mName
        def String mDescription
        def String mImageUrl
        def long mDuration
        def String mParseRelatedUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public FoodNetworkSourcesPuppet(ParentPuppet parent, String baseUrl, String url, String name, String description, String imageUrl, long duration, String parseRelatedUrl) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
            mName = name
            mDescription = description
            mImageUrl = imageUrl
            mDuration = duration
            mParseRelatedUrl = parseRelatedUrl
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
            return new FoodNetworkSourceIterator(mUrl)
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
            return mDescription
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
        public PuppetIterator getRelated() {
            return new FoodNetworkPuppet(
                    mParent,
                    mBaseUrl,
                    "Related: " + mName,
                    null,
                    mParseRelatedUrl,
                    null,
                    null,
                    false
            ).getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class FoodNetworkSourceIterator implements SourcesPuppet.SourceIterator {

            def String mUrl
            def List<SourceDescription> mSources
            def int currentIndex = 0

            public FoodNetworkSourceIterator(String url) {
                mUrl = url
            }

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()

                    Document document = Jsoup.connect(mUrl).ignoreContentType(true).get()

                    Elements videos = document.select("video")
                    for (Element video in videos) {
                        SourceDescription source = new SourceDescription()
                        source.url = video.attr("src")
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
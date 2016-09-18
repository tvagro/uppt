package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class IciTouTvPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def String mBaseUrl
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    public IciTouTvPuppet() {
        this(
                null,
                "https://api.tou.tv",
                "ICI Tou.tv",
                "Découvrez toute nos émissions d'un seul coup d'oeil et notre offre Extra. ICI Tou.tv est la plus importante webtélé de divertissement francophone au Canada.",
                "/v1/toutvapiservice.svc/json/GetGenres",
                "https://pbs.twimg.com/profile_images/771736597670797312/7txcDom5.jpg",
                "http://ici.tou.tv/Content/images/generic.jpg",
                true
        )
    }

    protected IciTouTvPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
        mParent = parent
        mBaseUrl = baseUrl
        mName = name
        mDescription = description
        mUrl = url
        mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl
        mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? mBaseUrl + backgroundImageUrl : backgroundImageUrl
        mIsTopLevel = isTopLevel
    }

    @Override
    PuppetIterator getChildren() {
        return new IciTouTvIterator(this, mBaseUrl, mUrl)
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
        return mParent == null ? "Public Service" : mParent.getName()
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
        return new IciTouTvSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFFE21A21
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF00CCCC
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF00CCCC
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF00CCCC
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

    def static class IciTouTvSearchesPuppet extends IciTouTvPuppet implements SearchesPuppet {

        def static final String SEARCH_URL = "/v1/toutvapiservice.svc/json/SearchTermsMax?maximumNumberOfResults=10&query="

        public IciTouTvSearchesPuppet(ParentPuppet parent) {
            super(
                    parent,
                    "https://api.tou.tv",
                    "Recherche",
                    "Recherche ICI Tou.Tv",
                    SEARCH_URL,
                    "https://pbs.twimg.com/profile_images/448556282404818944/rM6tjmAB_400x400.png",
                    "http://ici.tou.tv/Content/images/generic.jpg",
                    false
            )
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            setUrl(SEARCH_URL + searchQuery.replace(" ", "%20"))
        }
    }

    def class IciTouTvIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def ArrayList<Puppet> mPuppets

        def int numSources = 0

        def int currentCategoryIteration = 0
        def int currentSourcesIteration = 0

        public IciTouTvIterator(ParentPuppet parent, String baseUrl, String url) {
            mParent = parent
            mBaseUrl = baseUrl
            mUrl = url
        }

        @Override
        boolean hasNext() {
            if (mUrl == null || mUrl.trim() == "") {
                return false
            } else if (mPuppets == null) {
                String page
                try {
                    page = new URL(mBaseUrl + mUrl).getText(connectTimeout:500, readTimeout:5000, requestProperties: ['User-Agent': 'Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7'])
                } catch (Exception ex) {
                    return false // This api has a habit of hanging leaving us waiting with a hung UI so let's prevent that with a short timeout
                }

                mPuppets = new ArrayList<>()
                if (mUrl.equals("/v1/toutvapiservice.svc/json/GetGenres")) {
                    JSONArray categoryItems = new JSONObject(page).getJSONArray("d")

                    for (int i = 0; i < categoryItems.length(); i++) {
                        JSONObject item = categoryItems.getJSONObject(i)

                        mPuppets.add(new IciTouTvPuppet(
                                mParent,
                                IciTouTvPuppet.this.mBaseUrl,
                                item.getString("Title"),
                                null,
                                "/v1/toutvapiservice.svc/json/GetPageRepertoire",
                                "https://pbs.twimg.com/profile_images/448556282404818944/rM6tjmAB_400x400.png",
                                "http://ici.tou.tv/Content/images/generic.jpg",
                                false
                        ))
                    }
                } else if (mUrl.startsWith("/v1/toutvapiservice.svc/json/GetPageRepertoire")) {
                    JSONArray categoryItems = new JSONObject(page).getJSONObject("d").getJSONArray("Emissions")

                    for (int i = 0; i < categoryItems.length(); i++) {
                        JSONObject item = categoryItems.getJSONObject(i)

                        if (mParent.getName().equals(item.getString("Genre"))) {
                            mPuppets.add(new IciTouTvPuppet(
                                    mParent,
                                    IciTouTvPuppet.this.mBaseUrl,
                                    item.getString("Titre"),
                                    item.get("NombreSaisons").toString() + " saisons, " + item.get("NombreEpisodes").toString() + " episodes",
                                    "/v1/toutvapiservice.svc/json/GetEpisodesForEmission?emissionid=" + item.get("Id"),
                                    item.getString("ImageJorC"),
                                    item.getString("ImageJorC"),
                                    false
                            ))
                        }
                    }
                } else if (mUrl.startsWith("/v1/toutvapiservice.svc/json/GetEpisodesForEmission")) {
                    JSONArray sourcesItems = new JSONObject(page).getJSONArray("d")

                    for (int i = 0; i < sourcesItems.length(); i++) {
                        JSONObject item = sourcesItems.getJSONObject(i)

                        mPuppets.add(new IciTouTvSourcesPuppet(
                                mParent,
                                item.get("PID").toString(),
                                item.getString("FullTitle"),
                                item.getString("Description"),
                                item.getString("ImagePlayerNormalC"),
                                item.getString("ImagePlayerNormalC"),
                                Long.parseLong(item.get("Length").toString()),
                                item.getString("AirDateLongString"),
                        ))
                    }
                } else if (mUrl.startsWith("/v1/toutvapiservice.svc/json/SearchTerms")) {
                    JSONArray sourcesItems = new JSONObject(page).getJSONObject("d").getJSONArray("Results")

                    for (int i = 0; i < sourcesItems.length(); i++) {
                        try {
                            JSONObject item = sourcesItems.getJSONObject(i).getJSONObject("Episode")

                            mPuppets.add(new IciTouTvSourcesPuppet(
                                    mParent,
                                    item.get("PID").toString(),
                                    item.getString("FullTitle"),
                                    item.getString("Description"),
                                    item.getString("ImagePlayerNormalC"),
                                    item.getString("ImagePlayerNormalC"),
                                    Long.parseLong(item.get("Length").toString()),
                                    item.getString("AirDateLongString"),
                            ))
                        } catch (Exception ex) {
                            try {
                                JSONObject item = sourcesItems.getJSONObject(i).getJSONObject("Emission")

                                mPuppets.add(new IciTouTvPuppet(
                                        mParent,
                                        IciTouTvPuppet.this.mBaseUrl,
                                        item.getString("Title"),
                                        item.getString("Description"),
                                        "/v1/toutvapiservice.svc/json/GetEpisodesForEmission?emissionid=" + item.get("Id"),
                                        item.getString("ImageJorC"),
                                        item.getString("ImageJorC"),
                                        false
                                ))
                            } catch (ignore) {

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

    def static class IciTouTvSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mId
        def String mName
        def String mDescription
        def String mImageUrl
        def String mBackgroundImageUrl
        def long mDuration
        def String mPublicationDate

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        public IciTouTvSourcesPuppet(ParentPuppet parent, String id, String name, String description, String imageUrl, String backgroundImageUrl, long duration, String publicationDate) {
            mParent = parent
            mId = id
            mName = name
            mDescription = description
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
            mDuration = duration
            mPublicationDate = publicationDate
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
            return new IciTouTvSourceIterator(mId)
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
            return mParent.getName()
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
            return region != 'ca'
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
            return mParent
        }

        @Override
        public PuppetIterator getRelated() {
            mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class IciTouTvSourceIterator implements SourcesPuppet.SourceIterator {

            def static SOURCE_URL = "http://api.radio-canada.ca/validationMedia/v1/Validation.html?output=json&appCode=thePlatform&deviceType=Android&connectionType=wifi&idMedia="
            def String mId
            def List<SourceDescription> mSources
            def int currentIndex = 0

            public IciTouTvSourceIterator(String id) {
                mId = id
            }

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    String page
                    try {
                        page = new URL(SOURCE_URL + mId).getText(connectTimeout:500, readTimeout:5000, requestProperties: ['User-Agent': 'Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7'])
                    } catch (Exception ex) {
                        return false // This api has a habit of hanging leaving us waiting with a hung UI so let's prevent that with a short timeout
                    }
                    SourceDescription source = new SourceDescription()
                    source.url = new JSONObject(page).get("url").toString()
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
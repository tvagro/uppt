package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

import java.text.SimpleDateFormat
import java.util.regex.Matcher

class HotstarPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final MAIN_CATEGORIES = [
            [
                    name:   "Featured",
                    url:    "http://account.hotstar.com/AVS/besc?action=GetArrayContentList&categoryId=5637&channel=PCTV",
                    top:    true,
            ],
            [
                    name:   "Movie Collections",
                    url:    "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&categoryId=558&channel=PCTV",
                    top:    false,
            ],
            [
                    name:   "Movies",
                    url:    "http://search.hotstar.com/AVS/besc?action=SearchContents&channel=PCTV&startIndex=0&maxResult=10&type=MOVIE&query=*",
                    top:    true,
            ],
            [
                    name:   "TV Shows",
                    url:    "http://search.hotstar.com/AVS/besc?action=SearchContents&channel=PCTV&startIndex=0&maxResult=10&moreFilters=type:SERIES%3Blanguage:%3B&query=*",
                    top:    true,
            ],
            [
                    name:   "Sports",
                    url:    "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&categoryId=1678&channel=PCTV",
                    top:    true,
            ],
            [
                    name:   "Sports - New",
                    url:    "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&categoryId=5962&channel=PCTV",
                    top:    false,
            ],
            [
                    name:   "Sports - Live",
                    url:    "http://account.hotstar.com/AVS/besc?action=GetArrayContentList&categoryId=5891&channel=PCTV",
                    top:    true,
            ],
            [
                    name:   "TV Channels",
                    url:    "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&appVersion=5.0.21&categoryId=564&channel=PCTV",
                    top:    true,
            ],
    ]

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mUrl

    HotstarPuppet() {
        this(null, true, "Hotstar", null)
    }

    HotstarPuppet(ParentPuppet parent, boolean isTopLevel, String name, String url) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mName = name
        mUrl = url
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return new HotstarSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000001
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFFEDF00
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF285244
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF082537
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new HotstarPuppetIterator()
        if (mUrl == null) {
            MAIN_CATEGORIES.each { category ->
                HotstarPuppet adding = new HotstarPuppet(this, category.top, category.name, category.url)
                if (category.name == "Featured") {
                    for (Puppet p : adding.getChildren()) {
                        children.add(p)
                    }
                } else {
                    children.add(adding)
                }
            }
        } else {
            JSONObject json = new JSONObject(new URL(mUrl).getText()).getJSONObject("resultObj")
            JSONArray items
            if (mName.startsWith("Movies") || mName.startsWith("TV Shows") || mName == "Search") {
                items = json.getJSONObject("response").getJSONArray("docs")
            } else {
                switch (mName) {
                    case "Featured":
                    case "Sports - Live":
                        items = json.getJSONArray("contentList")
                        break
                    default:
                        items = json.getJSONArray("categoryList").getJSONObject(0).getJSONArray("categoryList")
                        break

                }
            }
            for (int i = 0; items != null && i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                if (mName.startsWith("Movies")) {
                    children.add(new HotstarSourcesPuppet(this, item))
                } else if (mName.startsWith("TV Shows")) {
                    children.add(new HotstarTVShowsPuppet(this, item))
                } else {
                    switch (mName) {
                        case "Movie Collections":
                            children.add(new HotstarMovieCollectionsPuppet(this, item))
                            break
                        case "Sports":
                            children.add(new HotstarSportsPuppet(this, item))
                            break
                        case "Sports - New":
                            children.add(new HotstarSportsEventPuppet(this, item))
                            break
                        case "TV Channels":
                            children.add(new HotstarChannelsPuppet(this, item))
                            break
                        default:
                            children.add(new HotstarSourcesPuppet(this, item))
                    }
                }
            }
            if (mName.startsWith("Movies") || mName.startsWith("TV Shows")) {
                Matcher matcher = mUrl =~ /startIndex=(.+?)&maxResult=(.+?)&/
                if (matcher.find()) {
                    String oldStartIndex = matcher.group(1)
                    int maxResult = Integer.parseInt(matcher.group(2))
                    int newStartIndex = Integer.parseInt(oldStartIndex) + maxResult
                    String url = mUrl.replace("startIndex=" + oldStartIndex, "startIndex="+Integer.toString(newStartIndex))
                    url = url.replace("maxResult=" + maxResult, "maxResult=50")
                    children.add(new HotstarPuppet(this, false, mName + " >", url))
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
        return "India"
    }

    @Override
    String getShortDescription() {
        return mParent == null ? "TV Shows, Movies & Live Cricket Matches Online" : null
    }

    @Override
    String getImageUrl() {
        return "http://techtalks.ideacellular.com/wp-content/uploads/2016/07/1.jpeg"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://media-starag.startv.in/social/hotstar.jpg"
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

    def static class HotstarPuppetIterator extends PuppetIterator {

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

    def static class HotstarSearchesPuppet extends HotstarPuppet implements SearchesPuppet {

        static final String URL_TEMPLATE = "http://search.hotstar.com/AVS/besc?action=SearchContents&channel=PCTV&facets=type%3Blanguage%3Bgenre&maxResult=10&startIndex=0&type=MOVIE,SERIES,SPORT,SPORT_LIVE&query="

        public HotstarSearchesPuppet(ParentPuppet parent) {
            super(parent, false, "Search", URL_TEMPLATE)
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mUrl = URL_TEMPLATE + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }

    def static class HotstarJSONParentPuppet implements ParentPuppet {

        def ParentPuppet mParent
        def JSONObject mItem

        HotstarJSONParentPuppet(ParentPuppet parent, JSONObject item) {
            mParent = parent
            mItem = item
        }

        @Override
        PuppetIterator getChildren() {
            return null
        }

        @Override
        boolean isTopLevel() {
            return false
        }

        @Override
        String getName() {
            return mItem.getString("contentTitle")
        }

        @Override
        String getCategory() {
            return null
        }

        @Override
        String getShortDescription() {
            try {
                return mItem.getString("contentSubtitle")
            } catch (Exception ex) {
                try {
                    return mItem.getString("shortDescription")
                } catch (Exception exx) {
                    try {
                        return mItem.getString("longDescription")
                    } catch (Exception exxx) {
                        try {
                            return mItem.getString("description")
                        } catch (ignore) {}
                    }
                }
            }
            return null
        }

        @Override
        String getImageUrl() {
            String code = "vl"
            try {
                try {
                    if (mItem.getString("contentType") == "SPORT") {
                        code = "hs"
                    }
                } catch (ignore) {}
                String urlPictures = mItem.getString("urlPictures")
                if (urlPictures.length() > 2) {
                    return sprintf('http://media0-starag.startv.in/r1/thumbs/PCTV/%1$s/%2$s/PCTV-%2$s-%3$s.jpg', urlPictures[-2..-1], urlPictures, code)
                }
            } catch (ignore) {}
            return mParent.getImageUrl()
        }

        @Override
        String getBackgroundImageUrl() {
            return mParent.getBackgroundImageUrl()
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
    }

    def class HotstarChannelsPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetArrayContentList&channel=PCTV&categoryId="

        def long mId

        HotstarChannelsPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("contentList")
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                children.add(new HotstarChannelShowsPuppet(this, item))
            }
            return children
        }
    }

    def class HotstarChannelShowsPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetAggregatedContentDetails&channel=PCTV&contentId="

        def long mId

        HotstarChannelShowsPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("contentId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("contentInfo")
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                children.add(new HotstarSeasonsPuppet(this, item))
            }
            return children
        }
    }

    def class HotstarSportsPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&channel=PCTV&categoryId="

        def long mId

        HotstarSportsPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("categoryList").getJSONObject(0).getJSONArray("categoryList")
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                if (item.getString("categoryType") == "TYPE_NODE") {
                    children.add(new HotstarSportsPuppet(this, item))
                } else {
                    children.add(new HotstarSportsEventPuppet(this, item))
                }
            }
            return children
        }
    }

    def class HotstarSportsEventPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetArrayContentList&channel=PCTV&categoryId="

        def long mId

        HotstarSportsEventPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONObject json = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj")
            try {
                JSONArray items = json.getJSONArray("contentList")
                for (int i = 0; items != null && i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i)
                    children.add(new HotstarSourcesPuppet(this, item))
                }
            } catch (Exception ex) {
                try {
                    JSONArray items = json.getJSONArray("categoryList").getJSONObject(0).getJSONArray("categoryList")
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i)
                        children.add(new HotstarSportsEventPuppet(this, item))
                    }
                } catch (ignore) {
                }
            }
            return children
        }
    }

    def class HotstarMovieCollectionsPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetArrayContentList&channel=PCTV&categoryId="

        def long mId

        HotstarMovieCollectionsPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("contentList")
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                children.add(new HotstarSourcesPuppet(this, item))
            }
            return children
        }
    }

    def class HotstarTVShowsPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetAggregatedContentDetails&channel=PCTV&contentId="

        def long mId

        HotstarTVShowsPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("contentId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("contentInfo")
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                children.add(new HotstarSeasonsPuppet(this, item))
            }
            return children
        }
    }

    def class HotstarSeasonsPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&channel=PCTV&categoryId="

        def long mId

        HotstarSeasonsPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("categoryList")
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i)
                children.add(new HotstarSeasonsEpisodesPuppet(this, item))
            }
            return children
        }
    }

    def class HotstarSeasonsEpisodesPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetCatalogueTree&channel=PCTV&categoryId="

        def long mId

        HotstarSeasonsEpisodesPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("categoryList")
            for (int i = 0; i < items.length(); i++) {
                JSONArray itemsInner = items.getJSONObject(i).getJSONArray("categoryList");
                for (int j = 0; j < itemsInner.length(); j++) {
                    JSONObject item = itemsInner.getJSONObject(j)
                    children.add(new HotstarEpisodesPuppet(this, item))
                }
            }
            return children
        }
    }

    def class HotstarEpisodesPuppet extends HotstarJSONParentPuppet {

        static final URL_TEMPLATE = "http://account.hotstar.com/AVS/besc?action=GetArrayContentList&channel=PCTV&categoryId="

        def long mId
        def String mName

        HotstarEpisodesPuppet(ParentPuppet parent, JSONObject item) {
            super(parent, item)
            mId = item.getLong("categoryId")
            mName = item.getString("categoryName")
        }

        @Override
        PuppetIterator getChildren() {
            PuppetIterator children = new HotstarPuppetIterator()
            try {
                JSONArray items = new JSONObject(new URL(URL_TEMPLATE + mId).getText()).getJSONObject("resultObj").getJSONArray("contentList")
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i)
                    children.add(new HotstarSourcesPuppet(this, item))
                }
            } catch (ignore) {
            }
            return children
        }

        @Override
        String getName() {
            return mName
        }
    }

    def static class HotstarSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def JSONObject mItem
        def long mId

        HotstarSourcesPuppet(parent, JSONObject item) {
            mParent = parent
            mItem = item
            mId = item.getLong("contentId")
        }

        @Override
        String getPublicationDate() {
            long publicationDate = 0

            try {
                publicationDate = mItem.getLong("broadcastDate")
            } catch (ignore) {
            }
            return publicationDate > 0 ? new SimpleDateFormat("MMMM d, yyyy").format(publicationDate) : null
        }

        @Override
        long getDuration() {
            long duration = 0
            try {
                duration = mItem.getLong("duration")
            } catch (ignore) {
            }
            return duration > 0 ? duration : -1
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new HotstarSourceIterator()
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
            try {
                int episodeNumber = mItem.getInt("episodeNumber")
                if (episodeNumber != 0) {
                    return episodeNumber + " - " + mItem.getString("episodeTitle") + ": " + mItem.getString("contentTitle")
                }
            } catch (ignore) {
            }
            return mItem.getString("contentTitle")
        }

        @Override
        String getCategory() {
            return mItem.getString("genre")
        }

        @Override
        String getShortDescription() {
            try {
                return mItem.getString("contentSubtitle")
            } catch (ignore) {
            }
            return mParent.getShortDescription()
        }

        @Override
        String getImageUrl() {
            String code = "vl"
            try {
                try {
                    if (mItem.getString("contentType") == "SPORT") {
                        code = "hs"
                    }
                } catch (ignore) {}
                String urlPictures = mItem.getString("urlPictures")
                if (urlPictures.length() > 2) {
                    return sprintf('http://media0-starag.startv.in/r1/thumbs/PCTV/%1$s/%2$s/PCTV-%2$s-%3$s.jpg', urlPictures[-2..-1], urlPictures, code)
                }
            } catch (ignore) {}
            return mParent.getImageUrl()
        }

        @Override
        String getBackgroundImageUrl() {
            return mParent.getBackgroundImageUrl()
        }

        @Override
        boolean isUnavailableIn(String region) {
            return region != 'in'
        }

        @Override
        String getPreferredRegion() {
            return 'in'
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
        String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class HotstarSourceIterator implements SourcesPuppet.SourceIterator {

            static final QUERY_SOURCE = "http://getcdn.hotstar.com/AVS/besc?action=GetCDN&asJson=Y&channel=PCTV&type=VOD&id="

            def SourcesPuppet.SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {
                    mSource = new SourcesPuppet.SourceDescription()
                    String content = new URL(QUERY_SOURCE + HotstarSourcesPuppet.this.mId).getText(
                            requestProperties: [
                                    Accept: "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8', 'Accept-Encoding':'gzip, deflate, sdch', 'Connection':'keep-alive', 'User-Agent':'AppleCoreMedia/1.0.0.12B411 (iPhone; U; CPU OS 8_1 like Mac OS X; en_gb)"
                            ]
                    )
                    JSONObject json = new JSONObject(content).getJSONObject("resultObj")
                    mSource.url = json.getString("src")
                            .replace("http://", "https://")
                            .replace("/z/", "/i/")
                            .replace("manifest.f4m", "master.m3u8")
                    mSource.width = json.getString("width")
                    mSource.height = json.getString("height")
                    return true
                }
                return false
            }

            @Override
            SourcesPuppet.SourceDescription next() {
                return mSource
            }

            @Override
            void remove() {

            }
        }
    }
}
package tv.puppetmaster.extra

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

class CBCPlusPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final boolean USE_PROXY = true
    static final String PROXY = "http://www.proxyforme.ml/browse.php?b=0&f=norefer&u="

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl

    CBCPlusPuppet() {
        this(
                null,
                true,
                "CBC+",
                "TV shows, movies, kids TV and documentaries from Canada's public broadcaster",
                null,
                null,
                "https://api-cbc.cloud.clearleap.com/cloffice/client/web/browse/"
        )
        /*def CookieManager cookieManager = (CookieManager) CookieHandler.getDefault()
        if (!cookieManager) {
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            CookieHandler.setDefault(cookieManager)
        }
        def CookieStore cookieStore = cookieManager.getCookieStore()
        def Connection.Response res = Jsoup.connect("http://watch.cbc.ca/")
                .userAgent("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Safari/537.36")
                .execute()
        cookieStore.add(new URI(mUrl), HttpCookie.parse("JSESSION=" + res.cookies().get("JSESSION")).get(0))
        def String session = cookieStore.get(new URI(mUrl)).get(0)*/
    }

    CBCPlusPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String url) {
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
        return new CBCPlusSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFE21A21
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
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
    PuppetIterator getChildren() {
        PuppetIterator children = new CBCPlusPuppetIterator()
        def String page
        if (USE_PROXY) {
            println PROXY + URLEncoder.encode(mUrl, "UTF-8")
            println new URL(PROXY + URLEncoder.encode(mUrl, "UTF-8")).getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36'])
            page = new URL(PROXY + URLEncoder.encode(mUrl, "UTF-8")).getText(requestProperties: [
                    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36',
                    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
                    'Accept-Encoding': 'gzip, deflate, sdch',
                    'Accept-Language': 'en-US,en;q=0.8',
                    'Connection': 'keep-alive',
                    'Cookie': 's=ets6enlsnn3nupt6q9erjomaa1; c[clearleap.com][/cloffice/][JSESSIONID]=A0E7A502F74B1B8E1E2004206D810E96.ip-10-93-91-239',
                    'Upgrade-Insecure-Requests': '1',
            ])
        } else {
            page = new URL(mUrl).getText()
        }
        def Document feed = Jsoup.parse(page
                .replace("<media:", "<media_")
                .replace("</media:", "</media_")
                .replace("clearleap:", "clearleap_")
                .replace("</clearleap:", "</clearleap_")
                , "", Parser.xmlParser())

        feed.select("item").each {
            def String itemType =  it.select("clearleap_itemtype").first().text()
            def String name = it.select("title").first().text()
            def String url =  it.select("link").first().text()
            if (itemType == "media") {
                children.add(new CBCPlusSourcesPuppet(
                        this,
                        name,
                        it.select("description").first().text(),
                        it.select("media_credit[role='releaseDate']").first().text(),
                        1000 * Long.parseLong(it.select("media_content[medium='video']").first().attr("duration")),
                        it.select("media_thumbnail").first().attr("url"),
                        it.select("media_thumbnail").last().attr("url"),
                        it.select("media_content[medium='video']").first().attr("url")
                ))
            } else if (mParent != null || !(itemType in ["SEARCH", "IDENTITY"])) {
                ParentPuppet items = new CBCPlusPuppet(
                        this,
                        mParent == null && name != "Featured",
                        name,
                        it.select("description") ? it.select("description").first().text() : null,
                        it.select("media_thumbnail") ? it.select("media_thumbnail").first().attr("url") : null,
                        it.select("media_thumbnail") ? it.select("media_thumbnail").last().attr("url") : null,
                        url
                )
                if (name == "Featured") {
                    for (Puppet c : items.getChildren()) {
                        children.add(c)
                    }
                } else {
                    children.add(items)
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
        return "Public Service"
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return mImageUrl != null ? mImageUrl : "http://rlv.zcache.ca/cbc_radio_canada_gem_poster-r6444b9c96ae14eedb62b6a8c7f18b27e_w10_8byvr_324.jpg"
    }

    @Override
    String getBackgroundImageUrl() {
        return mBackgroundImageUrl != null ? mBackgroundImageUrl : "http://www.cbc.ca/bc/community/blog/photo/Laptop%20Decal_V2.jpg"
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

    def static class CBCPlusPuppetIterator extends PuppetIterator {

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

    def static class CBCPlusSearchesPuppet extends CBCPlusPuppet implements SearchesPuppet {

        static final String URL_TEMPLATE = "https://api-cbc.cloud.clearleap.com/cloffice/client/web/search/&query="

        public CBCPlusSearchesPuppet(ParentPuppet parent) {
            super(parent, false, "Search", "Search CBC+", null, null, URL_TEMPLATE)
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mUrl = URL_TEMPLATE + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }


    def static class CBCPlusSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mPublicationDate
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl
        def String mUrl

        CBCPlusSourcesPuppet(ParentPuppet parent, String name, String description, String publicationDate, long duration, String imageUrl, String backgroundImageUrl, String url) {
            mParent = parent
            mName = name
            mDescription = description
            mPublicationDate = publicationDate
            mDuration = duration
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
            mUrl = url
        }

        @Override
        String getPublicationDate() {
            return mPublicationDate.split("T")[0]
        }

        @Override
        long getDuration() {
            mDuration
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new CBCPlusSourceIterator()
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
            mName
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
        PuppetIterator getRelated() {
            return null
        }

        @Override
        String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class CBCPlusSourceIterator implements SourcesPuppet.SourceIterator {

            def SourcesPuppet.SourceDescription mSource = null

            @Override
            boolean hasNext() {
                if (mSource == null) {
                    mSource = new SourcesPuppet.SourceDescription()
                    mSource.url = mUrl
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
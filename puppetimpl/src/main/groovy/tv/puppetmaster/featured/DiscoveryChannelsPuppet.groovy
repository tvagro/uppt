package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.puppetmaster.data.i.*

import java.text.SimpleDateFormat
import java.util.regex.Matcher

class DiscoveryChannelsPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def CHANNELS = [
            [
                    name:       "Discovery Channel",
                    baseUrl:    "https://www.discoverygo.com",
                    page:       "/free-preview-on-discovery/",
            ],
            [
                    name:       "Kids",
                    baseUrl:    "http://discoverykids.com",
                    page:       "/videos/",
            ],
            [
                    name:       "Animal Planet",
                    baseUrl:    "https://www.animalplanetgo.com",
                    page:       "/free-preview-on-animal-planet/",
            ],
            [
                    name:       "Science",
                    baseUrl:    "https://www.sciencechannelgo.com",
                    page:       "/free-preview-on-science/",
            ],
            [
                    name:       "Investigation",
                    baseUrl:    "https://www.investigationdiscoverygo.com",
                    page:       "/free-preview-on-id/",
            ],
    ]

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl

    DiscoveryChannelsPuppet() {
        this(
                null,
                true,
                "Discovery Channel",
                "Full episodes and short videos from Discovery.",
                "https://media.licdn.com/media/AAEAAQAAAAAAAAR_AAAAJDQzYTEzNjU4LWM2MGYtNDMzZS1hNDM4LTBiOWNjNmMxNTk2MA.png",
                "http://www.troika.tv/wp-content/uploads/Discovery.jpg",
        )
    }

    DiscoveryChannelsPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl) {
        mParent = parent
        mIsTopLevel = isTopLevel
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
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF245188
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFF89000
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF245188
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF245188
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    Puppet.PuppetIterator getChildren() {
        Puppet.PuppetIterator children = new DiscoveryChannelsPuppetIterator()

        CHANNELS.each {
            ParentPuppet p = new DiscoveryChannelsScraperPuppet(
                    this,
                    true,
                    it.name,
                    null,
                    mImageUrl,
                    mBackgroundImageUrl,
                    it.baseUrl,
                    it.page,
            )
            if (it.name == "Discovery Channel") {
                for (Puppet c : p.getChildren()) {
                    children.add(c)
                }
            } else {
                children.add(p)
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

    class DiscoveryChannelsScraperPuppet extends DiscoveryChannelsPuppet {

        def String mBaseUrl
        def String mPage

        DiscoveryChannelsScraperPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String baseUrl, String page) {
            super(parent, isTopLevel, name, description, imageUrl, backgroundImageUrl)
            mBaseUrl = baseUrl
            mPage = page
        }

        @Override
        Puppet.PuppetIterator getChildren() {
            Puppet.PuppetIterator children = new DiscoveryChannelsPuppetIterator()

            if (mBaseUrl == "http://discoverykids.com" && mPage == "/videos/") {

                Document document = Jsoup.connect(mBaseUrl + mPage).get()
                Elements categories = document.select(".dropdown-menu a")
                for (Element c in categories) {
                    String page = sprintf('/app/themes/discoverykids/ajax-load-more/ajax-load-more.php?postType=video&taxonomyName=category&taxonomyTerm=%s&orderBy=post_date&order=DESC&device=computer&numPosts=24&onScroll=false&pageNumber=1', c.attr("data-category"))
                    children.add(new DiscoveryKidsScraperPuppet(
                            this,
                            false,
                            c.text(),
                            null,
                            mImageUrl,
                            mBackgroundImageUrl,
                            mBaseUrl,
                            page,
                    ))
                }

            } else {

                def CookieManager cookieManager = (CookieManager) CookieHandler.getDefault()
                if (!cookieManager) {
                    cookieManager = new CookieManager();
                    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
                    CookieHandler.setDefault(cookieManager)
                }
                def CookieStore cookieStore = cookieManager.getCookieStore()

                def Connection.Response res = Jsoup.connect(mBaseUrl + mPage)
                        .userAgent("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Safari/537.36")
                        .execute()
                cookieStore.add(new URI(mBaseUrl + mPage), HttpCookie.parse("session=" + res.cookies().get("session")).get(0))
                def String session = cookieStore.get(new URI(mBaseUrl + mPage)).get(0)
                Document document = res.parse()

                for (Element item : document.select(".content-item")) {
                    def String json = item.attr("data-json")
                    if (json) {
                        children.add(new DiscoveryChannelsSourcesPuppet(this, new JSONObject(item.attr("data-json")), mBaseUrl, session))
                    }
                }

            }

            return children
        }

    }

    class DiscoveryChannelsSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mPublicationDate
        def String mDuration
        def String mImageUrl

        def JSONObject mItem
        def String mBaseUrl
        def String mSession

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        DiscoveryChannelsSourcesPuppet(ParentPuppet parent, JSONObject item, String baseUrl, session) {
            mParent = parent
            mItem = item
            mBaseUrl = baseUrl
            mSession = session
        }

        @Override
        String getPublicationDate() {
            def String dateString = mItem.getJSONArray("networks").getJSONObject(0).getString("airDate")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            Date publicationDate = sdf.parse(dateString)
            return new SimpleDateFormat("d MMM yyyy").format(publicationDate)
        }

        @Override
        long getDuration() {
            return mItem.getInt("duration") * 1000l
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new DiscoveryChannelsSourceIterator()
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
            return getCategory() + ": " + mItem.getString("name")
        }

        @Override
        String getCategory() {
            return mItem.getJSONObject("show").getString("name")
        }

        @Override
        String getShortDescription() {
            return mItem.getJSONObject("description").getString("standard")
        }

        @Override
        String getImageUrl() {
            JSONArray images = mItem.getJSONObject("image").getJSONArray("links")
            for (int i = 0; i < images.length(); i++) {
                JSONObject j = images.getJSONObject(i)
                if (j.getString("rel") == "1x1") {
                    return j.getString("href").replace("{width}", "300")
                }
            }
            return DiscoveryChannelsPuppet.this.mImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            JSONArray images = mItem.getJSONObject("image").getJSONArray("links")
            for (int i = 0; i < images.length(); i++) {
                JSONObject j = images.getJSONObject(i)
                if (j.getString("rel") == "16x9") {
                    return j.getString("href").replace("{width}", "1024")
                }
            }
            return DiscoveryChannelsPuppet.this.mBackgroundImageUrl
        }

        @Override
        boolean isUnavailableIn(String region) {
            return region != 'us'
        }

        @Override
        String getPreferredRegion() {
            return 'us'
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
            return mParent != null ? mParent.getChildren() : null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        class DiscoveryChannelsSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourcesPuppet.SourceDescription>()

                    JSONArray links = mItem.getJSONArray("links")
                    String url
                    for (int i = 0; i < links.length(); i++) {
                        def JSONObject json = links.getJSONObject(i)
                        if (json.getString("rel").startsWith("play_")) {
                            url = json.getString("href")
                            break
                        }
                    }
                    if (url) {
                        url = mBaseUrl + "/api/v1/proxy?url=" + url
                        def String page = new URL(url).getText(
                                requestProperties: [
                                        'Cookie': 'session=' + mSession,
                                        'Referer': mBaseUrl,
                                ]
                        )
                        def JSONObject json = new JSONObject(page)
                        def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                        source.url = json.getString("streamUrl")
                        mSources.add(source)
                        JSONArray captions = json.getJSONArray("captions")
                        for (int j = 0; j < captions.length(); j++) {
                            JSONObject caption = captions.getJSONObject(j)

                            SourcesPuppet.SubtitleDescription subtitle = new SourcesPuppet.SubtitleDescription()
                            subtitle.url = caption.getString("fileUrl")
                            subtitle.locale = caption.getString("fileLang")
                            subtitle.mime = caption.getString("fileType")
                            mSubtitles.add(subtitle)
                        }}
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

    class DiscoveryKidsScraperPuppet extends DiscoveryChannelsScraperPuppet {
        DiscoveryKidsScraperPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String baseUrl, String page) {
            super(parent, isTopLevel, name, description, imageUrl, backgroundImageUrl, baseUrl, page)
        }

        @Override
        Puppet.PuppetIterator getChildren() {
            Puppet.PuppetIterator children = new DiscoveryChannelsPuppetIterator()

            Document document = Jsoup.connect(mBaseUrl + mPage).get()
            Elements items = document.select(".thumbnail.super-item")
            for (Element item in items) {
                children.add(new DiscoveryKidsSourcesPuppet(
                        item.select(".caption").first().text(),
                        item.select("img").first().absUrl("src"),
                        item.select("a").first().absUrl("href"),
                ))
            }

            return children
        }
    }

    class DiscoveryKidsSourcesPuppet implements SourcesPuppet {

        def String mName
        def String mImageUrl
        def String mUrl

        DiscoveryKidsSourcesPuppet(String name, String imageUrl, String url) {
            mName = name
            mImageUrl = imageUrl
            mUrl = url
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return 0
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new DiscoveryKidsSourceIterator()
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
            return "Kids"
        }

        @Override
        String getShortDescription() {
            return null
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
            return mParent != null ? mParent.getChildren() : null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        class DiscoveryKidsSourceIterator implements SourcesPuppet.SourceIterator {

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {

                    mSources = new ArrayList<SourcesPuppet.SourceDescription>()

                    def Document document = Jsoup.connect(mUrl).get()
                    def Element video = document.select("video source").first()
                    if (video) {
                        // Embedded video
                        def String url = video.absUrl("src")
                        def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                        source.url = url
                        mSources.add(source)
                    } else {
                        // Facebook video
                        def String url = document.select(".video-container").first().select("[src]").first().absUrl("src")
                        def String page = new URL(url).getText(requestProperties: ['User-Agent': 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Safari/537.36'])
                        def Matcher matcher = page =~ /"params","(.+?)"/
                        if (matcher.find()) {
                            def JSONObject json = new JSONObject(URLDecoder.decode(unescapeJavaString(matcher.group(1)), "UTF-8"))
                            def JSONObject data = json.getJSONObject("video_data_preference")
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    def JSONObject d = data.getJSONObject(Integer.toString(i + 1))
                                    ["hd_src_no_ratelimit", "hd_src", "sd_src_no_ratelimit", "sd_src"].each {
                                        def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                                        source.url = d.getString(it)
                                        mSources.add(source)
                                    }
                                } catch (ignore) {
                                }
                            }
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

            /**
             * Via http://stackoverflow.com/a/19067245/6716223
             * Unescapes a string that contains standard Java escape sequences.
             * <ul>
             * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
             * BS, FF, NL, CR, TAB, double and single quote.</li>
             * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
             * specification (0 - 377, 0x00 - 0xFF).</li>
             * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode character.</li>
             * </ul>
             *
             * @param st
             *            A string optionally containing standard java escape sequences.
             * @return The translated string.
             */
            public String unescapeJavaString(String st) {

                StringBuilder sb = new StringBuilder(st.length());

                for (int i = 0; i < st.length(); i++) {
                    char ch = st.charAt(i);
                    if (ch == '\\') {
                        char nextChar = (i == st.length() - 1) ? '\\' : st
                                .charAt(i + 1);
                        // Octal escape?
                        if (nextChar >= '0' && nextChar <= '7') {
                            String code = "" + nextChar;
                            i++;
                            if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                    && st.charAt(i + 1) <= '7') {
                                code += st.charAt(i + 1);
                                i++;
                                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                        && st.charAt(i + 1) <= '7') {
                                    code += st.charAt(i + 1);
                                    i++;
                                }
                            }
                            sb.append((char) Integer.parseInt(code, 8));
                            continue;
                        }
                        switch (nextChar) {
                            case '\\':
                                ch = '\\';
                                break;
                            case 'b':
                                ch = '\b';
                                break;
                            case 'f':
                                ch = '\f';
                                break;
                            case 'n':
                                ch = '\n';
                                break;
                            case 'r':
                                ch = '\r';
                                break;
                            case 't':
                                ch = '\t';
                                break;
                            case '\"':
                                ch = '\"';
                                break;
                            case '\'':
                                ch = '\'';
                                break;
                        // Hex Unicode: u????
                            case 'u':
                                if (i >= st.length() - 5) {
                                    ch = 'u';
                                    break;
                                }
                                int code = Integer.parseInt(
                                        "" + st.charAt(i + 2) + st.charAt(i + 3)
                                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
                                sb.append(Character.toChars(code));
                                i += 5;
                                continue;
                        }
                        i++;
                    }
                    sb.append(ch);
                }
                return sb.toString();
            }
        }
    }

    static class DiscoveryChannelsPuppetIterator extends Puppet.PuppetIterator {

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
}
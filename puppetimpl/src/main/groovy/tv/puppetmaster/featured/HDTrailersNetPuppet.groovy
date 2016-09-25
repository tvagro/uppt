package tv.puppetmaster.featured

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

public class HDTrailersNetPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    def ParentPuppet mParent
    def String mBaseUrl
    def String mName
    def String mDescription
    def String mUrl
    def String mImageUrl
    def String mBackgroundImageUrl
    def boolean mIsTopLevel

    public HDTrailersNetPuppet() {
        this(
                null,
                "http://www.hd-trailers.net/",
                "HD-Trailers.net",
                "View the latest movie trailers for many current and upcoming releases (.mp4 format).",
                "top-movies/",
                "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                true
        )
    }

    private HDTrailersNetPuppet(ParentPuppet parent, String baseUrl, String name, String description, String url, String imageUrl, String backgroundImageUrl, boolean isTopLevel) {
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
        HDTrailersNetIterator children = new HDTrailersNetIterator(this, mBaseUrl, mUrl)
        if (mParent == null) {
            children.add(new HDTrailersNetPuppet(
                    this,
                    "http://www.hd-trailers.net/",
                    "Most watched",
                    "",
                    "most-watched/",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    true
            ))
            children.add(new HDTrailersNetPuppet(
                    this,
                    "http://www.hd-trailers.net/",
                    "Opening this week",
                    "",
                    "opening-this-week/",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    true
            ))
            children.add(new HDTrailersNetPuppet(
                    this,
                    "http://www.hd-trailers.net/",
                    "Coming soon",
                    "",
                    "coming-soon/",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    true
            ))
            children.add(new HDTrailersNetPuppet(
                    this,
                    "http://www.hd-trailers.net/",
                    "Latest",
                    "",
                    "page/1/",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    "https://raw.githubusercontent.com/dersphere/plugin.video.hdtrailers_net/master/icon.png",
                    true
            ))
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
        return mParent == null ? "Entertainment" : mParent.getName()
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
        return null
    }

    @Override
    SettingsPuppet getSettingsProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF0066CC
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF0066CC
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF0066CC
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

    def class HDTrailersNetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def String mBaseUrl
        def String mUrl
        def ArrayList<Puppet> mPuppets = null
        def ArrayList<Puppet> mCategoryPuppets = new ArrayList<>()
        def int currentIndex = 0

        public HDTrailersNetIterator(ParentPuppet parent, String baseUrl, String url) {
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

                Document document = Jsoup.connect(mBaseUrl + mUrl).get()

                def items = document.select("td.indexTableTrailerImage")
                for (Element item in items) {
                    Element a = item.getElementsByTag("a")[0]
                    String url = a.attr("href")
                    try {
                        Element img = a.getElementsByTag("img")[0]
                        String title = img.attr("alt").trim()
                        String imageUrl = img.absUrl("src")

                        mPuppets.add(new HDTrailersNetItemPuppet(
                                mParent,
                                url.startsWith("/") ? mBaseUrl + url : url,
                                title,
                                imageUrl
                        ))
                    } catch (ignore) {
                        // TODO: what's happening here?
                    }
                }
                items = document.select("a.startLink")
                for (Element item in items) {
                    mPuppets.add(new HDTrailersNetPuppet(
                            mParent,
                            "http://www.hd-trailers.net/",
                            item.text(),
                            "",
                            item.attr("href").substring(1), // Remove starting /
                            "http://static.hd-trailers.net/images/mobile/next.png",
                            "http://static.hd-trailers.net/images/mobile/next.png",
                            false
                    ))
                }
            }
            return currentIndex < (mPuppets.size() + mCategoryPuppets.size())
        }

        @Override
        void add(Puppet puppet) {
            mCategoryPuppets.add(puppet)
        }

        @Override
        Puppet next() {
            if (currentIndex < mPuppets.size()) {
                return mPuppets.get(currentIndex++)
            } else {
                int categoryIndex = currentIndex - mPuppets.size()
                currentIndex++
                return mCategoryPuppets.get(categoryIndex)
            }
        }

        @Override
        void remove() {

        }
    }

    def class HDTrailersNetItemPuppet implements ParentPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mImageUrl

        public HDTrailersNetItemPuppet(ParentPuppet parent, String url, String name, String imageUrl) {
            mParent = parent
            mUrl = url
            mName = name
            mImageUrl = imageUrl
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
            return null
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
            return null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        @Override
        PuppetIterator getChildren() {
            return new HDTrailersNetVideosIterator(this, mUrl)
        }

        @Override
        boolean isTopLevel() {
            return false
        }

        def class HDTrailersNetVideosIterator extends PuppetIterator {

            def ParentPuppet mParent
            def String mUrl
            def int currentIndex = 0

            def ArrayList<Puppet> mPuppets = null

            public HDTrailersNetVideosIterator(ParentPuppet parent, String url) {
                mParent = parent
                mUrl = url
            }

            @Override
            boolean hasNext() {
                if (mPuppets == null) {
                    mPuppets = new ArrayList<>()
                    Document document = Jsoup.connect(mUrl).get()
                    String imageUrl = document.select(".topTableImage img").first().absUrl("src")
                    def items = document.select("table.bottomTable tr")
                    for (Element item in items) {
                        try {
                            HDTrailersNetSourcesPuppet sourcesPuppet = new HDTrailersNetSourcesPuppet()
                            sourcesPuppet.setParent(mParent)
                            sourcesPuppet.setPublicationDate(item.select(".bottomTableDate").text())
                            sourcesPuppet.setName(item.select(".bottomTableName").text())
                            sourcesPuppet.setImageUrl(imageUrl)
                            sourcesPuppet.setBackgroundImageUrl(imageUrl)
                            def urls = []
                            Elements aTags = item.select(".bottomTableResolution a")
                            for (Element a in aTags) {
                                String url = a.attr("href").trim()
                                if (url.endsWith(".mp4") || url.endsWith(".flv")) {
                                    urls << url
                                }
                            }
                            sourcesPuppet.setUrls(urls.reverse() as String[])
                            if (urls.size() > 0) {
                                add(sourcesPuppet)
                            }
                        } catch (all) {
                            // Probably a non-source row
                        }
                    }
                }
                return currentIndex < mPuppets.size()
            }

            @Override
            Puppet next() {
                return mPuppets.get(currentIndex++)
            }

            @Override
            void add(Puppet puppet) {
                mPuppets.add(puppet)
            }

            @Override
            void remove() {

            }
        }
    }

    def static class HDTrailersNetSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def mName
        def mImageUrl
        def mBackgroundImageUrl
        def mPublicationDate
        def String[] mUrls

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        void setPublicationDate(String publicationDate) {
            mPublicationDate = publicationDate
        }

        @Override
        String getPublicationDate() {
            return mPublicationDate
        }

        @Override
        long getDuration() {
            return -1
        }

        void setUrls(String[] urls) {
            mUrls = urls
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new HDTrailersNetSourceIterator(mUrls)
        }

        @Override
        boolean isLive() {
            return false
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return mSubtitles
        }

        void setName(String name) {
            mName = name
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
            return null
        }

        void setImageUrl(String imageUrl) {
            mImageUrl = imageUrl
        }

        @Override
        String getImageUrl() {
            return mImageUrl
        }

        void setBackgroundImageUrl(String backgroundImageUrl) {
            mBackgroundImageUrl = backgroundImageUrl
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

        void setParent(ParentPuppet parent) {
            mParent = parent
        }

        @Override
        ParentPuppet getParent() {
            return mParent
        }

        @Override
        public PuppetIterator getRelated() {
            return mParent != null ? mParent.getChildren() : null
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class HDTrailersNetSourceIterator implements SourcesPuppet.SourceIterator {

            def String[] mUrls
            def int currentIndex = 0

            public HDTrailersNetSourceIterator(String[] urls) {
                mUrls = urls
            }

            @Override
            boolean hasNext() {
                currentIndex < mUrls.length
            }

            @Override
            SourceDescription next() {
                SourceDescription sourceDescription = new SourceDescription()
                sourceDescription.url = mUrls[currentIndex++]
                return sourceDescription
            }

            @Override
            void remove() {

            }
        }
    }
}
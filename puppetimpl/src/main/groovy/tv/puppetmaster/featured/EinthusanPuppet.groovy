package tv.puppetmaster.featured

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

class EinthusanPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl

    EinthusanPuppet() {
        this(
                null,
                true,
                "Einthusan",
                "Watch Hindi, Tamil movies",
                null,
                null,
                "https://api-cbc.cloud.clearleap.com/cloffice/client/web/browse/"
        )
    }

    EinthusanPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundImageUrl, String url) {
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
        return new EinthusanSearchesPuppet(this)
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
        PuppetIterator children = new EinthusanPuppetIterator()

        if (mParent == null) {
            ['Hindi', 'Tamil', 'Telugu', 'Malayalam', 'Kannada', 'Bengali', 'Marathi', 'Punjabi'].each {
                /*HttpURLConnection connection = makeURL('post.groovy').toURL().openConnection()
                def url = new URL('http://www.einthusan.com/webservice/discovery.php')
                def connection = url.openConnection()
                connection.setRequestMethod("POST")
                'http://www.einthusan.com/webservice/discovery.php'.toURL()
                ['A-Z', 'Years', 'Actors', 'Director', 'Recent', 'Top Rated', 'Featured', 'Blu-Ray', 'Music Video'].each {

                }*/
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
        return mImageUrl != null ? mImageUrl : "https://superrepo.org/static/images/icons/original/plugin.video.einthusan.png.pagespeed.ce.9F6uJE1NUo.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return mBackgroundImageUrl != null ? mBackgroundImageUrl : "http://s1.dmcdn.net/JNqpo/1280x720-FYQ.jpg"
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

    def static class EinthusanPuppetIterator extends PuppetIterator {

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

    def static class EinthusanSearchesPuppet extends EinthusanPuppet implements SearchesPuppet {

        static final String URL_TEMPLATE = "https://api-cbc.cloud.clearleap.com/cloffice/client/web/search/&query="

        public EinthusanSearchesPuppet(ParentPuppet parent) {
            super(parent, false, "Search", "Search CBC+", null, null, URL_TEMPLATE)
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mUrl = URL_TEMPLATE + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }


    def static class EinthusanSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mPublicationDate
        def long mDuration
        def String mImageUrl
        def String mBackgroundImageUrl
        def String mUrl

        EinthusanSourcesPuppet(ParentPuppet parent, String name, String description, String publicationDate, long duration, String imageUrl, String backgroundImageUrl, String url) {
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
            return new EinthusanSourceIterator()
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

        def class EinthusanSourceIterator implements SourcesPuppet.SourceIterator {

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
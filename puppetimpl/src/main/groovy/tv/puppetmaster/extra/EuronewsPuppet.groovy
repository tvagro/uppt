package tv.puppetmaster.extra

import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class EuronewsPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    static final SOURCES = [
            [
                    name:           "deutsch",
                    urls:           [
                            "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/14-live.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsde.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "english",
                    urls:           [
                            "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/ipad_en.m3u8"
                    ],
                    image:          "http://topsoundfm.com.ve/wp-content/uploads/2013/04/euronews.png",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "español",
                    urls:           [
                            "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/ipad_es.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewses.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "français",
                    urls:           [
                            "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_fr.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsfr.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "italiano",
                    urls:           [
                            "http://fr-par-iphone-2.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_it.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsit.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "português",
                    urls:           [
                            "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/ipad_pt.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewspt1.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "türkçe",
                    urls:           [
                            "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_tr.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewstr.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
            [
                    name:           "العربية",
                    urls:           [
                            "http://fr-par-iphone-1.cdn.hexaglobe.net/streaming/euronews_ewns/iphone_ar.m3u8"
                    ],
                    image:          "http://oklivetv.com/wp-content/uploads/2015/02/euronewsar.jpg",
                    background:     "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
            ],
    ]

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new EuronewsPuppetIterator()
        SOURCES.each { source ->
            EuronewsSourcesPuppet sourcesPuppet = new EuronewsSourcesPuppet(this, source.urls, source.name, source.image, source.background)
            children.add(sourcesPuppet)
        }

        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return "Euronews"
    }

    @Override
    String getCategory() {
        return "News"
    }

    @Override
    String getShortDescription() {
        return "Euronews is a multilingual news media headquartered in Lyon, France."
    }

    @Override
    String getImageUrl() {
        return "https://upload.wikimedia.org/wikipedia/commons/3/39/Euronews._2016_alternative_logo.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return "https://about.flipboard.com/wp-content/uploads/2013/10/euronews_blog.jpg"
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
        return null
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
        return 0xFF00317D
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFDCA300
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF00317D
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF00317D
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        def list = []
        SOURCES.each { source ->
            list << [
                    name       : "Euronews: " + source.name,
                    genres     : "NEWS",
                    logo       : source.image,
                    url        : source.urls[0]
            ]
        }
        return list
    }

    @Override
    PuppetIterator getRelated() {
        return null
    }

    @Override
    public String toString() {
        return getName()
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    def class EuronewsPuppetIterator extends PuppetIterator {

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

    def static class EuronewsSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def mUrls = []
        def String mName
        def String mShortDescription
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        EuronewsSourcesPuppet(ParentPuppet parent, ArrayList<String> urls, String name, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mUrls = urls
            mName = name
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
        }

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return -1
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new EuronewsSourceIterator()
        }

        @Override
        boolean isLive() {
            return true
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
            return "News"
        }

        @Override
        String getShortDescription() {
            return mShortDescription
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
        PuppetIterator getRelated() {
            return mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class EuronewsSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    for (String url : EuronewsSourcesPuppet.this.mUrls) {
                        SourceDescription source = new SourceDescription()
                        source.url = url
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
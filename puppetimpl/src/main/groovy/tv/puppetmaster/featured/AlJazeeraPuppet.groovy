package tv.puppetmaster.featured

import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class AlJazeeraPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 5

    static final SOURCES = [
            [
                    name:                   "Al Jazeera News",
                    description:            "A news and current affairs satellite TV channel",
                    urls:           [
                                            "http://aljazeera-eng-hd-live.hls.adaptive.level3.net/aljazeera/english2/index2073.m3u8",
                                            "http://aljazeera-eng-apple-live.adaptive.level3.net/apple/aljazeera/english/appleman.m3u8"
                    ],
                    image:                  "http://www.aljazeera.com/assets/images/aljazeera-logo.png",
                    background:             "http://trollback.com/main/wp-content/uploads/2013/09/31_Aljazeera_OnScreen_Breaking_Headline_Fact.jpg",
                    inaccessibleRegions:    ['us']
            ],
            [
                    name:                   "الجزيرة",
                    description:            "البث الحي",
                    urls:           [
                            "http://aljazeera-ara-apple-live.adaptive.level3.net/apple/aljazeera/arabic/800.m3u8",
                            "http://aljazeera-ara-apple-live.adaptive.level3.net/apple/aljazeera/arabic/160.m3u8"
                    ],
                    image:                  "http://www.aljazeera.com/assets/images/aljazeera-logo.png",
                    background:             "http://trollback.com/main/wp-content/uploads/2013/09/31_Aljazeera_OnScreen_Breaking_Headline_Fact.jpg",
                    inaccessibleRegions:    ['us']
            ],
            [
                    name:                   "ج",
                    description:            "ﺗﻠﻔﺰﻳﻮﻥ ﺝ. ﺷﺎﻫﺪ اﻟﺒﺚ اﻟﻤﺒﺎﺷﺮ",
                    urls:           [
                            "http://bcoveliveios-i.akamaihd.net/hls/live/206572/2014288370001/stream1/masterPlaylist.m3u8"
                    ],
                    image:                  "http://www.aljazeera.com/assets/images/aljazeera-logo.png",
                    background:             "http://trollback.com/main/wp-content/uploads/2013/09/31_Aljazeera_OnScreen_Breaking_Headline_Fact.jpg"
            ],
            [
                    name:                   "الجزيرة الوثائقية",
                    description:            "وراء كل صورة حكاية",
                    urls:           [
                            "http://aljazeera-doc-apple-live.adaptive.level3.net/apple/aljazeera/hq-doc/800kStream.m3u8"
                    ],
                    image:                  "https://raw.githubusercontent.com/naoufelboukari/kodi-aljazeera-doc/master/icon.png",
                    background:             "http://trollback.com/main/wp-content/uploads/2013/09/31_Aljazeera_OnScreen_Breaking_Headline_Fact.jpg"
            ],
    ]

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new AlJazeeraPuppetIterator(this)
        SOURCES.each { source ->
            AlJazeeraSourcesPuppet sourcesPuppet = new AlJazeeraSourcesPuppet()
            sourcesPuppet.setParent(this)
            sourcesPuppet.setName(source.name)
            sourcesPuppet.setShortDescription(source.description)
            sourcesPuppet.setUrls(source.urls)
            sourcesPuppet.setImageUrl(source.image)
            sourcesPuppet.setBackgroundImageUrl(source.background)
            sourcesPuppet.setInaccessibleRegions(source.inaccessibleRegions as String[])
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
        return "Al Jazeera"
    }

    @Override
    String getCategory() {
        return "News"
    }

    @Override
    String getShortDescription() {
        return "Focus on people and events that affect people's lives."
    }

    @Override
    String getImageUrl() {
        return "http://www.aljazeera.com/assets/images/aljazeera-logo.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://trollback.com/main/wp-content/uploads/2013/09/31_Aljazeera_OnScreen_Breaking_Headline_Fact.jpg"
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
        return 0xFFDCA300
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF00317D
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFDCA300
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
                    name       : source.name,
                    description: source.description,
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

    def class AlJazeeraPuppetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        public AlJazeeraPuppetIterator(ParentPuppet parent) {
            mParent = parent
        }

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

    def static class AlJazeeraSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mShortDescription
        def mUrls = []
        def String mImageUrl
        def String mBackgroundImageUrl
        def mUnavailableRegions = []

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return -1
        }

        void setUrls(def urls) {
            mUrls = urls
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new AlJazeeraSourceIterator()
        }

        @Override
        boolean isLive() {
            return true
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
            return null
        }

        void setShortDescription(String shortDescription) {
            mShortDescription = shortDescription
        }

        @Override
        String getShortDescription() {
            return mShortDescription
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

        void setInaccessibleRegions(String[] unavailableRegions) {
            mUnavailableRegions = unavailableRegions
        }

        @Override
        boolean isUnavailableIn(String region) {
            return region in mUnavailableRegions
        }

        @Override
        String getPreferredRegion() {
            return 'ca'
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
        PuppetIterator getRelated() {
            return mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class AlJazeeraSourceIterator implements SourcesPuppet.SourceIterator {

            def List<SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<SourceDescription>()
                    for (String url : AlJazeeraSourcesPuppet.this.mUrls) {
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
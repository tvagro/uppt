package tv.puppetmaster.featured

import tv.puppetmaster.data.i.InstallablePuppet
import tv.puppetmaster.data.i.ParentPuppet
import tv.puppetmaster.data.i.Puppet
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SearchesPuppet
import tv.puppetmaster.data.i.SourcesPuppet
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

public class NASAPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new NASAPuppetIterator(this)

        NASALiveSourcesPuppet sourcesPuppet = new NASALiveSourcesPuppet()
        sourcesPuppet.setParent(this)
        sourcesPuppet.setName("Nasa TV")
        sourcesPuppet.setUrl("http://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8")
        sourcesPuppet.setImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/public.jpg")
        sourcesPuppet.setBackgroundImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/public.jpg")
        children.add(sourcesPuppet)

        sourcesPuppet = new NASALiveSourcesPuppet()
        sourcesPuppet.setParent(this)
        sourcesPuppet.setName("Media Channel")
        sourcesPuppet.setUrl("http://nasatv-lh.akamaihd.net/i/NASA_103@319271/master.m3u8")
        sourcesPuppet.setImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/media.jpg")
        sourcesPuppet.setBackgroundImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/media.jpg")
        children.add(sourcesPuppet)

        sourcesPuppet = new NASALiveSourcesPuppet()
        sourcesPuppet.setParent(this)
        sourcesPuppet.setName("ISS Earth Viewing - ustream")
        sourcesPuppet.setUrl("http://iphone-streaming.ustream.tv/uhls/17074538/streams/live/iphone/playlist.m3u8")
        sourcesPuppet.setImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/isshd.jpg")
        sourcesPuppet.setBackgroundImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/isshd.jpg")
        children.add(sourcesPuppet)

        sourcesPuppet = new NASALiveSourcesPuppet()
        sourcesPuppet.setParent(this)
        sourcesPuppet.setName("ISS Earth Viewing - urthecast")
        sourcesPuppet.setUrl("http://d2ai41bknpka2u.cloudfront.net/live/iss.stream_source/playlist.m3u8")
        sourcesPuppet.setImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/isshd.jpg")
        sourcesPuppet.setBackgroundImageUrl("https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/isshd.jpg")
        children.add(sourcesPuppet)

        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return "NASA"
    }

    @Override
    String getCategory() {
        return "Education"
    }

    @Override
    String getShortDescription() {
        return "Provides access to 4 nasa-tv livestreams."
    }

    @Override
    String getImageUrl() {
        return "https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/icon.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://www.space.com/images/i/000/008/844/original/nasa-logo.jpg"
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
    int getFastlaneBackgroundColor() {
        return 0xFF0B3D91
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFFC3D21
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFFC3D21
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFFC3D21
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return [
                [
                        name:   "Nasa TV",
                        genres: "EDUCATION, FAMILY_KIDS, TECH_SCIENCE, TRAVEL",
                        logo:   "https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/public.jpg",
                        url:    "http://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8",
                ],
                [
                        name:   "ISS Live Stream",
                        genres: "EDUCATION, FAMILY_KIDS, TECH_SCIENCE, TRAVEL",
                        logo:   "https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/iss.jpg",
                        url:    "http://iphone-streaming.ustream.tv/ustreamVideo/9408562/streams/live/playlist.m3u8",
                ],
                [
                        name:   "Media Channel",
                        genres: "NEWS, EDUCATION, FAMILY_KIDS, TECH_SCIENCE, TRAVEL",
                        logo:   "https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/media.jpg",
                        url:    "http://nasatv-lh.akamaihd.net/i/NASA_103@319271/master.m3u8",
                ],
                [
                        name:   "ISS Earth Viewing - ustream",
                        genres: "EDUCATION, FAMILY_KIDS, TECH_SCIENCE, TRAVEL",
                        logo:   "https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/isshd.jpg",
                        url:    "http://iphone-streaming.ustream.tv/uhls/17074538/streams/live/iphone/playlist.m3u8",
                ],
                [
                        name:   "ISS Earth Viewing - urthecast",
                        genres: "EDUCATION, FAMILY_KIDS, TECH_SCIENCE, TRAVEL",
                        logo:   "https://raw.githubusercontent.com/dersphere/plugin.video.nasa/master/resources/media/isshd.jpg",
                        url:    "http://d2ai41bknpka2u.cloudfront.net/live/iss.stream_source/playlist.m3u8",
                ]
        ]
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

    def class NASAPuppetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        public NASAPuppetIterator(ParentPuppet parent) {
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

    def static class NASALiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String mUrl
        def String mImageUrl
        def String mBackgroundImageUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        @Override
        String getPublicationDate() {
            return null
        }

        @Override
        long getDuration() {
            return -1
        }

        void setUrl(String url) {
            mUrl = url
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new NASALiveSourceIterator(mUrl)
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
        PuppetIterator getRelated() {
            return mParent.getChildren()
        }

        @Override
        public String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class NASALiveSourceIterator implements SourcesPuppet.SourceIterator {

            def String mSourceUrl
            def int currentIndex

            public NASALiveSourceIterator(String sourceUrl) {
                mSourceUrl = sourceUrl
            }

            @Override
            boolean hasNext() {
                return currentIndex == 0
            }

            @Override
            SourceDescription next() {
                currentIndex++
                SourceDescription sourceDescription = new SourceDescription()
                sourceDescription.url = mSourceUrl
                return sourceDescription
            }

            @Override
            void remove() {

            }
        }
    }
}
package tv.puppetmaster.featured

import org.json.JSONArray
import org.json.JSONObject
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator
import tv.puppetmaster.data.i.SourcesPuppet.SourceDescription

import java.util.concurrent.TimeUnit
import java.util.regex.Matcher

public class CBSNPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    @Override
    PuppetIterator getChildren() {
        PuppetIterator children = new CBSNPuppetIterator(this)

        CBSNLiveSourcesPuppet sourcesPuppet = new CBSNLiveSourcesPuppet()
        sourcesPuppet.setParent(this)
        sourcesPuppet.setName("CBS News Live")
        sourcesPuppet.setShortDescription("Live English-language internet news channel.")

        String js = new URL("http://cbsn1.cbsistatic.com/scripts/VideoPlayer.js").getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11'])
        Matcher matcher = js =~ /this.hlsurl_nab = '(.+?)'/
        String urls = ""
        if (matcher.find()) {
            urls = matcher.group(1) + "|"
        }
        matcher = js =~ /this.hlsurl_na = '(.+?)'/
        if (matcher.find()) {
            if (urls != "") {
                urls += "|"
            }
            urls += matcher.group(1)
        }

        sourcesPuppet.setUrl(urls)
        sourcesPuppet.setImageUrl("https://pbs.twimg.com/profile_images/530311535550152704/Q5pSZZk6.jpeg")
        sourcesPuppet.setBackgroundImageUrl("http://cbsnews2.cbsistatic.com/hub/i/2015/02/09/bb9b2060-bfbe-43d2-8878-0a154eac7a27/cbsn-generic.jpg")
        children.add(sourcesPuppet)

        children.add(new CBSNClipsPuppet(
                this,
                "Clips",
                "Live English-language internet news channel.",
                "https://pbs.twimg.com/profile_images/530311535550152704/Q5pSZZk6.jpeg",
                "http://cbsnews2.cbsistatic.com/hub/i/2015/02/09/bb9b2060-bfbe-43d2-8878-0a154eac7a27/cbsn-generic.jpg"
        ))
        return children
    }

    @Override
    boolean isTopLevel() {
        return true
    }

    @Override
    String getName() {
        return "CBS News"
    }

    @Override
    String getCategory() {
        return "News"
    }

    @Override
    String getShortDescription() {
        return "Live English-language internet news channel"
    }

    @Override
    String getImageUrl() {
        return "https://pbs.twimg.com/profile_images/530311535550152704/Q5pSZZk6.jpeg"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://cbsnews2.cbsistatic.com/hub/i/2015/02/09/bb9b2060-bfbe-43d2-8878-0a154eac7a27/cbsn-generic.jpg"
    }

    @Override
    boolean isAvailable(String region) {
        return true
    }

    @Override
    String[] preferredRegions() {
        return null
    }

    @Override
    int immigrationStricture() {
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
        return 0xFF424B55
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFB12124
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFB12124
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return [[
                        name:           "CBSN",
                        description:    "A live 24/7 streaming video news channel that features original CBS News reporting",
                        genres:         "NEWS",
                        logo:           getImageUrl(),
                        url:            "http://cbsnewshd-lh.akamaihd.net/i/CBSNHD_7@199302/master.m3u8"
                ]]
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

    def class CBSNClipsPuppet implements ParentPuppet {

        def ParentPuppet mParent
        def String mName
        def String mDescription
        def String mImageUrl
        def String mBackgroundImageUrl

        public CBSNClipsPuppet(ParentPuppet parent, String name, String description, String imageUrl, String backgroundImageUrl) {
            mParent = parent
            mName = name
            mDescription = description
            mImageUrl = imageUrl != null && imageUrl.startsWith("/") ? mBaseUrl + imageUrl : imageUrl
            mBackgroundImageUrl = backgroundImageUrl != null && backgroundImageUrl.startsWith("/") ? mBaseUrl + backgroundImageUrl : backgroundImageUrl
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
        boolean isAvailable(String region) {
            return true
        }

        @Override
        String[] preferredRegions() {
            return null
        }

        @Override
        int immigrationStricture() {
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
            return getName()
        }

        @Override
        PuppetIterator getChildren() {
            return new CBSNPuppetIterator(this)
        }

        @Override
        boolean isTopLevel() {
            return mParent == null || mParent.getParent() == null
        }
    }

    def class CBSNPuppetIterator extends PuppetIterator {

        def ParentPuppet mParent
        def ArrayList<Puppet> mPuppets
        def int currentIndex = 0

        public CBSNPuppetIterator(ParentPuppet parent) {
            mParent = parent
        }

        @Override
        boolean hasNext() {
            if (mPuppets != null && currentIndex < mPuppets.size()) {
                return true
            } else if (mPuppets == null) {
                mPuppets = new ArrayList<>()
                String json = new URL("http://cbsn.cbsnews.com/rundown/?device=desktop").getText(requestProperties: ['User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11'])
                JSONArray items = (JSONArray) ((JSONObject) new JSONObject(json).get("navigation")).get("data")
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i)
                    if (item.get("type") == "dvr") {
                        mPuppets.add(new CBSNSourcesPuppet(
                                mParent,
                                item.get("url"),
                                item.get("headlineshort"),
                                item.get("headline"),
                                item.get("thumbnail_url_hd"),
                                item.get("startDate"),
                                convertDuration(item.get("segmentDur").toString())
                        ))
                    }
                }
            }
            return currentIndex < mPuppets.size()
        }

        @Override
        void add(Puppet puppet) {
            if (mPuppets == null) {
                mPuppets = new ArrayList<>()
            }
            mPuppets.add(puppet)
        }

        @Override
        Puppet next() {
            return mPuppets.get(currentIndex++)
        }

        @Override
        void remove() {

        }

        private static long convertDuration(String str) {  // HH:MM[:SS] to milliseconds
            if (str.equals("")) {
                return -1
            }

            String[] data = str.split(":")

            int time
            if (data.length > 2) {
                int hours  = Integer.parseInt(data[0])
                int minutes = Integer.parseInt(data[1])
                int seconds = Integer.parseInt(data[2])
                time = seconds + 60 * minutes + 3600 * hours
            } else if (data.length > 1) {
                int minutes = Integer.parseInt(data[0])
                int seconds = Integer.parseInt(data[1])
                time = seconds + 60 * minutes
            } else {
                int seconds = Integer.parseInt(data[0])
                time = seconds
            }

            return TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS)
        }
    }

    def static class CBSNSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mUrl
        def String mName
        def String mShortDescription
        def String mImageUrl
        def String mPublicationDate
        def long mDuration

        public CBSNSourcesPuppet(parent, url, name, shortDescription, imageUrl, publicationDate, duration) {
            mParent = parent
            mUrl = url
            mName = name
            mShortDescription = shortDescription
            mImageUrl = imageUrl
            mPublicationDate = publicationDate
            mDuration = duration
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
            return new CBSNSourceIterator()
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
            return null
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
            return "http://cbsnews2.cbsistatic.com/hub/i/2015/02/09/bb9b2060-bfbe-43d2-8878-0a154eac7a27/cbsn-generic.jpg"
        }

        @Override
        boolean isAvailable(String region) {
            return true
        }

        @Override
        String[] preferredRegions() {
            return null
        }

        @Override
        int immigrationStricture() {
            return 0
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
            return getName()
        }

        def class CBSNSourceIterator implements SourcesPuppet.SourceIterator {

            def SourceDescription mSource
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSource == null) {
                    mSource = new SourceDescription()
                    mSource.url = CBSNSourcesPuppet.this.mUrl
                }
                return currentIndex < 1
            }

            @Override
            SourceDescription next() {
                currentIndex++
                return mSource
            }

            @Override
            void remove() {

            }
        }
    }

    def static class CBSNLiveSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent
        def String mName
        def String[] mUrls
        def String mShortDescription
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
            mUrls = url.split("\\|")
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new CBSNLiveSourceIterator(mUrls)
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

        @Override
        boolean isAvailable(String region) {
            return true
        }

        @Override
        String[] preferredRegions() {
            return null
        }

        @Override
        int immigrationStricture() {
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
            return null
        }

        @Override
        public String toString() {
            return getName()
        }

        def class CBSNLiveSourceIterator implements SourcesPuppet.SourceIterator {

            def String[] mSourceUrls
            def int currentIndex = 0

            public CBSNLiveSourceIterator(String[] sourceUrls) {
                mSourceUrls = sourceUrls
            }

            @Override
            boolean hasNext() {
                return mSourceUrls != null && currentIndex < mSourceUrls.length
            }

            @Override
            SourceDescription next() {
                SourceDescription sourceDescription = new SourceDescription()
                sourceDescription.url = mSourceUrls[currentIndex++]
                return sourceDescription
            }

            @Override
            void remove() {

            }
        }
    }
}